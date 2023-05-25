package com.content.contentprocess.mapper.mysql;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.content.contentprocess.entity.table.SMembersTable;
import org.apache.ibatis.annotations.*;

import java.util.List;


@Mapper
public interface SMembersMapper extends BaseMapper<SMembersTable> {
    @Update("<script>" +
            "UPDATE s_members SET " +
            "<if test='uid != null'>uid = #{uid},</if>" +
            "<if test='name != null'>name = #{name}</if>" +
            "WHERE schedule_id = #{scheduleId} AND uid = #{olduid}" +
            "</script>")
    int updateSMembers(@Param("uid")String uid,
                       @Param("name")String name,
                       @Param("scheduleId")String scheduleId,
                       @Param("olduid")String olduid);

    @Delete("DELETE FROM s_members WHERE schedule_id = #{scheduleId}")
    int deleteByScheduleId(@Param("scheduleId")String scheduleId);

    @Select("SELECT * FROM s_members WHERE schedule_id = #{scheduleId}")
    List<SMembersTable> getSMembersByScheduleId(String scheduleId);
}
