package com.bizzan.bc.wallet;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@SpringBootApplication
public class EctApplication {
    public static void main(String[] args){
        SpringApplication.run(EctApplication.class,args);
    }
}
