package com.github.laim0nas100.lmstudio4j.model.v1;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author Lemmin
 */
public class ImageInput implements InputItem{

    @JsonProperty("data_url")
    private String dataUrl;

    public ImageInput() {
    }

    public ImageInput(String dataUrl) {
        this.dataUrl = dataUrl;
    }

    public String getDataUrl() {
        return dataUrl;
    }

    public void setDataUrl(String dataUrl) {
        this.dataUrl = dataUrl;
    }
}
