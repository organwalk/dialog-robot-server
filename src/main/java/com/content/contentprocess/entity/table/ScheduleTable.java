package com.content.contentprocess.entity.table;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.apache.ibatis.annotations.Many;

import java.util.List;

@Data
@TableName(value = "schedule")
public class ScheduleTable {

    @TableId(type= IdType.AUTO)
    private Integer id;
    private String uid;
    private String name;
    private String content;
    private String begintime;
    private String endtime;
    private String iswarn;
    private String straddr;
    private String strdescrip;
    private String scheduleId;
    private String members;
    private String action;
}
