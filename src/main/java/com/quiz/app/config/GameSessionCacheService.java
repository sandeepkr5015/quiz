package com.quiz.app.config;

import com.quiz.app.cache.GameSessionCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class GameSessionCacheService {

    @Autowired
    private CacheManager cacheManager;

    @Cacheable(value = "gameSessionCache", key = "#gameSessionId")
    public GameSessionCache getGameSessionCache(Long gameSessionId) {
        Cache cache = cacheManager.getCache("gameSessionCache");
        if (cache != null) {
            Cache.ValueWrapper valueWrapper = cache.get(gameSessionId);
            if (valueWrapper != null) {
                return (GameSessionCache) valueWrapper.get();
            }
        }
        return null;
    }

    @CacheEvict(value = "gameSessionCache", key = "#gameSessionId")
    public void clearGameSessionCache(Long gameSessionId) {
        Cache cache = cacheManager.getCache("gameSessionCache");
        if (cache != null) {
            cache.evict(gameSessionId);
        }
    }
    }