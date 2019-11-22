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
package org.eclipse.starter.presentation;

import org.eclipse.starter.business.artifacts.control.Creator;
import org.eclipse.starter.business.artifacts.control.ZipFileCreator;
import org.eclipse.starter.business.exception.JessieException;
import org.eclipse.starter.business.exception.JessieUnexpectedException;
import org.eclipse.starter.business.model.boundary.ModelManager;
import org.eclipse.starter.business.model.entity.*;
import org.eclipse.starter.business.version.control.Version;

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
        defineExampleSpecs();
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
        JakartaRuntime jakartaRuntime = JakartaRuntime.valueFor(engineData.getSupportedServer());
        List<MicroProfileVersion> versions = jakartaRuntime.getMpVersions();
        MicroProfileVersion microProfileVersion = versions.get(versions.size() - 1);
        engineData.setMpVersion(microProfileVersion.getCode());
    }

    private void defineExampleSpecs() {
        specs = new ArrayList<>();
        List<String> currentSelected = new ArrayList<>(selectedSpecs);
        selectedSpecs.clear();

        for (SpecificationExample specificationExample : SpecificationExample.values()) {
            specs.add(new SelectItem(specificationExample.getCode(), specificationExample.getLabel()));
            if (currentSelected.contains(specificationExample.getCode())) {
                // If the spec is currently selected, keep it selected.
                // But if it is not listed anymore in the MP version, it has to go.
                selectedSpecs.add(specificationExample.getCode());
            }
        }

    }

    private void defineSupportedServerItems(MicroProfileVersion version) {

        supportedServerItems = new ArrayList<>();
        for (JakartaRuntime jakartaRuntime : JakartaRuntime.values()) {
            if (version == null || jakartaRuntime.getMpVersions().contains(version)) {
                supportedServerItems.add(new SelectItem(jakartaRuntime.getCode(), jakartaRuntime.getDisplayName()));
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
        model.setDirectory(engineData.getArtifactId());
        model.setGroupId(engineData.getGroupId());
        model.setArtifactId(engineData.getArtifactId());

        model.setJavaSEVersion(JavaSEVersion.valueFor(engineData.getJavaSEVersion()));

        model.setMicroProfileVersion(MicroProfileVersion.valueFor(engineData.getMpVersion()));

        model.setRuntime(JakartaRuntime.valueFor(engineData.getSupportedServer()));
        selectedSpecs.stream()
                .map(SpecificationExample::valueFor)
                .filter(Objects::nonNull)
                .forEach(model.getSelectedExamples()::add);

        engineData.setSelectedSpecs(selectedSpecs);

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
        String fileName = engineData.getArtifactId() + ".zip";
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
