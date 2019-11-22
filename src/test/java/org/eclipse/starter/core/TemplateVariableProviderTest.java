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
package org.eclipse.starter.core;

import org.eclipse.starter.business.templates.control.TemplateVariableProvider;
import org.eclipse.starter.business.model.entity.JavaSEVersion;
import org.eclipse.starter.business.model.entity.JessieModel;
import org.eclipse.starter.business.model.entity.MicroProfileVersion;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

public class TemplateVariableProviderTest {

    @Test
    public void shouldStoreValidApplicationName() {
        TemplateVariableProvider provider = new TemplateVariableProvider();
        JessieModel model = new JessieModel();
        model.setMicroProfileVersion(MicroProfileVersion.MP22);
        model.setJavaSEVersion(JavaSEVersion.SE8);
        model.setArtifactId("demo-service");

        Map<String, Object> variables = provider.determineVariables(model);

        Assert.assertEquals("Demoservice", variables.get("application"));
    }
}