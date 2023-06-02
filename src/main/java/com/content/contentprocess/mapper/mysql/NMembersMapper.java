package com.content.contentprocess.mapper.mysql;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.content.contentprocess.entity.table.NMembersTable;
import com.content.contentprocess.entity.table.SMembersTable;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface NMembersMapper extends BaseMapper<NMembersTable> {
    @Update("<script>" +
            "UPDATE n_members SET " +
            "<if test='uid != null'>uid = #{uid},</if>" +
            "<if test='name != null'>name = #{name}</if>" +
            "WHERE notification_id = #{notification_id} AND uid = #{olduid}" +
            "</script>")
    int updateNMembers(@Param("uid")String uid,
                       @Param("name")String name,
                       @Param("notification_id")String notification_id,
                       @Param("olduid")String olduid);

    @Delete("DELETE FROM n_members WHERE notification_id = #{notification_id}")
    int deleteBynoticeId(@Param("notification_id")String notification_id);

    @Select("SELECT * FROM n_members WHERE notification_id = #{notification_id}")
    List<NMembersTable> getNMembersByNoticeId(String notification_id);
}
