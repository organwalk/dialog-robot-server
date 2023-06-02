package com.content.contentprocess.mapper.redis;

import com.content.contentprocess.entity.request.DeptRequest;
import com.content.contentprocess.entity.request.GroupRequest;
import com.content.contentprocess.entity.request.PersonRequest;
import com.content.contentprocess.entity.request.UserRequest;
import org.apache.catalina.User;
import org.springframework.stereotype.Repository;

@Repository
public interface SaveDataListRedis {

    boolean saveGroupInfo(GroupRequest groupRequest,String mobile);
    boolean saveDeptInfo(DeptRequest deptRequest,String mobile);
    boolean savePerSonInfo(PersonRequest personRequest,String mobile);
    boolean saveUserInfo(UserRequest personRequest);
}
