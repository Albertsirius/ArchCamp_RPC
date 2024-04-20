package org.galaxy.siriusrpc.core.filter;

import lombok.SneakyThrows;
import org.galaxy.siriusrpc.core.api.Filter;
import org.galaxy.siriusrpc.core.api.RpcRequest;
import org.galaxy.siriusrpc.core.api.RpcResponse;
import org.galaxy.siriusrpc.core.util.MethodUtils;
import org.galaxy.siriusrpc.core.util.MockUtils;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @author AlbertSirius
 * @since 2024/4/15
 */
public class MockFilter implements Filter {

    @SneakyThrows
    @Override
    public Object preFilter(RpcRequest request) {
        Class service = Class.forName(request.getService());
        Method method = findeMethod(service, request.getMethodSign());
        Class clazz = method.getReturnType();
        return MockUtils.mock(clazz);
    }

    private Method findeMethod(Class service, String methodSign) {
        return Arrays.stream(service.getMethods())
                .filter(method -> !MethodUtils.checkLocalMethod(method))
                .filter(method -> methodSign.equals(MethodUtils.methodSign(method)))
                .findFirst().orElse(null);
    }

    @Override
    public Object postFilter(RpcRequest request, RpcResponse response, Object result) {
        return null;
    }
}
