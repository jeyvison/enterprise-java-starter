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
package org.eclipse.starter.endpoints;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.starter.business.artifacts.control.Creator;
import org.eclipse.starter.business.artifacts.control.ZipFileCreator;
import org.eclipse.starter.business.files.control.FilesLocator;
import org.eclipse.starter.business.model.boundary.ModelManager;
import org.eclipse.starter.business.model.control.validation.PackageNameValidator;
import org.eclipse.starter.business.model.entity.*;
import org.eclipse.starter.business.version.control.Version;
import org.eclipse.starter.endpoints.model.MPOptionsAvailable;
import org.eclipse.starter.endpoints.model.Project;
import org.eclipse.starter.presentation.EngineData;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Michal Karm Babacek <karm@redhat.com>
 */
@ApplicationScoped
public class APIService {

    private static final Logger LOG = Logger.getLogger(APIService.class.getName());

    public static final String ERROR001 =
            "{\"error\":\"supportedServer query parameter is mandatory\",\"code\":\"ERROR001\"}";
    public static final String ERROR002 =
            "{\"error\":\"Selected supportedServer is not available for the given mpVersion\",\"code\":\"ERROR002\"}";
    public static final String ERROR003 =
            "{\"error\":\"One or more selectedSpecs is not available for the given mpVersion\",\"code\":\"ERROR003\"}";
    public static final String ERROR004 =
            "{\"error\":\"groupId contains illegal characters, does not start with a word or is longer than " +
                    PackageNameValidator.MAX_LENGTH + "\",\"code\":\"ERROR004\"}";
    public static final String ERROR005 =
            "{\"error\":\"artifactId contains illegal characters, does not start with a word or is longer than " +
                    PackageNameValidator.MAX_LENGTH + "\",\"code\":\"ERROR005\"}";

    private Map<String, String> specsDescriptions;

    private TreeMap<MicroProfileVersion, MPOptionsAvailable> mpvToOptions;
    private EntityTag mpvToOptionsEtag;

    private Map<JakartaRuntime, Map<MicroProfileVersion, List<SpecificationExample>>> serversToOptions;
    private EntityTag serversToOptionsEtag;

    private String readme;
    private EntityTag readmeEtag;

    @Inject
    private ModelManager modelManager;

    @Inject
    private Creator creator;

    @Inject
    private ZipFileCreator zipFileCreator;

    @Inject
    private Version version;

    @Inject
    private PackageNameValidator packageNameValidator;

    @PostConstruct
    public void init() {
        specsDescriptions = Stream.of(SpecificationExample.values())
                .collect(Collectors.toMap(SpecificationExample::toString, SpecificationExample::getDescription));
        // Keys are MP versions and values are servers and specs
        mpvToOptions = new TreeMap<>();
        List<SpecificationExample> allExamples = Stream.of(SpecificationExample.values()).collect(Collectors.toList());

        Stream.of(MicroProfileVersion.values()).sorted().forEach(mpv -> {
            List<JakartaRuntime> jakartaRuntimes = Stream.of(JakartaRuntime.values()).collect(Collectors.toList());
            mpvToOptions.put(mpv, new MPOptionsAvailable(jakartaRuntimes, allExamples));
        });
        mpvToOptionsEtag = new EntityTag(Integer.toHexString(
                31 * version.getGit().hashCode() + mpvToOptions.hashCode() + specsDescriptions.hashCode()));
        // Keys are servers and values are MP versions and specs
        serversToOptions = new HashMap<>(JakartaRuntime.values().length);
        for (JakartaRuntime s : JakartaRuntime.values()) {
            Map<MicroProfileVersion, List<SpecificationExample>> mpvToSpec = new HashMap<>(s.getMpVersions().size());
            s.getMpVersions().forEach(mpv -> mpvToSpec.put(
                    mpv,
                    allExamples));
            serversToOptions.put(s, mpvToSpec);
        }
        serversToOptionsEtag = new EntityTag(Integer.toHexString(
                31 * version.getGit().hashCode() + serversToOptions.hashCode() + specsDescriptions.hashCode()));
        try (Scanner s = new Scanner(FilesLocator.class.getClassLoader()
                .getResourceAsStream("/REST-README.md")).useDelimiter("\\A")) {
            readme = (s.hasNext() ? s.next() : "") + "\n" + version.getGit() + "\n";
            readmeEtag = new EntityTag(Integer.toHexString(readme.hashCode()));
        } catch (Exception e) {
            LOG.log(Level.SEVERE, e.getMessage());
        }
    }

