package com.york.doghealthtracker.config;

import com.york.doghealthtracker.model.HealthHighlight;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "highlights")
public class HighlightConfig {

    private Map<String, HealthHighlight> map = new HashMap<>();

}
