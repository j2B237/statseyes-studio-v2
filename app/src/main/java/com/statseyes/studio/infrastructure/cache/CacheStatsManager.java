package com.statseyes.studio.infrastructure.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.stats.CacheStats;

import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;

import org.springframework.stereotype.Service;

@Service 
public class CacheStatsManager {

    //
    // Public API
    // 

    private CacheManager cacheManager;

    // Constructor
    public CacheStatsManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public void printStats(CacheType type) {

        String cachename = switch (type){
            case ACCOUNT -> type.getName();
            case ACCOUNT_SUMMARY -> type.getName();
            case ACCOUNT_SETTING -> type.getName();
            case CLUB -> type.getName();
            case CLUB_SUMMARY -> type.getName();
            case TEAM -> type.getName();
            case ATHLETES -> type.getName();
            case POSITION -> type.getName();
            case IMPORTED_SESSIONS -> type.getName();
        };

        CaffeineCache cache = 
        (CaffeineCache)cacheManager.getCache(cachename);

        
        Cache<?, ?> nativeCache = (Cache<?, ?>)cache.getNativeCache();
        CacheStats stats = nativeCache.stats();

        System.out.println("***** Account " + cachename + " Cache Stats ******");
        System.out.println("Hits       : " + stats.hitCount());
        System.out.println("Misses     : " + stats.missCount());
        System.out.println("Hit rate   : " + stats.hitRate());
        System.out.println("Evictions  : " + stats.evictionCount());
        System.out.println("Entries    : " + nativeCache.estimatedSize());
        System.out.println();
    }
}
