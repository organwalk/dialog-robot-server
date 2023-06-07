package com.content.contentprocess.service;

import com.content.contentprocess.entity.request.SaveNotificationRequest;
import com.content.contentprocess.entity.request.UpdataNotificationRequest;
import com.content.contentprocess.entity.respond.NotificationListRespond;
import com.content.contentprocess.entity.respond.StatusRespond;

public interface NotificationService {
    StatusRespond saveNotificationData(SaveNotificationRequest saveNotificationRequest);
    StatusRespond updataNotificationData(UpdataNotificationRequest updataNotificationRequest, String noticeId);
    StatusRespond deleteNotificationData(String noticeId);
    NotificationListRespond getNotificationDataList(String uid,String gtime);
}
