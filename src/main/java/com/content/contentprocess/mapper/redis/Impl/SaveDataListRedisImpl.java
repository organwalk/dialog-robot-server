package com.content.contentprocess.mapper.redis.Impl;

import com.content.contentprocess.entity.request.DeptRequest;
import com.content.contentprocess.entity.request.GroupRequest;
import com.content.contentprocess.entity.request.PersonRequest;
import com.content.contentprocess.entity.request.UserRequest;
import com.content.contentprocess.mapper.redis.SaveDataListRedis;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class SaveDataListRedisImpl implements SaveDataListRedis {
    private final RedisTemplate redisTemplate;
    @Override
    public boolean saveGroupInfo(GroupRequest groupRequest,String mobile) {
        List<GroupRequest.Group> group = groupRequest.getGroup();
        String hashKeyName = null;
        for (GroupRequest.Group groups : group){
            hashKeyName = "group:" + groups.getGroupName();
            redisTemplate.opsForHash().put(hashKeyName,"deptId",groups.getDeptId());
            redisTemplate.opsForHash().put(hashKeyName,"groupId",groups.getGroupId());
        }
        return redisTemplate.getExpire(hashKeyName) != null ? true : false;
    }

    @Override
    public boolean saveDeptInfo(DeptRequest deptRequest, String mobile) {
        List<DeptRequest.Dept> depts = deptRequest.getDept();
        String hashKeyName = null;
        for (DeptRequest.Dept dept : depts){
            for (DeptRequest.Departments departments : dept.getDepartments()){
                hashKeyName = "dept:" + departments.getName();
                redisTemplate.opsForHash().put(hashKeyName,"deptId",departments.getDeptId());
                redisTemplate.opsForHash().put(hashKeyName,"parentId",departments.getOrder());
                redisTemplate.opsForHash().put(hashKeyName,"orderId",departments.getOrder());
            }
        }

        return redisTemplate.getExpire(hashKeyName) != null ? true : false;
    }

    @Override
    public boolean savePerSonInfo(PersonRequest personRequest, String mobile) {
        List<PersonRequest.Dept> depts = personRequest.getDept();
        String hashKeyName = null;
        for (PersonRequest.Dept dept : depts){
            for (PersonRequest.Users users : dept.getUsers()){
                hashKeyName = "dept_persion:" + personRequest.getDeptName() + ":" + users.getName();
                redisTemplate.opsForHash().put(hashKeyName,"id",users.getId());
                redisTemplate.opsForHash().put(hashKeyName,"mobile",users.getMobile());
                redisTemplate.opsForHash().put(hashKeyName,"sequence",users.getSequence());
                redisTemplate.opsForHash().put(hashKeyName,"orgId",users.getOrgId());
            }
        }
        return redisTemplate.getExpire(hashKeyName) != null ? true : false;
    }

    @Override
    public boolean saveUserInfo(UserRequest userRequest) {
        String hashKeyName = "user:mobile:"+userRequest.getMobile();
        redisTemplate.opsForHash().put(hashKeyName,"id",userRequest.getUid());
        redisTemplate.opsForHash().put(hashKeyName,"name",userRequest.getName());
        redisTemplate.opsForHash().put(hashKeyName,"dept",userRequest.getDeptName());
        return redisTemplate.getExpire(hashKeyName) != null ? true : false;
    }

    @Override
    public boolean saveFeedback(String orderType, String entity, String content, String mobile) {
        String hashKeyName = "feedback:mobile:"+mobile;
        redisTemplate.opsForHash().put(hashKeyName,"intention",orderType);
        redisTemplate.opsForHash().put(hashKeyName,"entity", entity);
        redisTemplate.opsForHash().put(hashKeyName, "content", content);
        return redisTemplate.getExpire(hashKeyName) != null ? true : false;
    }
}
