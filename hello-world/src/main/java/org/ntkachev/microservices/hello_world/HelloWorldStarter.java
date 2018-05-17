package org.ntkachev.microservices.hello_world;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;


@SpringBootApplication
@EnableEurekaClient
@RibbonClient(name = "say-hello"/*, configuration = RibbonConfig.class*/)
@EnableAutoConfiguration
@EnableOAuth2Sso
public class HelloWorldStarter {

    public static void main(String[] args) {
        SpringApplication.run(HelloWorldStarter.class, args);
    }


    @RestController
    @EnableDiscoveryClient
    @EnableCircuitBreaker
    public static class HelloWorldController {
        @Autowired
        private RestTemplate restTemplate;
        @Autowired
        private DiscoveryClient discoveryClient;

        @GetMapping("/helloWorld")
        @HystrixCommand(fallbackMethod = "reliable")
        public String hello() {
            //String response = restTemplate.getForObject("http://localhost:8080/hello?name=World", String.class);
            return this.restTemplate.getForObject("http://hello-service/hello?name=World", String.class);
        }

        public String reliable() {
            return "Could not get response from service";
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