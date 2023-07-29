package com.content.contentprocess.entity.request;

import lombok.Data;

@Data
public class EditFeedbackRequest {
    private int id;
    private String newIntention;
    private String newEntity;
}
