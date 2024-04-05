package org.galaxy.siriusrpc.demo.provider;

import org.galaxy.siriusrpc.core.api.RpcRequest;
import org.galaxy.siriusrpc.core.api.RpcResponse;
import org.galaxy.siriusrpc.core.provider.ProviderBootstrap;
import org.galaxy.siriusrpc.core.provider.ProviderConfig;
import org.galaxy.siriusrpc.core.provider.ProviderInvoker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author AlbertSirius
 * @since 2024/3/10
 */

@SpringBootApplication
@RestController
@Import({ProviderConfig.class})
public class SiriusrpcDemoProviderApplication {

    @Autowired
    private ProviderInvoker providerInvoker;

    public static void main(String[] args) {
        SpringApplication.run(SiriusrpcDemoProviderApplication.class);
    }

    @RequestMapping("/")
    public RpcResponse<Object> invoke(@RequestBody RpcRequest request) {

        return providerInvoker.invoke(request);

    }

    @Bean
    ApplicationRunner providerRun() {
        return x -> {
          RpcRequest request = new RpcRequest();
          request.setMethodSign("findById@1_int");
          request.setService("org.galaxy.siriusrpc.demo.api.UserService");
          request.setArgs(new Object[]{100});
          RpcResponse<Object> rpcResponse = invoke(request);
          System.out.println("return: " + rpcResponse.getData());
        };
    }
}
