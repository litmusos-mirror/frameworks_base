/*
 * Copyright (C) 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.server.companion;

import android.annotation.NonNull;
import android.annotation.Nullable;
import android.annotation.UserIdInt;
import android.companion.AssociationInfo;
import android.net.MacAddress;
import android.util.Log;
import android.util.SparseArray;

import com.android.internal.annotations.GuardedBy;
import com.android.internal.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Implementation of the {@link AssociationStore}, with addition of the methods for modification.
 * <ul>
 * <li> {@link #addAssociation(AssociationInfo)}
 * <li> {@link #removeAssociation(int)}
 * <li> {@link #updateAssociation(AssociationInfo)}
 * </ul>
 *
 * The class has package-private access level, and instances of the class should only be created by
 * the {@link CompanionDeviceManagerService}.
 * Other system component (both inside and outside if the com.android.server.companion package)
 * should use public {@link AssociationStore} interface.
 */
class AssociationStoreImpl implements AssociationStore {
    private static final boolean DEBUG = false;
    private static final String TAG = "AssociationStore";

    private final Object mLock = new Object();

    @GuardedBy("mLock")
    private final Map<Integer, AssociationInfo> mIdMap;
    @GuardedBy("mLock")
    private final Map<MacAddress, Set<Integer>> mAddressMap;
    @GuardedBy("mLock")
    private final SparseArray<List<AssociationInfo>> mCachedPerUser = new SparseArray<>();

    @GuardedBy("mListeners")
    private final Set<OnChangeListener> mListeners = new LinkedHashSet<>();

    AssociationStoreImpl(Collection<AssociationInfo> associations) {
        synchronized (mLock) {
            final int size = associations.size();
            mIdMap = new HashMap<>(size);
            mAddressMap = new HashMap<>(size);

            for (AssociationInfo association : associations) {
                final int id = association.getId();
                mIdMap.put(id, association);

                final MacAddress address = association.getDeviceMacAddress();
                if (address != null) {
                    mAddressMap.computeIfAbsent(address, it -> new HashSet<>()).add(id);
                }
            }
        }
    }

    void addAssociation(@NonNull AssociationInfo association) {
        final int id = association.getId();

        if (DEBUG) {
            Log.i(TAG, "addAssociation() " + association.toShortString());
            Log.d(TAG, "  association=" + association);
        }

        synchronized (mLock) {
            if (mIdMap.containsKey(id)) {
                if (DEBUG) Log.w(TAG, "Association already stored.");
                return;
            }
            mIdMap.put(id, association);

            final MacAddress address = association.getDeviceMacAddress();
            if (address != null) {
                mAddressMap.computeIfAbsent(address, it -> new HashSet<>()).add(id);
            }

            invalidateCacheForUserLocked(association.getUserId());
        }

        broadcastChange(CHANGE_TYPE_ADDED, association);
    }

    void updateAssociation(@NonNull AssociationInfo updated) {
        final int id = updated.getId();

        if (DEBUG) {
            Log.i(TAG, "updateAssociation() " + updated.toShortString());
            Log.d(TAG, "  updated=" + updated);
        }

        final AssociationInfo current;
        final boolean macAddressChanged;
        synchronized (mLock) {
            current = mIdMap.get(id);
            if (current == null) {
                if (DEBUG) Log.w(TAG, "Association with id " + id + " does not exist.");
                return;
            }
            if (DEBUG) Log.d(TAG, "  current=" + current);

            if (current.equals(updated)) {
                if (DEBUG) Log.w(TAG, "  No changes.");
                return;
            }

            // Update the ID-to-Association map.
            mIdMap.put(id, updated);

            // Update the MacAddress-to-List<Association> map if needed.
            final MacAddress updatedAddress = updated.getDeviceMacAddress();
            final MacAddress currentAddress = current.getDeviceMacAddress();
            macAddressChanged = Objects.equals(
                    current.getDeviceMacAddress(), updated.getDeviceMacAddress());
            if (macAddressChanged) {
                if (currentAddress != null) {
                    mAddressMap.get(currentAddress).remove(id);
                }
                if (updatedAddress != null) {
                    mAddressMap.computeIfAbsent(updatedAddress, it -> new HashSet<>()).add(id);
                }
            }
        }

        final int changeType = macAddressChanged ? CHANGE_TYPE_UPDATED_ADDRESS_CHANGED
                : CHANGE_TYPE_UPDATED_ADDRESS_UNCHANGED;
        broadcastChange(changeType, updated);
    }

