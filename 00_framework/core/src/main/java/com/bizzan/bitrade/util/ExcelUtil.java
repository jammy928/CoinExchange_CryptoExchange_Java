package com.bizzan.bitrade.util;

import cn.afterturn.easypoi.exception.excel.ExcelExportException;
import cn.afterturn.easypoi.exception.excel.ExcelImportException;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import javax.servlet.http.HttpServletResponse;

import com.bizzan.bitrade.annotation.Excel;
import com.bizzan.bitrade.annotation.ExcelSheet;
import com.bizzan.bitrade.annotation.IgnoreExcel;
import com.bizzan.bitrade.entity.MemberWallet;
import com.bizzan.bitrade.vo.OtcOrderVO;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.text.SimpleDateFormat;
import java.util.*;

public class ExcelUtil  {

    /**
     * @MethodName  : listToExcel
     * @Description : 导出Excel（可以导出到本地文件系统，也可以导出到浏览器，可自定义工作表大小）
     * @param list      数据源
     * @param fields      类的英文属性和Excel中的中文列名的对应关系
     * 如果需要的是引用对象的属性，则英文属性使用类似于EL表达式的格式
     * 如：list中存放的都是student，student中又有college属性，而我们需要学院名称，则可以这样写
     * fieldMap.put("college.collegeName","学院名称")
     * @param out       导出流
     * @throws ExcelExportException
     */
    public static <T>  void   listToExcel (List<T> list , Field[] fields, OutputStream out) throws ExcelExportException{

        if(list.size()==0 || list==null){
            throw new ExcelExportException("数据源中没有任何数据");
        }
        int sheetSize = 65535 ;
        //创建工作簿并发送到OutputStream指定的地方
        WritableWorkbook wwb;
        try {
            wwb = Workbook.createWorkbook(out);

            //因为2003的Excel一个工作表最多可以有65536条记录，除去列头剩下65535条
            //所以如果记录太多，需要放到多个工作表中，其实就是个分页的过程
            //1.计算一共有多少个工作表
            double sheetNum=Math.ceil(list.size()/new Integer(sheetSize).doubleValue());

            //2.创建相应的工作表，并向其中填充数据
            for(int i=0; i<sheetNum; i++){
                //如果只有一个工作表的情况
                if(1==sheetNum){
                    WritableSheet sheet=wwb.createSheet(OtcOrderVO.class.getAnnotation(ExcelSheet.class).name(), i);
                    fillSheet(sheet, list, fields, 0, list.size()-1);

                    //有多个工作表的情况
                }else{
                    WritableSheet sheet=wwb.createSheet(OtcOrderVO.class.getAnnotation(ExcelSheet.class).name()+(i+1), i);

                    //获取开始索引和结束索引
                    int firstIndex=i*sheetSize;
                    int lastIndex=(i+1)*sheetSize-1>list.size()-1 ? list.size()-1 : (i+1)*sheetSize-1;
                    //填充工作表
                    fillSheet(sheet, list, fields, firstIndex, lastIndex);
                }
            }

            wwb.write();
            wwb.close();

        }catch (Exception e) {
            e.printStackTrace();
            //如果是ExcelException，则直接抛出
            if(e instanceof ExcelExportException){
                throw (ExcelExportException)e;

                //否则将其它异常包装成ExcelException再抛出
            }else{
                throw new ExcelExportException("导出Excel失败");
            }
        }

    }

    /**
     * @MethodName          : excelToList
     * @Description             : 将Excel转化为List
     * @param in                    ：承载着Excel的输入流
     * @param entityClass       ：List中对象的类型（Excel中的每一行都要转化为该类型的对象）
     * @param fieldMap          ：Excel中的中文列头和类的英文属性的对应关系Map
     * @param uniqueFields  ：指定业务主键组合（即复合主键），这些列的组合不能重复
     * @return                      ：List
     * @throws ExcelImportException
    */