    public Response readme(String ifNoneMatch) {
        if (ifNoneMatch != null) {
            if (readmeEtag.toString().equals(ifNoneMatch)) {
                return Response.notModified().build();
            }
        }
        return Response.ok(readme).tag(readmeEtag).build();
    }

    public Response listMPVersions() {
        return Response.ok(
                Stream.of(MicroProfileVersion.values())
                        .collect(Collectors.toList()), MediaType.APPLICATION_JSON_TYPE
        ).build();
    }

    public Response supportMatrixV1(String ifNoneMatch) {
        if (ifNoneMatch != null) {
            if (mpvToOptionsEtag.toString().equals(ifNoneMatch)) {
                return Response.notModified().build();
            }
        }
        return Response.ok(mpvToOptions, MediaType.APPLICATION_JSON_TYPE).tag(mpvToOptionsEtag).build();
    }

    public Response supportMatrix(String ifNoneMatch) {
        if (ifNoneMatch != null) {
            if (mpvToOptionsEtag.toString().equals(ifNoneMatch)) {
                return Response.notModified().build();
            }
        }
        Map<String, Map> mpvToOptionsAndSpecsDescriptions = new HashMap<>(2);
        mpvToOptionsAndSpecsDescriptions.put("configs", mpvToOptions);
        mpvToOptionsAndSpecsDescriptions.put("descriptions", specsDescriptions);
        return Response.ok(mpvToOptionsAndSpecsDescriptions, MediaType.APPLICATION_JSON_TYPE).tag(mpvToOptionsEtag).build();
    }

    public Response supportMatrixServersV1(String ifNoneMatch) {
        if (ifNoneMatch != null) {
            if (serversToOptionsEtag.toString().equals(ifNoneMatch)) {
                return Response.notModified().build();
            }
        }
        List<JakartaRuntime> servers = new ArrayList<>(serversToOptions.keySet());
        Collections.shuffle(servers);
        Map<JakartaRuntime, Map<MicroProfileVersion, List<SpecificationExample>>> rndServersToOptions = new LinkedHashMap<>(servers.size());
        for (JakartaRuntime s : servers) {
            rndServersToOptions.put(s, serversToOptions.get(s));
        }
        return Response.ok(rndServersToOptions, MediaType.APPLICATION_JSON_TYPE).tag(serversToOptionsEtag).build();
    }

    public Response supportMatrixServers(String ifNoneMatch) {
        if (ifNoneMatch != null) {
            if (serversToOptionsEtag.toString().equals(ifNoneMatch)) {
                return Response.notModified().build();
            }
        }
        List<JakartaRuntime> servers = new ArrayList<>(serversToOptions.keySet());
        Collections.shuffle(servers);
        Map<JakartaRuntime, Map<MicroProfileVersion, List<SpecificationExample>>> rndServersToOptions = new LinkedHashMap<>(servers.size());
        for (JakartaRuntime s : servers) {
            rndServersToOptions.put(s, serversToOptions.get(s));
        }

        Map<String, Map> serversAndSpecsDescriptions = new HashMap<>(2);
        serversAndSpecsDescriptions.put("configs", rndServersToOptions);
        serversAndSpecsDescriptions.put("descriptions", specsDescriptions);

        return Response.ok(serversAndSpecsDescriptions, MediaType.APPLICATION_JSON_TYPE).tag(serversToOptionsEtag).build();
    }

    public Response listOptions(MicroProfileVersion mpVersion) {
        return Response.ok(mpvToOptions.get(mpVersion)).build();
    }

    public Response getProject(String ifNoneMatch,
                               JakartaRuntime jakartaRuntime,
                               String groupId, String artifactId,
                               MicroProfileVersion mpVersion,
                               JavaSEVersion javaSEVersion,
                               List<SpecificationExample> selectedSpecs,
                               List<Feature> selectedFeatures) {
        Project project = new Project();
        project.setJakartaRuntime(jakartaRuntime);
        project.setGroupId(groupId);
        project.setArtifactId(artifactId);
        project.setMpVersion(mpVersion);
        project.setJavaSEVersion(javaSEVersion);
        project.setSelectedSpecs(selectedSpecs);
        project.setSelectedFeatures(selectedFeatures);
        return processProject(ifNoneMatch, project);
    }

    public Response getProject(String ifNoneMatch, Project body) {
        return processProject(ifNoneMatch, body);
    }

    private Response validate(Project p) {
        if (p.getJakartaRuntime() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ERROR001)
                    .type("application/json")
                    .header("Content-Length", ERROR001.length())
                    .header("Content-Disposition", "attachment; filename=\"error.json\"")
                    .build();
        }

