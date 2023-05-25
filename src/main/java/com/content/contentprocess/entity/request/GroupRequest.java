package com.content.contentprocess.entity.request;

import lombok.Data;

import java.util.List;

@Data
public class GroupRequest {
    private String action;
    private List<Group> group;

    @Data
    public static class Group {
        private long deptId;
        private long groupId;
        private String groupName;
    }

}