    public static <T>  List<T>  excelToList(
            InputStream in,
            String sheetName,
            Class<T> entityClass,
            LinkedHashMap<String, String> fieldMap,
            String[] uniqueFields
    ) throws ExcelImportException{

        //定义要返回的list
        List<T> resultList=new ArrayList<>();

        try {

            //根据Excel数据源创建WorkBook
            Workbook wb=Workbook.getWorkbook(in);
            //获取工作表
            Sheet sheet=wb.getSheet(sheetName);

            //获取工作表的有效行数
            int realRows=0;
            for(int i=0;i<sheet.getRows();i++){

                int nullCols=0;
                for(int j=0;j<sheet.getColumns();j++){
                    Cell currentCell=sheet.getCell(j,i);
                    if(currentCell==null || "".equals(currentCell.getContents().toString())){
                        nullCols++;
                    }
                }

                if(nullCols==sheet.getColumns()){
                    break;
                }else{
                    realRows++;
                }
            }


            //如果Excel中没有数据则提示错误
            if(realRows<=1){
                throw new ExcelImportException("Excel文件中没有任何数据");
            }


            Cell[] firstRow=sheet.getRow(0);

            String[] excelFieldNames=new String[firstRow.length];

            //获取Excel中的列名
            for(int i=0;i<firstRow.length;i++){
                excelFieldNames[i]=firstRow[i].getContents().toString().trim();
            }

            //判断需要的字段在Excel中是否都存在
            boolean isExist=true;
            List<String> excelFieldList= Arrays.asList(excelFieldNames);
            for(String cnName : fieldMap.keySet()){
                if(!excelFieldList.contains(cnName)){
                    isExist=false;
                    break;
                }
            }

            //如果有列名不存在，则抛出异常，提示错误
            if(!isExist){
                throw new ExcelImportException("Excel中缺少必要的字段，或字段名称有误");
            }


            //将列名和列号放入Map中,这样通过列名就可以拿到列号
            LinkedHashMap<String, Integer> colMap=new LinkedHashMap<String, Integer>();
            for(int i=0;i<excelFieldNames.length;i++){
                colMap.put(excelFieldNames[i], firstRow[i].getColumn());
            }



            //判断是否有重复行
            //1.获取uniqueFields指定的列
            Cell[][] uniqueCells=new Cell[uniqueFields.length][];
            for(int i=0;i<uniqueFields.length;i++){
                int col=colMap.get(uniqueFields[i]);
                uniqueCells[i]=sheet.getColumn(col);
            }

            //2.从指定列中寻找重复行
            for(int i=1;i<realRows;i++){
                int nullCols=0;
                for(int j=0;j<uniqueFields.length;j++){
                    String currentContent=uniqueCells[j][i].getContents();
                    Cell sameCell=sheet.findCell(currentContent,
                            uniqueCells[j][i].getColumn(),
                            uniqueCells[j][i].getRow()+1,
                            uniqueCells[j][i].getColumn(),
                            uniqueCells[j][realRows-1].getRow(),
                            true);
                    if(sameCell!=null){
                        nullCols++;
                    }
                }

                if(nullCols==uniqueFields.length){
                    throw new ExcelImportException("Excel中有重复行，请检查");
                }
            }

            //将sheet转换为list
            for(int i=1;i<realRows;i++){
                //新建要转换的对象
                T entity=entityClass.newInstance();

                //给对象中的字段赋值
                for(Map.Entry<String, String> entry : fieldMap.entrySet()){
                    //获取中文字段名
                    String cnNormalName=entry.getKey();
                    //获取英文字段名
                    String enNormalName=entry.getValue();
                    //根据中文字段名获取列号
                    int col=colMap.get(cnNormalName);

                    //获取当前单元格中的内容
                    String content=sheet.getCell(col, i).getContents().toString().trim();

                    //给对象赋值
                    setFieldValueByName(enNormalName, content, entity);
                }

                resultList.add(entity);
            }
        } catch(Exception e){
            e.printStackTrace();
            //如果是ExcelException，则直接抛出
            if(e instanceof ExcelImportException){
                throw (ExcelImportException)e;

                //否则将其它异常包装成ExcelException再抛出
            }else{
                e.printStackTrace();
                throw new ExcelImportException("导入Excel失败");
            }
        }
        return resultList;
    }


/**
 * <-------------------------辅助的私有方法----------------------------------------------->
 */

/**
     * @MethodName  : getFieldValueByName
     * @Description : 根据字段名获取字段值
     * @param fieldName 字段名
     * @param o 对象
     * @return  字段值
*/


