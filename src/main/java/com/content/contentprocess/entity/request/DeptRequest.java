package com.content.contentprocess.entity.request;

import lombok.Data;

import java.util.List;

//请求部门列表
@Data
public class DeptRequest {
    private String action;
    private List<Dept> dept;

    @Data
    static public class Dept {
        private List<Departments> departments;
    }
    @Data
    static public class Departments {
        private long deptId;
        private long parentId;
        private String name;
        private long order;

    }


}
