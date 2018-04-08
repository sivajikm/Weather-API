package com.sivaji.weather.webservices.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Cache<T> {

    private static final Logger logger = LoggerFactory.getLogger(Cache.class.getName());

    /**
     * The default number of minutes to store an item in cache.
     */
    public static final int DEFAULT_MINUTES = 15;

    /**
     * The map of keys and values to store in cache.
     */
    protected final Map<String, T> valueMap = new HashMap<String, T>();

    /**
     * The map of keys and insert time.
     */
    protected final Map<String, Long> insertTimeMap = new HashMap<String, Long>();

    /**
     * The map of keys and the number of minutes to cache for.
     */
    protected final Map<String, Integer> cacheMinutesMap = new HashMap<String, Integer>();

    private final String name;
    private Timer scavengeTimer;
    private int defaultMinutes = DEFAULT_MINUTES;
    private boolean isScavengeRunning;


    protected Cache(String name) {
        this.name = name;
    }

    /**
     * @return The name of this cache.
     */
    public String getName() {
        return name;
    }

    /**
     * Add an item to the cache.
     *
     * @param key The key used to refer to the cached item.
     * @param value The cached item.
     */
    public synchronized void add(String key, T value) {
        valueMap.put(key, value);
        insertTimeMap.put(key, System.currentTimeMillis());

        // Scavenge cache to remove expired items if necessary
        if (doScavenge()) {
            scavengeCache();
        }
    }

    /**
     * Add an item to the cache.
     *
     * @param key The key used to refer to the cached item.
     * @param value The cached item.
     * @param expireMinutes The number of minutes the cached value should be considered fresh.
     */
    public synchronized void add(String key, T value, int expireMinutes) {
        add(key, value);
        cacheMinutesMap.put(key, expireMinutes);
    }

    /**
     * Retrieve a cached item.
     *
     * @param key The key used to refer to the cached item.
     * @return The cached value or null if it does not exist or has expired.
     */
    public T get(String key) {
        if (!isKeyInCache(key)) {
            return null;
        }

        return valueMap.get(key);
    }

    /**
     * Checks to see whether a cached item has expired.
     *
     * @param key The key used to refer to the cached item.
     */
    protected boolean isExpired(String key) {
        Long timeEntered = insertTimeMap.get(key);

        if (timeEntered == null) {
            return true;
        }

        Integer cacheMinutes = cacheMinutesMap.get(key);
        if (cacheMinutes == null) {
            cacheMinutes = getDefaultMinutes();
        }

        return System.currentTimeMillis() > timeEntered + (cacheMinutes * 60 * 1000);
    }

    /**
     * Remove cached item with the specified key.
     *
     * @param key The key used to refer to the cached item.
     */
    public synchronized void remove(String key) {
        remove(key, true);
    }

    /**
     * Remove cached item with the specified key.
     *
     * @param key The key used to refer to the cached item.
     * @param fireEvents Flag indicating whether or not to fire events.
     */
    protected synchronized void remove(String key, boolean fireEvents) {
        valueMap.remove(key);
        insertTimeMap.remove(key);
        cacheMinutesMap.remove(key);
    }

    /**
     * Clear all items in the cache.
     */
    public synchronized void removeAll() {
        removeAll(true);
    }

    /**
     * Clear all items in the cache.
     *
     * @param fireEvents Flag indicating whether or not to fire events.
     */
    protected synchronized void removeAll(boolean fireEvents) {
        valueMap.clear();
        insertTimeMap.clear();
        cacheMinutesMap.clear();
    }

    /**
     * Remove all expired items from this cache.
     *
     * @return The number of expired cached elements removed.
     */
    public int removeExpired() {
        int count = 0;

        List<String> toBeRemoved = new ArrayList<String>();

        for (String key : valueMap.keySet()) {
            if (isExpired(key)) {
                toBeRemoved.add(key);
            }
        }

        // Run removal in separate loop to avoid concurrent modification exception
        for (String key : toBeRemoved) {
            remove(key, false);
            count++;
        }

        return count;
    }

    /**
     * @return The default number of minutes a cached item should be considered fresh.
     */
    public int getDefaultMinutes() {
        return defaultMinutes;
    }

    /**
     * @param defaultMinutes The default number of minutes a cached item should be considered fresh.
     */
    public void setDefaultMinutes(int defaultMinutes) {
        this.defaultMinutes = defaultMinutes;
    }

    /**
     * @return The keys in this cache.
     */
    public String[] getKeys() {
        return valueMap.keySet().toArray(new String[0]);
    }

    /**
     * Return <code>true</code> if the key is in the specified cache, even if the value associated
     * with that key is <code>null</code>.
     */
    public boolean isKeyInCache(String key) {
        if (isExpired(key)) {
            remove(key, false);
        }

        return valueMap.containsKey(key);
    }

    /**
     * Check if this cache should scavenge expired items.
     */
    private boolean doScavenge() {
        //Random number is even test
		/*Random generator = new Random();
		 return (generator.nextInt(100) + 1) % 2 == 0;*/

        // Memory free test
        Runtime rt = Runtime.getRuntime();
        long allocated = rt.totalMemory();
        long free = rt.freeMemory();
        float percentFree = (float) free / allocated;

        return percentFree < .10;
    }

    /**
     * Run separate thread to remove expired items from cache.
     */
    private synchronized void scavengeCache() {
        if (isScavengeRunning) {
            return;
        }

        logger.trace("Running scavenge for cache: {}", getName());

        scavengeTimer = new Timer("TIMER-" + this.toString());
        scavengeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                int count = removeExpired();
                isScavengeRunning = false;

                if (count > 0) {
                    logger.debug("Scavenged {} items from cache: {}", count, getName());
                }

                scavengeTimer.cancel();
            }
        }, 5 * 1000);

        isScavengeRunning = true;
    }
}

