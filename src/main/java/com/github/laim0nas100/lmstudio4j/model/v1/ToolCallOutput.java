package com.github.laim0nas100.lmstudio4j.model.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import com.github.laim0nas100.commonslb.reflect.Refl.SelfIDBean;

/**
 *
 * @author Lemmin
 */
public class ToolCallOutput extends SelfIDBean implements OutputItem {

    private String tool;
    private Map<String, Object> arguments;
    private String output;
    @JsonProperty("provider_info")
    private ProviderInfo providerInfo;

    public String getTool() {
        return tool;
    }

    public void setTool(String tool) {
        this.tool = tool;
    }

    public Map<String, Object> getArguments() {
        return arguments;
    }

    public void setArguments(Map<String, Object> arguments) {
        this.arguments = arguments;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public ProviderInfo getProviderInfo() {
        return providerInfo;
    }

    public void setProviderInfo(ProviderInfo providerInfo) {
        this.providerInfo = providerInfo;
    }
}
