package com.example.demo.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "spring.data.mongodb")
@Getter
@Setter
@Component
public class MongoProperties {

    private String uri;
    private String database;
}
