package org.ntkachev.microservices.hello_world;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@SpringBootApplication
@EnableEurekaClient
@RibbonClient(name = "say-hello", configuration = RibbonConfig.class)
public class HelloWorldStarter {

    public static void main(String[] args) {
        SpringApplication.run(HelloWorldStarter.class, args);
    }


    @RestController
    @EnableDiscoveryClient
    public static class HelloWorldController {
        @Autowired
        private RestTemplate restTemplate;
        @Autowired
        private DiscoveryClient discoveryClient;

        @GetMapping("/helloWorld")
        public String hello() {
            //String response = restTemplate.getForObject("http://localhost:8080/hello?name=World", String.class);
            //List<ServiceInstance> clients = discoveryClient.getInstances("HELLO_WORLD_CLIENT");
            String greeting = this.restTemplate.getForObject("http://hello_service/hello?name=Vasya", String.class);
            return greeting;
        }
    }

    @org.springframework.context.annotation.Configuration
    public static class Configuration {
        @Bean
        @LoadBalanced
        RestTemplate restTemplate() {
            return new RestTemplate();
        }
    }
}