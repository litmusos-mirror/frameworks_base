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

package com.android.server.companion.virtual;

import android.companion.virtual.IVirtualDevice;
import android.window.DisplayWindowPolicyController;

/**
 * Virtual device manager local service interface.
 * Only for use within system server.
 */
public abstract class VirtualDeviceManagerInternal {

    /**
     * Validate the virtual device.
     */
    public abstract boolean isValidVirtualDevice(IVirtualDevice virtualDevice);

    /**
     * Notify a virtual display is created.
     *
     * @param virtualDevice The virtual device where the virtual display located.
     * @param displayId The display id of the created virtual display.
     *
     * @return The {@link DisplayWindowPolicyController} of the virtual device.
     */
    public abstract DisplayWindowPolicyController onVirtualDisplayCreated(
            IVirtualDevice virtualDevice, int displayId);

    /**
     * Notify a virtual display is removed.
     *
     * @param virtualDevice The virtual device where the virtual display located.
     * @param displayId The display id of the removed virtual display.
     */
    public abstract void onVirtualDisplayRemoved(IVirtualDevice virtualDevice, int displayId);

    /**
     * Returns true if the given {@code uid} is the owner of any virtual devices that are
     * currently active.
     */
    public abstract boolean isAppOwnerOfAnyVirtualDevice(int uid);

    /**
     * Returns true if the given {@code uid} is currently running on any virtual devices. This is
     * determined by whether the app has any activities in the task stack on a virtual-device-owned
     * display.
     */
    public abstract boolean isAppRunningOnAnyVirtualDevice(int uid);
}