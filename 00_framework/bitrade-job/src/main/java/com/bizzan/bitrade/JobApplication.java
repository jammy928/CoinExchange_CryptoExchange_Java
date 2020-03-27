package com.bizzan.bitrade;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @Description:
 * @author: GuoShuai
 * @date: create in 14:27 2018/6/29
 * @Modified:
 */
@SpringBootApplication
@EnableScheduling
@EnableEurekaClient
@EnableAspectJAutoProxy(exposeProxy=true)
public class JobApplication {
    public static void main(String[] args){
        SpringApplication.run(JobApplication.class,args);
    }
}
