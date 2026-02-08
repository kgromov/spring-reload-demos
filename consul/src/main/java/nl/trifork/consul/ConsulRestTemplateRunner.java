package nl.trifork.consul;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class ConsulRestTemplateRunner implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(ConsulRestTemplateRunner.class);

    private final RestTemplate restTemplate;
    private final RestClient.Builder builder;
    @Value("${spring.application.name}")
    private String applicationName;

    public ConsulRestTemplateRunner(RestTemplate restTemplate, RestClient.Builder builder) {
        this.restTemplate = restTemplate;
        this.builder = builder;
    }

    @Override
    public void run(String... args) {
        log.info("=".repeat(10) .concat(ConsulRestTemplateRunner.class.getSimpleName()).concat("=").repeat(10));
        String serviceUri = "http://%s/api".formatted(applicationName);
        var config = restTemplate.getForObject(serviceUri, Map.class);
        log.info("Config data (via rest template): {}", config);

        Map<String, String> config2 = builder.baseUrl(serviceUri).build()
                .get()
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
        log.info("Config data (via rest client): {}", config2);
        log.info("=".repeat(30));
    }
}
