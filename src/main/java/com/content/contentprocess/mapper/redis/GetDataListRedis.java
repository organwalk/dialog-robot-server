package com.content.contentprocess.mapper.redis;

import com.content.contentprocess.entity.table.IntentionAndEntityResult;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface GetDataListRedis {
    Object getGroupByName(String groupName, String mobile);
    Object getDeptByName(String deptName, String mobile);
    Object getPersonByName(String username,String mobile);
    Object getUserByMobile(String mobile);
    boolean deleteUserByMobile(String mobile);
    List<IntentionAndEntityResult> getIntentionAndEntity(String mobile);

}
