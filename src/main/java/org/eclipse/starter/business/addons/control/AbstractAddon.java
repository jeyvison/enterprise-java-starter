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

import org.eclipse.starter.business.artifacts.control.DirectoryCreator;
import org.eclipse.starter.business.artifacts.control.FileCreator;
import org.eclipse.starter.business.artifacts.control.MavenCreator;
import org.eclipse.starter.business.files.control.FileCopyEngine;
import org.eclipse.starter.business.files.control.ThymeleafEngine;
import org.eclipse.starter.business.model.entity.JessieModel;
import org.eclipse.starter.business.model.entity.OptionValue;

import javax.inject.Inject;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractAddon implements JessieAddon {

    protected Map<String, OptionValue> options;
    protected Map<String, String> defaultOptions = new HashMap<>();

    @Inject
    protected ThymeleafEngine thymeleafEngine;

    @Inject
    protected FileCopyEngine fileCopyEngine;

    @Inject
    protected DirectoryCreator directoryCreator;

    @Inject
    protected FileCreator fileCreator;

    @Override
    public int priority() {
        return 70;
    }

    @Override
    public List<String> getDependentAddons(JessieModel model) {
        return Collections.emptyList();
    }

    @Override
    public final void validate(JessieModel model) {
        validateModel(model);
    }

    protected void validateModel(JessieModel model) {

    }

    protected final String getResourceDirectory(JessieModel model) {
        return model.getDirectory() + "/" + MavenCreator.SRC_MAIN_RESOURCES;
    }

    protected final String getJavaApplicationRootPackage(JessieModel model) {
        return MavenCreator.SRC_MAIN_JAVA + "/" + directoryCreator.createPath(model);
    }

    protected final String getJavaTestRootPackage(JessieModel model) {
        return MavenCreator.SRC_TEST_JAVA + "/" + directoryCreator.createPath(model);
    }

    protected final void processTemplateFile(String directory, String templateFileName, String fileName,
                                             Map<String, Object> variables) {
        String javaFile = thymeleafEngine.processFile(templateFileName, variables);
        fileCreator.writeContents(directory, fileName, javaFile);
    }

    protected final void processTemplateFile(String directory, String fileName, Map<String, Object> variables) {
        String javaFile = thymeleafEngine.processFile(fileName, variables);
        fileCreator.writeContents(directory, fileName, javaFile);
    }

    protected final void processFile(String directory, String fileName) {
        byte[] fileContent = fileCopyEngine.processFile(fileName);
        fileCreator.writeContents(directory, fileName, fileContent);
    }
}
