package com.content.contentprocess.entity.request;

import lombok.Data;

@Data
public class SendFeedbackRequest {
    private String content;
    private String intention;
    private String entity;
    private String evaluate;
    private String newIntention;
    private String newEntity;
}
