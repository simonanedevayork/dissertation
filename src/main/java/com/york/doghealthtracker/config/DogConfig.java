package com.york.doghealthtracker.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "dog")
public class DogConfig {

    private Map<String, Map<String, Float>> goalWeightRanges;

}
