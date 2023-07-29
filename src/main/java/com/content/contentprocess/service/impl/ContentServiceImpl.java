package com.content.contentprocess.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.content.contentprocess.entity.request.OrderRequest;
import com.content.contentprocess.entity.resource.Image;
import com.content.contentprocess.entity.resource.Voice;
import com.content.contentprocess.entity.respond.OrderRespond;
import com.content.contentprocess.mapper.redis.SaveDataListRedis;
import com.content.contentprocess.service.ContentService;
import com.content.contentprocess.utils.ContentProcess;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
@AllArgsConstructor
public class ContentServiceImpl implements ContentService {
    private final ContentProcess content;
    private final SaveDataListRedis redis;

    @Override
    public OrderRespond getProcessResultByContent(OrderRequest orderRequest,String mobile) {
        try {
            JSONObject jsonObject = JSON.parseObject(orderRequest.getOrderContent());
            if (jsonObject.containsKey("voiceUrl")) {
                return OrderRespond.ok(Voice.set(jsonObject));
            }
            else if (jsonObject.containsKey("imageUrl")) {
                return OrderRespond.ok(Image.set(jsonObject.getString("imageUrl")));
            }
        } catch (JSONException e) {
            // 说明content不是JSON格式,进入普通字符串处理逻辑
            //向模型传递指令内容，并获取参数模板
            Object template = dataSecondaryProcess(modelProcess(orderRequest.getOrderContent()),mobile, orderRequest.getOrderContent());
            return template!=null ? OrderRespond.ok(template) : OrderRespond.fail();
        }
        return null;
    }


    //  定义语言处理模型方法
    @SneakyThrows
    public String modelProcess(String content){
        CommandLine commandLine = new CommandLine("python");
//        commandLine.addArgument("src/main/resources/xeno/XenoPyScript.py");
        commandLine.addArgument("/root/python/XenoPyScript.py");
        commandLine.addArgument(content);
        DefaultExecutor executor = new DefaultExecutor();
        executor.setWorkingDirectory(new File("."));
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        executor.setStreamHandler(new PumpStreamHandler(stream, stream));
        executor.execute(commandLine);
        return stream.toString("UTF-8");
    }

    //  参数模板二次处理，调用redis
    public Object dataSecondaryProcess(String template, String mobile, String inputContent) {
        //打印参数模板日志
        JSONObject jsonTemplate = JSON.parseObject(template);
        System.out.println(jsonTemplate);
        String orderType = jsonTemplate.getString("orderType");
        jsonTemplate.remove("orderType");
        redis.saveFeedback(orderType, jsonTemplate.toJSONString(), inputContent, mobile);
        return content.jsonProcess(JSON.parseObject(template),mobile);
    }
}
