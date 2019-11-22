/*
 * Copyright (c) 2019 Contributors to the Eclipse Foundation
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
 */
package org.eclipse.starter.business.addons.control;

import org.eclipse.starter.business.addons.control.AbstractAddon;
import org.eclipse.starter.business.model.entity.JakartaRuntime;
import org.eclipse.starter.business.model.entity.JessieModel;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TomeeServer extends AbstractAddon {

    @Override
    public String addonName() {
        return JakartaRuntime.TOMEE.getCode();
    }

    @Override
    public void createFiles(JessieModel model) {
        // nothing to do
    }

    @Override
    public void initProperties(JessieModel model) {
        String tomeeVersion = "";
        switch (model.getMicroProfileVersion()) {
            case MP22:
                break;
            case MP21:
                tomeeVersion = "8.0.0-M3";
                break;
            case MP20:
                tomeeVersion = "8.0.0-M3";
                break;
            case MP14:
                tomeeVersion = "8.0.0-M3";
                break;
            case MP13:
                tomeeVersion = "8.0.0-M3";
                break;
            case MP12:
                tomeeVersion = "8.0.0-M3";
                break;
            default:
        }
        model.addVariable("tomee.version", tomeeVersion);
    }

}
