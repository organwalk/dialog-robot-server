package com.content.contentprocess.entity.request;

import lombok.Data;

import java.util.List;

@Data
public class SaveNotificationRequest {
    private String action;
    private Long uid;
    private String name;
    private Long noticeId;
    private NoticeData data;

    @Data
    public static class NoticeData{
        private String content;
        private Long remindTime;
        private String members;
        private boolean isPushMail;
    }

}
