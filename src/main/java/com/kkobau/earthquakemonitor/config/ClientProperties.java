package com.kkobau.earthquakemonitor.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "earthquake-client")
@Data
public class ClientProperties {
    private String url;
}
