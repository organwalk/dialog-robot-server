package com.content.contentprocess.entity.respond;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class  OrderRespond {
    private long status;
    private boolean success;
    private Object orderRes;

    public static OrderRespond ok(Object orderRes){
        return OrderRespond.builder()
                .status(200)
                .success(true)
                .orderRes(orderRes)
                .build();
    }

    public static OrderRespond dataNull(){
        return OrderRespond.builder()
                .status(404)
                .success(false)
                .build();
    }

    public static OrderRespond fail(){
        return OrderRespond.builder()
                .status(500)
                .success(false)
                .build();
    }

}
