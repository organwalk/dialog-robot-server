package com.content.contentprocess.entity.resource;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;

@Data
@Builder
public class Image {
    private String orderType;
    private ArrayList object;
    private String image;

    public static Image set(String image){
        return Image.builder()
                .orderType("PicMsg")
                .image(image)
                .build();
    }
}
