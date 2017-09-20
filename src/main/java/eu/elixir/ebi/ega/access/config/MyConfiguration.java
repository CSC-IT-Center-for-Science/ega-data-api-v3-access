/*
 * Copyright 2016 ELIXIR EBI
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.elixir.ebi.ega.access.config;

import com.google.common.cache.CacheBuilder;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cache.guava.GuavaCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 *
 * @author asenf
 */
@Configuration
@EnableCaching
public class MyConfiguration extends WebMvcConfigurerAdapter { 

    // Ribbon Load Balanced Rest Template for communication with other Microservices
    
    @Bean
    @LoadBalanced
    RestTemplate restTemplate() {
        return new RestTemplate();
    }
/* 
    @Bean
    HystrixContextInterceptor hystrixContextInterceptor() {
        return new HystrixContextInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(hystrixContextInterceptor());
    }
*/    
    @Bean
    public Docket swaggerSettings() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build()
                .pathMapping("/");
    }
    
    //@Bean
    //public CacheManager cacheManager() {
    //    return new ConcurrentMapCacheManager("tokens");
    //}    

    //@Bean
    //public CacheManager concurrentCacheManager() {
    //
    //        ConcurrentMapCacheManager manager = new ConcurrentMapCacheManager();
    //        manager.setCacheNames(Arrays.asList("tokens", "fileFile", "fileDatasetFile", 
    //                "appOrgCache", "appDatasetCache", "appOrgDatasetCache", "appDatasetFileCache", 
    //                "appDacDatasetCache", "appElixirDatasetCache"));
    //
    //        return manager;
    //}

    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager simpleCacheManager = new SimpleCacheManager();
        GuavaCache tokens = new GuavaCache("tokens", CacheBuilder.newBuilder()
                .expireAfterAccess(1, TimeUnit.HOURS)
                .build());
        GuavaCache fileFile = new GuavaCache("fileFile", CacheBuilder.newBuilder()
                .expireAfterAccess(24, TimeUnit.HOURS)
                .build());
        GuavaCache fileDatasetFile = new GuavaCache("fileDatasetFile", CacheBuilder.newBuilder()
                .expireAfterAccess(24, TimeUnit.HOURS)
                .build());
        GuavaCache appOrgCache = new GuavaCache("appOrgCache", CacheBuilder.newBuilder()
                .expireAfterAccess(24, TimeUnit.HOURS)
                .build());
        GuavaCache appDatasetCache = new GuavaCache("appDatasetCache", CacheBuilder.newBuilder()
                .expireAfterAccess(24, TimeUnit.HOURS)
                .build());
        GuavaCache appOrgDatasetCache = new GuavaCache("appOrgDatasetCache", CacheBuilder.newBuilder()
                .expireAfterAccess(24, TimeUnit.HOURS)
                .build());
        GuavaCache appDatasetFileCache = new GuavaCache("appDatasetFileCache", CacheBuilder.newBuilder()
                .expireAfterAccess(24, TimeUnit.HOURS)
                .build());
        GuavaCache appDacDatasetCache = new GuavaCache("appDacDatasetCache", CacheBuilder.newBuilder()
                .expireAfterAccess(24, TimeUnit.HOURS)
                .build());
        GuavaCache appElixirDatasetCache = new GuavaCache("appElixirDatasetCache", CacheBuilder.newBuilder()
                .expireAfterAccess(24, TimeUnit.HOURS)
                .build());
        
        simpleCacheManager.setCaches(Arrays.asList(tokens, fileFile, fileDatasetFile, 
                    appOrgCache, appDatasetCache, appOrgDatasetCache, appDatasetFileCache, 
                    appDacDatasetCache, appElixirDatasetCache));
        return simpleCacheManager;
    }
}