package com.content.contentprocess.mapper.redis;

import com.content.contentprocess.entity.table.IntentionAndEntityResult;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface GetDataListRedis {
    Object getGroupByName(String groupName, String mobile);
    Object getDeptByName(String deptName, String mobile);
    Object getPersonByDeptAndName(String username,String deptName, String mobile);
    Object getUserByMobile(String mobile);
    List<IntentionAndEntityResult> getIntentionAndEntity(String mobile);
    boolean deleteUserByMobile(String mobile);
    boolean deleteDept(String mobile);
    boolean deletePersonByName(String mobile, String deptName, String name);


}
