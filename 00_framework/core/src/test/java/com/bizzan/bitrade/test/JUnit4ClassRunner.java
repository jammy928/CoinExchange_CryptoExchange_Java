package com.bizzan.bitrade.test;

import org.junit.runners.model.InitializationError;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Log4jConfigurer;

public class JUnit4ClassRunner extends SpringJUnit4ClassRunner {
    public JUnit4ClassRunner(Class<?> clazz) throws InitializationError {
        super(clazz);
    }
    static {
        try {
            Log4jConfigurer.initLogging("classpath:log4j.properties");
        } catch (Exception e) {
            System.out.println("Cannot initialize log4j");
        }
    }
}
