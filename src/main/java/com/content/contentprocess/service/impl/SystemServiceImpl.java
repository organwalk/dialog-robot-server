package com.content.contentprocess.service.impl;

import com.content.contentprocess.entity.request.ActionRequest;
import com.content.contentprocess.entity.respond.StatusRespond;
import com.content.contentprocess.mapper.redis.GetDataListRedis;
import com.content.contentprocess.service.SystemService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SystemServiceImpl implements SystemService {

    private GetDataListRedis getDataListRedis;
    @Override
    public StatusRespond destroyResources(ActionRequest actionRequest) {
        if (actionRequest==null){
            return StatusRespond.dataNull();
        }
        return getDataListRedis.deleteUserByMobile(actionRequest.getMobile()) ?
                StatusRespond.ok() : StatusRespond.fail();
    }
}
