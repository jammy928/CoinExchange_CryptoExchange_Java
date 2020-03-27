package com.bizzan.bitrade.service.base;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.bizzan.bitrade.dto.PageParam;
import com.bizzan.bitrade.dto.Pagenation;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;


@Service
public class MongoBaseService<T> {

    @Autowired
    protected MongoTemplate mongoTemplate ;

    /**
     * mongodb分页排序查询
     * @param pageParam：分页参数（pageNo、pageSize、order、direction）
     * @param query
     * @param cla
     * @param collectionName
     * @return
     */
    public Pagenation page(PageParam pageParam , Query query, Class<T> cla, String collectionName ){
        if(pageParam.getOrders()!=null&&pageParam.getDirection()!=null) {
            query.with(new Sort(pageParam.getDirection(),pageParam.getOrders()));
        }
        long total  = mongoTemplate.count(query,cla,collectionName);
        query.limit(pageParam.getPageSize()).skip((pageParam.getPageNo()-1)*pageParam.getPageSize());
        List<T> list = mongoTemplate.find(query,cla,collectionName);
        long consult = total/pageParam.getPageSize() ;
        long residue = total%pageParam.getPageSize();
        long totalPage = residue == 0 ?consult :(consult+1) ;
        Pagenation pagenation = new Pagenation(pageParam);
        return pagenation.setData(list,total,totalPage);
    }

    /**
     * 条件数组
     * @param map
     * @return
     */
    public Criteria getCriteria(Map<String,Map<String,Object>> map){

        Criteria criteria = new Criteria() ;

        "1".equals(Criteria.where("1"));

        Map<String,Object> condition = map.get("eq") ;
        if(condition!=null){
            for(String key : condition.keySet()){
                criteria.and(key).equals(condition.get(key)) ;
            }
        }
        condition = map.get("gte") ;
        if(condition!=null){
            for(String key : condition.keySet()){
                criteria.and(key).gte(condition.get(key)) ;
            }
        }


        condition = map.get("gt") ;
        if(condition!=null){
            for(String key : condition.keySet()){
                criteria.and(key).gt(condition.get(key)) ;
            }
        }


        condition = map.get("lte") ;
        if(condition!=null){
            for(String key : condition.keySet()){
                criteria.and(key).lte(condition.get(key)) ;
            }
        }


        condition = map.get("lt") ;
        if(condition!=null){
            for(String key : condition.keySet()){
                criteria.and(key).lt(condition.get(key)) ;
            }
        }

        condition = map.get("like-prefix") ;
        if(condition!=null){
            for(String key : condition.keySet()){
                Pattern pattern = Pattern.compile("^"+condition.get(key)+".*$");
                criteria.and(key).regex(pattern) ;
            }
        }

        condition = map.get("like-suffix") ;
        if(condition!=null){
            for(String key : condition.keySet()){
                Pattern pattern = Pattern.compile("^.*"+condition.get(key)+"$");
                criteria.and(key).regex(pattern) ;
            }
        }

        condition = map.get("like-all") ;
        if(condition!=null){
            for(String key : condition.keySet()){
                Pattern pattern = Pattern.compile("^.*"+condition.get(key)+".*$");
                criteria.and(key).regex(pattern) ;
            }
        }


        return criteria ;
    }
}
