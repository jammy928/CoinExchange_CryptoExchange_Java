package com.bizzan.bitrade.config;

import com.bizzan.bitrade.util.BigDecimalToDecimal128Converter;
import com.bizzan.bitrade.util.Decimal128ToBigDecimalConverter;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.convert.CustomConversions;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

import java.util.ArrayList;
import java.util.List;

@Configuration
@ConditionalOnProperty(name="spring.data.mongodb.uri")
public class MongoConfig extends AbstractMongoConfiguration{

    @Value("${spring.data.mongodb.uri}")
    private String uri ;

    @Override
    protected String getDatabaseName() {
        return getMongoClientURI().getDatabase();
    }

    @Override
    public Mongo mongo() {
        MongoClient mongoClient = new MongoClient(this.getMongoClientURI());
        return mongoClient;
    }

    public MongoClientURI getMongoClientURI(){
        return new MongoClientURI(uri);
    }

    @Override
    @Bean
    public MappingMongoConverter mappingMongoConverter() throws Exception {
        DefaultDbRefResolver dbRefResolver = new DefaultDbRefResolver(this.dbFactory());
        MappingMongoConverter converter = new MappingMongoConverter(dbRefResolver, this.mongoMappingContext());
        List<Object> list = new ArrayList<>();
        list.add(new BigDecimalToDecimal128Converter());//自定义的类型转换器
        list.add(new Decimal128ToBigDecimalConverter());//自定义的类型转换器
        //list.add(new DateLocalConvert());
        converter.setCustomConversions(new CustomConversions(list));
        return converter;
    }
    @Bean
    public MongoDbFactory dbFactory() throws Exception {
        return new SimpleMongoDbFactory(getMongoClientURI());
    }
    @Override
    @Bean
    public MongoMappingContext mongoMappingContext() {
        MongoMappingContext mappingContext = new MongoMappingContext();
        return mappingContext;
    }
    @Override
    @Bean(name="newMongoTemplate")
    public MongoTemplate mongoTemplate() throws Exception {
        return new MongoTemplate(this.dbFactory(), this.mappingMongoConverter());
    }

}
