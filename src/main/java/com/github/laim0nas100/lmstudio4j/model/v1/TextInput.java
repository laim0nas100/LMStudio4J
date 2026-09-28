package com.github.laim0nas100.lmstudio4j.model.v1;

/**
 *
 * @author Lemmin
 */
public class TextInput implements InputItem {

    private String content;

    public TextInput() {
    }

    public TextInput(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
