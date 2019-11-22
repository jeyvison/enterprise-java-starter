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
package org.eclipse.starter.business.model.boundary;

import org.eclipse.starter.business.addons.control.JessieAddon;
import org.eclipse.starter.business.templates.control.TemplateVariableProvider;
import org.eclipse.starter.business.addons.boundary.AddonManager;
import org.eclipse.starter.business.model.control.JessieModelInitializer;
import org.eclipse.starter.business.model.control.validation.ModelValidation;
import org.eclipse.starter.business.model.entity.JessieModel;
import org.eclipse.starter.business.templates.control.TemplateModelValues;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 */
@ApplicationScoped
public class ModelManager {

    @Inject
    private JessieModelInitializer modelInitializer;

    @Inject
    private ModelValidation modelValidation;

    @Inject
    private TemplateModelValues templateModelValues;

    @Inject
    private TemplateVariableProvider templateVariableProvider;

    @Inject
    private AddonManager addonManager;

    /**
     * @param model
     * @param localExecution Is the generation run on local machine and thus need real directories.
     */
    public void prepareModel(JessieModel model, boolean localExecution) {
        modelInitializer.defineDefaults(model, localExecution);

        modelValidation.validate(model);

        templateModelValues.applyTemplateValues(model);

        List<JessieAddon> allAddons = determineAddons(model);
        model.addParameter(JessieModel.Parameter.ADDONS, allAddons);

        modelValidation.validateByAddons(model);

        Map<String, Object> variables = templateVariableProvider.determineVariables(model);
        model.addVariables(variables);
    }

    private List<JessieAddon> determineAddons(JessieModel model) {
        List<JessieAddon> allAddons = addonManager.getAddons(model.getAddons());

        addDependentAddons(allAddons, model);

        orderAddons(allAddons);

        model.setAddons(allAddons.stream()
                .map(JessieAddon::addonName)
                .collect(Collectors.toList()));

        return allAddons;
    }

    private void orderAddons(List<JessieAddon> allAddons) {
        allAddons.sort(Comparator.comparing(JessieAddon::priority));
    }

    private void addDependentAddons(List<JessieAddon> allAddons, JessieModel model) {

        Set<String> dependents = allAddons.stream()
                .map(a -> a.getDependentAddons(model))
                .flatMap(Collection::stream)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        List<String> addons = allAddons.stream().map(JessieAddon::addonName).collect(Collectors.toList());

        dependents.removeAll(addons);

        if (!dependents.isEmpty()) {
            allAddons.addAll(addonManager.getAddons(dependents));
            addDependentAddons(allAddons, model);
        }

    }

}
