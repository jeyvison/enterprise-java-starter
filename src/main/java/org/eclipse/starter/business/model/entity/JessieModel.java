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
package org.eclipse.starter.business.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.eclipse.starter.business.model.control.deserializer.JavaSEVersionDeserializer;
import org.eclipse.starter.business.model.control.deserializer.MicroProfileVersionDeserializer;
import org.eclipse.starter.business.model.control.deserializer.SpecificationExampleDeserializer;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.*;

/**
 *
 */
public class JessieModel {

    /**
     * The name of the top-level directory in the zip file. Usually corresponds to the project name.
     */
    private String directory;

    private String template;

    @NotNull
    private String groupId;

    @NotNull
    private String artifactId;

    @NotNull
    private JakartaRuntime runtime;

    @JsonProperty(value = "MP")
    @JsonDeserialize(using = MicroProfileVersionDeserializer.class)
    private MicroProfileVersion microProfileVersion;

    @JsonProperty(value = "javaSE")
    @JsonDeserialize(using = JavaSEVersionDeserializer.class)
    private JavaSEVersion javaSEVersion;

    private List<String> addons = new ArrayList<>();

    @JsonProperty(value = "examples")
    @JsonDeserialize(using = SpecificationExampleDeserializer.class)
    private Set<SpecificationExample> selectedExamples = EnumSet.noneOf(SpecificationExample.class);

    @JsonIgnore
    private Map<String, Object> parameters = new HashMap<>();

    @JsonIgnore
    private Map<String, Object> variables = new HashMap<>();

    public String getPackageName() {
        return artifactId.replaceAll("-", ".");
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public JakartaRuntime getRuntime() {
        return runtime;
    }

    public void setRuntime(JakartaRuntime runtime) {
        this.runtime = runtime;
    }

    public MicroProfileVersion getMicroProfileVersion() {
        return microProfileVersion;
    }

    public void setMicroProfileVersion(MicroProfileVersion microProfileVersion) {
        this.microProfileVersion = microProfileVersion;
    }

    public JavaSEVersion getJavaSEVersion() {
        return javaSEVersion;
    }

    public void setJavaSEVersion(JavaSEVersion javaSEVersion) {
        this.javaSEVersion = javaSEVersion;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public Set<SpecificationExample> getSelectedExamples() {
        return selectedExamples;
    }

    public List<String> getAddons() {
        if (addons == null) {
            addons = new ArrayList<>();
        }
        return addons;
    }

    public void setAddons(List<String> addons) {
        this.addons = addons;
    }

    public void addVariable(String name, Object value) {
        variables.put(name, value);
    }

    public void addVariables(Map<String, Object> variables) {
        variables.forEach(this::addVariable);
    }

    public Map<String, Object> getVariables() {
        return variables;
    }

    public void addParameter(Parameter parameter, Object value) {
        parameters.put(parameter.name(), value);
    }

    public <T extends Serializable> T getParameter(Parameter parameter) {
        return (T) parameters.get(parameter.name());
    }

    public enum Parameter {
        ADDONS
    }

}
