package com.dzone.tnas.introspectorfilter.model;

import com.dzone.tnas.introspectorfilter.annotation.Filterable;

public abstract class Publication {

    @Filterable
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
