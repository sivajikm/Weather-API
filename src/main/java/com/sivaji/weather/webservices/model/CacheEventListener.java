package com.sivaji.weather.webservices.model;

public interface CacheEventListener {
    void onRemove(String cacheName, String key);
    void onRemoveAll(String cacheName);
}