    void removeAssociation(int id) {
        if (DEBUG) Log.i(TAG, "removeAssociation() id=" + id);

        final AssociationInfo association;
        synchronized (mLock) {
            association = mIdMap.remove(id);

            if (association == null) {
                if (DEBUG) Log.w(TAG, "Association with id " + id + " is not stored.");
                return;
            } else {
                if (DEBUG) {
                    Log.i(TAG, "removed " + association.toShortString());
                    Log.d(TAG, "  association=" + association);
                }
            }

            final MacAddress macAddress = association.getDeviceMacAddress();
            if (macAddress != null) {
                mAddressMap.get(macAddress).remove(id);
            }

            invalidateCacheForUserLocked(association.getUserId());
        }

        broadcastChange(CHANGE_TYPE_REMOVED, association);
    }

    public @NonNull Collection<AssociationInfo> getAssociations() {
        final Collection<AssociationInfo> allAssociations;
        synchronized (mLock) {
            allAssociations = mIdMap.values();
        }
        return Collections.unmodifiableCollection(allAssociations);
    }

    public @NonNull List<AssociationInfo> getAssociationsForUser(@UserIdInt int userId) {
        synchronized (mLock) {
            return getAssociationsForUserLocked(userId);
        }
    }

    public @NonNull List<AssociationInfo> getAssociationsForPackage(
            @UserIdInt int userId, @NonNull String packageName) {
        final List<AssociationInfo> associationsForUser = getAssociationsForUser(userId);
        final List<AssociationInfo> associationsForPackage =
                CollectionUtils.filter(associationsForUser,
                        it -> it.getPackageName().equals(packageName));
        return Collections.unmodifiableList(associationsForPackage);
    }

    public @Nullable AssociationInfo getAssociationsForPackageWithAddress(
            @UserIdInt int userId, @NonNull String packageName, @NonNull String macAddress) {
        final List<AssociationInfo> associations = getAssociationsByAddress(macAddress);
        return CollectionUtils.find(associations,
                it -> it.belongsToPackage(userId, packageName));
    }

    public @Nullable AssociationInfo getAssociationById(int id) {
        synchronized (mLock) {
            return mIdMap.get(id);
        }
    }

    public @NonNull List<AssociationInfo> getAssociationsByAddress(@NonNull String macAddress) {
        final MacAddress address = MacAddress.fromString(macAddress);

        synchronized (mLock) {
            final Set<Integer> ids = mAddressMap.get(address);
            if (ids == null) return Collections.emptyList();

            final List<AssociationInfo> associations = new ArrayList<>();
            for (AssociationInfo association : mIdMap.values()) {
                if (address.equals(association.getDeviceMacAddress())) {
                    associations.add(association);
                }
            }

            return Collections.unmodifiableList(associations);
        }
    }

    @GuardedBy("mLock")
    private @NonNull List<AssociationInfo> getAssociationsForUserLocked(@UserIdInt int userId) {
        final List<AssociationInfo> cached = mCachedPerUser.get(userId);
        if (cached != null) {
            return cached;
        }

        final List<AssociationInfo> associationsForUser = new ArrayList<>();
        for (AssociationInfo association : mIdMap.values()) {
            if (association.getUserId() == userId) {
                associationsForUser.add(association);
            }
        }
        final List<AssociationInfo> set = Collections.unmodifiableList(associationsForUser);
        mCachedPerUser.set(userId, set);
        return set;
    }

    @GuardedBy("mLock")
    private void invalidateCacheForUserLocked(@UserIdInt int userId) {
        mCachedPerUser.delete(userId);
    }

    public void registerListener(@NonNull OnChangeListener listener) {
        synchronized (mListeners) {
            mListeners.add(listener);
        }
    }

    public void unregisterListener(@NonNull OnChangeListener listener) {
        synchronized (mListeners) {
            mListeners.remove(listener);
        }
    }

    private void broadcastChange(@ChangeType int changeType, AssociationInfo association) {
        synchronized (mListeners) {
            for (OnChangeListener listener : mListeners) {
                listener.onAssociationChanged(changeType, association);

                switch (changeType) {
                    case CHANGE_TYPE_ADDED:
                        listener.onAssociationAdded(association);
                        break;

                    case CHANGE_TYPE_REMOVED:
                        listener.onAssociationRemoved(association);
                        break;

                    case CHANGE_TYPE_UPDATED_ADDRESS_CHANGED:
                        listener.onAssociationUpdated(association, true);
                        break;

                    case CHANGE_TYPE_UPDATED_ADDRESS_UNCHANGED:
                        listener.onAssociationUpdated(association, false);
                        break;
                }
            }
        }
    }
}