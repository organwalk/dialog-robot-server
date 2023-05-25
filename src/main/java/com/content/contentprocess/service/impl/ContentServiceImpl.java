package com.content.contentprocess.service.impl;

import com.content.contentprocess.entity.request.OrderRequest;
import com.content.contentprocess.entity.respond.OrderRespond;
import com.content.contentprocess.service.ContentService;
import lombok.Builder;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class ContentServiceImpl implements ContentService {

    //-------------暂时不维护该类所有内容----------------

    @Override
    public OrderRespond getProcessResultByContent(OrderRequest orderRequest) {
        String content = orderRequest.getOrderContent();
        //向模型传递指令内容
        Object template = modelProcess(content);

        //json字符串处理

        return template!=null ? OrderRespond.ok(template) : OrderRespond.fail();
    }


    //调用模型
    public Object modelProcess(String content){
        //传递content

        //返回参数模板
        return TestObject.test();
    }

    //参数模板二次处理，调用redis



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
                    .object(Arrays.asList("张三", "李四"))
                    .build();
        }
    }
}
