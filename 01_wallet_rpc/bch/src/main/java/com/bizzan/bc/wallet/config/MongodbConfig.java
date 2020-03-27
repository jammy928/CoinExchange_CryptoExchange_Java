package com.bizzan.bc.wallet.config;

import com.bizzan.bc.wallet.converter.BigDecimalToDecimal128Converter;
import com.bizzan.bc.wallet.converter.Decimal128ToBigDecimalConverter;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.core.convert.CustomConversions;

import java.util.ArrayList;
import java.util.List;

@Configuration
@ConditionalOnProperty(name="spring.data.mongodb.uri")
public class MongodbConfig extends AbstractMongoConfiguration {
    @Value("${spring.data.mongodb.uri}")
    private String uri;


    public MongoClientURI getMongoClientURI(){
        return new MongoClientURI(uri);
    }

    @Override
    protected String getDatabaseName() {
        return  this.getMongoClientURI().getDatabase();
    }

    @Override
    public Mongo mongo() throws Exception {
        MongoClient mongoClient = new MongoClient(this.getMongoClientURI());
        return mongoClient;
    }

    @Bean
    public MongoDbFactory dbFactory() throws Exception {
        return new SimpleMongoDbFactory(this.mongo(),this.getDatabaseName());
    }

    @Bean
    public MongoMappingContext mongoMappingContext() {
        MongoMappingContext mappingContext = new MongoMappingContext();
        return mappingContext;
    }

    @Bean
    public MappingMongoConverter mappingMongoConverter(MongoDbFactory dbFactory) throws Exception {
        DefaultDbRefResolver dbRefResolver = new DefaultDbRefResolver(dbFactory);
        MappingMongoConverter converter = new MappingMongoConverter(dbRefResolver, this.mongoMappingContext());
        List<Object> list = new ArrayList<>();
        list.add(new BigDecimalToDecimal128Converter());//自定义的类型转换器
        list.add(new Decimal128ToBigDecimalConverter());//自定义的类型转换器
        converter.setCustomConversions(new CustomConversions(list));
        return converter;
    }


    @Bean
    public MongoTemplate mongoTemplate(MongoDbFactory dbFactory,MappingMongoConverter converter) throws Exception {
        return new MongoTemplate(dbFactory, converter);
    }
}
