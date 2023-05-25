package com.content.contentprocess.entity.table;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "s_members")
public class SMembersTable {
    @TableId(type= IdType.AUTO)
    private Integer id;
    private String scheduleId;
    private String uid;
    private String name;
}
