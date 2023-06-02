package com.content.contentprocess.service.impl;

import com.content.contentprocess.entity.request.SaveNotificationRequest;
import com.content.contentprocess.entity.request.UpdataNotificationRequest;
import com.content.contentprocess.entity.respond.NotificationListRespond;
import com.content.contentprocess.entity.respond.StatusRespond;
import com.content.contentprocess.entity.table.NMembersTable;
import com.content.contentprocess.entity.table.NotificationTable;
import com.content.contentprocess.mapper.mysql.NMembersMapper;
import com.content.contentprocess.mapper.mysql.NotificationMapper;
import com.content.contentprocess.service.NotificationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationMapper notificationMapper;
    private final NMembersMapper nMembersMapper;

    @Override
    public StatusRespond saveNotificationData(SaveNotificationRequest saveNotificationRequest) {
        if (saveNotificationRequest == null){
            return  StatusRespond.dataNull();
        }
        NotificationTable notificationTable = new NotificationTable();
        SaveNotificationRequest.NoticeData noticeData = saveNotificationRequest.getData();
        System.out.println("noticeData = " + noticeData);
        notificationTable.setUid(String.valueOf(saveNotificationRequest.getUid()));
        notificationTable.setNotice_id(String.valueOf(saveNotificationRequest.getNoticeId()));
        notificationTable.setContent(String.valueOf(noticeData.getContent()));
        notificationTable.setRemind_time(String.valueOf(noticeData.getRemindTime()));
        notificationTable.setIs_push_mail(String.valueOf(noticeData.isPushMail()));
        notificationTable.setAction(String.valueOf(saveNotificationRequest.getAction()));
        System.out.println("notificationTable = " + notificationTable);
        if (notificationMapper.insert(notificationTable)>0) {
            List<SaveNotificationRequest.Member> members = saveNotificationRequest.getData().getMembers();
            System.out.println("members = " + members);
            int memberRow = 0;
            for (SaveNotificationRequest.Member member:
                 members) {
                NMembersTable nMembersTable = new NMembersTable();
                nMembersTable.setNotificationId(notificationTable.getNotice_id());
                nMembersTable.setUid(member.getUid());
                nMembersTable.setName(member.getName());
                memberRow = nMembersMapper.insert(nMembersTable);
            }
            return  memberRow > 0 ? StatusRespond.ok() : StatusRespond.fail();
        }else {
            return StatusRespond.fail();
        }
    }

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
            if(notificationMapper.updateNotification(content,remindTime,isPushMail,noticeId) > 0){
                List<UpdataNotificationRequest.Member> members = updataNotificationRequest.getData().getMembers();
                int memberRow = 0;
                for (UpdataNotificationRequest.Member member:
                        members) {
                    memberRow = nMembersMapper.updateNMembers(member.getNewuid(),member.getNewname(),noticeId, member.getOlduid());
                }
                return  memberRow > 0 ? StatusRespond.ok() : StatusRespond.fail();
            }else {
                return StatusRespond.fail();
            }
        }


        if(updataNotificationRequest.getAction().equals("cancel")){
            return notificationMapper.cancelNotification(updataNotificationRequest.getAction(),noticeId) > 0 ?
                    StatusRespond.ok() : StatusRespond.fail();
        }else {
            return StatusRespond.fail();
        }

    }

    @Override
    public StatusRespond deleteNotificationData(String noticeId) {

        return notificationMapper.deleteBynoticeId(noticeId) > 0 && nMembersMapper.deleteBynoticeId(noticeId) > 0 ?
                StatusRespond.ok() : StatusRespond.fail();
    }

    @Override
    public NotificationListRespond getNotificationDataList(String uid) {
        if (uid.isEmpty()) {
            return NotificationListRespond.fail();
        }
        System.out.println("notificationMapper.getNotificationById(uid) = " + notificationMapper.getNotificationById(uid));
        return notificationMapper.getNotificationById(uid) != null ?
                NotificationListRespond.ok(notificationMapper.getNotificationById(uid)) : NotificationListRespond.dataNull();

    }
}
