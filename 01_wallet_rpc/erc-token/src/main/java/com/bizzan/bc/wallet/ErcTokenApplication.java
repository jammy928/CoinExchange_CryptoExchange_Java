package com.bizzan.bc.wallet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class ErcTokenApplication {

    public static void main(String ... args){
        SpringApplication.run(ErcTokenApplication.class,args);
    }
}
