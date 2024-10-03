package com.dzone.tnas.introspectorfilter.model;

import com.dzone.tnas.introspectorfilter.annotation.Filterable;

public abstract class Publication {

    @Filterable
    private String text;
    
    @Filterable
    private int relevance;
    
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

	public int getRelevance() {
		return relevance;
	}

	public void setRelevance(int relevance) {
		this.relevance = relevance;
	}
}
