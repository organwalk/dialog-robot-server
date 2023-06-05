package com.content.contentprocess.entity.table;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "schedule")
public class ScheduleCountTable {
    private String date;
    private String count;
}
