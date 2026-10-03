package com.github.laim0nas100.lmstudio4j.model.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.laim0nas100.commonslb.reflect.Refl.SelfIDBean;

/**
 *
 * @author Lemmin
 */
public class Stats extends SelfIDBean {

    @JsonProperty("input_tokens")
    private Integer inputTokens;
    @JsonProperty("total_output_tokens")
    private Integer totalOutputTokens;
    @JsonProperty("reasoning_output_tokens")
    private Integer reasoningOutputTokens;
    @JsonProperty("tokens_per_second")
    private Double tokensPerSecond;
    @JsonProperty("time_to_first_token_seconds")
    private Double timeToFirstTokenSeconds;
    @JsonProperty("model_load_time_seconds")
    private Double modelLoadTimeSeconds;

    public Integer getInputTokens() {
        return inputTokens;
    }

    public void setInputTokens(Integer inputTokens) {
        this.inputTokens = inputTokens;
    }

    public Integer getTotalOutputTokens() {
        return totalOutputTokens;
    }

    public void setTotalOutputTokens(Integer totalOutputTokens) {
        this.totalOutputTokens = totalOutputTokens;
    }

    public Integer getReasoningOutputTokens() {
        return reasoningOutputTokens;
    }

    public void setReasoningOutputTokens(Integer reasoningOutputTokens) {
        this.reasoningOutputTokens = reasoningOutputTokens;
    }

    public Double getTokensPerSecond() {
        return tokensPerSecond;
    }

    public void setTokensPerSecond(Double tokensPerSecond) {
        this.tokensPerSecond = tokensPerSecond;
    }

    public Double getTimeToFirstTokenSeconds() {
        return timeToFirstTokenSeconds;
    }

    public void setTimeToFirstTokenSeconds(Double timeToFirstTokenSeconds) {
        this.timeToFirstTokenSeconds = timeToFirstTokenSeconds;
    }

    public Double getModelLoadTimeSeconds() {
        return modelLoadTimeSeconds;
    }

    public void setModelLoadTimeSeconds(Double modelLoadTimeSeconds) {
        this.modelLoadTimeSeconds = modelLoadTimeSeconds;
    }
}
