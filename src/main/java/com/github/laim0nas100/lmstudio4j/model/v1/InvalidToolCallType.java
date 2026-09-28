package com.github.laim0nas100.lmstudio4j.model.v1;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 *
 * @author Lemmin
 */
public enum InvalidToolCallType {
    INVALID_NAME("invalid_name"), INVALID_ARGUMENTS("invalid_arguments");
    private final String value;

    InvalidToolCallType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
