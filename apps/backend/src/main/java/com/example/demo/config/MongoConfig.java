package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;

@Configuration
public class MongoConfig {

    private final MongoProperties mongoProperties;

    public MongoConfig(MongoProperties mongoProperties) {
        this.mongoProperties = mongoProperties;
    }

    @Bean
    public MongoClient mongoClient() {
        return MongoClients.create(mongoProperties.getUri());
    }

    @Bean
    public MongoTemplate mongoTemplate() {
        MongoTemplate mongoTemplate = new MongoTemplate(mongoClient(), mongoProperties.getDatabase());
        // Get the MappingMongoConverter from MongoTemplate
        MappingMongoConverter converter = (MappingMongoConverter) mongoTemplate.getConverter();

        // Disable the '_class' field from being saved in the MongoDB documents
        converter.setTypeMapper(new DefaultMongoTypeMapper(null));

        return mongoTemplate;
    }
}
