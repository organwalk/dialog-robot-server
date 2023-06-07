package com.content.contentprocess.entity.request;

import lombok.Data;

import java.util.List;

@Data
public class UpdataNotificationRequest {
    private String action;
    private NoticeData data;
    @Data
    public static class NoticeData{
        private String content;
        private long remindTime;
        private String members;
        private boolean isPushMail;
    }
}
