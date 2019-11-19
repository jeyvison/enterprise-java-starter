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
package org.eclipse.microprofile.starter.view;

import org.eclipse.microprofile.starter.Version;
import org.eclipse.microprofile.starter.ZipFileCreator;
import org.eclipse.microprofile.starter.addon.microprofile.servers.model.MicroprofileSpec;
import org.eclipse.microprofile.starter.addon.microprofile.servers.model.SupportedServer;
import org.eclipse.microprofile.starter.core.artifacts.Creator;
import org.eclipse.microprofile.starter.core.exception.JessieException;
import org.eclipse.microprofile.starter.core.exception.JessieUnexpectedException;
import org.eclipse.microprofile.starter.core.model.*;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@ViewScoped
@Named
public class GeneratorDataBean implements Serializable {

    @Inject
    private ModelManager modelManager;

    @Inject
    private Creator creator;

    @Inject
    private ZipFileCreator zipFileCreator;

    @Inject
    private Version version;

    private EngineData engineData;

    private List<SelectItem> supportedServerItems;
    private List<String> selectedSpecs = new ArrayList<>();
    private List<SelectItem> specs;

    @PostConstruct
    public void init() {
        engineData = new EngineData();
        defineSupportedServerItems(null);
    }

    public void onMPVersionSelected() {
        MicroProfileVersion version = MicroProfileVersion.valueFor(engineData.getMpVersion());
        defineExampleSpecs(version);
        defineSupportedServerItems(version);
    }

    public void onMPRuntimeSelected() {
        if (engineData.getMpVersion() == null || engineData.getMpVersion().trim().isEmpty()) {
            defineMPVersionValue();
            onMPVersionSelected();  // So that example specs are filled and shown on screen.
            // This also limit the supportedServers as the MPVersion is now filled with a value.
        }
    }

    private void defineMPVersionValue() {
        // Look for the latest MP version
        SupportedServer supportedServer = SupportedServer.valueFor(engineData.getSupportedServer());
        List<MicroProfileVersion> versions = supportedServer.getMpVersions();
        MicroProfileVersion microProfileVersion = versions.get(versions.size() - 1);
        engineData.setMpVersion(microProfileVersion.getCode());
    }

    private void defineExampleSpecs(MicroProfileVersion version) {
        specs = new ArrayList<>();
        List<String> currentSelected = new ArrayList<>(selectedSpecs);
        selectedSpecs.clear();

        for (MicroprofileSpec microprofileSpec : MicroprofileSpec.values()) {
            if (microprofileSpec.getMpVersions().contains(version)) {
                specs.add(new SelectItem(microprofileSpec.getCode(), microprofileSpec.getLabel()));
                if (currentSelected.contains(microprofileSpec.getCode())) {
                    // If the spec is currently selected, keep it selected.
                    // But if it is not listed anymore in the MP version, it has to go.
                    selectedSpecs.add(microprofileSpec.getCode());
                }
            }
        }

    }

    private void defineSupportedServerItems(MicroProfileVersion version) {

        supportedServerItems = new ArrayList<>();
        for (SupportedServer supportedServer : SupportedServer.values()) {
            if (version == null || supportedServer.getMpVersions().contains(version)) {
                supportedServerItems.add(new SelectItem(supportedServer.getCode(), supportedServer.getDisplayName()));
            }
        }
        randomizeSupportedServers();
    }

    private void randomizeSupportedServers() {
        List<Integer> rnd = generateUniqueRandomNumbers(supportedServerItems.size());

        Iterator<Integer> keyIterator = rnd.iterator();
        Iterator<SelectItem> valueIterator = supportedServerItems.iterator();
        Map<Integer, SelectItem> data = IntStream.range(0, rnd.size()).boxed()
                .collect(Collectors.toMap(i -> keyIterator.next(), i -> valueIterator.next()));

        supportedServerItems = new ArrayList<>(data.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new))
                .values());

    }

    private List<Integer> generateUniqueRandomNumbers(int randomNumberSize) {
        Random rnd = new Random();
        List<Integer> result = new ArrayList<>();
        while (result.size() < randomNumberSize) {
            int value = rnd.nextInt(500);
            if (!result.contains(value)) {
                result.add(value);
            }
        }
        return result;
    }

    public void generateProject() {

        JessieModel model = new JessieModel();
        model.setDirectory(engineData.getMavenData().getArtifactId());
        JessieMaven mavenModel = new JessieMaven();
        mavenModel.setGroupId(engineData.getMavenData().getGroupId());
        mavenModel.setArtifactId(engineData.getMavenData().getArtifactId());
        model.setMaven(mavenModel);

        JessieSpecification specifications = new JessieSpecification();

        specifications.setJavaSEVersion(JavaSEVersion.valueFor(engineData.getJavaSEVersion()));

        specifications.setMicroProfileVersion(MicroProfileVersion.valueFor(engineData.getMpVersion()));

        model.getOptions().put("jk.server", new OptionValue(engineData.getSupportedServer()));
        model.getOptions().put("jk.specs", new OptionValue(selectedSpecs));

        engineData.setSelectedSpecs(selectedSpecs);

        model.setSpecification(specifications);

        try {
            modelManager.prepareModel(model, false);
            creator.createArtifacts(model);

            download(zipFileCreator.createArchive());
        } catch (JessieException e) {
            String messageText = "Unexpected error occurred; please file GitHub issue if problem persist. Error : " + e.getMessage();
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, messageText, messageText);
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }

    private void download(byte[] archive) {
        String fileName = engineData.getMavenData().getArtifactId() + ".zip";
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();

        ec.responseReset();
        ec.setResponseContentType("application/zip");
        ec.setResponseContentLength(archive.length);
        ec.setResponseHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

        try {
            OutputStream outputStream = ec.getResponseOutputStream();

            outputStream.write(archive);
            outputStream.close();
        } catch (IOException e) {
            throw new JessieUnexpectedException("IO Error during download of ZIP");
        }

        // Important! Otherwise JSF will attempt to render the response which obviously will fail
        // since it's already written with a file and closed.
        fc.responseComplete();
    }

    public Version getVersion() {
        return version;
    }

    public EngineData getEngineData() {
        return engineData;
    }

    public List<SelectItem> getSupportedServerItems() {
        return supportedServerItems;
    }

    public List<SelectItem> getSpecs() {
        return specs;
    }

    public List<String> getSelectedSpecs() {
        return selectedSpecs;
    }

    public void setSelectedSpecs(List<String> selectedSpecs) {
        this.selectedSpecs = selectedSpecs;
    }

    public void selectAll() {
        selectedSpecs = specs.stream().map(si -> si.getValue().toString()).collect(Collectors.toList());
    }

    public void unselectAll() {
        selectedSpecs.clear();
    }
}
