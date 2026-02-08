package nl.trifork.consul;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RefreshScope
@RestController
@RequestMapping("/api")
public class ConfigRestController {
    @Value("${basic-key}")
    private String basicKey;

    private final MutableConfigProperties mutableConfig;
    private final ImmutableConfigProperties immutableConfig;

    public ConfigRestController(MutableConfigProperties mutableConfig, ImmutableConfigProperties immutableConfig) {
        this.mutableConfig = mutableConfig;
        this.immutableConfig = immutableConfig;
    }

    @GetMapping
    Map<String, String> getConfig() {
        var properties = new LinkedHashMap<String, String>();
        properties.put("basic-key", basicKey);
        properties.put("mutable-config.key", mutableConfig.getKey());
        properties.put("mutable-config.number", Integer.toString(mutableConfig.getNumber()));
        properties.put("immutable-config.key", immutableConfig.key());
        return  properties;
    }
}
