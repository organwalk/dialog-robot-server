package com.content.contentprocess.mapper.redis.Impl;

import com.content.contentprocess.entity.request.GroupRequest;
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
            hashKeyName = "group:" + groups.getGroupName() + ":mobile:" + mobile;
            redisTemplate.opsForHash().put(hashKeyName,"deptId",groups.getDeptId());
            redisTemplate.opsForHash().put(hashKeyName,"groupId",groups.getGroupId());
        }
        return redisTemplate.getExpire(hashKeyName) != null ? true : false;
    }
}
