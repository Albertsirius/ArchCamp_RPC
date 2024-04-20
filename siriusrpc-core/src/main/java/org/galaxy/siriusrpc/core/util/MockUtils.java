package org.galaxy.siriusrpc.core.util;

import lombok.SneakyThrows;

import java.lang.reflect.Field;

/**
 * @author AlbertSirius
 * @since 2024/4/15
 */
public class MockUtils {
    public static Object mock(Class type) {
        if (type.equals(Integer.class) || type.equals(Integer.TYPE)) {
            return 1;
        } else if (type.equals(Long.class) || type.equals(Long.TYPE)) {
            return 10000L;
        }
        if (Number.class.isAssignableFrom(type)) {
            return 1;
        }
        if (type.equals(String.class)) {
            return "this_is_a_mock_string";
        }
        return mockPojo(type);
    }

    @SneakyThrows
    private static Object mockPojo(Class type) {
        Object result = type.getDeclaredConstructor().newInstance();
        Field[] fields = type.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            Class<?> fType = field.getType();
            Object fValue = mock(fType);
            field.set(result, fValue);
        }
        return result;
    }
}
