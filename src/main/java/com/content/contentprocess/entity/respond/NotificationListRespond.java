package com.content.contentprocess.entity.respond;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationListRespond {
    private long status;
    private boolean success;
    private Object notificationData;

    public static NotificationListRespond ok(Object notificationData){
        return NotificationListRespond.builder()
                .status(200)
                .success(true)
                .notificationData(notificationData)
                .build();
    }

    public static NotificationListRespond dataNull(){
        return NotificationListRespond.builder()
                .status(404)
                .success(false)
                .build();
    }

    public static NotificationListRespond fail(){
        return NotificationListRespond.builder()
                .status(500)
                .success(false)
                .build();
    }
}
