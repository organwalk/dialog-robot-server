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
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@AllArgsConstructor

public class ContentServiceImpl implements ContentService {

    //-------------暂时不维护该类所有内容----------------
    private final GetDataListRedis getDataListRedis;
    private final String[] messagePush ={"测试消息","AppMsg=>应用消息","TxtMsg=>文本消息","PicMsg=>图片消息","LinkMsg链接消息","VocMsg语音消息","MulMsg图文消息","IDMsg名片消息","SysMsg系统消息"};
    private final String[] deptAndPersonManagement = {"AddMan新增人员","DelMan删除人员","ModMan修改人员","GetManDept获取用户所属部门","GetMan获取用户详细信息","AddDept新增部门","DelDept删除部门"};
    @Override
    public OrderRespond getProcessResultByContent(OrderRequest orderRequest,String mobile) {
        String content = orderRequest.getOrderContent();
        //向模型传递指令内容
        Object template = dataSecondaryProcess(modelProcess(content),mobile);
        return template!=null ? OrderRespond.ok(template) : OrderRespond.fail();
    }


    //调用模型
    public Object modelProcess(String content){
        //传递content

        //返回参数模板
        return TestObject.test();
    }

    //参数模板二次处理，调用redis
    public Object dataSecondaryProcess(Object o, String mobile) {
        String json = JSON.toJSONString(o);
        JSONObject jsonObject = JSON.parseObject(json);

        //消息推送
        String orderType = (String) jsonObject.get("orderType");
        boolean flag = true;
        for (String mp:
                messagePush) {
            if(mp.equals(orderType)){
                flag = false;
                Object object = jsonObject.get("object");
                if(object instanceof List<?>) {
                    List<String> names = (List<String>) object;
                    List<String> uids = new ArrayList<>();
                    for (String name :
                            names) {
                        List<String> personInfo = (List<String>) getDataListRedis.getPersonByName(name, mobile);
                        if (!personInfo.isEmpty()){
                            uids.add(personInfo.get(0));
                        }
                    }
                    jsonObject.put("object", uids);
                }else if(object instanceof String){
                    String name = (String) object;
                    List<String> deptInfo = (List<String>) getDataListRedis.getDeptByName(name,mobile);
                    String deptId = null;
                    if(!deptInfo.isEmpty()) {
                        deptId = deptInfo.get(0);
                    }
                    jsonObject.put("object",deptId);
                }
            }
        }

        //人员管理、部门管理
        if (flag){
            for (String pm:
                    deptAndPersonManagement) {
                if(pm.equals(orderType)){
                    if("AddMan新增人员".equals(orderType)||"DelDept删除部门".equals(orderType)){
                       String deptName = (String) jsonObject.get("dept");
                       List<String> deptInfo = (List<String>) getDataListRedis.getDeptByName(deptName,mobile);
                       String deptId = null;
                       if(!deptInfo.isEmpty()) {
                            deptId = deptInfo.get(0);
                       }
                       jsonObject.put("dept",deptId);
                    }else if ("DelMan删除人员".equals(orderType)||"GetMan获取用户详细信息".equals(orderType)){
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
                    } else if ("ModMan修改人员".equals(orderType)||"AddDept新增部门".equals(orderType)) {
                        break;
                    } else if ("GetManDept获取用户所属部门".equals(orderType)) {
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


    //测试用输出，待模型完成即可删除
    @Data
    @Builder
    static class TestObject {
        private String orderType;
        private String title;
        private String content;
        private List<String> object;

        static TestObject test(){
            return TestObject.builder()
                    .orderType("测试消息")
                    .title("测试标题")
                    .content("测试文字内容")
                    .object(Arrays.asList("张三","李四"))
                    .build();
        }
    }
}
