package com.github.laim0nas100.lmstudio4j.model.v1;

import com.github.laim0nas100.commonslb.reflect.Refl.SelfIDBean;

/**
 *
 * @author Lemmin
 */
public class MessageOutput extends SelfIDBean implements OutputItem {

    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
