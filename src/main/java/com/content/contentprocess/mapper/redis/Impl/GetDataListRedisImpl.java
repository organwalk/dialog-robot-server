package com.content.contentprocess.mapper.redis.Impl;

import com.content.contentprocess.entity.table.IntentionAndEntityResult;
import com.content.contentprocess.mapper.redis.GetDataListRedis;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.*;

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

    @Override
    public List<IntentionAndEntityResult> getIntentionAndEntity(String mobile) {
        HashOperations<String, String, String> hashOps = redisTemplate.opsForHash();
        Map<String, String> map = hashOps.entries("feedback:mobile:" + mobile);
        List<IntentionAndEntityResult> result = new ArrayList<>();
        String content = null;
        String intention = null;
        String entity = null;
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            switch (key) {
                case "content":
                    content = value;
                    break;
                case "intention":
                    intention = value;
                    break;
                case "entity":
                    entity = value;
                    break;
                default:
                    break;
            }
        }
        if (content != null && intention != null && entity != null) {
            IntentionAndEntityResult feedback = new IntentionAndEntityResult(content, intention, entity);
            result.add(feedback);
        }
        return result;
    }
}
