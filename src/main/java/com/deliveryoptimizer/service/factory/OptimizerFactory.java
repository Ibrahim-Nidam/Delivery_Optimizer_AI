package com.deliveryoptimizer.service.factory;

import com.deliveryoptimizer.annotation.OptimizerType;
import com.deliveryoptimizer.config.OptimizerProperties;
import com.deliveryoptimizer.service.interfaces.TourOptimizer;
import lombok.RequiredArgsConstructor;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class OptimizerFactory {
    private final ApplicationContext context;
    private final OptimizerProperties properties;

    public TourOptimizer getOptimizer() {
        String optimizerType = properties.getType();

        if (optimizerType == null || optimizerType.isEmpty()) {
            throw new IllegalStateException("Optimizer type not configured in application.yml");
        }

        Map<String, Object> optimizers = context.getBeansWithAnnotation(OptimizerType.class);

        for (Object optimizer : optimizers.values()) {
            // Get real class behind Spring proxy
            Class<?> targetClass = AopUtils.getTargetClass(optimizer);
            OptimizerType annotation = targetClass.getAnnotation(OptimizerType.class);

            if (annotation != null && annotation.value().equalsIgnoreCase(optimizerType)) {
                return (TourOptimizer) optimizer;
            }
        }

        throw new IllegalArgumentException("No optimizer found for type: " + optimizerType +
                ". Available types: NN, CW, AI");
    }

    public TourOptimizer getOptimizer(String type) {
        Map<String, Object> optimizers = context.getBeansWithAnnotation(OptimizerType.class);

        for (Object optimizer : optimizers.values()) {
            OptimizerType annotation = optimizer.getClass().getAnnotation(OptimizerType.class);
            if (annotation != null && annotation.value().equalsIgnoreCase(type)) {
                return (TourOptimizer) optimizer;
            }
        }

        throw new IllegalArgumentException("No optimizer found for type: " + type);
    }
}