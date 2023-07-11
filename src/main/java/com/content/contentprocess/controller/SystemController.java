package com.content.contentprocess.controller;

import com.content.contentprocess.entity.request.ActionRequest;
import com.content.contentprocess.entity.respond.StatusRespond;
import com.content.contentprocess.service.SystemService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class SystemController {
    private SystemService systemService;
    @PostMapping("/system/redis")
    public StatusRespond destroy(@RequestBody ActionRequest actionRequest){
        return systemService.destroyResources(actionRequest);
    }

    @GetMapping("/system/server")
    public String server(){
        return "The Xeno-Server is running";
    }
}
