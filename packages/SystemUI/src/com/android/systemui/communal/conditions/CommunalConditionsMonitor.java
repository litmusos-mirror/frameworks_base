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

package com.android.systemui.communal.conditions;


import static com.android.systemui.communal.dagger.CommunalModule.COMMUNAL_CONDITIONS;

import com.android.systemui.dagger.SysUISingleton;
import com.android.systemui.util.condition.Condition;
import com.android.systemui.util.condition.Monitor;

import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * A concrete implementation of {@Monitor} with conditions for monitoring when communal mode should
 * be enabled.
 */
@SysUISingleton
public class CommunalConditionsMonitor extends Monitor {
    @Inject
    public CommunalConditionsMonitor(
            @Named(COMMUNAL_CONDITIONS) Set<Condition> communalConditions) {
        super(communalConditions);
    }
}