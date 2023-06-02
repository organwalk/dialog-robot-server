package com.content.contentprocess.entity.table;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.List;

@Data
@TableName(value = "notification")
public class NotificationTable {
    @TableId(type= IdType.AUTO)
    private Integer id;
    private String uid;
    private String notice_id;
    private String content;
    private String remind_time;
    private String is_push_mail;
    private String action;
    private List<NMembersTable> members;
}
