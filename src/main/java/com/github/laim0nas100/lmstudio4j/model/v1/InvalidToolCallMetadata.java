package com.github.laim0nas100.lmstudio4j.model.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

/**
 *
 * @author Lemmin
 */
public class InvalidToolCallMetadata {

    private InvalidToolCallType type;
    @JsonProperty("tool_name")
    private String toolName;
    private Map<String, Object> arguments;
    @JsonProperty("provider_info")
    private ProviderInfo providerInfo;

    public InvalidToolCallType getType() {
        return type;
    }

    public void setType(InvalidToolCallType type) {
        this.type = type;
    }

    public String getToolName() {
        return toolName;
    }

    public void setToolName(String toolName) {
        this.toolName = toolName;
    }

    public Map<String, Object> getArguments() {
        return arguments;
    }

    public void setArguments(Map<String, Object> arguments) {
        this.arguments = arguments;
    }

    public ProviderInfo getProviderInfo() {
        return providerInfo;
    }

    public void setProviderInfo(ProviderInfo providerInfo) {
        this.providerInfo = providerInfo;
    }
}
