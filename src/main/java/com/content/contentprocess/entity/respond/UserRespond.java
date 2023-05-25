package com.content.contentprocess.entity.respond;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserRespond {
    private long status;
    private boolean success;
    private String uid;
    private String name;
    private String dept;

    public static UserRespond ok(String uid,String name,String dept){
        return UserRespond.builder()
                .status(200)
                .success(true)
                .uid(uid)
                .name(name)
                .dept(dept)
                .build();
    }

    public static UserRespond dataNull(){
        return UserRespond.builder()
                .status(404)
                .success(false)
                .build();
    }

    public static UserRespond fail(){
        return UserRespond.builder()
                .status(500)
                .success(false)
                .build();
    }
}
