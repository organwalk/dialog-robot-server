package com.content.contentprocess.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.content.contentprocess.entity.request.OrderRequest;
import com.content.contentprocess.entity.respond.OrderRespond;
import com.content.contentprocess.mapper.redis.GetDataListRedis;
import com.content.contentprocess.service.ContentService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@AllArgsConstructor

public class ContentServiceImpl implements ContentService {
    private final GetDataListRedis getDataListRedis;
    private final String[] messagePush ={"测试消息","AppMsg","TxtMsg","PicMsg","LinkMsg","VocMsg","MulMsg","IDMsg","SysMsg"};
    private final String[] deptAndPersonManagement = {"AddMan","DelMan","ModMan","GetManDept","GetMan","AddDept","DelDept"};
    @Override
    public OrderRespond getProcessResultByContent(OrderRequest orderRequest,String mobile) {
        String content = orderRequest.getOrderContent();
        //向模型传递指令内容
        Object template = dataSecondaryProcess(modelProcess(content),mobile);
        System.out.println(template);
        return template!=null ? OrderRespond.ok(template) : OrderRespond.fail();
    }


    //调用模型
    public String modelProcess(String content){

        //传递content
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


    //参数模板二次处理，调用redis
    public Object dataSecondaryProcess(String template, String mobile) {
        //该方法接收的是一个JSON字符串,此处应转化
        JSONObject jsonObject = JSON.parseObject(template);
        //消息推送
        String orderType = (String) jsonObject.get("orderType");
        boolean flag = true;
        //人员管理、部门管理
        if (flag){
            for (String pm:
                    deptAndPersonManagement) {
                if(pm.equals(orderType)){
                    if("AddMan".equals(orderType)||"DelDept".equals(orderType)){
                       String deptName = (String) jsonObject.get("dept");
                       List<String> deptInfo = (List<String>) getDataListRedis.getDeptByName(deptName,mobile);
                       String deptId = null;
                       if(!deptInfo.isEmpty()) {
                            deptId = deptInfo.get(0);
                       }
                       jsonObject.put("dept",deptId);
                    }else if ("DelMan".equals(orderType)||"GetMan".equals(orderType)){
                        String deptName = (String) jsonObject.get("dept");
                        List<String> deptInfo = (List<String>) getDataListRedis.getDeptByName(deptName,mobile);
                        String deptId = null;
                        if(!deptInfo.isEmpty()) {
                            deptId = deptInfo.get(0);
                        }
                        jsonObject.put("dept",deptId);
                        String personName = (String) jsonObject.get("name");
                        List<String> personInfo = (List<String>) getDataListRedis.getPersonByName(personName,mobile);
                        String uId = null;
                        if(!personInfo.isEmpty()){
                            uId = personInfo.get(0);
                        }
                        jsonObject.put("name",uId);
                    } else if ("ModMan".equals(orderType)||"AddDept".equals(orderType)) {
                        break;
                    } else if ("GetManDept".equals(orderType)) {
                        String personName = (String) jsonObject.get("name");
                        List<String> personInfo = (List<String>) getDataListRedis.getPersonByName(personName,mobile);
                        String uId = null;
                        if(!personInfo.isEmpty()){
                            uId = personInfo.get(0);
                        }
                        jsonObject.put("name",uId);
                    }
                }
            }
        }
        return jsonObject;
    }
}
