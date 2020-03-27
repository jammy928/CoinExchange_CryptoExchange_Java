package com.bizzan.bitrade.util;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class TupleUtils {
    /**
     * @param tuples      查询结果
     * @param expressions 查询字段
     * @return
     */
    public static List<Map<String, Object>> tupleToMap(List<Tuple> tuples, List<Expression> expressions) {
        List<Map<String, Object>> list = new LinkedList<>();//返回结果
        //封装结果
        for (int i = 0; i < tuples.size(); i++) {
            //遍历tuples
            Map<String, Object> map = new LinkedHashMap<>();//一条信息
            for (Expression expression : expressions) {
                map.put(expression.toString().split(" as ")[1],//别名作为Key
                        tuples.get(i).get(expression));//获取结果
            }
            list.add(map);
        }
        return list;
    }
}
