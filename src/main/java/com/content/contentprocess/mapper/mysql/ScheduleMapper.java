package com.content.contentprocess.mapper.mysql;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.content.contentprocess.entity.table.ScheduleCountTable;
import com.content.contentprocess.entity.table.ScheduleTable;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ScheduleMapper extends BaseMapper<ScheduleTable> {

    //  更新日程数据
    @Update("UPDATE schedule SET " +
            "content = #{content}, " +
            "begintime = #{begintime}, " +
            "endtime = #{endtime}, " +
            "iswarn = #{iswarn}, " +
            "straddr = #{straddr}, " +
            "strdescrip = #{strdescrip}, " +
            "members = #{members} " +
            "WHERE schedule_id = #{scheduleId}")
    int updateSchedule(@Param("content")String content,
                        @Param("begintime")String begintime,
                        @Param("endtime")String endtime,
                        @Param("iswarn")String iswarn,
                        @Param("straddr")String straddr,
                        @Param("strdescrip")String strdescrip,
                       @Param("scheduleId")String scheduleId,
                       @Param("members")String members);

    //  根据日程id取消日程
    @Update("<script>" +
            "UPDATE schedule SET " +
            "action = #{action}"+
            "WHERE schedule_id = #{scheduleId}" +
            "</script>")
    int cancelSchedule(@Param("action")String action,
                       @Param("scheduleId")String scheduleId);

    //  根据日程id删除日程
    @Delete("DELETE FROM schedule WHERE schedule_id = #{scheduleId}")
    int deleteByScheduleId(@Param("scheduleId")String scheduleId);

    //  根据uid获取指定时间日程列表
    @Select("SELECT * FROM schedule " +
            "WHERE (JSON_SEARCH(members, 'one', #{uid}, NULL, '$[*].uid') IS NOT NULL " +
            "OR uid=#{uid}) " +
            "AND ((DATE(CONVERT_TZ(FROM_UNIXTIME(begintime / 1000), 'UTC', 'Asia/Shanghai')) <= DATE(CONVERT_TZ(FROM_UNIXTIME(#{g_time} / 1000), 'UTC', 'Asia/Shanghai'))" +
            "AND DATE(CONVERT_TZ(FROM_UNIXTIME(endtime / 1000), 'UTC', 'Asia/Shanghai')) >= DATE(CONVERT_TZ(FROM_UNIXTIME(#{g_time} / 1000), 'UTC', 'Asia/Shanghai'))))")
    @Results(id = "selectList",
            value = {
            @Result(property = "uid",column = "uid"),
            @Result(property = "name",column = "name"),
            @Result(property = "content",column = "content"),
            @Result(property = "begintime",column = "begintime"),
            @Result(property = "endtime",column = "endtime"),
            @Result(property = "iswarn",column = "iswarn"),
            @Result(property = "straddr",column = "straddr"),
            @Result(property = "scheduleId",column = "schedule_id"),
            @Result(property = "action",column = "action"),
            @Result(property = "members",column = "members")
    })
    List<ScheduleTable> getScheduleById(@Param("uid")String uid,
                                        @Param("g_time")Long gTime);

    //  获取指定日期日程数量
    @Select("SELECT DATE(CONVERT_TZ(FROM_UNIXTIME(begintime / 1000), 'UTC', 'Asia/Shanghai')) AS date, COUNT(*) AS count " +
            "FROM schedule WHERE (JSON_SEARCH(members, 'one', #{uid}, NULL, '$[*].uid') IS NOT NULL OR uid = #{uid}) " +
            "GROUP BY date")
    @Results(value = {
            @Result(property = "date",column = "date"),
            @Result(property = "count",column = "count")
    })
    List<ScheduleCountTable> getScheduleCountByDate(@Param("uid") String uid);

    //  根据特定内容获取日程id
    @Select("select schedule_id FROM schedule where uid=#{uid} and content = #{content} and " +
            "begintime=#{begintime} and strdescrip=#{strdescrip}")
    Object getScheduleId(@Param("uid")String uid,
                      @Param("content")String content,
                      @Param("begintime")String begintime,
                      @Param("strdescrip")String strdescrip);

    //  根据日程id查询日程
    @Select("SELECT * FROM schedule " +
            "WHERE schedule_id=#{schedule_id}")
    @ResultMap(value = "selectList")
    List<ScheduleTable> getScheduleBySid(@Param("schedule_id")String schedule_id);

    // 根据宽泛时间查询日程
    @Select("SELECT * FROM schedule " +
            "WHERE begintime <= UNIX_TIMESTAMP(CONVERT_TZ(#{endTime}, 'Asia/Shanghai', 'UTC')) * 1000 " +
            "AND endtime >= UNIX_TIMESTAMP(CONVERT_TZ(#{beginTime}, 'Asia/Shanghai', 'UTC')) * 1000 " +
            "AND (begintime >= UNIX_TIMESTAMP(CONVERT_TZ(#{beginTime}, 'Asia/Shanghai', 'UTC')) * 1000 " +
            "OR endtime <= UNIX_TIMESTAMP(CONVERT_TZ(#{endTime}, 'Asia/Shanghai', 'UTC')) * 1000) " +
            "AND JSON_CONTAINS(JSON_EXTRACT(members, '$'), #{memberJson})")
    @ResultMap(value = "selectList")
    List<ScheduleTable> findByTimeAndMember(@Param("beginTime") String beginTime,
                                            @Param("endTime") String endTime,
                                            @Param("memberJson") String memberJson);
    // 根据创建者及参与者查询日程
    @Select("SELECT * FROM schedule " +
            "WHERE name = #{name} " +
            "AND JSON_CONTAINS(JSON_EXTRACT(members, '$'), #{memberJson})" +
            "AND begintime <= UNIX_TIMESTAMP(CONVERT_TZ(#{endTime}, 'Asia/Shanghai', 'UTC')) * 1000 " +
            "AND endtime >= UNIX_TIMESTAMP(CONVERT_TZ(#{beginTime}, 'Asia/Shanghai', 'UTC')) * 1000 " +
            "AND (begintime >= UNIX_TIMESTAMP(CONVERT_TZ(#{beginTime}, 'Asia/Shanghai', 'UTC')) * 1000 " +
            "OR endtime <= UNIX_TIMESTAMP(CONVERT_TZ(#{endTime}, 'Asia/Shanghai', 'UTC')) * 1000)")
    @ResultMap(value = "selectList")
    List<ScheduleTable> findByNameAndMember(@Param("name") String name,
                                            @Param("memberJson") String memberJson,
                                            @Param("beginTime") String beginTime,
                                            @Param("endTime") String endTime);

    @Select("SELECT * FROM schedule " +
            "WHERE (content like CONCAT('%', #{content}, '%') or strdescrip LIKE CONCAT('%', #{content}, '%')) " +
            "AND begintime <= UNIX_TIMESTAMP(CONVERT_TZ(#{endTime}, 'Asia/Shanghai', 'UTC')) * 1000 " +
            "AND endtime >= UNIX_TIMESTAMP(CONVERT_TZ(#{beginTime}, 'Asia/Shanghai', 'UTC')) * 1000 " +
            "AND (begintime >= UNIX_TIMESTAMP(CONVERT_TZ(#{beginTime}, 'Asia/Shanghai', 'UTC')) * 1000 " +
            "OR endtime <= UNIX_TIMESTAMP(CONVERT_TZ(#{endTime}, 'Asia/Shanghai', 'UTC')) * 1000)")
    @ResultMap(value = "selectList")
    List<ScheduleTable> findByVagueContent(@Param("content") String content,
                                           @Param("beginTime") String beginTime,
                                           @Param("endTime") String endTime);



}
