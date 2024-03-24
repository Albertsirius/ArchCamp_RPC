package org.galaxy.siriusrpc.demo.consumer;

import org.galaxy.siriusrpc.core.annotation.SiriusConsumer;
import org.galaxy.siriusrpc.core.consumer.ConsumerConfig;
import org.galaxy.siriusrpc.demo.api.OrderService;
import org.galaxy.siriusrpc.demo.api.User;
import org.galaxy.siriusrpc.demo.api.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.util.Arrays;

/**
 * @author AlbertSirius
 * @since 2024/3/17
 */

@SpringBootApplication
@Import({ConsumerConfig.class})
public class SiriusrpcDemoConsumerApplication {

    @SiriusConsumer
    UserService userService;

    @SiriusConsumer
    OrderService orderService;

    @Autowired
    Demo2 demo2;

    public static void main(String[] args) {
        SpringApplication.run(SiriusrpcDemoConsumerApplication.class);
    }

    @Bean
    public ApplicationRunner consumer_runner() {
        return x -> {

            System.out.println("userService.getId(new User(100, \"HZH\")) = " + userService.getId(new User(100, "HZH")));
            //User user = userService.findById(1);
            //System.out.println("RPC result userService.findById(1) = " + user);
            //User user2 = userService.findById(1, "aa");
            //System.out.println("RPC result userService.findById(1, \"aa\") = " + user2);
            //Order order = orderService.findById(2);
            //System.out.println("RPC result orderService.findByid(2) = " + order);
            //Order order404 = orderService.findById(404);
            //System.out.println("RPC result orderService.findByid(404) = " + order404);
            //demo2.test();
            System.out.println(" ===> userService.getLongIds()");
            Arrays.stream(userService.getLongIds()).forEach(System.out::println);

            System.out.println(" ===> userService.getIds(int[])");
            Arrays.stream(userService.getIds(new int[]{4,5,6})).forEach(System.out::println);
        };
    }
}
