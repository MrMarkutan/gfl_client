package com.geeksforless.client.service;

import com.geeksforless.client.model.ProxyConfigHolder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Service
public class ProxySourceServiceFile implements ProxySourceService {

    private static final Logger LOGGER = LogManager.getLogger(ProxySourceServiceFile.class);
    private final String PROXY_CONFIG_HOLDER_JSON = "ProxyConfigHolder.json";

    private final JsonConfigReader jsonConfigReader;

    public ProxySourceServiceFile(JsonConfigReader jsonConfigReader) {
        this.jsonConfigReader = jsonConfigReader;
    }

    @Override
    public List<ProxyConfigHolder> getProxies() {

        // Look for ProxyConfigHolder.json inside /resources folder
        byte[] file = null;
        try {
            file = Objects.requireNonNull(
                    getClass().getClassLoader().getResourceAsStream(PROXY_CONFIG_HOLDER_JSON)
            ).readAllBytes();
        } catch (IOException e) {
            LOGGER.error(e);
        }

        return jsonConfigReader.readFile(file, ProxyConfigHolder.class);
    }
}
