package nl.trifork.consul;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DiscoveryRunner implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(DiscoveryRunner.class);

    private final DiscoveryClient discoveryClient;
    @Value("${spring.application.name}")
    private String applicationName;

    public DiscoveryRunner(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }

    @Override
    public void run(String... args) {
        log.info("=".repeat(10) .concat(DiscoveryRunner.class.getSimpleName()).concat("=").repeat(10));
        log.info("Description = {}", discoveryClient.description());
        List<String> serviceIds = discoveryClient.getServices();
        log.info("Registered {} service(s)", serviceIds);
        List<ServiceInstance> instances = discoveryClient.getInstances(applicationName);
        log.info("Service {} instance(s) details: {}", applicationName, instances);
        log.info("=".repeat(30));
    }
}
