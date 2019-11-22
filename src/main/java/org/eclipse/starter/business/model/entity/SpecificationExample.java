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

public enum SpecificationExample {

    TESTS("tests", "Testing", "Tests - add code level and end-to-end level test examples"),

    MP_CONFIG("mp_config", "Config",
            "Configuration - externalize and manage your configuration parameters outside your microservices"),

    MP_FAULT_TOLERANCE("mp_fault_tolerance", "Fault Tolerance",
            "Fault Tolerance - all about bulkheads, timeouts, circuit breakers, retries, etc. for your microservices"),

    MP_JWT_AUTH("mp_JWT_auth", "JWT Auth", "JWT Propagation - propagate security across your microservices"),

    MP_METRICS("mp_metrics", "Metrics",
            "Metrics - Gather and create operational and business measurements for your microservices"),

    MP_HEALTH_CHECKS("mp_health_checks", "Health Checks",
            "Health Checks - Verify the health of your microservices with custom verifications"),

    MP_OPEN_API("mp_open_API", "OpenAPI",
            "Open API - Generate OpenAPI-compliant API documentation for your microservices"),

    MP_OPEN_TRACING("mp_open_tracing", "OpenTracing",
            "Open Tracing - trace the flow of requests as they traverse your microservices"),

    MP_REST_CLIENT("mp_rest_client", "TypeSafe Rest Client",
            "Rest Client - Invoke RESTful services in a type-safe manner");

    private String code;
    private String label;
    private String description;

    SpecificationExample(String code, String label, String description) {
        this.code = code;
        this.label = label;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public String getLabel() {
        return label;
    }

    public static SpecificationExample valueFor(String data) {
        SpecificationExample result = null;
        for (SpecificationExample spec : SpecificationExample.values()) {
            if (spec.code.equals(data)) {
                result = spec;
            }
        }
        return result;
    }
}
