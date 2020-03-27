package com.bizzan.bc.wallet;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@SpringBootApplication
public class ActApplication {
    public static void main(String[] args){
        SpringApplication.run(ActApplication.class,args);
    }
}
