package com.content.contentprocess.entity.request;

import lombok.Data;

//
@Data
public class OrderRequest {
    private String action;
    private String deptName;
    private String orderContent;
}
