package org.eclipse.starter.business.model.entity;

import java.util.stream.Stream;

public enum Feature {

    POSTGRES("postgres"),
    KAFKA("kafka-client");

    private final String code;

    Feature(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static Feature valueFor(String feature) {
        return Stream.of(Feature.values())
                .filter(f -> f.code.equals(feature))
                .findAny()
                .orElse(null);
    }

}
