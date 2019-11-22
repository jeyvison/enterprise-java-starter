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
package org.eclipse.starter.business.addons.control;

import org.eclipse.starter.business.model.entity.JessieModel;

import java.util.List;

/**
 *
 */
public interface JessieAddon {

    /**
     * The name (identifier) of the addon.
     */
    String addonName();

    /**
     * The priority after which addons will be sorted. A lower value will be considered first.
     * <p>
     * We can't use {@code @Priority} as the annotation is lost when CDI proxies are created.
     */
    int priority();

    /**
     * Validates the project model for the specified values.
     */
    void validate(JessieModel model);

    /**
     * Initializes potential properties and the templating variables.
     * <p>
     * In case variables are required to be set that are used in an earlier Creator
     * (which is handled earlier than this addon), then they must be initialized in this method.
     */
    void initProperties(JessieModel model);

    /**
     * Return the addons on which this addon is dependent. Conditionally when based on the JessieModel
     *
     * @return List of add-on names.
     */
    List<String> getDependentAddons(JessieModel model);

    /**
     * Creates the project files based on the templates.
     * <p>
     * The templates can use variables that have been defined in {@link #initProperties(JessieModel)},
     * or earlier addons.
     */
    void createFiles(JessieModel model);

}
