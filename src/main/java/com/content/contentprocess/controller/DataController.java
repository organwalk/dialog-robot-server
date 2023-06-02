package com.content.contentprocess.controller;

import com.content.contentprocess.entity.request.*;
import com.content.contentprocess.entity.respond.NotificationListRespond;
import com.content.contentprocess.entity.respond.ScheduleListRespond;
import com.content.contentprocess.entity.respond.StatusRespond;
import com.content.contentprocess.mapper.redis.GetDataListRedis;
import com.content.contentprocess.mapper.redis.SaveDataListRedis;
import com.content.contentprocess.service.NotificationService;
import com.content.contentprocess.service.ScheduleService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/data")
@AllArgsConstructor
public class DataController {
    private final ScheduleService scheduleService;
    private final NotificationService notificationService;
    private final SaveDataListRedis saveDataListRedis;
    private final GetDataListRedis getDataListRedis;
    @PostMapping("/group/{mobile}")
    public StatusRespond saveGroupInfo(@PathVariable String mobile,
                                       @RequestBody GroupRequest groupRequest){
        return saveDataListRedis.saveGroupInfo(groupRequest, mobile) ?
                StatusRespond.ok() : StatusRespond.fail();
    }

    @PostMapping("/dept/{mobile}")
    public StatusRespond saveDeptInfo(@PathVariable String mobile,
                                       @RequestBody DeptRequest deptRequest){
        return saveDataListRedis.saveDeptInfo(deptRequest, mobile) ?
                StatusRespond.ok() : StatusRespond.fail();
    }

    @PostMapping("/dept/person/{mobile}")
    public StatusRespond savePersonInfo(@PathVariable String mobile,
                                      @RequestBody PersonRequest personRequest){
        return saveDataListRedis.savePerSonInfo(personRequest, mobile) ?
                StatusRespond.ok() : StatusRespond.fail();
    }

    @PostMapping("/user")
    public StatusRespond getUserInfo(@RequestBody UserRequest userRequest) {
        return saveDataListRedis.saveUserInfo(userRequest) ?
                StatusRespond.ok() : StatusRespond.fail();
   }
    @DeleteMapping("/delete/{mobile}")
    public void deleteScheduleData(@PathVariable String mobile){
       getDataListRedis.deleteUserByMobile(mobile);
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

    @PostMapping("/notification")
    public StatusRespond saveNotification(@RequestBody SaveNotificationRequest saveNotificationRequest){
        return notificationService.saveNotificationData(saveNotificationRequest);
    }

    @PutMapping("/notification/{noticeId}")
    public StatusRespond updataScheduleData(@PathVariable String noticeId,
                                            @RequestBody UpdataNotificationRequest updataNotificationRequest){
        return notificationService.updataNotificationData(updataNotificationRequest,noticeId);
    }

    @DeleteMapping("/notification/{noticeId}")
    public StatusRespond deleteNotificationData(@PathVariable String noticeId,
                                                @RequestBody ActionRequest actionRequest){
        System.out.println("noticeId = " + noticeId);
        return notificationService.deleteNotificationData(noticeId);
    }

    @GetMapping("/notification/{uid}")
    public NotificationListRespond getNotificationDataList(@PathVariable String uid,
                                                       @RequestBody ActionRequest actionRequest){
        return notificationService.getNotificationDataList(uid);
    }

}
