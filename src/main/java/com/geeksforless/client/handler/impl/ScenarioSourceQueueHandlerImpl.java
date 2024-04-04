package com.geeksforless.client.handler.impl;

import com.geeksforless.client.handler.ScenarioSourceQueueHandler;
import com.geeksforless.client.model.Scenario;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.LinkedBlockingQueue;

@Service
public class ScenarioSourceQueueHandlerImpl implements ScenarioSourceQueueHandler {

    private final LinkedBlockingQueue<Scenario> queue = new LinkedBlockingQueue<>();

    @Override
    public void addScenario(Scenario scenario) {
        queue.add(scenario);
    }

    @Override
    public Optional<Scenario> getScenario() throws InterruptedException {
        return Optional.of(queue.take());
    }
}