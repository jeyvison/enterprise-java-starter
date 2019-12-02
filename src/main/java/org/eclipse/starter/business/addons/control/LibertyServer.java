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

import org.eclipse.starter.business.model.entity.JakartaRuntime;
import org.eclipse.starter.business.model.entity.JessieModel;
import org.eclipse.starter.business.model.entity.MavenPlugin;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class LibertyServer extends AbstractAddon {

    @Override
    public String addonName() {
        return JakartaRuntime.LIBERTY.getCode();
    }

    @Override
    public void initProperties(JessieModel model) {
        model.getVariables().put("jk_server_http_port", "9080");

        List<MavenPlugin> plugins = (List<MavenPlugin>) model.getVariables().get("maven_plugins");
        plugins.add(new MavenPlugin("io.openliberty.tools", "liberty-maven-plugin", "3.1"));
    }

    @Override
    public void createFiles(JessieModel model) {
        Map<String, Object> variables = model.getVariables();

        processTemplateFile(model.getDirectory(), "Dockerfile", variables);

        String resourceDirectory = model.getDirectory() + "/src/main/liberty/config";

        directoryCreator.createDirectory(resourceDirectory);

        processTemplateFile(resourceDirectory, "server.xml", variables);
    }

}
