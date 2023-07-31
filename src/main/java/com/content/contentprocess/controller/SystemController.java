package com.content.contentprocess.controller;

import com.content.contentprocess.entity.request.ActionRequest;
import com.content.contentprocess.entity.request.EditFeedbackRequest;
import com.content.contentprocess.entity.request.SendFeedbackRequest;
import com.content.contentprocess.entity.respond.BaseDataRespond;
import com.content.contentprocess.entity.respond.StatusRespond;
import com.content.contentprocess.entity.table.FeedbackTable;
import com.content.contentprocess.entity.table.IntentionAndEntityResult;
import com.content.contentprocess.mapper.mysql.FeedbackMapper;
import com.content.contentprocess.mapper.redis.GetDataListRedis;
import com.content.contentprocess.service.SystemService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/api_c")
@AllArgsConstructor
public class SystemController {
    private SystemService systemService;
    private GetDataListRedis redis;
    private FeedbackMapper feedbackMapper;
    @PostMapping("/system/redis")
    public StatusRespond destroy(@RequestBody ActionRequest actionRequest){
        return systemService.destroyResources(actionRequest);
    }

    @GetMapping("/system/server")
    public String server(){
        return "The Xeno-Server is running";
    }

    @PostMapping("/system/feedback")
    public BaseDataRespond getFeedback(@RequestBody ActionRequest actionRequest){
        List<IntentionAndEntityResult> result = redis.getIntentionAndEntity(actionRequest.getMobile());
        return !result.isEmpty() ? BaseDataRespond.ok(result) : BaseDataRespond.dataNull();
    }
    @PutMapping("/system/feedback/send")
    public BaseDataRespond sendFeedback(@RequestBody SendFeedbackRequest sendFeedbackRequest){
        FeedbackTable feedbackTable = FeedbackTable.fromSendFeedbackRequest(sendFeedbackRequest);
        boolean result = feedbackMapper.insert(feedbackTable) > 0;
        return result ? BaseDataRespond.ok(feedbackTable.getId()) : BaseDataRespond.fail();
    }
    @GetMapping("/system/feedback/get/{id}")
    public BaseDataRespond getFeedback(@PathVariable int id){
        List<FeedbackTable> feedback = feedbackMapper.getFeedbackById(id);
        return !feedback.isEmpty() ? BaseDataRespond.ok(feedback) : BaseDataRespond.dataNull();
    }

    @PutMapping("/system/feedback/edit")
    public StatusRespond updateFeedback(@RequestBody EditFeedbackRequest req){
        int result = feedbackMapper.updateFeedbackById(req.getNewIntention(), req.getNewEntity(), req.getId());
        return result > 0 ? StatusRespond.ok() : StatusRespond.fail();
    }
}
