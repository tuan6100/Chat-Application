package com.chat.app.repository.redis;

import com.chat.app.model.redis.MessageCache;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface MessageCacheRepository extends CrudRepository<MessageCache, String> {


}
