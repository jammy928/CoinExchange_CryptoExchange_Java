package com.bizzan.bitrade;

import org.junit.Test;

import java.math.BigDecimal;

public class BigDecimalTest {
    @Test
    public void testScale(){
        BigDecimal a = new BigDecimal("1.2800");
        System.out.println(a.setScale(2,BigDecimal.ROUND_UP));
    }
}
