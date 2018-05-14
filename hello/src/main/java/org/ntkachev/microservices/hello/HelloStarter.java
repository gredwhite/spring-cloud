package org.ntkachev.microservices.hello;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.net.UnknownHostException;

@SpringBootApplication
@EnableEurekaClient
@EnableScheduling
public class HelloStarter {


    public static void main(String[] args) {
        SpringApplication.run(HelloStarter.class, args);
    }

    @RestController
    public static class HelloController {

        public static final Integer REQUEST_PER_MINUTE_LIMIT = 10;
        public Integer requestCounter = 0;

        private Logger logger = LoggerFactory.getLogger(HelloController.class);

        @GetMapping("/hello")

        public String hello(@RequestParam("name") String name) throws UnknownHostException, InterruptedException {
            logger.info("Starting performing request: {}", InetAddress.getLocalHost());
            synchronized (this) {
                if (++requestCounter > REQUEST_PER_MINUTE_LIMIT) {
                    Thread.sleep(300);
                    throw new RuntimeException("Could process request");
                }
            }
            return "Hello " + name + "!";
        }
        //each minute
        @Scheduled(fixedRate = 60_000)
        public void resetCounter(){
            synchronized (this){
                requestCounter = 0;
            }
        }
    }
}