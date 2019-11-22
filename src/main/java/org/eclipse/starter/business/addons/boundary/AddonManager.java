/*
 * Copyright (c) 2017-2018 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *   2018-09-29 - Rudy De Busscher
 *      Initially authored in Atbash Jessie
 */
package org.eclipse.starter.business.addons.boundary;

import org.eclipse.starter.business.addons.control.JessieAddon;
import org.eclipse.starter.business.exception.JessieUnexpectedException;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.*;

/**
 *
 */
@ApplicationScoped
public class AddonManager {

    @Inject
    private Instance<JessieAddon> addons;

    public List<JessieAddon> getAddons(Collection<String> names) {
        Set<String> addonNames = new HashSet<>(names);
        List<JessieAddon> addons = new ArrayList<>();

        for (JessieAddon addon : this.addons) {
            if (names.contains(addon.addonName())) {
                addons.add(addon);
                addonNames.remove(addon.addonName());
            }
        }

        if (!addonNames.isEmpty()) {
            throw new JessieUnexpectedException("Unable to find addons for names: " + addonNames);
        }

        return addons;
    }

}
