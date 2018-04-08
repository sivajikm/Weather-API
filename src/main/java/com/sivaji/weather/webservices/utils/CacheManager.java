package com.sivaji.weather.webservices.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.sivaji.weather.webservices.model.CacheEventListener;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public final class CacheManager implements CacheEventListener {

    private static final Logger logger = LoggerFactory.getLogger(CacheManager.class.getName());

    public static final String CACHE_NAME = "WEATHER_WIND_API_CACHE";
    private static final String CACHE_PARAM_REMOVE_SEPARATOR = ":";

    /* Singleton instance */
    private final static CacheManager instance = new CacheManager();

    /* The cache container */
    private Map<String, Cache> cacheMap = new HashMap<String, Cache>();

    /* Cache sync variables */
    private final Set<String> removeQueue = new HashSet<String>();
    private final Set<String> removeAllQueue = new HashSet<String>();
    private final Object CACHE_SYNC_LOCK = new Object();


    private CacheManager() {}

    public static CacheManager getInstance() {
        return instance;
    }

    private synchronized void add(String name, Cache cache) {
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("Cache name must not be blank.");
        }
        cacheMap.put(name, cache);
    }


    public synchronized void removeCache(String name) {
        if (StringUtils.isBlank(name)) {
            return;
        }
        cacheMap.remove(name);
    }

    /**
     * Get a cache with the specified name.
     */
    public <T> Cache<T> getCache(String name) {
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("The cache name must not be blank.");
        }

        if (cacheMap.get(name) == null) {
            Cache<T> cache = new Cache<T>(name);
            add(name, cache);
        }

        return cacheMap.get(name);
    }

    /**
     * Fires when a cache removes an individual item.
     */
    @Override
    public void onRemove(String cacheName, String key) {
        synchronized (CACHE_SYNC_LOCK) {
            removeQueue.add(cacheName + CACHE_PARAM_REMOVE_SEPARATOR + key);
        }
    }

    /**
     * Fires when a cache removes all items.
     */
    @Override
    public void onRemoveAll(String cacheName) {
        synchronized (CACHE_SYNC_LOCK) {
            removeAllQueue.add(cacheName);
        }
    }
}

