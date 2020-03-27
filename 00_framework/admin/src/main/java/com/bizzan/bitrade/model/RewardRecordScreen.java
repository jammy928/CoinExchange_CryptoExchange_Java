package com.bizzan.bitrade.model;

public class RewardRecordScreen {

    private String unit ;

    /**
     * 推荐者
     */
    private String presenter ;

    /**
     * 被推荐者
     */
    private String presentee ;

   /* public List<BooleanExpression> getBooleanExpressions() throws IllegalAccessException {
        List<BooleanExpression> booleanExpressions = new ArrayList<>();
        Field[] fields =  this.getClass().getDeclaredFields() ;
        for(Field field : fields){
            field.setAccessible(true);
            Object object = field.get(this.getClass());
            if(object!=null){
                booleanExpressions.add()
            }
        }
    }*/
}
