package com.rooney.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;


@RestController
public class ConsumerCommandController {
    @Autowired
    private RestTemplate restTemplate;


    @HystrixCommand(groupKey = "HelloGroup", commandKey = "GetHelloCommand",
            commandProperties = {@HystrixProperty(name = "execution.isolation.strategy", value = "THREAD"),
                    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "4000"), // Hystrix 默认超时时间为 2000 毫秒
                    @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "10"),
                    @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "40")},
            threadPoolProperties = {@HystrixProperty(name = "coreSize", value = "1"),
                    // @HystrixProperty(name = "maximumSize", value = "5"),
                    @HystrixProperty(name = "maxQueueSize", value = "10"), @HystrixProperty(name = "keepAliveTimeMinutes", value = "1000"),
                    @HystrixProperty(name = "queueSizeRejectionThreshold", value = "8"),
                    @HystrixProperty(name = "metrics.rollingStats.numBuckets", value = "12"),
                    @HystrixProperty(name = "metrics.rollingStats.timeInMilliseconds", value = "1440")})
    @GetMapping("/helloCommand")
    public String getHelloCommand() {
        long beginTime = System.currentTimeMillis();
        String result = restTemplate.getForObject("http://spring-cloud-producer/testService", String.class);
        long endTime = System.currentTimeMillis();
        System.out.println("Spend Time : " + (endTime - beginTime));
        return result;
    }

    public String helloFallback() {
        return "error";
    }

}