    private static  Object getFieldValueByName(String fieldName, Object o) throws Exception{

        Object value=null;
        Field field=getFieldByName(fieldName, o.getClass());

        if(field !=null){
            field.setAccessible(true);
            value=field.get(o);
        }else{
            throw new ExcelImportException(o.getClass().getSimpleName() + "类不存在字段名 "+fieldName);
        }

        return value;
    }

/**
     * @MethodName  : getFieldByName
     * @Description : 根据字段名获取字段
     * @param fieldName 字段名
     * @param clazz 包含该字段的类
     * @return 字段

*/

    private static Field getFieldByName(String fieldName, Class<?>  clazz){
        //拿到本类的所有字段
        Field[] selfFields=clazz.getDeclaredFields();

        //如果本类中存在该字段，则返回
        for(Field field : selfFields){
            if(field.getName().equals(fieldName)){
                return field;
            }
        }

        //否则，查看父类中是否存在此字段，如果有则返回
        Class<?> superClazz=clazz.getSuperclass();
        if(superClazz!=null  &&  superClazz !=Object.class){
            return getFieldByName(fieldName, superClazz);
        }

        //如果本类和父类都没有，则返回空
        return null;
    }



/**
     * @MethodName  : getFieldValueByNameSequence
     * @Description :
     * 根据带路径或不带路径的属性名获取属性值
     * 即接受简单属性名，如userName等，又接受带路径的属性名，如student.department.name等
     *
     * @param fieldNameSequence  带路径的属性名或简单属性名
     * @param o 对象
     * @return  属性值
     * @throws Exception
*/


    private static  Object getFieldValueByNameSequence(String fieldNameSequence, Object o) throws Exception{

        Object value=null;

        //将fieldNameSequence进行拆分
        String[] attributes=fieldNameSequence.split("\\.");
        if(attributes.length==1){
            value=getFieldValueByName(fieldNameSequence, o);
        }else{
            //根据属性名获取属性对象
            Object fieldObj=getFieldValueByName(attributes[0], o);
            String subFieldNameSequence=fieldNameSequence.substring(fieldNameSequence.indexOf(".")+1);
            value=getFieldValueByNameSequence(subFieldNameSequence, fieldObj);
        }
        return value;

    }


/**
     * @MethodName  : setFieldValueByName
     * @Description : 根据字段名给对象的字段赋值
     * @param fieldName  字段名
     * @param fieldValue    字段值
     * @param o 对象
*/


    private static void setFieldValueByName(String fieldName,Object fieldValue,Object o) throws Exception{

        Field field=getFieldByName(fieldName, o.getClass());
        if(field!=null){
            field.setAccessible(true);
            //获取字段类型
            Class<?> fieldType = field.getType();

            //根据字段类型给字段赋值
            if (String.class == fieldType) {
                field.set(o, String.valueOf(fieldValue));
            } else if ((Integer.TYPE == fieldType)
                    || (Integer.class == fieldType)) {
                field.set(o, Integer.parseInt(fieldValue.toString()));
            } else if ((Long.TYPE == fieldType)
                    || (Long.class == fieldType)) {
                field.set(o, Long.valueOf(fieldValue.toString()));
            } else if ((Float.TYPE == fieldType)
                    || (Float.class == fieldType)) {
                field.set(o, Float.valueOf(fieldValue.toString()));
            } else if ((Short.TYPE == fieldType)
                    || (Short.class == fieldType)) {
                field.set(o, Short.valueOf(fieldValue.toString()));
            } else if ((Double.TYPE == fieldType)
                    || (Double.class == fieldType)) {
                field.set(o, Double.valueOf(fieldValue.toString()));
            } else if (Character.TYPE == fieldType) {
                if ((fieldValue!= null) && (fieldValue.toString().length() > 0)) {
                    field.set(o, Character
                            .valueOf(fieldValue.toString().charAt(0)));
                }
            }else if(Date.class==fieldType){
                field.set(o, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(fieldValue.toString()));
            }else{
                field.set(o, fieldValue);
            }
        }else{
            throw new ExcelImportException(o.getClass().getSimpleName() + "类不存在字段名 "+fieldName);
        }
    }


/**
     * @MethodName  : setColumnAutoSize
     * @Description : 设置工作表自动列宽和首行加粗
     * @param ws

*/

