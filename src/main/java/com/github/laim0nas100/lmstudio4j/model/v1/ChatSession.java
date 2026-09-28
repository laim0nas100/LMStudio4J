package com.github.laim0nas100.lmstudio4j.model.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import lt.lb.commons.F;
import lt.lb.commons.clone.CloneSupport;
import lt.lb.commons.clone.Cloner;
import lt.lb.commons.reflect.Refl.SelfIDBean;

/**
 *
 * @author Lemmin
 */
public class ChatSession extends SelfIDBean implements CloneSupport<Object> {

    protected String model;
    
    @JsonProperty("system_prompt")
    protected String systemPrompt;
    protected List<Integration> integrations;
    protected Boolean stream = false;
    protected Double temperature;
    @JsonProperty("top_p")
    protected Double topP;
    @JsonProperty("top_k")
    protected Integer topK;
    @JsonProperty("min_p")
    protected Double minP;
    @JsonProperty("repeat_penalty")
    protected Double repeatPenalty;
    @JsonProperty("max_output_tokens")
    protected Integer maxOutputTokens;
    protected Reasoning reasoning;
    @JsonProperty("context_length")
    protected Integer contextLength;
    protected Boolean store = true;

    public ChatSession() {
    }


    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getSystemPrompt() {
        return systemPrompt;
    }

    public void setSystemPrompt(String systemPrompt) {
        this.systemPrompt = systemPrompt;
    }

    public List<Integration> getIntegrations() {
        return integrations;
    }

    public void setIntegrations(List<Integration> integrations) {
        this.integrations = integrations;
    }

    public Boolean getStream() {
        return stream;
    }

    public void setStream(Boolean stream) {
        this.stream = stream;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Double getTopP() {
        return topP;
    }

    public void setTopP(Double topP) {
        this.topP = topP;
    }

    public Integer getTopK() {
        return topK;
    }

    public void setTopK(Integer topK) {
        this.topK = topK;
    }

    public Double getMinP() {
        return minP;
    }

    public void setMinP(Double minP) {
        this.minP = minP;
    }

    public Double getRepeatPenalty() {
        return repeatPenalty;
    }

    public void setRepeatPenalty(Double repeatPenalty) {
        this.repeatPenalty = repeatPenalty;
    }

    public Integer getMaxOutputTokens() {
        return maxOutputTokens;
    }

    public void setMaxOutputTokens(Integer maxOutputTokens) {
        this.maxOutputTokens = maxOutputTokens;
    }

    public Reasoning getReasoning() {
        return reasoning;
    }

    public void setReasoning(Reasoning reasoning) {
        this.reasoning = reasoning;
    }

    public Integer getContextLength() {
        return contextLength;
    }

    public void setContextLength(Integer contextLength) {
        this.contextLength = contextLength;
    }

    public Boolean getStore() {
        return store;
    }

    public void setStore(Boolean store) {
        this.store = store;
    }

    @Override
    public ChatSession clone() throws CloneNotSupportedException {
        return clone(Cloner.get());

    }

    @Override
    public ChatSession clone(Cloner cloner) throws CloneNotSupportedException {
        ChatSession session = F.cast(super.clone());
        session.integrations = cloner.cloneCollection(integrations, ArrayList::new);
        return session;
    }

}
