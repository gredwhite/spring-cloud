package org.ntkachev.microservices.hello_world;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextListener;

@SpringBootApplication
@EnableEurekaClient
@RibbonClient(name = "say-hello"/*, configuration = RibbonConfig.class*/)
@EnableAutoConfiguration
@EnableOAuth2Sso
public class HelloWorldStarter {

    public static void main(String[] args) {
        SpringApplication.run(HelloWorldStarter.class, args);
    }

    @Bean
    public RequestContextListener requestContextListener() {
        return new RequestContextListener();
    }


    @RestController
    @EnableDiscoveryClient
    @EnableCircuitBreaker
    public static class HelloWorldController {
        @Autowired
        private RestTemplate restTemplate;
        @Autowired
        private OAuth2RestTemplate oAuth2RestTemplate;
        @Autowired
        private DiscoveryClient discoveryClient;

        @GetMapping("/helloWorld")
        @HystrixCommand(groupKey = "somethingProxy", commandKey = "retrieveSomething", ignoreExceptions = {
                IllegalArgumentException.class }, fallbackMethod = "reliable", commandProperties = {
                @HystrixProperty(name = "execution.isolation.strategy", value = "SEMAPHORE") })
        public String hello() {
            try {
                return this.oAuth2RestTemplate.getForObject("http://hello-service/h/hello?name=World", String.class);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
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

        @Bean
        @ConfigurationProperties("security.oauth2")
        public ClientResources clientResources() {
            return new ClientResources();
        }

        @Autowired
        private OAuth2ClientContext oAuth2ClientContext;

        @Bean
        @LoadBalanced
        public OAuth2RestTemplate oAuth2RestTemplate() {
            return new OAuth2RestTemplate(clientResources().getClient(), oAuth2ClientContext);
        }

        class ClientResources {

            @NestedConfigurationProperty
            private AuthorizationCodeResourceDetails client = new AuthorizationCodeResourceDetails();

            @NestedConfigurationProperty
            private ResourceServerProperties resource = new ResourceServerProperties();

            public AuthorizationCodeResourceDetails getClient() {
                return client;
            }

            public ResourceServerProperties getResource() {
                return resource;
            }
        }
    }
}

