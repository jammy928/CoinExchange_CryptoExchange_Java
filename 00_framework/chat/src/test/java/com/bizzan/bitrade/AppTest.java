package com.bizzan.bitrade;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Set;

/**
 * Unit test for simple App.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class AppTest {


    @Autowired
    MongoTemplate mongoTemplate ;

    /**
     * 将chat_message_* 集合的数据全部迁移到集合 chat_message 这一个集合
     */
    @Test
    public void testMoveChatMessage(){

        Query query = new Query();
        Set<String> sets = mongoTemplate.getCollectionNames();

        for(String collectionName:sets){
            if(collectionName.startsWith("chat_message_")){
                 //mongoTemplate.dropCollection(collectionName);
              /* List<ChatMessageRecord> list = mongoTemplate.find(query, ChatMessageRecord.class,collectionName);
               mongoTemplate.insert(list,"chat_message");*/
            }
        }
    }
}
