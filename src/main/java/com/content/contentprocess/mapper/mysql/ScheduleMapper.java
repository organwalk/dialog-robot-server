package com.content.contentprocess.mapper.mysql;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.content.contentprocess.entity.table.ScheduleTable;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ScheduleMapper extends BaseMapper<ScheduleTable> {
    @Update("<script>" +
            "UPDATE schedule SET " +
            "<if test='content != null'>content = #{content},</if>" +
            "<if test='begintime != null'>begintime = #{begintime},</if>" +
            "<if test='endtime != null'>endtime = #{endtime},</if>" +
            "<if test='iswarn != null'>iswarn = #{iswarn},</if>" +
            "<if test='straddr != null'>straddr = #{straddr},</if>" +
            "<if test='strdescrip != null'>strdescrip = #{strdescrip}</if>" +
            "WHERE schedule_id = #{scheduleId}" +
            "</script>")
    int updateSchedule(@Param("content")String content,
                        @Param("begintime")String begintime,
                        @Param("endtime")String endtime,
                        @Param("iswarn")String iswarn,
                        @Param("straddr")String straddr,
                        @Param("strdescrip")String strdescrip,
                       @Param("scheduleId")String scheduleId);

    @Update("<script>" +
            "UPDATE schedule SET " +
            "action = #{action}"+
            "WHERE schedule_id = #{scheduleId}" +
            "</script>")
    int cancelSchedule(@Param("action")String action,
                       @Param("scheduleId")String scheduleId);
    @Delete("DELETE FROM schedule WHERE schedule_id = #{scheduleId}")
    int deleteByScheduleId(@Param("scheduleId")String scheduleId);

    @Select("SELECT * from schedule where uid = #{uid}")
    @Results(value = {
            @Result(property = "uid",column = "uid"),
            @Result(property = "name",column = "name"),
            @Result(property = "content",column = "content"),
            @Result(property = "begintime",column = "begintime"),
            @Result(property = "endtime",column = "endtime"),
            @Result(property = "iswarn",column = "iswarn"),
            @Result(property = "straddr",column = "straddr"),
            @Result(property = "scheduleId",column = "schedule_id"),
            @Result(property = "action",column = "action"),
            @Result(property = "members", column = "schedule_id",
                    many = @Many(select = "com.content.contentprocess.mapper.mysql.SMembersMapper.getSMembersByScheduleId"))
    })
    List<ScheduleTable> getScheduleById(String uid);

}
