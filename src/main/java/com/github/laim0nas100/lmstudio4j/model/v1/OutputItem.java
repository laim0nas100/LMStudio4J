package com.github.laim0nas100.lmstudio4j.model.v1;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 *
 * @author Lemmin
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes({
    @JsonSubTypes.Type(
        value = MessageOutput.class,
        name = "message"
    ),
    @JsonSubTypes.Type(
        value = ToolCallOutput.class,
        name = "tool_call"
    ),
    @JsonSubTypes.Type(
        value = ReasoningOutput.class,
        name = "reasoning"
    ),
    @JsonSubTypes.Type(
        value = InvalidToolCallOutput.class,
        name = "invalid_tool_call"
    )
})
public interface OutputItem {
    
}
