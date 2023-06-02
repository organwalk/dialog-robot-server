package com.content.contentprocess.entity.request;

import lombok.Data;

import java.util.List;

@Data
public class SaveNotificationRequest {
    private String action;
    private Long uid;
    private Long noticeId;
    private NoticeData data;

    @Data
    public static class NoticeData{
        private String content;
        private Long remindTime;
        private List<Member> members;
        private boolean isPushMail;
    }

    @Data
    public static class Member {
        private String uid;
        private String name;
    }
}
