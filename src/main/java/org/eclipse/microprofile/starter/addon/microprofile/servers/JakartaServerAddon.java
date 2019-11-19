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

package org.eclipse.microprofile.starter.addon.microprofile.servers;

import org.eclipse.microprofile.starter.addon.microprofile.servers.model.MicroprofileSpec;
import org.eclipse.microprofile.starter.addon.microprofile.servers.model.SupportedServer;
import org.eclipse.microprofile.starter.core.exception.JessieConfigurationException;
import org.eclipse.microprofile.starter.core.model.JessieModel;
import org.eclipse.microprofile.starter.core.model.OptionValue;
import org.eclipse.microprofile.starter.spi.AbstractAddon;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import java.util.*;

import static org.eclipse.microprofile.starter.core.model.JessieModel.Parameter.MICROPROFILESPECS;

/**
 *
 */
@ApplicationScoped
public class JakartaServerAddon extends AbstractAddon {

    private List<MicroprofileSpec> microprofileSpecs;

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
        OptionValue specs = options.get("specs");

        microprofileSpecs = new ArrayList<>();
        List<String> invalidSpecs = new ArrayList<>();
        for (String spec : specs.getValues()) {
            MicroprofileSpec microprofileSpec = MicroprofileSpec.valueFor(spec);
            if (microprofileSpec == null) {
                invalidSpecs.add(spec);
            } else {
                model.addVariable("mp_" + microprofileSpec.getCode(), "true");
                microprofileSpecs.add(microprofileSpec);
            }
        }

        if (!invalidSpecs.isEmpty()) {
            throw new JessieConfigurationException(invalidSpecValue(invalidSpecs));
        }

        model.addParameter(MICROPROFILESPECS, microprofileSpecs);
    }

    private void checkServerValue(JessieModel model) {
        String serverName = options.get("server").getSingleValue();
        SupportedServer supportedServer = SupportedServer.valueFor(serverName);

        if (supportedServer == null) {
            throw new JessieConfigurationException(invalidServerValue(serverName));
        }

        model.addVariable("jk_servername", supportedServer.getCode());
    }

    private String invalidServerValue(String serverName) {
        return "Unknown value for option 'jk.server' : " + serverName;
    }

    private String invalidSpecValue(List<String> invalidSpecs) {
        return "Unknown value for option 'jk.specs' : " + String.join(", ", invalidSpecs);
    }

    @Override
    public List<String> getDependentAddons(JessieModel model) {
        List<String> result = new ArrayList<>();
        result.add(model.getOptions().get("jk.server").getSingleValue());  // Here we have the original option, not translated.

        return result;
    }

    @Override
    public void initAddonProperties(JessieModel model) {
        model.getVariables().put("jk_runtime_features", new HashMap<>());
        model.getVariables().put("jk_dependencies", new ArrayList<>());
    }

    @Override
    public Set<String> alternativesNames(JessieModel model) {
        String serverName = options.get("server").getSingleValue();
        SupportedServer supportedServer = SupportedServer.valueFor(serverName);

        Set<String> alternatives = new HashSet<>();
        alternatives.add(supportedServer.getCode());
        return alternatives;
    }

    @Override
    public void createFiles(JessieModel model) {

        Set<String> alternatives = model.getParameter(JessieModel.Parameter.ALTERNATIVES);
        Map<String, Object> variables = model.getVariables();

        String serverName = model.getOptions().get("jk.server").getSingleValue();
        SupportedServer supportedServer = SupportedServer.valueFor(serverName);

        String artifactId = model.getMaven().getArtifactId();
        variables.put("jar_file", defineJarFileName(supportedServer, artifactId));
        variables.put("artifact_id", artifactId);

        String rootJava = getJavaApplicationRootPackage(model);

        processTemplateFile(model.getDirectory(), "pom.xml", alternatives, variables);

        if (microprofileSpecs.contains(MicroprofileSpec.HEALTH_CHECKS)) {
            String healthDirectory = model.getDirectory() + "/" + rootJava + "/health";
            directoryCreator.createDirectory(healthDirectory);

            processTemplateFile(healthDirectory, "ServiceHealthCheck.java", alternatives, variables);
        }

        if (microprofileSpecs.contains(MicroprofileSpec.CONFIG)) {
            String configDirectory = model.getDirectory() + "/" + rootJava + "/config";
            directoryCreator.createDirectory(configDirectory);

            processTemplateFile(configDirectory, "ConfigTestController.java", alternatives, variables);
        }

        if (microprofileSpecs.contains(MicroprofileSpec.METRICS)) {
            String metricDirectory = model.getDirectory() + "/" + rootJava + "/metric";
            directoryCreator.createDirectory(metricDirectory);

            processTemplateFile(metricDirectory, "MetricController.java", alternatives, variables);
        }

        if (microprofileSpecs.contains(MicroprofileSpec.FAULT_TOLERANCE)) {
            String faultDirectory = model.getDirectory() + "/" + rootJava + "/resilient";
            directoryCreator.createDirectory(faultDirectory);

            processTemplateFile(faultDirectory, "ResilienceController.java", alternatives, variables);
        }

        if (microprofileSpecs.contains(MicroprofileSpec.REST_CLIENT)) {
            String clientMainDirectory = model.getDirectory() + "/" + rootJava + "/client";
            directoryCreator.createDirectory(clientMainDirectory);

            processTemplateFile(clientMainDirectory, "Service.java", alternatives, variables);
            processTemplateFile(clientMainDirectory, "ClientController.java", alternatives, variables);
        }

        if (microprofileSpecs.contains(MicroprofileSpec.JWT_AUTH)) {
            String aSecureDirectory = model.getDirectory() + "/" + rootJava + "/secure";

            processTemplateFile(aSecureDirectory, "TestSecureController.java", alternatives, variables);
            processTemplateFile(aSecureDirectory, "MPJWTToken.java", alternatives, variables);

            String resourceDirectory = getResourceDirectory(model);

            processTemplateFile(resourceDirectory, "privateKey.pem", alternatives, variables);
        }

        processTemplateFile(model.getDirectory(), "readme.md", alternatives, variables);
    }

    private String defineJarFileName(SupportedServer supportedServer, String artifactId) {
        return String.format(supportedServer.getJarFileName(), artifactId);
    }

}
