package com.content.contentprocess.controller;

import com.content.contentprocess.entity.request.*;
import com.content.contentprocess.entity.respond.NotificationListRespond;
import com.content.contentprocess.entity.respond.ObjectIdArrayRespond;
import com.content.contentprocess.entity.respond.ScheduleListRespond;
import com.content.contentprocess.entity.respond.StatusRespond;
import com.content.contentprocess.mapper.mysql.ScheduleMapper;
import com.content.contentprocess.mapper.redis.GetDataListRedis;
import com.content.contentprocess.mapper.redis.SaveDataListRedis;
import com.content.contentprocess.service.NotificationService;
import com.content.contentprocess.service.ScheduleService;
import jakarta.servlet.http.PushBuilder;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/data")
@AllArgsConstructor
public class DataController {
    private final ScheduleService scheduleService;
    private final NotificationService notificationService;
    private final SaveDataListRedis saveDataListRedis;
    private final GetDataListRedis getDataListRedis;
    private final ScheduleMapper scheduleMapper;

    @PostMapping("/receivers/{mobile}")
    public ObjectIdArrayRespond getUidByNameList(@PathVariable String mobile,
                                                 @RequestBody ObjectIdRequest objectIdRequest){
        List<Object> nameIdList = new ArrayList<>();
        //['张三','李四']假设这是nameArray的数据，应该遍历取出其中的人名，然后传入下面的方法
        for (String name : objectIdRequest.getNameArray()) {
            nameIdList.add(getDataListRedis.getPersonByName(name, mobile));
        }
        return !nameIdList.isEmpty() ? ObjectIdArrayRespond.ok(nameIdList) : ObjectIdArrayRespond.fail();
    }

    @PostMapping("/groupId/{mobile}")
    public ObjectIdArrayRespond getGroupIdByGidList(@PathVariable String mobile,
                                                    @RequestBody ObjectIdRequest objectIdRequest){
        List<Object> groupIdList = new ArrayList<>();
        for (String groupName : objectIdRequest.getNameArray()){
            groupIdList.add(getDataListRedis.getGroupByName(groupName,mobile));
        }
        return !groupIdList.isEmpty() ? ObjectIdArrayRespond.ok(groupIdList) : ObjectIdArrayRespond.fail();
    }

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
    @GetMapping("/dept/id/{mobile}")
    public ObjectIdArrayRespond getDeptIdByName(@PathVariable String mobile,
                                                @RequestParam String deptName){
        return ObjectIdArrayRespond.ok(getDataListRedis.getDeptByName(deptName,mobile));
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

    @PostMapping ("/schedule/{uid}")
    public ScheduleListRespond getScheduleDataList(@PathVariable String uid,
                                                   @RequestBody GetScheduleByTimeRequest getScheduleByTimeRequest){
        return scheduleService.getScheduleDataList(uid, getScheduleByTimeRequest.getBegintime());
    }
    @GetMapping("/schedule/count/{uid}")
    public ScheduleListRespond getScheduleDataCount(@PathVariable String uid){
        return ScheduleListRespond.ok(scheduleMapper.getScheduleCountByDate(uid)) ;
    }
    @GetMapping("/schedule/id/{uid}")
    public ScheduleListRespond getScheduleDataId(@PathVariable String uid,
                                           @RequestBody ScheduleIdRequest scheduleIdRequest){
        return ScheduleListRespond.ok(scheduleMapper.getScheduleId(uid,scheduleIdRequest.getContent(),scheduleIdRequest.getBegintime(), scheduleIdRequest.getStrdescrip()));
    }
    @GetMapping("/schedule/sid/{sid}")
    public ScheduleListRespond getScheduleDataId(@PathVariable String sid){
        return ScheduleListRespond.ok(scheduleMapper.getScheduleBySid(sid));
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
