package org.fbsearch.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Created by magnus on 18/08/14.
 */
@SpringBootApplication
@ComponentScan(basePackages = {"org.fbsearch"})
@EntityScan(basePackages = {"org.fbsearch"})
@EnableJpaRepositories(basePackages = {"org.fbsearch"})
@EnableAsync
@PropertySources({
        @PropertySource("classpath:application.properties"),
        @PropertySource("file:${user.home}/properties/fbsearch.properties")})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
