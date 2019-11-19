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
package org.eclipse.microprofile.starter.addon.microprofile.servers.server;

import org.eclipse.microprofile.starter.addon.microprofile.servers.model.SupportedServer;
import org.eclipse.microprofile.starter.core.model.JessieModel;
import org.eclipse.microprofile.starter.spi.AbstractAddon;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import java.util.Map;
import java.util.Set;

@ApplicationScoped
public class LibertyServer extends AbstractAddon {

    @Override
    public String addonName() {
        return SupportedServer.LIBERTY.getCode();
    }

    @Override
    public void initAddonProperties(JessieModel model) {
        // nothing to do
    }

    @Override
    public void createFiles(JessieModel model) {
        Set<String> alternatives = model.getParameter(JessieModel.Parameter.ALTERNATIVES);
        Map<String, Object> variables = model.getVariables();

        processTemplateFile(model.getDirectory(), "Dockerfile", alternatives, variables);

        String resourceDirectory = model.getDirectory() + "/src/main/liberty/config";

        directoryCreator.createDirectory(resourceDirectory);

        processTemplateFile(resourceDirectory, "server.xml", alternatives, variables);
    }

}
