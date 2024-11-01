package com.exmoney.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Configuration
@ConfigurationProperties(prefix = "exmoney.application.default")
@Data
public class DefaultCategoryConfiguration {

    private List<String> categories = new ArrayList<>();
}
