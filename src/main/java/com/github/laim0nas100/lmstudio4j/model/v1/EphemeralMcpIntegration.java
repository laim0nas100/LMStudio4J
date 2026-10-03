package com.github.laim0nas100.lmstudio4j.model.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.github.laim0nas100.commonslb.F;
import com.github.laim0nas100.commonslb.clone.Cloner;

/**
 *
 * @author Lemmin
 */
public class EphemeralMcpIntegration implements Integration {

    private String type = "ephemeral_mcp";
    @JsonProperty("server_label")
    private String serverLabel;
    @JsonProperty("server_url")
    private String serverUrl;
    @JsonProperty("allowed_tools")
    private List<String> allowedTools;
    private Map<String, String> headers;

    public EphemeralMcpIntegration() {
    }

    public EphemeralMcpIntegration(String serverLabel, String serverUrl) {
        this.serverLabel = serverLabel;
        this.serverUrl = serverUrl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getServerLabel() {
        return serverLabel;
    }

    public void setServerLabel(String serverLabel) {
        this.serverLabel = serverLabel;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public List<String> getAllowedTools() {
        return allowedTools;
    }

    public void setAllowedTools(List<String> allowedTools) {
        this.allowedTools = allowedTools;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    @Override
    public EphemeralMcpIntegration clone() throws CloneNotSupportedException {
        return clone(Cloner.get());
    }

    @Override
    public EphemeralMcpIntegration clone(Cloner cloner) throws CloneNotSupportedException {
        EphemeralMcpIntegration clone = F.cast(super.clone());
        clone.headers = cloner.cloneOrNull(headers, HashMap::new);
        clone.allowedTools = cloner.cloneOrNull(allowedTools, ArrayList::new);
        return clone;
    }
}
