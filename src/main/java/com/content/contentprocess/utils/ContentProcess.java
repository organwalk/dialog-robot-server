package com.content.contentprocess.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.content.contentprocess.entity.table.NotificationTable;
import com.content.contentprocess.entity.table.ScheduleTable;
import com.content.contentprocess.mapper.mysql.NotificationMapper;
import com.content.contentprocess.mapper.mysql.ScheduleMapper;
import com.content.contentprocess.mapper.redis.GetDataListRedis;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class ContentProcess {
    private final GetDataListRedis getDataListRedis;
    private final ScheduleMapper scheduleMapper;
    private final NotificationMapper notificationMapper;

    // 参数模板二次处理
    public Object jsonProcess(JSONObject jsonObject, String mobile){
        //  如果该参数模板存在 object 字段的值
        if (jsonObject.containsKey("object")){
            //  首先判断该字段是否属于人名
            if (!receiversProcess(jsonObject,mobile).isEmpty()){
                jsonObject.put("receivers",receiversProcess(jsonObject,mobile));
                jsonObject.remove("object");
            }
            //  若不属于人名，则可能属于群名
            else if (!groupIdProcess(jsonObject,mobile).equals("")){
                jsonObject.put("groupId",Long.valueOf(groupIdProcess(jsonObject, mobile)));
                jsonObject.remove("object");
            }else {
                //  如果都不属于，则表明区分失败或者当前主体不可寻，应置空object
                jsonObject.put("object", "");
            }
        }

        if (jsonObject.containsKey("image")) {
            jsonObject.put("image", "");
        }

        if (jsonObject.containsKey("dept")) {
            if (jsonObject.get("orderType").equals("AddDept") || jsonObject.get("orderType").equals("DelDept")) {
                return jsonObject;
            }
            jsonObject.put("dept", deptIdProcess(jsonObject, mobile).isEmpty() ?
                    "" : Long.valueOf(deptIdProcess(jsonObject, mobile)));
        }

        if (jsonObject.containsKey("name")) {
            String nameResult = nameProcess(jsonObject, mobile);
            if (!nameResult.isEmpty()) {
                jsonObject.put("uid", Long.valueOf(nameResult));
                if (!jsonObject.get("orderType").equals("GetMan") && !jsonObject.get("orderType").equals("GetManDept")) {
                    jsonObject.remove("name");
                }
            } else {
                jsonObject.put("name", "");
            }
        }

        if (jsonObject.get("orderType").equals("TimeQueryPlan") && jsonObject.containsKey("timeDetected")){
            List<String> time = (List<String>) jsonObject.get("timeDetected");
            List<ScheduleTable> data = scheduleMapper.findByTimeAndMember(time.get(0),time.get(1), nameJson(mobile));
            jsonObject.put("timeQueryPlanData",scheduleProcessToHTML(data));
            jsonObject.remove("timeDetected");
        }

        if (jsonObject.get("orderType").equals("NameQueryPlan") && jsonObject.containsKey("planName")){
            List<ScheduleTable> data = scheduleMapper.findByNameAndMember(String.valueOf(jsonObject.get("planName")),nameJson(mobile),strStart(), strEnd());
            jsonObject.put("nameQueryPlanData",scheduleProcessToHTML(data));
            jsonObject.remove("planName");
        }

        if (jsonObject.get("orderType").equals("ContentQueryPlan") && jsonObject.containsKey("planContent")){
            List<ScheduleTable> data = scheduleMapper.findByVagueContent(String.valueOf(jsonObject.get("planContent")), strStart(), strEnd());
            jsonObject.put("contentQueryPlanData",scheduleProcessToHTML(data));
            jsonObject.remove("planContent");
        }

        if (jsonObject.get("orderType").equals("FastAddNotes")){
            jsonObject.put("remindTime",getRemindTime(jsonObject));
            jsonObject.put("content",jsonObject.get("noteContent"));
            jsonObject.put("members",getMembers(jsonObject,mobile));
            jsonObject.put("isPushMail",true);
            jsonObject.remove("timeDetected");
            jsonObject.remove("noteObject");
            jsonObject.remove("noteContent");
        }

        if (jsonObject.get("orderType").equals("FastQueryNotes")){
            if (jsonObject.get("notestatus").equals("undone")){
                List<NotificationTable> data = notificationMapper.findByMemberActionAndTime("save",nameJson(mobile),strStart(),strEnd());
                jsonObject.put("fastQueryNotesData",notificationProcessToHTML(data));
                jsonObject.remove("notestatus");
            }
            else if (jsonObject.get("notestatus").equals("done")){
                List<NotificationTable> data = notificationMapper.findByMemberActionAndTime("cancel",nameJson(mobile),strStart(),strEnd());
                jsonObject.put("fastQueryNotesData",notificationProcessToHTML(data));
                jsonObject.remove("notestatus");
            }
        }

        return jsonObject;
    }




    // --------- 内部工具方法 -------------

    //  处理receivers字段的工具方法
    private List<Object> receiversProcess(JSONObject jsonObject, String mobile){
        return getUid((List) jsonObject.get("object"),mobile);
    }

    //  处理groupId字段的工具方法
    private String groupIdProcess(JSONObject jsonObject,String mobile){
        List<String> obj = (List) jsonObject.get("object");
        for (String dept : obj){
            Field[] fields =  getDataListRedis.getGroupByName(dept,mobile).getClass().getDeclaredFields();
            if ( fields.length != 0 ){
                List<Object> tempList = obj.stream()
                        .map(d -> (List<Object>) getDataListRedis.getGroupByName(d,mobile))
                        .map(list -> list.get(1))
                        .collect(Collectors.toList());
                return String.valueOf(tempList.get(0));
            }
        }
        return "";
    }

    //  处理deptId字段的工具方法
    private String deptIdProcess(JSONObject jsonObject,String mobile){
        Object result = getDataListRedis.getDeptByName(String.valueOf(jsonObject.get("dept")),mobile);
        if (result instanceof List){
            List<Object> deptId = (List<Object>) result;
            return String.valueOf(deptId.get(0));
        }
        return "";
    }

    //  处理name字段的工具方法
    private String nameProcess(JSONObject jsonObject,String mobile){
        List<Object> result = (List<Object>) getDataListRedis.getPersonByName(String.valueOf(jsonObject.get("name")),mobile);
        if (!result.isEmpty()){
            return String.valueOf(result.get(0));
        }
        return "";
    }

    //获取登录用户姓名的JSON字符串的工具方法
    private String nameJson(String mobile){
        List <Object> name = (List<Object>) getDataListRedis.getUserByMobile(mobile);
        JSONObject nameJSON = new JSONObject();
        nameJSON.put("name",name.get(1));
        return nameJSON.toString();
    }

    // 将日程List内容渲染为HTML代码
    private List<String> scheduleProcessToHTML(List<ScheduleTable> data){
        List<String> result = new ArrayList<>();
        for(ScheduleTable schedule : data) {
            String planHTML = String.format("<li><strong>%s</strong>&nbsp至&nbsp<strong>%s</strong>&nbsp&nbsp&nbsp<br/>此日程关于:&nbsp<strong>%s</strong><br/>地点:&nbsp<strong>%s</strong></li><br/>",
                    formatDate(schedule.getBegintime()),
                    formatDate(schedule.getEndtime()),
                    schedule.getContent(),
                    getLocation(schedule.getStraddr()));
            result.add(planHTML);
        }
        return result;
    }

    // 将事项List内容渲染为HTML代码
    private List<String> notificationProcessToHTML(List<NotificationTable> data){
        List<String> result = new ArrayList<>();
        for(NotificationTable notification : data) {
            String noteHTML = String.format("<li>通知时间:&nbsp<strong>%s</strong><br/>发起人:&nbsp<strong>%s</strong><br/>此事项关于:&nbsp<strong>%s</strong></li><br/>",
                    formatDate(notification.getRemind_time()),
                    notification.getName(),
                    notification.getContent());
            result.add(noteHTML);
        }
        return result;
    }

    private static String formatDate(String timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date(Long.parseLong(timestamp)));
    }

    private static String getLocation(String straddr) {
        try {
            JSONObject json = JSON.parseObject(straddr);
            return String.valueOf(json.get("title"));
        } catch(JSONException e) {
            return "";
        }
    }

    @SneakyThrows
    private String getRemindTime(JSONObject jsonObject){
        List<String> timeList = (List<String>) jsonObject.get("timeDetected");
        if (timeList.isEmpty()){
            return "";
        }
        String time = timeList.get(0);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = dateFormat.parse(time);
        return String.valueOf(date.getTime());
    }

    private List<Map<String, Object>> getMembers(JSONObject jsonObject, String mobile) {
        List<String> names = (List<String>) jsonObject.get("noteObject");
        List<Map<String, Object>> members = new ArrayList<>();
        for (String name : names) {
            List<Object> checkUid = (List<Object>) getDataListRedis.getPersonByName(name, mobile);
            if (!checkUid.isEmpty()) {
                List<Map<String, Object>> tempList = names.stream()
                        .map(n -> {
                            Map<String, Object> map = new HashMap<>();
                            map.put("name", n);
                            List<Object> uidList = (List<Object>) getDataListRedis.getPersonByName(n, mobile);
                            if (!uidList.isEmpty()) {
                                map.put("uid", uidList.get(0));
                            }
                            return map;
                        })
                        .collect(Collectors.toList());
                members.addAll(tempList);
                return members;
            }
        }
        return new ArrayList<>();
    }

    private List<Object> getUid(List<String> obj,String mobile){
        List<Object> uid = new ArrayList<>();
        for (String name : obj){
            List<Object> checkUid = (List<Object>) getDataListRedis.getPersonByName(name,mobile);
            if (!checkUid.isEmpty()){
                List<Object> tempList = obj.stream()
                        .map(n -> (List<Object>) getDataListRedis.getPersonByName(n,mobile))
                        .map(list -> list.get(0))
                        .collect(Collectors.toList());
                uid.addAll(tempList);
                return uid;
            }
        }
        return new ArrayList<>();
    }

    private String strStart(){
        return LocalDateTime.of(LocalDate.now(), LocalTime.MIN).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    private String strEnd(){
        return LocalDateTime.of(LocalDate.now(), LocalTime.MAX).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
