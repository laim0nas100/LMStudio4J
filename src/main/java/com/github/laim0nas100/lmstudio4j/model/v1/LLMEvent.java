package com.github.laim0nas100.lmstudio4j.model.v1;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.github.laim0nas100.lmstudio4j.model.v1.LLMEvent.*;


/**
 *
 * @author Lemmin
 */

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY, // Uses the 'type' field already in the JSON
    property = "type",
    visible = true
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = ChatStart.class, name = "chat.start"),
    @JsonSubTypes.Type(value = PromptProcessingStart.class, name = "prompt_processing.start"),
    @JsonSubTypes.Type(value = PromptProcessingProgress.class, name = "prompt_processing.progress"),
    @JsonSubTypes.Type(value = PromptProcessingEnd.class, name = "prompt_processing.end"),
    @JsonSubTypes.Type(value = MessageStart.class, name = "message.start"),
    @JsonSubTypes.Type(value = MessageDelta.class, name = "message.delta"),
    @JsonSubTypes.Type(value = MessageEnd.class, name = "message.end"),
    @JsonSubTypes.Type(value = ChatEnd.class, name = "chat.end")
})
public interface LLMEvent {
    
    public static final Dummy DUMMY = new Dummy("dummy");

    public String type();
    
    public record Dummy(String type) implements LLMEvent{
        
    }

    public record ChatStart(String type, String model_instance_id) implements LLMEvent {

    }

    public record ChatEnd(String type, ChatResponse result) implements LLMEvent {

    }

    public record PromptProcessingStart(String type) implements LLMEvent {

    }

    public record PromptProcessingEnd(String type) implements LLMEvent {

    }

    public record PromptProcessingProgress(String type, double progress) implements LLMEvent {

    }

    public record MessageStart(String type) implements LLMEvent {

    }
    
    public record MessageEnd(String type) implements LLMEvent {

    }

    public record MessageDelta(String type, String content) implements LLMEvent {

    }

}
