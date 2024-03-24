package org.galaxy.siriusrpc.core.util;

import com.alibaba.fastjson.JSONObject;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author AlbertSirius
 * @since 2024/3/24
 */
public class TypeUtils {

    public static Object cast(Object origin, Class<?> type) {
        if (Objects.isNull(origin)) return origin;
        Class<?> aClass = origin.getClass();
        if (type.isAssignableFrom(aClass)) {
            return origin;
        }
        if (origin instanceof Map map) {
            JSONObject jsonObject = new JSONObject(map);
            return jsonObject.toJavaObject(type);
        }

        if (type.isArray()) {
            if (origin instanceof List list) {
                origin = list.toArray();
            }
            int length = Array.getLength(origin);
            Class<?> compontType = type.getComponentType();
            Object resultArray = Array.newInstance(compontType, length);
            for (int i = 0; i < length; i++) {
                Array.set(resultArray, i, Array.get(origin, i));
            }
            return resultArray;
        }

        if (type.equals(Long.class) || type.equals(Long.TYPE)) {
            return Long.valueOf(origin.toString());
        } else if (type.equals(Integer.class) || type.equals(Integer.TYPE)) {
            return Integer.valueOf(origin.toString());
        } else if (type.equals(Float.class) || type.equals(Float.TYPE)) {
            return Float.valueOf(origin.toString());
        } else if (type.equals(Double.class) || type.equals(Double.TYPE)) {
            return Double.valueOf(origin.toString());
        } else if (type.equals(Short.class) || type.equals(Short.TYPE)) {
            return Short.valueOf(origin.toString());
        } else if (type.equals(Byte.class) || type.equals(Byte.TYPE)) {
            return Byte.valueOf(origin.toString());
        } else if (type.equals(Character.class) || type.equals(Character.TYPE)) {
            return Character.valueOf(origin.toString().charAt(0));
        }
        return null;
    }
}
