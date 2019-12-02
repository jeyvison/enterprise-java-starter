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
package org.eclipse.starter.business.model.entity;

/**
 *
 */
public enum JavaSEVersion {

    SE7("1.7", "Java 7", false),
    SE8("1.8", "Java 8", false),
    SE11("11", "Java 11", true),
    SE12("12", "Java 12", true);

    private final String code;
    private final String label;
    private final boolean jigsaw;

    JavaSEVersion(String code, String label, boolean jigsaw) {
        this.code = code;
        this.label = label;
        this.jigsaw = jigsaw;
    }

    public String getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    public boolean isJigsaw() {
        return jigsaw;
    }

    public static JavaSEVersion valueFor(String code) {
        JavaSEVersion result = null;
        for (JavaSEVersion javaSEVersion : JavaSEVersion.values()) {
            if (javaSEVersion.code != null && javaSEVersion.code.equals(code)) {
                result = javaSEVersion;
            }
        }
        return result;
    }
}
