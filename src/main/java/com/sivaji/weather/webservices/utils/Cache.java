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


    public String getName() {
        return name;
    }


    public synchronized void add(String key, T value) {
        valueMap.put(key, value);
        insertTimeMap.put(key, System.currentTimeMillis());

        // Scavenge cache to remove expired items if necessary
        if (doScavenge()) {
            scavengeCache();
        }
    }


    public synchronized void add(String key, T value, int expireMinutes) {
        add(key, value);
        cacheMinutesMap.put(key, expireMinutes);
    }


    public T get(String key) {
        if (!isKeyInCache(key)) {
            return null;
        }

        return valueMap.get(key);
    }


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


    public synchronized void remove(String key) {
        remove(key, true);
    }

    protected synchronized void remove(String key, boolean fireEvents) {
        valueMap.remove(key);
        insertTimeMap.remove(key);
        cacheMinutesMap.remove(key);
    }


    public synchronized void removeAll() {
        removeAll(true);
    }


    protected synchronized void removeAll(boolean fireEvents) {
        valueMap.clear();
        insertTimeMap.clear();
        cacheMinutesMap.clear();
    }

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

    public int getDefaultMinutes() {
        return defaultMinutes;
    }

    public void setDefaultMinutes(int defaultMinutes) {
        this.defaultMinutes = defaultMinutes;
    }

    public String[] getKeys() {
        return valueMap.keySet().toArray(new String[0]);
    }


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

