package org.galaxy.siriusrpc.core.consumer;

import lombok.extern.slf4j.Slf4j;
import org.galaxy.siriusrpc.core.api.Filter;
import org.galaxy.siriusrpc.core.api.LoadBalancer;
import org.galaxy.siriusrpc.core.api.RegistryCenter;
import org.galaxy.siriusrpc.core.api.Router;
import org.galaxy.siriusrpc.core.cluster.RandomLoadBalancer;
import org.galaxy.siriusrpc.core.filter.CacheFilter;
import org.galaxy.siriusrpc.core.filter.MockFilter;
import org.galaxy.siriusrpc.core.registry.ZkRegistryCenter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * @author AlbertSirius
 * @since 2024/3/17
 */

@Configuration
@Slf4j
public class ConsumerConfig {

    @Value("${siriusrpc.providers")
    String servers;

    @Bean
    public ConsumerBootstrap createConsumerBootstrap() {
        return new ConsumerBootstrap();
    }

    @Bean
    @Order(Integer.MIN_VALUE) //多个ApplicationRunner，这个先执行
    public ApplicationRunner consumerBootstrap_runner(@Autowired ConsumerBootstrap consumerBootstrap) {
        return x -> {
            log.info("consumerBootStrap starting...");
            consumerBootstrap.start();
        };
    }

    @Bean
    public LoadBalancer loadBalancer() {
        //return LoadBalancer.Default;
        return new RandomLoadBalancer();
    }

    @Bean
    public Router router() {
        return Router.Default;
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public RegistryCenter consumer_rc() {
        return new ZkRegistryCenter();
    }

    @Bean
    public Filter filter1() {
        return new CacheFilter();
    }

    @Bean
    public Filter filter2() {
        return new MockFilter();
    }
}
