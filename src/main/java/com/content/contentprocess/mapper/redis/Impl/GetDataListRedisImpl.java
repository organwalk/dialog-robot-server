package com.content.contentprocess.mapper.redis.Impl;

import com.content.contentprocess.mapper.redis.GetDataListRedis;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
@AllArgsConstructor
public class GetDataListRedisImpl implements GetDataListRedis {

    private final RedisTemplate redisTemplate;

    @Override
    public Object getGroupByName(String groupName,String mobile) {
        String hashKeyNamePattern = "group:*" + groupName + "*:mobile:" + mobile;
        Set<String> keys = redisTemplate.keys(hashKeyNamePattern);
        Object values = new Object();
        for (String key : keys) {
            values = redisTemplate.opsForHash().values(key);
        }
        return values;
    }

    @Override
    public Object getDeptByName(String deptName,String mobile) {
        String hashKeyNamePattern = "dept:*" + deptName + "*:mobile:" + mobile;
        Set<String> keys = redisTemplate.keys(hashKeyNamePattern);
        Object values = new Object();
        for (String key : keys) {
            values = redisTemplate.opsForHash().values(key);
        }
        return values;
    }

    @Override
    public Object getPersonByName(String userName,String mobile) {
        String hashKeyName = "dept_persion:" + userName + ":mobile:" + mobile;
        return redisTemplate.opsForHash().values(hashKeyName);
    }

    @Override
    public Object getUserByMobile(String mobile) {
        String hashKeyName = "user:mobile:"+mobile;
        return redisTemplate.opsForHash().values(hashKeyName);
    }

    @Override
    public boolean deleteUserByMobile(String mobile) {
        String hashKeyName = "mobile:"+mobile;
        List<String> hashKeyNames = redisTemplate.opsForHash().values(hashKeyName);
        if(hashKeyNames.isEmpty()){
            return false;
        }else {
            for (String str:
                    hashKeyNames) {
                redisTemplate.delete(str);
            }
            redisTemplate.delete(hashKeyName);
            return true;
        }
    }
}
