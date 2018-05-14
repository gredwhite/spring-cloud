package org.ntkachev.microservices.hello;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableEurekaClient
public class HelloStarter {

    public static void main(String[] args) {
        SpringApplication.run(HelloStarter.class, args);
    }

    @RestController
    public static class HelloController {
        @GetMapping("/hello")
        public String hello(@RequestParam("name") String name) {
            return "Hello " + name + "!";
        }
    }
}