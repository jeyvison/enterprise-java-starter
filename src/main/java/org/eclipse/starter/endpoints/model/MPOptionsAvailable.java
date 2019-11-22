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

import org.eclipse.starter.business.model.entity.JakartaRuntime;
import org.eclipse.starter.business.model.entity.SpecificationExample;

import java.util.Collections;
import java.util.List;

public class MPOptionsAvailable {

    private final List<JakartaRuntime> jakartaRuntimes;
    private final List<SpecificationExample> specs;

    public MPOptionsAvailable(List<JakartaRuntime> jakartaRuntimes, List<SpecificationExample> specs) {
        this.jakartaRuntimes = jakartaRuntimes;
        this.specs = specs;
    }

    public List<JakartaRuntime> getJakartaRuntimes() {
        Collections.shuffle(jakartaRuntimes);
        return jakartaRuntimes;
    }

    public List<SpecificationExample> getSpecs() {
        return specs;
    }
}
