package com.content.contentprocess.entity.respond;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BaseDataRespond {
    private long status;
    private boolean success;
    private Object data;

    public static BaseDataRespond ok(Object data){
        return BaseDataRespond.builder()
                .status(200)
                .success(true)
                .data(data)
                .build();
    }

    public static BaseDataRespond dataNull(){
        return BaseDataRespond.builder()
                .status(404)
                .success(false)
                .build();
    }

    public static BaseDataRespond fail(){
        return BaseDataRespond.builder()
                .status(500)
                .success(false)
                .build();
    }

}
