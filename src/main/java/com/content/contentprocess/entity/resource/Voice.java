package com.content.contentprocess.entity.resource;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;

@Data
@Builder
public class Voice {
    private String orderType;
    private ArrayList object;
    private Object voiceUrl;
    public static Voice set(Object voiceObj){
        return Voice.builder()
                .orderType("VocMsg")
                .voiceUrl(voiceObj)
                .build();
    }
}
