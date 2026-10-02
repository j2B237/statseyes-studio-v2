package com.statseyes.studio.infrastructure.cache;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.benmanes.caffeine.cache.Caffeine;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

@Configuration 
@EnableCaching // Active le mecanisme d'interception des annotations de cache
public class CacheConfig {

    @Bean 
    public CacheManager cacheManager(){
        SimpleCacheManager manager = new SimpleCacheManager();

        List<CaffeineCache> caches = new ArrayList<>();

        caches.add(
            buildCache(
                CacheType.ACCOUNT.getName(), 
                15, 
                TimeUnit.MINUTES,
                10
            )
        );

        caches.add(
            buildCache(
                CacheType.ACCOUNT_SETTING.getName(), 
                15, 
                TimeUnit.MINUTES, 
                100
            )
        );

        caches.add(
            buildCache(
                CacheType.CLUB.getName(), 
                10, 
                TimeUnit.HOURS, 
                100
            )
        );

        caches.add(
            buildCache(
                CacheType.TEAM.getName(), 
                1, 
                TimeUnit.HOURS,
                100
            )
        );

        caches.add(
            buildCache(
                CacheType.ATHLETE.getName(), 
                1, 
                TimeUnit.HOURS,
                500
            )
        );

        caches.add(
            buildCache(
                CacheType.POSITION.getName(), 
                1, 
                TimeUnit.HOURS, 
                100
            )
        );
        
        manager.setCaches(caches);

        return manager;
    }

    private CaffeineCache buildCache(
        String name, 
        long duration, 
        TimeUnit unit, 
        int maxSize
    ) {
        return new CaffeineCache(
            name,
            Caffeine.newBuilder()
                    .expireAfterWrite(duration, unit)   // expiration apres ecriture
                    .maximumSize(maxSize)               // taille max -> éviction LRU si dépassée
                    .recordStats()                      // active les statistiques (hit/miss)
                    .build());
    }
}