    private static void setColumnAutoSize(WritableSheet ws,int extraWith){
        //获取本列的最宽单元格的宽度
        for(int i=0;i<ws.getColumns();i++){
            int colWith=0;
            for(int j=0;j<ws.getRows();j++){
                String content=ws.getCell(i,j).getContents().toString();
                int cellWith=content.length();
                if(colWith<cellWith){
                    colWith=cellWith;
                }
            }
            //设置单元格的宽度为最宽宽度+额外宽度
            ws.setColumnView(i, colWith+extraWith);
        }

    }

/**
     * @MethodName  : fillSheet
     * @Description : 向工作表中填充数据
     * @param sheet     工作表
     * @param list  数据源
     * @param fieldMap 中英文字段对应关系的Map
     * @param firstIndex    开始索引
     * @param lastIndex 结束索引
*/


    private static <T> void fillSheet(
            WritableSheet sheet,
            List<T> list,
            LinkedHashMap<String,String> fieldMap,
            int firstIndex,
            int lastIndex
    )throws Exception{

        //定义存放英文字段名和中文字段名的数组
        String[] enFields=new String[fieldMap.size()];
        String[] cnFields=new String[fieldMap.size()];

        //填充数组
        int count=0;
        for(Map.Entry<String,String> entry:fieldMap.entrySet()){
            enFields[count]=entry.getKey();
            cnFields[count]=entry.getValue();
            count++;
        }
        //填充表头
        for(int i=0;i<cnFields.length;i++){
            Label label=new Label(i,0,cnFields[i]);
            sheet.addCell(label);
        }

        //填充内容
        int rowNo=1;
        for(int index=firstIndex;index<=lastIndex;index++){
            //获取单个对象
            T item=list.get(index);
            for(int i=0;i<enFields.length;i++){
                Object objValue=getFieldValueByNameSequence(enFields[i], item);
                String fieldValue=objValue==null ? "" : objValue.toString();
                Label label =new Label(i,rowNo,fieldValue);
                sheet.addCell(label);
            }

            rowNo++;
        }

        //设置自动列宽
        setColumnAutoSize(sheet, 5);
    }

    private static <T> void fillSheet(
            WritableSheet sheet,
            List<T> list,
            Field[] fields ,
            int firstIndex,
            int lastIndex
    )throws Exception{

        //填充表头
        int j = 0 ;
        for(int i=0;i<fields.length;i++){
            if(fields[i].getAnnotation(Excel.class)==null) {
                continue ;
            }
            Label label=new Label(j,0,fields[i].getAnnotation(Excel.class).name());
            sheet.addCell(label);
            j++ ;
        }

        //填充内容
        int rowNo=1;
        for(int index=firstIndex;index<=lastIndex;index++){
            //获取单个对象
            T item=list.get(index);
            int k = 0 ;
            for(int i=0;i<fields.length;i++){
                if(fields[i].getAnnotation(Excel.class)==null) {
                    continue ;
                }
                Object objValue=getFieldValueByNameSequence(fields[i].getName(), item);
                String fieldValue=objValue==null ? "" : objValue.toString();
                Label label =new Label(k,rowNo,fieldValue);
                sheet.addCell(label);
                k++ ;
            }

            rowNo++;
        }

        //设置自动列宽
        setColumnAutoSize(sheet, 5);
    }

    public static void main(String[] args) {
        Field[] fields = MemberWallet.class.getFields();
        System.out.println(fields.length);
    }

}
