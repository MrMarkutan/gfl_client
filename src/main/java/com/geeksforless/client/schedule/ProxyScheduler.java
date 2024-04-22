package com.geeksforless.client.schedule;

import com.geeksforless.client.handler.ProxySourceQueueHandler;
import com.geeksforless.client.model.ProxyConfigHolder;
import com.geeksforless.client.service.ProxySourceService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class ProxyScheduler {

    private static final Logger LOGGER = LogManager.getLogger(ProxyScheduler.class);

    private final ProxySourceService proxySourceService;
    private final ProxySourceQueueHandler proxySourceQueueHandler;

    public ProxyScheduler(ProxySourceService proxySourceService,
                          ProxySourceQueueHandler proxySourceQueueHandler) {
        this.proxySourceService = proxySourceService;
        this.proxySourceQueueHandler = proxySourceQueueHandler;
    }

    @Scheduled(fixedDelayString = "${client.proxy.scheduler.fixeddelay:3600}",
            initialDelayString = "${client.proxy.scheduler.initialdelay:10}",
            timeUnit = TimeUnit.SECONDS)
    public void runSchedule() {
        LOGGER.info("Starting a scheduled proxy addition to queue");
        try {
            List<ProxyConfigHolder> proxies = proxySourceService.getProxies();
            proxies.forEach(proxySourceQueueHandler::addProxy);

            LOGGER.info("Successfully added {} proxies to queue", proxies.size());
        } catch (Exception e) {
            LOGGER.error("Error occurred during a scheduled proxy addition to queue {}", e.getMessage());
        }
    }
}
