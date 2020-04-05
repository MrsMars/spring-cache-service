package com.aoher.service;

import com.aoher.exception.CacheException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.aoher.util.Constants.CACHE_IS_NULL_EXCEPTION;
import static com.aoher.util.Constants.VALUE_WRAPPER_IS_NULL_EXCEPTION;

@Service
public class CacheService {

    private static final String INTEGER_CACHE = "integerCache";

    private CacheManager cacheManager;

    @Autowired
    public CacheService(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public Integer getInteger(int value) {
        Cache cache = cacheManager.getCache(INTEGER_CACHE);
        if (cache == null) {
            throw new CacheException(CACHE_IS_NULL_EXCEPTION);
        }

        ValueWrapper cacheWrapper = cache.get(value);
        if (cacheWrapper == null) {
            throw new CacheException(VALUE_WRAPPER_IS_NULL_EXCEPTION);
        }

        return Optional.ofNullable((Integer) cacheWrapper.get()).orElse(null);
    }

    public void putInteger(int value) {
        Cache cache = cacheManager.getCache(INTEGER_CACHE);
        if (cache == null) {
            throw new CacheException(CACHE_IS_NULL_EXCEPTION);
        }

        cache.put(value, value);
    }
}
