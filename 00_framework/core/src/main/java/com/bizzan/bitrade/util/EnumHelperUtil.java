package com.bizzan.bitrade.util;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
/**
 * 此帮助类严格限定为有typeName和typeCode的枚举类，如定义枚举类型为 ADMIN(typeName,typeCode)这种
 *
 * ClassName: EnumOperatorUtil.java
 */
public class EnumHelperUtil{
    /**
     * indexOf,传入的参数ordinal指的是需要的枚举值在设定的枚举类中的顺序，以0开始
     * T
     * @param clazz
     * @param ordinal
     * @return
     */
    public static <T extends Enum<T>> T indexOf(Class<T> clazz, int ordinal){
        return (T) clazz.getEnumConstants()[ordinal];
    }
    /**
     * nameOf,传入的参数name指的是枚举值的名称，一般是大写加下划线的
     * T
     * @param clazz
     * @param name
     * @return Enum T
     */
    public static <T extends Enum<T>> T nameOf(Class<T> clazz, String name){

        return (T) Enum.valueOf(clazz, name);
    }
    /**
     * 使用枚举类型对应的typeCode获取枚举类型
     * T
     * @param clazz    枚举类的class
     * @param getTypeCodeMethodName  传入的typeCode的get方法
     * @param typeCode  传入的typeCode值，这个方法为String类型
     * @return
     */
    public static <T extends Enum<T>> T getByStringTypeCode(Class<T> clazz,String getTypeCodeMethodName, String typeCode){
        T result = null;
        try{
            T[] arr = clazz.getEnumConstants();
            Method targetMethod = clazz.getDeclaredMethod(getTypeCodeMethodName);
            String typeCodeVal = null;
            for(T entity:arr){
                typeCodeVal = targetMethod.invoke(entity).toString();
                if(typeCodeVal.contentEquals(typeCode)){
                    result = entity;
                    break;
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 使用枚举类型对应的typeCode获取枚举类型
     * T
     * @param clazz    枚举类的class
     * @param getTypeCodeMethodName  传入的typeCode的get方法
     * @param typeCode  传入的typeCode值，这个方法为Integer类型
     * @return
     */
    public static <T extends Enum<T>> T getByIntegerTypeCode(Class<T> clazz,String getTypeCodeMethodName, Integer typeCode){
        T result = null;
        try{
            T[] arr = clazz.getEnumConstants();
            Method targetMethod = clazz.getDeclaredMethod(getTypeCodeMethodName);
            Integer typeCodeVal = null;
            for(T entity:arr){
                typeCodeVal = Integer.valueOf(targetMethod.invoke(entity).toString());
                if(typeCodeVal.equals(typeCode)){
                    result = entity;
                    break;
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return result;
    }
    /**
     * 使用枚举类型对应的typeName获取枚举类型
     * T
     * @param clazz   枚举类的class
     * @param getTypeNameMethodName  传入的typeName的get方法
     * @param typeName 传入的typeName值，这个方法为String类型
     * @return
     */
    public static <T extends Enum<T>> T getByStringTypeName(Class<T> clazz,String getTypeNameMethodName, String typeName){
        T result = null;
        try{
            T[] arr = clazz.getEnumConstants();
            Method targetMethod = clazz.getDeclaredMethod(getTypeNameMethodName);
            String typeNameVal = null;
            for(T entity:arr){
                typeNameVal = targetMethod.invoke(entity).toString();
                if(typeNameVal.contentEquals(typeName)){
                    result = entity;
                    break;
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return result;
    }
}