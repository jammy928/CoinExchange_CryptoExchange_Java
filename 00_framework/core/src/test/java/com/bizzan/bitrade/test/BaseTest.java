package com.bizzan.bitrade.test;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

@RunWith(JUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring-service.xml"})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = false)
public class BaseTest extends AbstractJUnit4SpringContextTests {

}
