package com.content.contentprocess.entity.table;

import lombok.Data;

@Data
public class IntentionAndEntityResult {
    private String content;
    private String intention;
    private String entity;

    public IntentionAndEntityResult(String content, String intention, String entity) {
        this.content = content;
        this.intention = intention;
        this.entity = entity;
    }
}
