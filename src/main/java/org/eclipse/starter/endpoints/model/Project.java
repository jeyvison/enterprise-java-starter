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
package org.eclipse.starter.endpoints.model;

import org.eclipse.starter.business.model.entity.*;

import java.util.List;
import java.util.Objects;


public class Project {

    private String groupId = null;
    private String artifactId = null;
    private MicroProfileVersion mpVersion = null;
    private JavaSEVersion javaSEVersion = null;
    private JakartaRuntime jakartaRuntime = null;
    private List<SpecificationExample> selectedSpecs = null;
    private List<Feature> selectedFeatures = null;

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

    public MicroProfileVersion getMpVersion() {
        return mpVersion;
    }

    public void setMpVersion(MicroProfileVersion mpVersion) {
        this.mpVersion = mpVersion;
    }

    public JavaSEVersion getJavaSEVersion() {
        return javaSEVersion;
    }

    public void setJavaSEVersion(JavaSEVersion javaSEVersion) {
        this.javaSEVersion = javaSEVersion;
    }

    public JakartaRuntime getJakartaRuntime() {
        return jakartaRuntime;
    }

    public void setJakartaRuntime(JakartaRuntime jakartaRuntime) {
        this.jakartaRuntime = jakartaRuntime;
    }

    public List<SpecificationExample> getSelectedSpecs() {
        return selectedSpecs;
    }

    public void setSelectedSpecs(List<SpecificationExample> selectedSpecs) {
        this.selectedSpecs = selectedSpecs;
    }

    public List<Feature> getSelectedFeatures() {
        return selectedFeatures;
    }

    public void setSelectedFeatures(List<Feature> selectedFeatures) {
        this.selectedFeatures = selectedFeatures;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Project project = (Project) o;
        return Objects.equals(groupId, project.groupId)
                && Objects.equals(artifactId, project.artifactId)
                && mpVersion == project.mpVersion
                && javaSEVersion == project.javaSEVersion
                && jakartaRuntime == project.jakartaRuntime
                && Objects.equals(selectedFeatures, project.selectedFeatures)
                && Objects.equals(selectedSpecs, project.selectedSpecs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupId, artifactId, mpVersion, javaSEVersion, jakartaRuntime, selectedFeatures, selectedSpecs);
    }
}
