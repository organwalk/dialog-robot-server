package com.content.contentprocess.mapper.mysql;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.content.contentprocess.entity.table.NotificationTable;
import com.content.contentprocess.entity.table.ScheduleTable;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface NotificationMapper extends BaseMapper<NotificationTable> {
    @Update("<script>" +
            "UPDATE notification SET " +
            "<if test='content != null'>content = #{content},</if>" +
            "<if test='remindTime != null'>remind_time = #{remindTime},</if>" +
            "<if test='is_push_mail != null'>is_push_mail = #{is_push_mail}</if>" +
            "WHERE notice_id = #{notice_id}" +
            "</script>")
    int updateNotification(@Param("content")String content,
                       @Param("remindTime")String remindTime,
                       @Param("is_push_mail")String is_push_mail,
                       @Param("notice_id")String notice_id);

    @Update("<script>" +
            "UPDATE notification SET " +
            "action = #{action}"+
            "WHERE notice_id = #{notice_id}" +
            "</script>")
    int cancelNotification(@Param("action")String action,
                       @Param("notice_id")String notice_id);

    @Delete("DELETE FROM notification WHERE notice_id = #{notice_id}")
    int deleteBynoticeId(@Param("notice_id")String notice_id);

    @Select("SELECT * from notification where uid = #{uid}")
    @Results(value = {
            @Result(property = "uid",column = "uid"),
            @Result(property = "notice_id",column = "notice_id"),
            @Result(property = "content",column = "content"),
            @Result(property = "remind_time",column = "remind_time"),
            @Result(property = "is_push_mail",column = "is_push_mail"),
            @Result(property = "action",column = "action"),
            @Result(property = "members", column = "notice_id",
                    many = @Many(select = "com.content.contentprocess.mapper.mysql.NMembersMapper.getNMembersByNoticeId"))
    })
    List<NotificationTable> getNotificationById(String uid);
}
