package com.github.laim0nas100.lmstudio4j.model.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import lt.lb.commons.F;
import lt.lb.commons.clone.Cloner;

/**
 *
 * @author Lemmin
 */
public class ChatRequest extends ChatSession {

    /* * 
    LM Studio accepts either: 
    * * "input": "Hello" * * or: * * 
    "input": [ * {"type": "text", "content": "Hello"}, {"type": "image", "data_url": "..."} * ] 
    Object allows Jackson to serialize both forms. */
    protected Object input;
    @JsonProperty("previous_response_id")
    protected String previousResponseId;

    public ChatRequest() {
    }

    public ChatRequest(ChatSession session) {
        this.contextLength = session.contextLength;
        this.integrations = Cloner.get().cloneCollection(session.integrations, ArrayList::new);
        this.maxOutputTokens = session.maxOutputTokens;
        this.minP = session.minP;
        this.model = session.model;
        this.reasoning = session.reasoning;
        this.repeatPenalty = session.repeatPenalty;
        this.store = session.store;
        this.stream = session.stream;
        this.systemPrompt = session.systemPrompt;
        this.temperature = session.temperature;
        this.topK = session.topK;
        this.topP = session.topP;
    }

    public Object getInput() {
        return input;
    }

    public void setInput(Object input) {
        this.input = input;
    }

    public String getPreviousResponseId() {
        return previousResponseId;
    }

    public void setPreviousResponseId(String previousResponseId) {
        this.previousResponseId = previousResponseId;
    }

    @Override
    public ChatRequest clone(Cloner cloner) throws CloneNotSupportedException {
        return F.cast(super.clone(cloner));
    }

    @Override
    public ChatRequest clone() throws CloneNotSupportedException {
        return F.cast(super.clone());
    }
    
    
}
