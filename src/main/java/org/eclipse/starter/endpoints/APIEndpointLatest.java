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

import org.eclipse.starter.business.model.entity.*;
import org.eclipse.starter.endpoints.model.Project;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * @author Michal Karm Babacek <karm@redhat.com>
 */
@Path("/")
public class APIEndpointLatest {

    @Inject
    private APIService api;

    @Path("/")
    @GET
    @Produces({"text/x-markdown"})
    public Response readme(@HeaderParam(HttpHeaders.IF_NONE_MATCH) String ifNoneMatch) {
        return api.readme(ifNoneMatch);
    }

    @Path("/mpVersion")
    @GET
    @Produces({"application/json"})
    public Response listMPVersions() {
        return api.listMPVersions();
    }

    @Path("/supportMatrix")
    @GET
    @Produces({"application/json"})
    public Response supportMatrix(@HeaderParam(HttpHeaders.IF_NONE_MATCH) String ifNoneMatch) {
        return api.supportMatrix(ifNoneMatch);
    }

    @Path("/supportMatrix/servers")
    @GET
    @Produces({"application/json"})
    public Response supportMatrixServers(@HeaderParam(HttpHeaders.IF_NONE_MATCH) String ifNoneMatch) {
        return api.supportMatrixServers(ifNoneMatch);
    }

    @Path("/mpVersion/{mpVersion}")
    @GET
    @Produces({"application/json"})
    public Response listOptions(@NotNull @PathParam("mpVersion") MicroProfileVersion mpVersion) {
        return api.listOptions(mpVersion);
    }

    @Path("/project")
    @GET
    @Produces({"application/zip", "application/json"})
    public Response getProject(@HeaderParam(HttpHeaders.IF_NONE_MATCH) String ifNoneMatch,
                               @QueryParam("supportedServer") JakartaRuntime jakartaRuntime,
                               @QueryParam("groupId") String groupId,
                               @QueryParam("artifactId") String artifactId,
                               @QueryParam("mpVersion") MicroProfileVersion mpVersion,
                               @QueryParam("javaSEVersion") JavaSEVersion javaSEVersion,
                               @QueryParam("selectedSpecs") List<SpecificationExample> selectedSpecs,
                               @QueryParam("selectedFeatures") List<Feature> selectedFeatures) {
        return api.getProject(ifNoneMatch, jakartaRuntime, groupId, artifactId, mpVersion, javaSEVersion, selectedSpecs, selectedFeatures);
    }

    @Path("/project")
    @POST
    @Consumes({"application/json"})
    @Produces({"application/zip", "application/json"})
    public Response projectPost(@HeaderParam(HttpHeaders.IF_NONE_MATCH) String ifNoneMatch, @NotNull Project body) {
        return api.getProject(ifNoneMatch, body);
    }
}
