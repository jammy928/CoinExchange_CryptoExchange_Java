package com.bizzan.bitrade.util;

import java.math.BigDecimal;

/**
 * @author GS
 * @date 2018年01月17日
 */
public class BigDecimalUtils {

    /**
     * 默认除法运算精度
     */
    private static final int DEFAULT_DIV_SCALE = 8;

    /**
     * 采用 BigDecimal 的字符串构造器进行初始化。
     *
     * @param v double 值
     * @return BigDecimal 对象
     */
    private static BigDecimal createBigDecimal(double v) {
        return new BigDecimal(Double.toString(v));
    }

    /**
     * 提供精确的加法运算。
     *
     * @param v1 被加数
     * @param v2 加数
     * @return 两个参数的和
     */
    public static BigDecimal add(BigDecimal v1, BigDecimal v2) {
        return v1.add(v2);
    }

    /**
     * 提供精确的加法运算。
     *
     * @param v1 被加数
     * @param v2 加数
     * @return 两个参数的和
     */
    public static BigDecimal add(double v1, double v2) {
        BigDecimal b1 = createBigDecimal(v1);
        BigDecimal b2 = createBigDecimal(v2);
        return b1.add(b2);
    }

    /**
     * 提供精确的加法运算。
     *
     * @param v1 被加数
     * @param v2 加数
     * @return 两个参数的和
     */
    public static BigDecimal add(BigDecimal v1, double v2) {
        BigDecimal b2 = createBigDecimal(v2);
        return v1.add(b2);
    }

    /**
     * 提供精确的减法运算。
     *
     * @param v1 被减数
     * @param v2 减数
     * @return 两个参数的差
     */
    public static BigDecimal sub(double v1, double v2) {
        BigDecimal b1 = createBigDecimal(v1);
        BigDecimal b2 = createBigDecimal(v2);
        return b1.subtract(b2);
    }

    /**
     * 提供精确的减法运算。
     *
     * @param v1 被减数
     * @param v2 减数
     * @return 两个参数的差
     */
    public static BigDecimal sub(BigDecimal v1, double v2) {
        BigDecimal b2 = createBigDecimal(v2);
        return v1.subtract(b2);
    }

    /**
     * 提供精确的减法运算。
     *
     * @param v1 被减数
     * @param v2 减数
     * @return 两个参数的差
     */
    public static BigDecimal sub(BigDecimal v1, BigDecimal v2) {
        return v1.subtract(v2);
    }

