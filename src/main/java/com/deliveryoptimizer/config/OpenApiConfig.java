package com.deliveryoptimizer.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.parser.core.models.ParseOptions;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() throws IOException {
        // Get the absolute path to the YAML file in the classpath
        ClassPathResource resource = new ClassPathResource("static/openapi.yaml");
        String absolutePath = resource.getFile().getAbsolutePath();

        // Configure parse options to resolve external references
        ParseOptions parseOptions = new ParseOptions();
        parseOptions.setResolve(true);
        parseOptions.setResolveFully(true);

        // Parse the YAML with the absolute path so $refs can be resolved
        SwaggerParseResult result = new OpenAPIV3Parser().readLocation(absolutePath, null, parseOptions);

        if (result.getOpenAPI() != null) {
            // Return the parsed OpenAPI object
            return result.getOpenAPI();
        } else {
            // Log errors and throw exception
            String errors = result.getMessages() != null ?
                    String.join(", ", result.getMessages()) : "Unknown error";
            throw new RuntimeException("Failed to parse OpenAPI YAML: " + errors);
        }
    }
}