package com.content.contentprocess.entity.table;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "n_members")
public class NMembersTable {
    @TableId(type= IdType.AUTO)
    private Integer id;
    private String notificationId;
    private String uid;
    private String name;
}
