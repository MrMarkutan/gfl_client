package com.geeksforless.client.handler.impl;

import com.geeksforless.client.handler.ScenarioSourceQueueHandler;
import com.geeksforless.client.model.Scenario;
import com.geeksforless.client.service.Publisher;
import com.geeksforless.client.service.PublisherImpl;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.LinkedBlockingQueue;

@Service
public class ScenarioSourceQueueHandlerImpl implements ScenarioSourceQueueHandler {

    private final LinkedBlockingQueue<Scenario> queue = new LinkedBlockingQueue<>();
    private final Publisher publisher;

    public ScenarioSourceQueueHandlerImpl(Publisher publisher) {
        this.publisher = publisher;
    }

    @Override
    public void addScenario(Scenario scenario) {
        queue.add(scenario);

        publisher.sendMessage();
    }
    @Override
    public Optional<Scenario> takeScenario() {
        return Optional.ofNullable(queue.poll());
    }
    public LinkedBlockingQueue<Scenario> getQueue() {
        return queue;
    }
}
