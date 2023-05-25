package com.content.contentprocess.entity.respond;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ScheduleListRespond {
    private long status;
    private boolean success;
    private Object scheduleData;

    public static ScheduleListRespond ok(Object scheduleData){
        return ScheduleListRespond.builder()
                .status(200)
                .success(true)
                .scheduleData(scheduleData)
                .build();
    }

    public static ScheduleListRespond dataNull(){
        return ScheduleListRespond.builder()
                .status(404)
                .success(false)
                .build();
    }

    public static ScheduleListRespond fail(){
        return ScheduleListRespond.builder()
                .status(500)
                .success(false)
                .build();
    }
}
