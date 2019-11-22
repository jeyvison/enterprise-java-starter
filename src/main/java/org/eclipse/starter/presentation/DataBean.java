/*
 * Copyright (c) 2017-2019 Contributors to the Eclipse Foundation
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
package org.eclipse.starter.presentation;

import org.eclipse.starter.business.model.entity.JakartaRuntime;
import org.eclipse.starter.business.model.entity.JavaSEVersion;
import org.eclipse.starter.business.model.entity.MicroProfileVersion;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.faces.model.SelectItem;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ApplicationScoped
@Named
public class DataBean {

    private List<SelectItem> javaSEItems;
    private List<SelectItem> mpItems;

    @PostConstruct
    public void init() {
        defineJavaSEItems();

        defineMPVersions();
    }

    private void defineMPVersions() {
        mpItems = new ArrayList<>();
        for (MicroProfileVersion microProfileVersion : MicroProfileVersion.values()) {
            if (versionHasImplementations(microProfileVersion)) {
                mpItems.add(new SelectItem(microProfileVersion.getCode(), microProfileVersion.getLabel()));
            }
        }
    }

    private boolean versionHasImplementations(MicroProfileVersion microProfileVersion) {
        return Arrays.stream(JakartaRuntime.values())
                .anyMatch(server -> server.getMpVersions().contains(microProfileVersion));

    }

    private void defineJavaSEItems() {
        javaSEItems = new ArrayList<>();
        for (JavaSEVersion javaSEVersion : JavaSEVersion.values()) {
            javaSEItems.add(new SelectItem(javaSEVersion.getCode(), javaSEVersion.getLabel()));
        }
    }

    public List<SelectItem> getJavaSEItems() {
        return javaSEItems;
    }

    public List<SelectItem> getMpItems() {
        return mpItems;
    }

}
