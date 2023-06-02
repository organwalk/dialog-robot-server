package com.content.contentprocess.mapper.redis;

import com.content.contentprocess.entity.request.DeptRequest;
import com.content.contentprocess.entity.request.GroupRequest;
import com.content.contentprocess.entity.request.UserRequest;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GetDataListRedis {
    Object getGroupByName(String groupName, String mobile);
    Object getDeptByName(String deptName, String mobile);
    Object getPersonByName(String username,String mobile);
    Object getUserByMobile(String mobile);
    boolean deleteUserByMobile(String mobile);
}
