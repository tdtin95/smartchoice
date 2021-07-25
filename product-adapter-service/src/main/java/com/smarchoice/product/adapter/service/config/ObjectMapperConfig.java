package com.smarchoice.product.adapter.service.config;

import com.smarchoice.product.adapter.service.dto.AvroJsonMixin;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ObjectMapperConfig {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
        return builder -> builder.mixIn(SpecificRecordBase.class, AvroJsonMixin.class);
    }
}
