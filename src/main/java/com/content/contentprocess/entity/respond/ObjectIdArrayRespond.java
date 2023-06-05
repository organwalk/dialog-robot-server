package com.content.contentprocess.entity.respond;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ObjectIdArrayRespond {
    private long status;
    private boolean success;
    private Object data;

    public static ObjectIdArrayRespond ok(Object object){
        return ObjectIdArrayRespond.builder()
                .status(200)
                .success(true)
                .data(object)
                .build();
    }

    public static ObjectIdArrayRespond dataNull(){
        return ObjectIdArrayRespond.builder()
                .status(404)
                .success(false)
                .build();
    }

    public static ObjectIdArrayRespond fail(){
        return ObjectIdArrayRespond.builder()
                .status(500)
                .success(false)
                .build();
    }
}
