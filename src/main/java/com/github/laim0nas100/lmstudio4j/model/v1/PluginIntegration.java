package com.github.laim0nas100.lmstudio4j.model.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import com.github.laim0nas100.commonslb.F;
import com.github.laim0nas100.commonslb.clone.Cloner;

/**
 *
 * @author Lemmin
 */
public class PluginIntegration implements Integration {

    private String type = "plugin";
    private String id;
    @JsonProperty("allowed_tools")
    private List<String> allowedTools;

    public PluginIntegration() {
    }

    public PluginIntegration(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getAllowedTools() {
        return allowedTools;
    }

    public void setAllowedTools(List<String> allowedTools) {
        this.allowedTools = allowedTools;
    }

    @Override
    public PluginIntegration clone() throws CloneNotSupportedException {
        return clone(Cloner.get());
    }

    @Override
    public PluginIntegration clone(Cloner cloner) throws CloneNotSupportedException {
        PluginIntegration clone = F.cast(super.clone());
        clone.allowedTools = cloner.cloneOrNull(allowedTools, ArrayList::new);
        return clone;
    }
}
