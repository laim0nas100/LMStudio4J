package com.github.laim0nas100.lmstudio4j.model.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import com.github.laim0nas100.commonslb.reflect.Refl.SelfIDBean;

/**
 *
 * @author Lemmin
 */
public class ChatResponse extends SelfIDBean {

    @JsonProperty("model_instance_id")
    private String modelInstanceId;
    private List<OutputItem> output;
    private Stats stats;
    @JsonProperty("response_id")
    private String responseId;

    public String getModelInstanceId() {
        return modelInstanceId;
    }

    public void setModelInstanceId(String modelInstanceId) {
        this.modelInstanceId = modelInstanceId;
    }

    public List<OutputItem> getOutput() {
        return output;
    }

    public void setOutput(List<OutputItem> output) {
        this.output = output;
    }

    public Stats getStats() {
        return stats;
    }

    public void setStats(Stats stats) {
        this.stats = stats;
    }

    public String getResponseId() {
        return responseId;
    }

    public void setResponseId(String responseId) {
        this.responseId = responseId;
    }

    /**
     * * Returns all normal text messages contained in the response.
     */
    public List<String> resolveMessages() {
        List<String> result = new ArrayList<>();
        if (output == null) {
            return result;
        }
        for (OutputItem item : output) {
            if (item instanceof MessageOutput message) {
                result.add(message.getContent());
            }
        }
        return result;
    }

    /**
     * * Returns the first normal message.
     */
    public String resolveFirstMessage() {
        if (output == null) {
            return null;
        }
        for (OutputItem item : output) {
            if (item instanceof MessageOutput message) {
                return message.getContent();
            }
        }
        return null;
    }
}
