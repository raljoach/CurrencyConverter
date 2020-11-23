package com.itembase.currency;

import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class CacheKeyGenerator
        implements org.springframework.cache.interceptor.KeyGenerator {

    @Override
    public Object generate(Object target, Method method, Object... params) {
        StringBuilder sb = new StringBuilder();
        sb.append(target.getClass().getSimpleName()).append("_")
                .append(method.getName()).append("_")
                .append(StringUtils.arrayToDelimitedString(params, "_"));

        var key = sb.toString().hashCode();
        return key;
    }
}