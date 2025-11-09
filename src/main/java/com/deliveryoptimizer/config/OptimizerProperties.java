package com.deliveryoptimizer.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "ai.optimizer")
public class OptimizerProperties {
    private String type; // NN, CW, AI
    private String provider; // local, api (for AI optimizer)

}