        if (StringUtils.isBlank(p.getGroupId())) {
            p.setGroupId(EngineData.DEFAULT_GROUP_ID);
        }
        if (!packageNameValidator.isValidPackageName(p.getGroupId())) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ERROR004)
                    .type("application/json")
                    .header("Content-Length", ERROR004.length())
                    .header("Content-Disposition", "attachment; filename=\"error.json\"")
                    .build();
        }
        if (StringUtils.isBlank(p.getArtifactId())) {
            p.setArtifactId(EngineData.DEFAULT_ARTIFACT_ID);
        }
        if (!packageNameValidator.isValidPackageName(p.getArtifactId())) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ERROR005)
                    .type("application/json")
                    .header("Content-Length", ERROR005.length())
                    .header("Content-Disposition", "attachment; filename=\"error.json\"")
                    .build();
        }
        if (p.getMpVersion() == null) {
            p.setMpVersion(
                    p.getJakartaRuntime().getMpVersions().get(p.getJakartaRuntime().getMpVersions().size() - 1));
        }
        if (!mpvToOptions.get(p.getMpVersion()).getJakartaRuntimes().contains(p.getJakartaRuntime())) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .type("application/json")
                    .header("Content-Length", ERROR002.length())
                    .header("Content-Disposition", "attachment; filename=\"error.json\"")
                    .entity(ERROR002)
                    .build();
        }
        if (p.getJavaSEVersion() == null) {
            p.setJavaSEVersion(EngineData.DEFAULT_JAVA_SE_VERSION);
        }
        if (p.getSelectedSpecs() == null || p.getSelectedSpecs().isEmpty()) {
            p.setSelectedSpecs(mpvToOptions.get(p.getMpVersion()).getSpecs());
        }
        if (!mpvToOptions.get(p.getMpVersion()).getSpecs().containsAll(p.getSelectedSpecs())) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .type("application/json")
                    .header("Content-Length", ERROR003.length())
                    .header("Content-Disposition", "attachment; filename=\"error.json\"")
                    .entity(ERROR003)
                    .build();
        }

        return Response.ok().build();
    }

    private EngineData getEngineData(Project p) {
        List<String> selectedSpecs = p.getSelectedSpecs().stream()
                .map(SpecificationExample::getCode).collect(Collectors.toList());
        List<String> selectedFeatures = p.getSelectedFeatures().stream()
                .map(Feature::getCode).collect(Collectors.toList());
        EngineData engineData = new EngineData();
        engineData.setGroupId(p.getGroupId());
        engineData.setArtifactId(p.getArtifactId());
        engineData.setSelectedSpecs(selectedSpecs);
        engineData.setSelectedFeatures(selectedFeatures);
        engineData.setSupportedServer(p.getJakartaRuntime().getCode());
        engineData.setMpVersion(p.getMpVersion().getCode());
        return engineData;
    }

    private Response processProject(String ifNoneMatch, Project project) {

        Response validatorResponse = validate(project);
        if (validatorResponse.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
            return validatorResponse;
        }

        EngineData ed = getEngineData(project);

        EntityTag etag = new EntityTag(Integer.toHexString(31 * version.getGit().hashCode() + project.hashCode()));

        if (ifNoneMatch != null) {
            if (etag.toString().equals(ifNoneMatch)) {
                return Response.notModified().build();
            }
        }

        JessieModel model = new JessieModel();
        model.setDirectory(ed.getArtifactId());
        model.setGroupId(ed.getGroupId());
        model.setArtifactId(ed.getArtifactId());
        model.setJavaSEVersion(project.getJavaSEVersion());
        model.setMicroProfileVersion(project.getMpVersion());

        model.setRuntime(JakartaRuntime.valueFor(ed.getSupportedServer()));
        ed.getSelectedSpecs().stream()
                .map(SpecificationExample::valueFor)
                .filter(Objects::nonNull)
                .forEach(model.getSelectedExamples()::add);

        model.getAddons().addAll(ed.getSelectedFeatures());

        modelManager.prepareModel(model, false);
        creator.createArtifacts(model);

        byte[] archive = zipFileCreator.createArchive();

        String fileName = ed.getArtifactId() + ".zip";

        return Response
                .ok()
                .tag(etag)
                .header("Content-Length", archive.length)
                .header("Content-Disposition", "attachment; filename=\"" + fileName + "\"")
                .type("application/zip")
                .entity(archive)
                .build();
    }
}