    /**
     * 提供精确的小数位四舍五入处理。
     *
     * @param v     需要四舍五入的数字
     * @param scale 小数点后保留几位
     * @return 四舍五入后的结果
     */
    public static BigDecimal round(double v, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }
        BigDecimal b = createBigDecimal(v);
        return b.divide(BigDecimal.ONE, scale, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 提供精确的小数位四舍五入处理。
     *
     * @param v     需要四舍五入的数字
     * @param scale 小数点后保留几位
     * @return 四舍五入后的结果
     */
    public static BigDecimal round(BigDecimal v, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }
        return v.divide(BigDecimal.ONE, scale, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 提供精确的乘法运算。
     *
     * @param v1 被乘数
     * @param v2 乘数
     * @return 两个参数的积
     */
    public static BigDecimal mul(double v1, double v2) {
        BigDecimal b1 = createBigDecimal(v1);
        BigDecimal b2 = createBigDecimal(v2);
        return b1.multiply(b2);
    }

    /**
     * 提供精确的乘法运算。
     *
     * @param v1 被乘数
     * @param v2 乘数
     * @return 两个参数的积
     */
    public static BigDecimal mul(BigDecimal v1, double v2) {
        BigDecimal b2 = createBigDecimal(v2);
        return v1.multiply(b2);
    }

    /**
     * 提供精确的乘法运算。
     *
     * @param v1 被乘数
     * @param v2 乘数
     * @return 两个参数的积
     */
    public static BigDecimal mul(BigDecimal v1, BigDecimal v2) {
        return v1.multiply(v2);
    }

    public static BigDecimal mulDown(BigDecimal v1, BigDecimal v2,int x) {
        return v1.multiply(v2).setScale(x,BigDecimal.ROUND_DOWN);
    }
    /**
     * 提供相对精确的乘法运算，四舍五入保留八位小数。
     *
     * @param v1 被乘数
     * @param v2 乘数
     * @return 两个参数的积
     */
    public static BigDecimal mulRound(BigDecimal v1, BigDecimal v2) {
        return mulRound(v1, v2, DEFAULT_DIV_SCALE);
    }

    /**
     * 提供相对精确的乘法运算，四舍五入保留v3位小数。
     *
     * @param v1 被乘数
     * @param v2 乘数
     * @param v3 保留位数
     * @return 两个参数的积
     */
    public static BigDecimal mulRound(BigDecimal v1, BigDecimal v2, int v3) {
        return round(v1.multiply(v2), v3);
    }

    /**
     * 提供（相对）精确的除法运算。 当发生除不尽的情况时，由scale参数指定精度，以后的数字四舍五入。
     *
     * @param v1    被除数
     * @param v2    除数
     * @param scale 表示表示需要精确到小数点以后几位。
     * @return 两个参数的商
     */
    public static BigDecimal div(double v1, double v2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }
        BigDecimal b1 = createBigDecimal(v1);
        BigDecimal b2 = createBigDecimal(v2);
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 提供（相对）精确的除法运算。 当发生除不尽的情况时，由scale参数指定精度，以后的数字四舍五入。
     *
     * @param v1    被除数
     * @param v2    除数
     * @param scale 表示表示需要精确到小数点以后几位。
     * @return 两个参数的商
     */
    public static BigDecimal div(BigDecimal v1, double v2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }
        BigDecimal b2 = createBigDecimal(v2);
        return v1.divide(b2, scale, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 提供（相对）精确的除法运算。 当发生除不尽的情况时，由scale参数指定精度，以后的数字四舍五入。
     *
     * @param v1    被除数
     * @param v2    除数
     * @param scale 表示表示需要精确到小数点以后几位。
     * @return 两个参数的商
     */
    public static BigDecimal div(BigDecimal v1, BigDecimal v2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }
        return v1.divide(v2, scale, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 提供（相对）精确的除法运算。 当发生除不尽的情况时，默认保留八位，以后的数字四舍五入。
     *
     * @param v1 被除数
     * @param v2 除数
     * @return 两个参数的商
     */
    public static BigDecimal div(BigDecimal v1, BigDecimal v2) {
        return v1.divide(v2, DEFAULT_DIV_SCALE, BigDecimal.ROUND_HALF_UP);
    }

    public static BigDecimal divDown(BigDecimal v1, BigDecimal v2) {
        return v1.divide(v2, DEFAULT_DIV_SCALE, BigDecimal.ROUND_DOWN);
    }

    /**
     * 得到利率
     *
     * @param v1
     * @return
     */
    public static BigDecimal getRate(BigDecimal v1) {
        BigDecimal hundred = new BigDecimal("100");
        return div(v1, hundred);
    }

    /**
     * 得到倍数
     *
     * @param v1
     * @return
     */
    public static BigDecimal rate(BigDecimal v1) {
        return add(getRate(v1), BigDecimal.ONE);
    }

    /**
     * 比较大小，v1>=v2返回true,否则返回false
     *
     * @param v1
     * @param v2
     * @return
     */
    public static boolean compare(BigDecimal v1, BigDecimal v2) {
        return v1.compareTo(v2) >= 0 ? true : false;
    }

    /**
     * 比较大小，(v1+v2)>=v2返回true,否则返回false
     *
     * @param v1
     * @param v2
     * @param v3
     * @return
     */
    public static boolean compare(BigDecimal v1, BigDecimal v2, BigDecimal v3) {
        return compare(add(v1, v2), v3);
    }

    /**
     * 判断两值是否相等
     *
     * @param v1
     * @param v2
     * @return
     */
    public static boolean isEqual(BigDecimal v1, BigDecimal v2) {
        return v1.compareTo(v2) == 0 ? true : false;
    }
}
