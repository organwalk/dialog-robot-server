package com.content.contentprocess.service.impl;

import com.content.contentprocess.entity.request.SaveScheduleRequest;
import com.content.contentprocess.entity.request.UpdataScheduleRequest;
import com.content.contentprocess.entity.respond.ScheduleListRespond;
import com.content.contentprocess.entity.respond.StatusRespond;
import com.content.contentprocess.entity.table.ScheduleTable;
import com.content.contentprocess.mapper.mysql.ScheduleMapper;
import com.content.contentprocess.service.ScheduleService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.swing.*;
import java.util.List;

@Service
@AllArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {
    private final ScheduleMapper scheduleMapper;

    //1.4 存储日程
    @Override
    public StatusRespond saveScheduleData(SaveScheduleRequest saveScheduleRequest) {

        if (saveScheduleRequest == null){
            return StatusRespond.dataNull();
        }

        //将请求体中的数据取出插入数据库表中
        ScheduleTable scheduleTable = new ScheduleTable();
        SaveScheduleRequest.ScheduleData scheduleData = saveScheduleRequest.getData();

        scheduleTable.setUid(String.valueOf(scheduleData.getUid()));
        scheduleTable.setName(scheduleData.getName());
        scheduleTable.setContent(scheduleData.getContent());
        scheduleTable.setBegintime(String.valueOf(scheduleData.getBegintime()));
        scheduleTable.setEndtime(String.valueOf(scheduleData.getEndtime()));
        scheduleTable.setIswarn(String.valueOf(scheduleData.getIswarn()));
        scheduleTable.setStraddr(scheduleData.getStraddr());
        scheduleTable.setStrdescrip(scheduleData.getStrdescrip());
        scheduleTable.setScheduleId(String.valueOf(saveScheduleRequest.getScheduleId()));
        scheduleTable.setMembers(scheduleData.getMembers());
        scheduleTable.setAction(saveScheduleRequest.getAction());

        //检查日程数据是否插入成功
        return scheduleMapper.insert(scheduleTable) > 0 ? StatusRespond.ok() : StatusRespond.fail();
    }

    //1.5 修改 / 取消日程
    @Override
    public StatusRespond updataScheduleData(UpdataScheduleRequest updataScheduleRequest,String scheduleId) {

        if (updataScheduleRequest == null){
            return StatusRespond.dataNull();
        }

        if (updataScheduleRequest.getAction().equals("updata")){
            UpdataScheduleRequest.ScheduleData data = updataScheduleRequest.getData();
            String content = data.getContent();
            String begintime = String.valueOf(data.getBegintime());
            String endtime = String.valueOf(data.getEndtime());
            String iswarn = String.valueOf(data.isIswarn());
            String straddr = data.getStraddr();
            String strdescrip = data.getStrdescrip();
            String members = data.getMembers();
            return  scheduleMapper.updateSchedule(content,begintime,endtime,iswarn,straddr,strdescrip,scheduleId,members) > 0 ?
                    StatusRespond.ok() : StatusRespond.fail();
        }
        if (updataScheduleRequest.getAction().equals("cancel")){
            return scheduleMapper.cancelSchedule(updataScheduleRequest.getAction(), scheduleId) > 0 ?
                    StatusRespond.ok() : StatusRespond.fail();
        }else {
            return StatusRespond.fail();
        }
    }

    //1.6 删除日程
    @Override
    public StatusRespond deleteScheduleData(String scheduleId) {
        return scheduleMapper.deleteByScheduleId(scheduleId) > 0 ?
                StatusRespond.ok() : StatusRespond.fail();
    }

    //1.7 获取用户发布的日程列表
    @Override
    public ScheduleListRespond getScheduleDataList(String uid,String begintime) {
        if (uid.isEmpty()){
            return ScheduleListRespond.fail();
        }
        return scheduleMapper.getScheduleById(uid, Long.valueOf(begintime)) != null ?
                ScheduleListRespond.ok(scheduleMapper.getScheduleById(uid, Long.valueOf(begintime))) : ScheduleListRespond.dataNull();
    }

}
