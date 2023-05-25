package com.content.contentprocess.mapper.redis;

import com.content.contentprocess.entity.request.GroupRequest;
import org.springframework.stereotype.Repository;

@Repository
public interface SaveDataListRedis {

    boolean saveGroupInfo(GroupRequest groupRequest,String mobile);

}
