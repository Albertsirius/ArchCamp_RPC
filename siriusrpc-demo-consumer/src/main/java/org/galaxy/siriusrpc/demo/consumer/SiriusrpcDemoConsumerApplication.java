package org.galaxy.siriusrpc.demo.consumer;

import org.galaxy.siriusrpc.core.annotation.SiriusConsumer;
import org.galaxy.siriusrpc.core.api.Router;
import org.galaxy.siriusrpc.core.cluster.GrayRouter;
import org.galaxy.siriusrpc.core.consumer.ConsumerConfig;
import org.galaxy.siriusrpc.demo.api.User;
import org.galaxy.siriusrpc.demo.api.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

/**
 * @author AlbertSirius
 * @since 2024/3/17
 */

@SpringBootApplication
@RestController
@Import({ConsumerConfig.class})
public class SiriusrpcDemoConsumerApplication {

    @SiriusConsumer
    UserService userService;

/*    @SiriusConsumer
    OrderService orderService;*/

 /*   @Autowired
    Demo2 demo2;*/

    @RequestMapping("/api/")
    public User findById(@RequestParam("id")int id) {
        return userService.findById(id);
    }

    @RequestMapping("/find/")
    public User find(@RequestParam("timeout")int timeout) {
        return userService.find(timeout);
    }

    @Autowired
    Router router;

    @RequestMapping("/gray")
    public String gray(@RequestParam("ratio") int ratio) { //留个后门设置Rati
        ((GrayRouter)router).setGrayRatio(ratio);
        return "OK-new gray ratio is " + ratio;
    }

    public static void main(String[] args) {

        SpringApplication.run(SiriusrpcDemoConsumerApplication.class);
    }

    @Bean
    public ApplicationRunner consumer_runner() {
        return x -> {

            //testAll();
        };
    }

    private void testAll() {
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
    }
}
