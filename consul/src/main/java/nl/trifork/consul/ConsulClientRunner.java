package nl.trifork.consul;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.QueryParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ConsulClientRunner implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(ConsulClientRunner.class);

    private final ConsulClient consulClient;
    @Value("${spring.application.name}")
    private String applicationName;

    public ConsulClientRunner(ConsulClient consulClient) {
        this.consulClient = consulClient;
    }

    @Override
    public void run(String... args) {
        log.info("=".repeat(10).concat(ConsulClientRunner.class.getSimpleName()).concat("=").repeat(10));
        log.info("services = {}", consulClient.getAgentServices());
        log.info("self = {}", consulClient.getAgentSelf());
        log.info("Health check = {}", consulClient.getHealthChecksState(QueryParams.DEFAULT));
        log.info("Keys at config location = {}", consulClient.getKVKeysOnly("config"));
        // provide response with metadata (as key); value is decoded by default. To get value - use #getDecodedValue
        log.info("Some K/V value = {}", consulClient.getKVValue("config/%s/basic-key".formatted(applicationName)).getValue());
        log.info("=".repeat(30));
    }
}
