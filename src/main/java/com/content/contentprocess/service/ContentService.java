package com.content.contentprocess.service;

import com.content.contentprocess.entity.request.OrderRequest;
import com.content.contentprocess.entity.respond.OrderRespond;

public interface ContentService {
    OrderRespond getProcessResultByContent(OrderRequest orderRequest,String mobile);
}
