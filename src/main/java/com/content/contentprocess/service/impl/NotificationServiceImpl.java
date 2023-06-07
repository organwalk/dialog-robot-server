package com.content.contentprocess.service.impl;

import com.content.contentprocess.entity.request.SaveNotificationRequest;
import com.content.contentprocess.entity.request.UpdataNotificationRequest;
import com.content.contentprocess.entity.respond.NotificationListRespond;
import com.content.contentprocess.entity.respond.StatusRespond;
import com.content.contentprocess.entity.table.NotificationTable;
import com.content.contentprocess.mapper.mysql.NotificationMapper;
import com.content.contentprocess.service.NotificationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationMapper notificationMapper;

    @Override
    public StatusRespond saveNotificationData(SaveNotificationRequest saveNotificationRequest) {
        if (saveNotificationRequest == null){
            return  StatusRespond.dataNull();
        }
        NotificationTable notificationTable = new NotificationTable();
        SaveNotificationRequest.NoticeData noticeData = saveNotificationRequest.getData();
        notificationTable.setUid(String.valueOf(saveNotificationRequest.getUid()));
        notificationTable.setName(saveNotificationRequest.getName());
        notificationTable.setNotice_id(String.valueOf(saveNotificationRequest.getNoticeId()));
        notificationTable.setContent(String.valueOf(noticeData.getContent()));
        notificationTable.setRemind_time(String.valueOf(noticeData.getRemindTime()));
        notificationTable.setIs_push_mail(String.valueOf(noticeData.isPushMail()));
        notificationTable.setAction(String.valueOf(saveNotificationRequest.getAction()));
        notificationTable.setMembers(noticeData.getMembers());
        return notificationMapper.insert(notificationTable) > 0 ? StatusRespond.ok() : StatusRespond.fail();
    }

    //更新事项数据
    @Override
    public StatusRespond updataNotificationData(UpdataNotificationRequest updataNotificationRequest, String noticeId) {
        if(updataNotificationRequest == null){
            return StatusRespond.dataNull();
        }
        if (updataNotificationRequest.getAction().equals("updata")){
            UpdataNotificationRequest.NoticeData data = updataNotificationRequest.getData();
            String content = data.getContent();
            String remindTime = String.valueOf(data.getRemindTime());
            String isPushMail = String.valueOf(data.isPushMail());
            String members = data.getMembers();
            return notificationMapper.updateNotification(content,remindTime,isPushMail,members,noticeId) > 0 ?
                    StatusRespond.ok() : StatusRespond.fail();
        }else {
            return StatusRespond.fail();
        }

    }

    @Override
    public StatusRespond deleteNotificationData(String noticeId) {

        return notificationMapper.deleteByNoticeId(noticeId) > 0 ?
                StatusRespond.ok() : StatusRespond.fail();
    }

    @Override
    public NotificationListRespond getNotificationDataList(String uid,String gtime) {
        if (uid.isEmpty()) {
            return NotificationListRespond.fail();
        }
        return notificationMapper.getNotificationById(uid, Long.valueOf(gtime)) != null ?
                NotificationListRespond.ok(notificationMapper.getNotificationById(uid, Long.valueOf(gtime))) : NotificationListRespond.dataNull();

    }
}
