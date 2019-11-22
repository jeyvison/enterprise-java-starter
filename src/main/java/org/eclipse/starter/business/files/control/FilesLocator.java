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
package org.eclipse.starter.business.files.control;


import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 *
 */
@ApplicationScoped
public class FilesLocator {

    private List<FileIdentification> fileIdentifications;
    private List<String> fileNames;

    @PostConstruct
    public void init() {
        defineResources(Pattern.compile(".*\\.tpl"));
    }

    public String findFile(String name) {
        List<FileIdentification> candidates = fileIdentifications
                .stream()
                .filter(fi -> fi.getName().equalsIgnoreCase(name))
                .collect(Collectors.toList());

        int result = -1; // not found
        if (!candidates.isEmpty()) {
            if (candidates.size() == 1) {
                result = fileIdentifications.indexOf(candidates.get(0));
            }
        }

        if (result == -1) {
            return null;
        }

        return fileNames.get(result);
    }

    private void defineResources(Pattern pattern) {
        String path = "src/main/resources/";
        Set<String> resources = new HashSet<>();

        try (Scanner scanner = new Scanner(FilesLocator.class.getClassLoader().getResourceAsStream("/files.lst"))) {

            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                if (pattern.matcher(line).matches()) {
                    resources.add(line.substring(path.length()));
                }
            }
        }

        fileIdentifications = new ArrayList<>();
        fileNames = new ArrayList<>();
        for (String resource : resources) {
            // Strip .tpl
            String fileName = resource.substring(0, resource.length() - 4);

            fileIdentifications.add(new FileIdentification(fileName));
            fileNames.add(resource);
        }

    }

    private static class FileIdentification {
        private static final Pattern FILE_PATH_PATTERN_SPLIT = Pattern.compile("\\\\|/");
        private String name;

        public FileIdentification(String fileName) {
            String[] fileParts = FILE_PATH_PATTERN_SPLIT.split(fileName);
            this.name = fileParts[fileParts.length - 1];
        }

        public String getName() {
            return name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof FileIdentification)) {
                return false;
            }

            FileIdentification that = (FileIdentification) o;

            return name.equals(that.name);
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }
    }
}
