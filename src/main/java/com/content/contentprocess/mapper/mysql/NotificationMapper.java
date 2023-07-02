package com.content.contentprocess.mapper.mysql;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.content.contentprocess.entity.table.NotificationCountTable;
import com.content.contentprocess.entity.table.NotificationTable;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface NotificationMapper extends BaseMapper<NotificationTable> {

    //  根据事项ID更新事项
    @Update("UPDATE notification SET " +
            "content = #{content}, " +
            "remind_time = #{remind_time}, " +
            "is_push_mail = #{is_push_mail}, " +
            "members = #{members} " +
            "WHERE notice_id = #{notice_id}")
    int updateNotification(@Param("content")String content,
                       @Param("remind_time")String remind_time,
                       @Param("is_push_mail")String is_push_mail,
                       @Param("members")String members,
                       @Param("notice_id")String notice_id);

    //  根据事项ID取消事项
    @Update("<script>" +
            "UPDATE notification SET " +
            "action = #{action}"+
            "WHERE notice_id = #{notice_id}" +
            "</script>")
    int cancelNotification(@Param("action")String action,
                       @Param("notice_id")String notice_id);

    //  根据事项ID删除事项
    @Delete("DELETE FROM notification WHERE notice_id = #{notice_id}")
    int deleteByNoticeId(@Param("notice_id")String notice_id);

    //  根据用户id获取指定时间事项列表
    @Select("SELECT * from notification where uid = #{uid} " +
            "AND DATE(CONVERT_TZ(FROM_UNIXTIME(remind_time / 1000), 'UTC', 'Asia/Shanghai')) = " +
            "DATE(CONVERT_TZ(FROM_UNIXTIME(#{remind_time} / 1000), 'UTC', 'Asia/Shanghai'));")
    @Results(id = "selectList",
            value = {
            @Result(property = "uid",column = "uid"),
            @Result(property = "notice_id",column = "notice_id"),
            @Result(property = "content",column = "content"),
            @Result(property = "remind_time",column = "remind_time"),
            @Result(property = "is_push_mail",column = "is_push_mail"),
            @Result(property = "members",column = "members"),
            @Result(property = "action",column = "action")

    })
    List<NotificationTable> getNotificationById(@Param("uid")String uid,
                                                @Param("remind_time")Long remind_time);

    //  获取指定日期日程数量
    @Select("SELECT DATE(CONVERT_TZ(FROM_UNIXTIME(remind_time / 1000), 'UTC', 'Asia/Shanghai')) AS date, COUNT(*) AS count " +
            "FROM notification WHERE (JSON_SEARCH(members, 'one', #{uid}, NULL, '$[*].uid') IS NOT NULL OR uid = #{uid}) " +
            "GROUP BY date")
    @Results(value = {
            @Result(property = "date",column = "date"),
            @Result(property = "count",column = "count")
    })
    List<NotificationCountTable> getNotificationCountByDate(@Param("uid") String uid);

    //  根据事项id获取事项信息
    @Select("SELECT * FROM notification " +
            "WHERE notice_id=#{notice_id}")
    @ResultMap(value = "selectList")
    List<NotificationTable> getNotificationByNid(@Param("notice_id")String notice_id);

    @Select("SELECT * FROM notification where action=#{action} AND JSON_CONTAINS(JSON_EXTRACT(members, '$'), #{memberJson})  " +
            "AND remind_time >= UNIX_TIMESTAMP(CONVERT_TZ(#{begintime}, 'Asia/Shanghai', 'UTC')) * 1000 "+
            "AND remind_time <= UNIX_TIMESTAMP(CONVERT_TZ(#{endtime}, 'Asia/Shanghai', 'UTC')) * 1000")
    @ResultMap(value = "selectList")
    List<NotificationTable> findByMemberActionAndTime(@Param("action") String action, @Param("memberJson") String memberJson,
                                                      @Param("begintime") String begintime, @Param("endtime") String endtime);

}
