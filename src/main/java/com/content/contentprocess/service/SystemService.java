package com.content.contentprocess.service;

import com.content.contentprocess.entity.request.ActionRequest;
import com.content.contentprocess.entity.respond.StatusRespond;

public interface SystemService {
    StatusRespond destroyResources(ActionRequest actionRequest);
}
