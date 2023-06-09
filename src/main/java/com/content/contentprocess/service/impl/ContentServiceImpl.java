package com.content.contentprocess.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.content.contentprocess.entity.request.OrderRequest;
import com.content.contentprocess.entity.respond.OrderRespond;
import com.content.contentprocess.mapper.redis.GetDataListRedis;
import com.content.contentprocess.service.ContentService;
import lombok.AllArgsConstructor;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor

public class ContentServiceImpl implements ContentService {
    private final GetDataListRedis getDataListRedis;
    @Override
    public OrderRespond getProcessResultByContent(OrderRequest orderRequest,String mobile) {
        //  提取请求中的指令
        String content = orderRequest.getOrderContent();
        //向模型传递指令内容，并获取参数模板
        Object template = dataSecondaryProcess(modelProcess(content),mobile);
        return template!=null ? OrderRespond.ok(template) : OrderRespond.fail();
    }


    //  定义语言处理模型方法
    public String modelProcess(String content){

        //  唤起模型并传递指令
        CommandLine commandLine = new CommandLine("python");
        commandLine.addArgument("src/main/resources/xeno/XenoPyScript.py");
        commandLine.addArgument(content);

        DefaultExecutor executor = new DefaultExecutor();
        executor.setWorkingDirectory(new File("."));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PumpStreamHandler streamHandler = new PumpStreamHandler(baos, baos);
        executor.setStreamHandler(streamHandler);

        String newTemplate = null;
        try {
            int exitValue = executor.execute(commandLine);
            newTemplate =new String(baos.toByteArray(),"GBK");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //返回参数模板
        return newTemplate;
    }


    //  参数模板二次处理，调用redis
    public Object dataSecondaryProcess(String template, String mobile) {
        //该方法接收的是一个JSON字符串,此处应转化
        JSONObject jsonObject = JSON.parseObject(template);
        //打印参数模板日志
        System.out.println(jsonObject);
        //  如果该参数模板存在 object 字段的值
        if (jsonObject.get("object") != null){
            //  首先判断该字段是否属于人名
            if (!receiversProcess(jsonObject,mobile).isEmpty()){
                jsonObject.put("receivers",receiversProcess(jsonObject,mobile));
                jsonObject.remove("object");
            }
            //  若不属于人名，则可能属于群名
            else if (groupIdProcess(jsonObject,mobile) != ""){
                jsonObject.put("groupId",Long.valueOf(groupIdProcess(jsonObject, mobile)));
                jsonObject.remove("object");
            }else {
                //  如果都不属于，则表明区分失败或者当前主体不可寻，应置空object
                jsonObject.put("object", "");
            }
        }

        if (jsonObject.get("image") != null){
            jsonObject.put("image","");
        }
        if (jsonObject.get("url") != null){
            jsonObject.put("url","");
        }
        if (jsonObject.get("dept") != null){
            if (jsonObject.get("orderType").equals("AddDept")){
                return jsonObject;
            }
            else if (jsonObject.get("orderType").equals("DelDept")){
                return jsonObject;
            }
            else if (deptIdProcess(jsonObject,mobile) != ""){
                jsonObject.put("dept",Long.valueOf(deptIdProcess(jsonObject,mobile)));
            }else {
                jsonObject.put("dept","");
            }
        }
        if (jsonObject.get("name") != null){
            if (nameProcess(jsonObject,mobile) != ""){
                jsonObject.put("uid",Long.valueOf(nameProcess(jsonObject, mobile)));
                if (! jsonObject.get("orderType").equals("GetMan") && !jsonObject.get("orderType").equals("GetManDept")){
                    jsonObject.remove("name");
                }
            }else {
                jsonObject.put("name","");
            }
        }

        return jsonObject;
    }

    //  处理receivers字段的工具方法
    public List<Object> receiversProcess(JSONObject jsonObject,String mobile){
        List<Object> uid = new ArrayList<>();
        List<String> obj = (List) jsonObject.get("object");
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

    //  处理groupId字段的工具方法
    public String groupIdProcess(JSONObject jsonObject,String mobile){
        List<String> obj = (List) jsonObject.get("object");
        for (String dept : obj){
            List<Object> checkGid = (List<Object>) getDataListRedis.getGroupByName(dept,mobile);
            if (!checkGid.isEmpty()){
                List<Object> tempList = obj.stream()
                        .map(d -> (List<Object>) getDataListRedis.getGroupByName(d,mobile))
                        .map(list -> list.get(1))
                        .collect(Collectors.toList());
                String groupId = String.valueOf(tempList.get(0));
                return groupId;
            }
        }
        return "";
    }

    //  处理deptId字段的工具方法
    public String deptIdProcess(JSONObject jsonObject,String mobile){
        String dept = String.valueOf(jsonObject.get("dept"));
        Object result = getDataListRedis.getDeptByName(dept,mobile);
        if (result instanceof List){
            List<Object> deptId = (List<Object>) result;
            return String.valueOf(deptId.get(0));
        }
        return "";
    }

    //  处理name字段的工具方法
    public String nameProcess(JSONObject jsonObject,String mobile){
        String name = String.valueOf(jsonObject.get("name"));
        List<Object> result = (List<Object>) getDataListRedis.getPersonByName(name,mobile);
        if (!result.isEmpty()){
            List<Object> uid = result;
            System.out.println(uid);
            return String.valueOf(uid.get(0));
        }
        return "";
    }
}
