package com.content.contentprocess.service;

import com.content.contentprocess.entity.request.SaveScheduleRequest;
import com.content.contentprocess.entity.request.UpdataScheduleRequest;
import com.content.contentprocess.entity.respond.ScheduleListRespond;
import com.content.contentprocess.entity.respond.StatusRespond;


public interface ScheduleService {
    StatusRespond saveScheduleData(SaveScheduleRequest saveScheduleRequest);
    StatusRespond updataScheduleData(UpdataScheduleRequest updataScheduleRequest,String scheduleId);
    StatusRespond deleteScheduleData(String scheduleId);
    ScheduleListRespond getScheduleDataList(String uid,String begintime);
}
