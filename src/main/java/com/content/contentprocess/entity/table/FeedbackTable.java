package com.content.contentprocess.entity.table;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.content.contentprocess.entity.request.SendFeedbackRequest;
import lombok.Data;

@Data
@TableName(value = "feedback")
public class FeedbackTable {
    @TableId(type= IdType.AUTO)
    private Integer id;
    private String content;
    private String intention;
    private String entity;
    private String evaluate;
    private String newIntention;
    private String newEntity;

    public static FeedbackTable fromSendFeedbackRequest(SendFeedbackRequest request) {
        FeedbackTable feedbackTable = new FeedbackTable();
        feedbackTable.setContent(request.getContent());
        feedbackTable.setIntention(request.getIntention());
        feedbackTable.setEntity(request.getEntity());
        feedbackTable.setEvaluate(request.getEvaluate());
        feedbackTable.setNewIntention(request.getNewIntention());
        feedbackTable.setNewEntity(request.getNewEntity());
        return feedbackTable;
    }
}
