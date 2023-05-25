package com.content.contentprocess.entity.respond;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StatusRespond {
    private long status;
    private boolean success;

    public static StatusRespond ok(){
        return StatusRespond.builder()
                .status(200)
                .success(true)
                .build();
    }

    public static StatusRespond dataNull(){
        return StatusRespond.builder()
                .status(404)
                .success(false)
                .build();
    }

    public static StatusRespond fail(){
        return StatusRespond.builder()
                .status(500)
                .success(false)
                .build();
    }
}
