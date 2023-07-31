package com.content.contentprocess.controller;

import com.content.contentprocess.entity.request.OrderRequest;
import com.content.contentprocess.entity.respond.OrderRespond;
import com.content.contentprocess.service.ContentService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api_c")
@AllArgsConstructor
public class OrderController {
    private final ContentService contentService;

    @PostMapping("/order/{mobile}")
    public OrderRespond getProcessResultByContent(@PathVariable String mobile,@RequestBody OrderRequest orderRequest){
        return contentService.getProcessResultByContent(orderRequest,mobile);
    }
}
