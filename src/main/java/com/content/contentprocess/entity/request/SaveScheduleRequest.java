package com.content.contentprocess.entity.request;

import lombok.Data;

import java.util.List;

@Data
public class SaveScheduleRequest {
    private String action;
    private long scheduleId;
    private ScheduleData data;

    @Data
    public static class ScheduleData {
        private Long uid;
        private String name;
        private String content;
        private Long begintime;
        private Long endtime;
        private Boolean iswarn;
        private String straddr;
        private String members;
        private String strdescrip;
    }
}
