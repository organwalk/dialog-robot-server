package com.content.contentprocess.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.content.contentprocess.entity.table.NotificationTable;
import com.content.contentprocess.entity.table.ScheduleTable;
import com.content.contentprocess.mapper.mysql.NotificationMapper;
import com.content.contentprocess.mapper.mysql.ScheduleMapper;
import com.content.contentprocess.mapper.redis.GetDataListRedis;
import com.mysql.cj.xdevapi.JsonArray;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import org.jsoup.nodes.Document;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class ContentProcess {
    private final GetDataListRedis getDataListRedis;
    private final ScheduleMapper scheduleMapper;
    private final NotificationMapper notificationMapper;

    // 参数模板二次处理
    public Object jsonProcess(JSONObject jsonObject, String deptName, String mobile){
        if (jsonObject.getString("orderType").matches("^(?!OAMsg|SendMsg).*Msg.*$")) {
            jsonObject.put("replyUseObject",jsonObject.get("object"));
        }
        //  如果该参数模板存在 object 字段的值
        if (jsonObject.get("object") != null && !jsonObject.get("object").equals("") &&
                !(jsonObject.get("object") instanceof JSONArray && ((JSONArray) jsonObject.get("object")).isEmpty())) {
            //  首先判断该字段是否属于人名
            if (!receiversProcess(jsonObject,deptName,mobile).isEmpty()){
                jsonObject.put("receivers",receiversProcess(jsonObject,deptName,mobile));
                jsonObject.remove("object");
            }
            //  若不属于人名，则可能属于群名
            else if (!groupIdProcess(jsonObject,mobile).equals("")){
                jsonObject.put("groupId",Long.valueOf(groupIdProcess(jsonObject, mobile)));
                jsonObject.remove("object");
            }else {
                //  如果都不属于，则表明区分失败或者当前主体不可寻
                jsonObject.put("notfoundKey", jsonObject.getString("object"));
                jsonObject.put("notfoundObject", "");
                jsonObject.remove("object");
            }
        }else {
            if (jsonObject.containsKey("object")){
                jsonObject.put("object","");
            }
        }

        if (jsonObject.containsKey("image")) {
            jsonObject.put("image", "");
        }

        if(jsonObject.containsKey("url")){
            if (jsonObject.getString("orderType").equals("VocMsg")){
                jsonObject.put("voiceUrl","");
                jsonObject.remove("url");
            }
        }

        if (jsonObject.containsKey("name")) {
            String nameResult = nameProcess(jsonObject, mobile);
            if(!jsonObject.getString("orderType").equals("AddMan") && !jsonObject.getString("orderType").equals("DelMan")){
                if (!nameResult.isEmpty()) {
                    jsonObject.put("uid", Long.valueOf(nameResult));
                }
                else if (jsonObject.getString("orderType").equals("GetManDept")){
                    jsonObject.put("deptName",deptName);
                    String gmdName = nameProcess(jsonObject, mobile);
                    if (!gmdName.isEmpty()) {
                        jsonObject.put("uid", Long.valueOf(gmdName));
                    }else {
                        jsonObject.put("name","");
                    }
                    jsonObject.remove("deptName");
                }
                else {
                    if (!jsonObject.containsKey("dept")){
                        jsonObject.remove("name");
                    }
                }
            }
        }

        if (jsonObject.containsKey("dept")) {
            if (jsonObject.get("orderType").equals("AddDept") || jsonObject.get("orderType").equals("DelDept")) {
                return jsonObject;
            }
            else if (jsonObject.get("orderType").equals("GetMan")){
                jsonObject.put("dept", deptIdProcess(jsonObject.getString("dept"), mobile).isEmpty() ?
                        "" : Long.valueOf(deptIdProcess(jsonObject.getString("dept"), mobile)));
                System.out.println(jsonObject);
            }else {
                jsonObject.put("dept", deptIdProcess(deptName, mobile).isEmpty() ?
                        "" : Long.valueOf(deptIdProcess(deptName, mobile)));
            }
        }

        if (jsonObject.containsKey("mobile")){
            if (!jsonObject.getString("mobile").equals("")){
                if (jsonObject.getString("mobile").length() != 11){
                    jsonObject.put("mobile","");
                }
            }
        }

        if (jsonObject.get("orderType").equals("TimeQueryPlan") && jsonObject.containsKey("timeDetected")){
            List<String> time = (List<String>) jsonObject.get("timeDetected");
            List<ScheduleTable> data;
            if(time.isEmpty()){
                data = scheduleMapper.findByTimeAndMember(strStart(),strEnd(), nameJson(mobile));
            }else {
                data = scheduleMapper.findByTimeAndMember(time.get(0),time.get(1), nameJson(mobile));
            }
            jsonObject.put("timeQueryPlanData",scheduleProcessToHTML(data));
            jsonObject.remove("timeDetected");
        }

        if (jsonObject.get("orderType").equals("NameQueryPlan") && jsonObject.containsKey("planName")){
            List<String> time = (List<String>) jsonObject.get("timeDetected");
            List<ScheduleTable> data;
            if (time.isEmpty()){
                data = scheduleMapper.findByNameAndMember(String.valueOf(jsonObject.get("planName")),nameJson(mobile),strStart(), strEnd());
            }else {
                data = scheduleMapper.findByNameAndMember(String.valueOf(jsonObject.get("planName")),nameJson(mobile),time.get(0), time.get(1));
            }
            jsonObject.put("nameQueryPlanData",scheduleProcessToHTML(data));
            jsonObject.remove("planName");
        }

        if (jsonObject.getString("orderType").equals("LinkMsg") || jsonObject.getString("orderType").equals("MulMsg")){
            String webUrl = jsonObject.getString("url");
            Pattern pattern = Pattern.compile("^(?!https://).*");
            Matcher matcher = pattern.matcher(webUrl);
            if (matcher.matches()) {
                webUrl = "https://" + webUrl;
            }
            try {
                if (!webUrl.equals("")){
                    Document doc = Jsoup.connect(webUrl).get();
                    if (jsonObject.getString("title").equals("")){
                        jsonObject.put("title",doc.title());
                    }
                    if (jsonObject.getString("content").equals("")) {
                        Element mainContent = doc.body();
                        jsonObject.put("content",mainContent.text().substring(0,50));
                    }
                }
            } catch (Exception e) {
                jsonObject.put("title","");
                jsonObject.put("content","");
            }
        }

        if (jsonObject.get("orderType").equals("ContentQueryPlan") && jsonObject.containsKey("planContent")){
            List<String> time = (List<String>) jsonObject.get("timeDetected");
            List<ScheduleTable> data;
            if (time.isEmpty()){
                data = scheduleMapper.findByVagueContent(String.valueOf(jsonObject.get("planContent")), strStart(), strEnd());
            }else {
                data = scheduleMapper.findByVagueContent(String.valueOf(jsonObject.get("planContent")), time.get(0), time.get(1));
            }
            jsonObject.put("contentQueryPlanData",scheduleProcessToHTML(data));
            jsonObject.remove("planContent");
        }

        if (jsonObject.get("orderType").equals("FastAddNotes")){
            jsonObject.put("remindTime",getRemindTime(jsonObject));
            jsonObject.put("content",jsonObject.get("noteContent"));
            jsonObject.put("members",getMembers(jsonObject,deptName,mobile));
            jsonObject.put("isPushMail",true);
            jsonObject.remove("timeDetected");
            jsonObject.remove("noteObject");
            jsonObject.remove("noteContent");
        }

        if (jsonObject.get("orderType").equals("FastQueryNotes")){
            List<String> time = (List<String>) jsonObject.get("timeDetected");
            if (jsonObject.get("notestatus").equals("undone")){
                List<NotificationTable> data;
                if (time.isEmpty()){
                    data = notificationMapper.findByMemberActionAndTime("save",nameJson(mobile),strStart(),strEnd());
                }else {
                    data = notificationMapper.findByMemberActionAndTime("save",nameJson(mobile),time.get(0),time.get(1));
                }
                jsonObject.put("fastQueryNotesData",notificationProcessToHTML(data,"normal"));
            }
            else if (jsonObject.get("notestatus").equals("done")){
                List<NotificationTable> data;
                if (time.isEmpty()){
                    data = notificationMapper.findByMemberActionAndTime("cancel",nameJson(mobile),strStart(),strEnd());
                }else {
                    data = notificationMapper.findByMemberActionAndTime("cancel",nameJson(mobile),time.get(0),time.get(1));
                }
                jsonObject.put("fastQueryNotesData",notificationProcessToHTML(data,"normal"));
            }else if (jsonObject.get("notestatus").equals("all")){
                List<NotificationTable> data;
                if (time.isEmpty()){
                    data = notificationMapper.findByMemberActionAndTime("save",nameJson(mobile),strStart(),strEnd());
                }else {
                    data = notificationMapper.findByMemberActionAndTime("save",nameJson(mobile),time.get(0),time.get(1));
                }
                jsonObject.put("fastQueryNotesData",notificationProcessToHTML(data,"normal"));
            }
            jsonObject.remove("notestatus");
            jsonObject.remove("timeDetected");
        }
        if (jsonObject.get("orderType").equals("FastContentQueryNotes")){
            if (jsonObject.get("notestatus").equals("ask")){
                List<String> time = (List<String>) jsonObject.get("timeDetected");
                List<NotificationTable> data;
                if (time.isEmpty()){
                    data = notificationMapper.findByVagueContent(String.valueOf(jsonObject.get("planContent")),nameJson(mobile),strStart(),strEnd());
                }else {
                    data = notificationMapper.findByVagueContent(String.valueOf(jsonObject.get("planContent")),nameJson(mobile),time.get(0),time.get(1));
                }
                if (data.size() == 1){
                    for (NotificationTable notification : data){
                        jsonObject.put("noteContentStatus",notification.getAction());
                        jsonObject.put("fastContentQueryNotesData",notificationProcessToHTML(data,"normal"));
                    }
                }else{
                    List<String> statusList = new ArrayList<>();
                    for (NotificationTable notification : data){
                        String status = notification.getAction();
                        statusList.add(status);
                    }
                    jsonObject.put("noteContentStatus",statusList);
                    jsonObject.put("fastContentQueryNotesData",notificationProcessToHTML(data,"status"));
                }
                jsonObject.remove("notestatus");
                jsonObject.remove("timeDetected");
            }
        }
        return jsonObject;
    }




    // --------- 内部工具方法 -------------

    //  处理receivers字段的工具方法
    private List<Object> receiversProcess(JSONObject jsonObject, String deptName, String mobile){
        return getUid((List) jsonObject.get("object"), deptName, mobile);
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
    private String deptIdProcess(String deptName, String mobile){
        if (!deptName.equals("")){
            Object result = getDataListRedis.getDeptByName(deptName,mobile);
            if (result instanceof List){
                List<Object> deptId = (List<Object>) result;
                return String.valueOf(deptId.get(0));
            }
        }
        return "";
    }

    //  处理name字段的工具方法
    private String nameProcess(JSONObject jsonObject,String mobile){
        List<Object> result;
        if (jsonObject.getString("orderType").equals("GetMan")){
            result = (List<Object>) getDataListRedis.getPersonByDeptAndName(jsonObject.getString("name"),jsonObject.getString("dept"), mobile);
        }else {
            result = (List<Object>) getDataListRedis.getPersonByDeptAndName(jsonObject.getString("name"),jsonObject.getString("deptName"), mobile);
        }
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
    private List<String> notificationProcessToHTML(List<NotificationTable> data, String type){
        List<String> result = new ArrayList<>();
        for(NotificationTable notification : data) {
            String noteHTML = "";
            if (type == "normal"){
                noteHTML = String.format("<li>通知时间:&nbsp<strong>%s</strong><br/>发起人:&nbsp<strong>%s</strong><br/>此事项关于:&nbsp<strong>%s</strong></li><br/>",
                        formatDate(notification.getRemind_time()),
                        notification.getName(),
                        notification.getContent());
            }else if (type == "status"){
                String strStatus;
                if (notification.getAction().equals("save")){
                    strStatus = "未完成";
                }else {
                    strStatus = "已完成";
                }
                noteHTML = String.format("<li>通知时间:&nbsp<strong>%s</strong><br/>状态:&nbsp<strong>%s</strong><br/>发起人:&nbsp<strong>%s</strong><br/>此事项关于:&nbsp<strong>%s</strong></li><br/>",
                        formatDate(notification.getRemind_time()),
                        strStatus,
                        notification.getName(),
                        notification.getContent());
            }
            result.add(noteHTML);
        }
        return result;
    }

    private static String formatDate(String timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai")); // 指定时区为中国标准时间
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
        if (timeList.isEmpty()) {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai")); // 设置时区为北京时间
            Date date = dateFormat.parse(getStrNow());
            return String.valueOf(date.getTime());
        } else {
            String time = timeList.get(0);
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai")); // 设置时区为北京时间
            Date date = dateFormat.parse(time);
            return String.valueOf(date.getTime());
        }
    }

    private List<Map<String, Object>> getMembers(JSONObject jsonObject, String deptName, String mobile) {
        List<String> names = (List<String>) jsonObject.get("noteObject");
        List<Map<String, Object>> members = new ArrayList<>();
        AtomicBoolean atomicExistNull = new AtomicBoolean(false);
        List<Map<String, Object>> tempList = names.stream()
                .map(n -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("name", n);
                    List<Object> uidList = (List<Object>) getDataListRedis.getPersonByDeptAndName(n,deptName, mobile);
                    System.out.println(uidList);
                    if (!uidList.isEmpty()) {
                        map.put("uid", uidList.get(0));
                    }else {
                        map.put("uid","");
                        atomicExistNull.set(true);
                    }
                    return map;
                })
                .collect(Collectors.toList());
        members.addAll(tempList);
        if (members.isEmpty() || atomicExistNull.get()){
            return new ArrayList<>();
        }else {
            return members;
        }
    }

    private List<Object> getUid(List<String> obj,String deptName, String mobile){
        List<Object> uid = new ArrayList<>();
        List<Object> tempList = obj.stream()
                .map(n -> (List<Object>) getDataListRedis.getPersonByDeptAndName(n,deptName, mobile))
                .map(list -> !list.isEmpty() ? list.get(0) : "")
                .collect(Collectors.toList());
        uid.addAll(tempList);
        if (uid.isEmpty() || uid.contains("")){
            return new ArrayList<>();
        }else {
            return uid;
        }
    }

    private String getStrNow(){
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    private String strStart(){
        return LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    private String strEnd(){
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        LocalDateTime endOfDay = LocalDateTime.of(tomorrow, LocalTime.MIDNIGHT).minusSeconds(1);
        return endOfDay.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
