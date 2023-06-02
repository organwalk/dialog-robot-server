package com.content.contentprocess.entity.request;

import lombok.Data;

@Data
public class UserRequest {
    private String action;
    private String uid;
    private String name;
    private String mobile;
    private String deptName;
}
