/*
 * Copyright (c) 2017-2018 Contributors to the Eclipse Foundation
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
package org.eclipse.starter.business.model.control.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.eclipse.starter.business.model.entity.SpecificationExample;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class SpecificationExampleDeserializer extends JsonDeserializer<Set<SpecificationExample>> {

    @Override
    public Set<SpecificationExample> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException {
        Set<SpecificationExample> examples = new HashSet<>();

        Iterator<String> elements = jsonParser.readValuesAs(String.class);
        while (elements.hasNext()) {
            String entry = elements.next();
            SpecificationExample specificationExample = SpecificationExample.valueFor(entry);
            if (specificationExample != null) {
                examples.add(specificationExample);
            }
        }
        return examples;
    }
}
