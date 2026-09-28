package com.github.laim0nas100.lmstudio4j.model.v1;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 *
 * @author Lemmin
 */
public enum Reasoning {
    OFF("off"), LOW("low"), MEDIUM("medium"), HIGH("high"), XHIGH("xhigh"), ON("on");
    private final String value;

    Reasoning(String value) {
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
