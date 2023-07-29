package com.content.contentprocess.mapper.mysql;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.content.contentprocess.entity.table.FeedbackTable;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface FeedbackMapper extends BaseMapper<FeedbackTable> {
    @Select("select * from feedback where id=#{id}")
    List<FeedbackTable> getFeedbackById(@Param("id") int id);

    @Update("update feedback set new_intention = #{new_intention}, new_entity = #{new_entity} where id = #{id}")
    int updateFeedbackById(@Param("new_intention") String new_intention,
                           @Param("new_entity") String new_entity,
                           @Param("id") int id);
}
