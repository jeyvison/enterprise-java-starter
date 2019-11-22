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
package org.eclipse.starter.presentation;

import org.eclipse.starter.business.model.entity.JavaSEVersion;

import java.util.List;

public class EngineData {

    public static final String DEFAULT_GROUP_ID = "com.example";
    public static final String DEFAULT_ARTIFACT_ID = "demo";
    public static final JavaSEVersion DEFAULT_JAVA_SE_VERSION = JavaSEVersion.SE8;

    private String javaSEVersion = DEFAULT_JAVA_SE_VERSION.getCode();

    private String groupId = DEFAULT_GROUP_ID;
    private String artifactId = DEFAULT_ARTIFACT_ID;
    private String mpVersion;
    private String supportedServer;
    private List<String> selectedSpecs;
    private List<String> selectedFeatures;

    public String getJavaSEVersion() {
        return javaSEVersion;
    }

    public void setJavaSEVersion(String javaSEVersion) {
        this.javaSEVersion = javaSEVersion;
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

    public String getMpVersion() {
        return mpVersion;
    }

    public void setMpVersion(String mpVersion) {
        this.mpVersion = mpVersion;
    }

    public String getSupportedServer() {
        return supportedServer;
    }

    public void setSupportedServer(String supportedServer) {
        this.supportedServer = supportedServer;
    }

    public List<String> getSelectedSpecs() {
        return selectedSpecs;
    }

    public void setSelectedSpecs(List<String> selectedSpecs) {
        this.selectedSpecs = selectedSpecs;
    }

    public List<String> getSelectedFeatures() {
        return selectedFeatures;
    }

    public void setSelectedFeatures(List<String> selectedFeatures) {
        this.selectedFeatures = selectedFeatures;
    }

}
