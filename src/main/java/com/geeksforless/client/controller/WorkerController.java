package com.geeksforless.client.controller;

import com.geeksforless.client.handler.ProxySourceQueueHandler;
import com.geeksforless.client.handler.ScenarioSourceQueueHandler;
import com.geeksforless.client.mapper.ScenarioMapper;
import com.geeksforless.client.model.ProxyConfigHolder;
import com.geeksforless.client.model.Scenario;
import com.geeksforless.client.model.dto.ScenarioDtoExternal;
import com.geeksforless.client.model.dto.ScenarioDtoInternal;
import com.geeksforless.client.model.dto.factory.ScenarioDtoFactory;
import com.geeksforless.client.model.factory.ProxyConfigHolderFactory;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/internal")
public class WorkerController {
    private static final Logger logger = LogManager.getLogger(WorkerController.class);

    private final ScenarioSourceQueueHandler scenarioSourceQueueHandler;
    private final ProxySourceQueueHandler proxySourceQueueHandler;
    private final ScenarioMapper scenarioMapper;
    private final ScenarioDtoFactory scenarioDtoFactory;
    private final ProxyConfigHolderFactory proxyConfigHolderFactory;

    public WorkerController(ScenarioSourceQueueHandler scenarioSourceQueueHandler,
                            ProxySourceQueueHandler proxySourceQueueHandler,
                            ScenarioMapper scenarioMapper,
                            ScenarioDtoFactory scenarioDtoFactory,
                            ProxyConfigHolderFactory proxyConfigHolderFactory) {
        this.scenarioSourceQueueHandler = scenarioSourceQueueHandler;
        this.proxySourceQueueHandler = proxySourceQueueHandler;
        this.scenarioMapper = scenarioMapper;
        this.scenarioDtoFactory = scenarioDtoFactory;
        this.proxyConfigHolderFactory = proxyConfigHolderFactory;
    }

    @PostMapping("/result")
    public ResponseEntity<?> setResult(@Valid @RequestBody ScenarioDtoInternal scenarioDto) {
        logger.info("Worker sending result");
        logger.info("Scenario {} going to updated", scenarioDto.getName());
        scenarioSourceQueueHandler.updateScenario(scenarioDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/proxy")
    public ResponseEntity<ProxyConfigHolder> getProxy() {
        logger.info("client requesting proxy");
        return ResponseEntity.ok(Optional.ofNullable(proxySourceQueueHandler.getProxy())
                .orElse(proxyConfigHolderFactory.createEmpty())
        );
    }

    @GetMapping("/scenario")
    public ResponseEntity<ScenarioDtoExternal> getScenario() {
        logger.info("client requesting scenario");
        return ResponseEntity.ok(scenarioSourceQueueHandler.takeScenario()
                .map(scenarioMapper::toDtoExternal)
                .orElse(scenarioDtoFactory.createExternalEmpty())
        );
    }

    @GetMapping("/scenarios")
    public ResponseEntity<List<ScenarioDtoInternal>> getScenarios() {
        logger.info("Client requesting scenarios");

        List<Scenario> scenarios = scenarioSourceQueueHandler.takeScenarios();
        if (scenarios.isEmpty())
            return ResponseEntity.ok(List.of(scenarioDtoFactory.createInternalEmpty()));

        return ResponseEntity.ok(scenarios.stream().map(scenarioMapper::toDtoInternal).toList());
    }
}
