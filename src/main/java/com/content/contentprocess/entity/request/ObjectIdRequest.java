package com.content.contentprocess.entity.request;

import lombok.Data;

import java.util.List;

@Data
public class ObjectIdRequest {
    private String deptName;
    private List<String> nameArray;
}
