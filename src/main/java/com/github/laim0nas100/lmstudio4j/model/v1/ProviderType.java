package com.github.laim0nas100.lmstudio4j.model.v1;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 *
 * @author Lemmin
 */
public enum ProviderType {
    PLUGIN("plugin"), EPHEMERAL_MCP("ephemeral_mcp");
    private final String value;

    ProviderType(String value) {
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
