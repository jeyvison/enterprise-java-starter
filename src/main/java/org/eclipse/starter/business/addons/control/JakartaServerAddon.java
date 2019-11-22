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

import org.eclipse.starter.business.exception.JessieConfigurationException;
import org.eclipse.starter.business.model.entity.JakartaRuntime;
import org.eclipse.starter.business.model.entity.JessieModel;
import org.eclipse.starter.business.model.entity.MavenDependency;
import org.eclipse.starter.business.model.entity.SpecificationExample;

import javax.enterprise.context.ApplicationScoped;
import java.util.*;

/**
 *
 */
@ApplicationScoped
public class JakartaServerAddon extends AbstractAddon {

    @Override
    public String addonName() {
        return "jk";
    }

    @Override
    public int priority() {
        return 60;
    }

    protected void validateModel(JessieModel model) {
        checkServerValue(model);

        handleSpecOptions(model);
    }

    private void handleSpecOptions(JessieModel model) {
        model.getSelectedExamples().forEach(e -> model.addVariable("examples_" + e.getCode(), "true"));
    }

    private void checkServerValue(JessieModel model) {
        if (model.getRuntime() == null) {
            throw new JessieConfigurationException("Unknown value for jakarta runtime");
        }

        model.addVariable("jk_servername", model.getRuntime().getCode());
    }

    @Override
    public List<String> getDependentAddons(JessieModel model) {
        List<String> result = new ArrayList<>();
        result.add(model.getRuntime().getCode());

        return result;
    }

    @Override
    public void initProperties(JessieModel model) {
        model.getVariables().put("runtime_features", new HashMap<>());
        model.getVariables().put("dependencies", new ArrayList<>());

        List<MavenDependency> testDependencies = new ArrayList<>();

        if (model.getSelectedExamples().contains(SpecificationExample.TESTS)) {
            testDependencies.add(new MavenDependency("junit", "junit", "4.12", "test"));
        }

        model.getVariables().put("test_dependencies", testDependencies);
    }

    @Override
    public void createFiles(JessieModel model) {
        Map<String, Object> variables = model.getVariables();
        Set<SpecificationExample> examples = model.getSelectedExamples();

        String artifactId = model.getArtifactId();
        variables.put("jar_file", defineJarFileName(model.getRuntime(), artifactId));
        variables.put("artifact_id", artifactId);

        String rootJava = getJavaApplicationRootPackage(model);
        String rootTestJava = getJavaTestRootPackage(model);

        processTemplateFile(model.getDirectory(), "pom.xml", variables);

        if (examples.contains(SpecificationExample.TESTS)) {
            String testCode = model.getDirectory() + "/" + rootTestJava;
            directoryCreator.createDirectory(testCode);

            processTemplateFile(testCode, "SimpleTest.java", variables);

        }

        if (examples.contains(SpecificationExample.MP_HEALTH_CHECKS)) {
            String healthDirectory = model.getDirectory() + "/" + rootJava + "/health";
            directoryCreator.createDirectory(healthDirectory);

            processTemplateFile(healthDirectory, "ServiceHealthCheck.java", variables);
        }

        if (examples.contains(SpecificationExample.MP_CONFIG)) {
            String configDirectory = model.getDirectory() + "/" + rootJava + "/config";
            directoryCreator.createDirectory(configDirectory);

            processTemplateFile(configDirectory, "ConfigTestController.java", variables);
        }

        if (examples.contains(SpecificationExample.MP_METRICS)) {
            String metricDirectory = model.getDirectory() + "/" + rootJava + "/metric";
            directoryCreator.createDirectory(metricDirectory);

            processTemplateFile(metricDirectory, "MetricController.java", variables);
        }

        if (examples.contains(SpecificationExample.MP_FAULT_TOLERANCE)) {
            String faultDirectory = model.getDirectory() + "/" + rootJava + "/resilient";
            directoryCreator.createDirectory(faultDirectory);

            processTemplateFile(faultDirectory, "ResilienceController.java", variables);
        }

        if (examples.contains(SpecificationExample.MP_REST_CLIENT)) {
            String clientMainDirectory = model.getDirectory() + "/" + rootJava + "/client";
            directoryCreator.createDirectory(clientMainDirectory);

            processTemplateFile(clientMainDirectory, "Service.java", variables);
            processTemplateFile(clientMainDirectory, "ClientController.java", variables);
        }

        if (examples.contains(SpecificationExample.MP_JWT_AUTH)) {
            String aSecureDirectory = model.getDirectory() + "/" + rootJava + "/secure";

            processTemplateFile(aSecureDirectory, "TestSecureController.java", variables);
            processTemplateFile(aSecureDirectory, "MPJWTToken.java", variables);

            String resourceDirectory = getResourceDirectory(model);

            processTemplateFile(resourceDirectory, "privateKey.pem", variables);
        }

        processTemplateFile(model.getDirectory(), "readme.md", variables);
    }

    private String defineJarFileName(JakartaRuntime jakartaRuntime, String artifactId) {
        return String.format(jakartaRuntime.getJarFileName(), artifactId);
    }

}
