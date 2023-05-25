package com.content.contentprocess.entity.request;

import lombok.Data;

import java.util.List;

@Data
public class DeptRequest {
    private String action;
    private List<Dept> dept;

    @Data
    static class Dept {
        private String action;
        private List<Departments> departments;
    }

    @Data
    static class Departments {
        private long deptId;
        private long parentId;
        private String name;
        private long order;

    }
}
