package com.bizzan.bitrade.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.bizzan.bitrade.util.IdWorkByTwitter;
import com.sparkframework.sql.DB;

/**
 * @author GS
 * @date 2017年12月22日
 */
@Configuration
public class SystemConfig {

    @Bean
    public IdWorkByTwitter idWorkByTwitter(@Value("${spark.system.work-id:0}")long workId,@Value("${spark.system.data-center-id:0}")long dataCenterId){
        return new IdWorkByTwitter(workId, dataCenterId);
    }

    @Bean
    public DB db(@Qualifier("dataSource") DataSource dataSource){
        return new DB(dataSource, true);
    }

}
