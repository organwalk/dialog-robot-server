package com.content.contentprocess.controller;

import com.content.contentprocess.entity.request.*;
import com.content.contentprocess.entity.respond.NotificationListRespond;
import com.content.contentprocess.entity.respond.ObjectIdArrayRespond;
import com.content.contentprocess.entity.respond.ScheduleListRespond;
import com.content.contentprocess.entity.respond.StatusRespond;
import com.content.contentprocess.mapper.mysql.NotificationMapper;
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
@RequestMapping("/api_c/data")
@AllArgsConstructor
public class DataController {
    private final ScheduleService scheduleService;
    private final NotificationService notificationService;
    private final SaveDataListRedis saveDataListRedis;
    private final GetDataListRedis getDataListRedis;
    private final ScheduleMapper scheduleMapper;
    private final NotificationMapper notificationMapper;

    //通过人名数组获得Uid
    @PostMapping("/receivers/{mobile}")
    public ObjectIdArrayRespond getUidByNameList(@PathVariable String mobile,
                                                 @RequestBody ObjectIdRequest objectIdRequest){
        List<Object> nameIdList = new ArrayList<>();
        for (String name : objectIdRequest.getNameArray()) {
            nameIdList.add(getDataListRedis.getPersonByDeptAndName(name, objectIdRequest.getDeptName(),mobile));
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

    @DeleteMapping("/dept/group/{mobile}")
    public StatusRespond saveDeptInfo(@PathVariable String mobile){
        return getDataListRedis.deleteDept(mobile) ? StatusRespond.ok() : StatusRespond.fail();
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

    @DeleteMapping("/dept/person/{mobile}")
    public StatusRespond deletePersonByName(@PathVariable String mobile, @RequestParam("name")String name, @RequestParam("deptName")String deptName){
        return getDataListRedis.deletePersonByName(mobile, deptName, name) ? StatusRespond.ok() : StatusRespond.fail();
    }

    @DeleteMapping("/delete/{mobile}")
    public void deleteScheduleDataRedis(@PathVariable String mobile){
       getDataListRedis.deleteUserByMobile(mobile);
    }


    //  日程
    @PostMapping("/schedule")
    public StatusRespond saveScheduleData(@RequestBody SaveScheduleRequest saveScheduleRequest){
        return scheduleService.saveScheduleData(saveScheduleRequest);
    }

    @PutMapping("/schedule/{scheduleId}")
    public StatusRespond updataScheduleData(@PathVariable String scheduleId,
                                            @RequestBody UpdataScheduleRequest updataScheduleRequest){
        return scheduleService.updataScheduleData(updataScheduleRequest,scheduleId);
    }

    @PutMapping("/schedule/cancel/{scheduleId}")
    public StatusRespond cancelScheduleData(@PathVariable String scheduleId){
        return scheduleMapper.cancelSchedule("cancel",scheduleId) > 0 ? StatusRespond.ok() : StatusRespond.fail();
    }

    @DeleteMapping("/schedule/{scheduleId}")
    public StatusRespond deleteScheduleData(@PathVariable String scheduleId){
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


    //  事项
    @PutMapping("/notification/{noticeId}")
    public StatusRespond updataScheduleData(@PathVariable String noticeId,
                                            @RequestBody UpdataNotificationRequest updataNotificationRequest){
        return notificationService.updataNotificationData(updataNotificationRequest,noticeId);
    }

    @PostMapping("/notification")
    public StatusRespond saveNotification(@RequestBody SaveNotificationRequest saveNotificationRequest){
        return notificationService.saveNotificationData(saveNotificationRequest);
    }

    @PutMapping("/notification/cancel/{noticeId}")
    public StatusRespond cancelNotification(@PathVariable String noticeId){
        return notificationMapper.cancelNotification("cancel",noticeId) > 0 ? StatusRespond.ok() : StatusRespond.fail();
    }

    @DeleteMapping("/notification/{noticeId}")
    public StatusRespond deleteNotificationData(@PathVariable String noticeId){
        return notificationService.deleteNotificationData(noticeId);
    }

    @PostMapping("/notification/{uid}")
    public NotificationListRespond getNotificationDataList(@PathVariable String uid,
                                                       @RequestBody GetNotificationRequest getNotificationRequest){
        return notificationService.getNotificationDataList(uid,getNotificationRequest.getRemind_time());
    }

    @GetMapping("/notification/nid/{nid}")
    public NotificationListRespond getNotification(@PathVariable String nid){
        return NotificationListRespond.ok(notificationMapper.getNotificationByNid(nid));
    }

    @GetMapping("/notification/count/{uid}")
    public NotificationListRespond getNotificationDataCount(@PathVariable String uid){
        return NotificationListRespond.ok(notificationMapper.getNotificationCountByDate(uid)) ;
    }


}
