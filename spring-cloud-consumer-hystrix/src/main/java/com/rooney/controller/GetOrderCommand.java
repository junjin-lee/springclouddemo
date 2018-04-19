package com.rooney.controller;

import java.util.concurrent.Future;

import org.junit.Test;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolKey;
import com.netflix.hystrix.HystrixThreadPoolProperties;

public class GetOrderCommand extends HystrixCommand<String> {
    private final String name;

    public GetOrderCommand(String name) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("ThreadPoolTestGroup")).andCommandKey(HystrixCommandKey.Factory.asKey("testCommandKey"))
                .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey(name))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter().withExecutionTimeoutInMilliseconds(5000))
                .andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter().withMaxQueueSize(10) // 配置队列大小
                        .withCoreSize(2) // 配置线程池里的线程数
        ));
        this.name = name;
    }

    @Override
    protected String run() throws Exception {
        return "Hello" + name + " ,current thread:" + Thread.currentThread().getName();
    }

    public static class UnitTest {
        @Test
        public void testGetOrder() {
            // new GetOrderCommand("hystrix-order").execute();
            Future<String> future = new GetOrderCommand("hystrix-order").queue();
        }

    }
}
