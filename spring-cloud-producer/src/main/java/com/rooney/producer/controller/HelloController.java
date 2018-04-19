package com.rooney.producer.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    private final Logger logger = Logger.getLogger(getClass());

    @Autowired
    private DiscoveryClient client;

    @RequestMapping("/hello1")
    public String index1(@RequestParam String name) {
        return "hello " + name + "，this is first messge";
    }

    @RequestMapping("/hello")
    public String index(@RequestParam String name) {
        ServiceInstance instance = client.getLocalServiceInstance();
        // 让处理线程等待几秒钟 Hystrix 默认超时时间为 2000 毫秒
        // int sleepTime = new Random().nextInt(3000);
        int sleepTime = 3000;
        logger.info("sleepTime:" + sleepTime);

        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        logger.info("/hello：host:" + instance.getHost() + " port:" + instance.getPort() + " service_id:" + instance.getServiceId());
        return "hello world!";
    }

    @GetMapping("/testService")
    public String dc() {
        // int sleepTime = 3000;
        // logger.info("sleepTime:" + sleepTime);
        //
        // try {
        // Thread.sleep(sleepTime);
        // } catch (InterruptedException e) {
        // e.printStackTrace();
        // }
        String services = "Services: " + client.getServices();
        System.out.println(services);
        return services;
    }
}
