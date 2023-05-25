package com.content.contentprocess.controller;

import com.content.contentprocess.entity.request.ActionRequest;
import com.content.contentprocess.entity.request.GroupRequest;
import com.content.contentprocess.entity.request.SaveScheduleRequest;
import com.content.contentprocess.entity.request.UpdataScheduleRequest;
import com.content.contentprocess.entity.respond.ScheduleListRespond;
import com.content.contentprocess.entity.respond.StatusRespond;
import com.content.contentprocess.mapper.redis.SaveDataListRedis;
import com.content.contentprocess.service.ScheduleService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/data")
@AllArgsConstructor
public class DataController {
    private final ScheduleService scheduleService;
    private final SaveDataListRedis saveDataListRedis;

    @PostMapping("/group/{mobile}")
    public StatusRespond saveGroupInfo(@PathVariable String mobile,
                                       @RequestBody GroupRequest groupRequest){
        return saveDataListRedis.saveGroupInfo(groupRequest,mobile) == true ?
                StatusRespond.ok() : StatusRespond.fail();
    }

    @PostMapping("/schedule")
    public StatusRespond saveScheduleData(@RequestBody SaveScheduleRequest saveScheduleRequest){
        return scheduleService.saveScheduleData(saveScheduleRequest);
    }

    @PutMapping("/schedule/{scheduleId}")
    public StatusRespond updataScheduleData(@PathVariable String scheduleId,
                                            @RequestBody UpdataScheduleRequest updataScheduleRequest){
        return scheduleService.updataScheduleData(updataScheduleRequest,scheduleId);
    }

    @DeleteMapping("/schedule/{scheduleId}")
    public StatusRespond deleteScheduleData(@PathVariable String scheduleId,
                                            @RequestBody ActionRequest actionRequest){
        return scheduleService.deleteScheduleData(scheduleId);
    }

    @GetMapping("/schedule/{uid}")
    public ScheduleListRespond getScheduleDataList(@PathVariable String uid,
                                                   @RequestBody ActionRequest actionRequest){
        return scheduleService.getScheduleDataList(uid);
    }
}
