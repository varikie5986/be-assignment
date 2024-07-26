package com.nathapot.assignment.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class ExecutorConfig {

    @Value("${spring.taskpool.corePoolSize}")
    private int corePoolSize;

    @Value("${spring.taskpool.maxPoolSize}")
    private int maxPoolSize;

    @Value("${spring.taskpool.queueCapacity}")
    private int queueCapacity;

    @Value("${spring.taskpool.allowCoreThreadTimeOut}")
    private boolean isAllowCoreThreadTimeOut;

    @Bean
    public ExecutorService taskExecutor() {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                1, TimeUnit.MINUTES,
                new LinkedBlockingQueue<>(queueCapacity));
        executor.allowCoreThreadTimeOut(isAllowCoreThreadTimeOut);
        return executor;
    }
}

