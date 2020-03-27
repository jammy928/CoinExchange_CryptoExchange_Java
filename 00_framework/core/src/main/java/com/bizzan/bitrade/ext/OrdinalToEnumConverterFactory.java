package com.bizzan.bitrade.ext;

import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * ‘整型’字符串映射成枚举
 *
 * @author GS
 * @date 2017年12月12日
 */
public class OrdinalToEnumConverterFactory implements ConverterFactory<String, Enum<?>> {

    @Override
    public <T extends Enum<?>> Converter<String, T> getConverter(Class<T> targetType) {
        return new OrdinalToEnum<>(targetType);
    }

    private class OrdinalToEnum<T extends Enum<?>> implements Converter<String, T> {

        private final Class<T> enumType;

        private Map<String, T> enumMap = new HashMap<>();

        public OrdinalToEnum(Class<T> enumType) {
            this.enumType = enumType;
            T[] enums = enumType.getEnumConstants();
            for (T e : enums) {
                enumMap.put(e.ordinal() + "", e);
            }
        }

        @Override
        public T convert(String source) {
            T result = enumMap.get(source);
            if (result == null) {
                throw new IllegalArgumentException("No element matches " + source);
            }
            return result;
        }
    }
}
