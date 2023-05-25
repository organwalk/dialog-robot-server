package com.content.contentprocess.entity.request;

import lombok.Data;

import java.util.List;

@Data
public class UpdataScheduleRequest {
    private String action;
    private ScheduleData data;
    @Data
    public static class ScheduleData {
        private Long uid;
        private String name;
        private String content;
        private Long begintime;
        private Long endtime;
        private boolean iswarn;
        private String straddr;
        private List<Member> members;
        private String strdescrip;
    }
    @Data
    public static class Member {
        private String olduid;
        private String oldname;
        private String newuid;
        private String newname;
    }
}
