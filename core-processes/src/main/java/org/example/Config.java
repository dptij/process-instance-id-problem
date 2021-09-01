package org.example;

import org.apache.commons.lang3.ArrayUtils;
import org.camunda.bpm.engine.impl.history.HistoryLevel;
import org.camunda.bpm.engine.impl.history.event.HistoryEvent;
import org.camunda.bpm.engine.impl.history.handler.CompositeHistoryEventHandler;
import org.camunda.bpm.engine.impl.history.handler.HistoryEventHandler;
import org.camunda.bpm.engine.spring.SpringProcessEngineConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.apache.commons.lang3.ArrayUtils.addAll;

@Configuration
public class Config {
    private static final Logger LOGGER = LoggerFactory.getLogger(Config.class);

    @Autowired
    @Qualifier("camundaBpmDataSource")
    private DataSource dataSource;

    @Autowired
    @Qualifier("camundaTxManager")
    private PlatformTransactionManager txManager;

    @Autowired
    private ResourcePatternResolver resourceLoader;

    @Bean
    public SpringProcessEngineConfiguration processEngineConfiguration() {
        final SpringProcessEngineConfiguration config = new SpringProcessEngineConfiguration();

        config.setDataSource(dataSource);
        config.setTransactionManager(txManager);
        config.setDatabaseSchemaUpdate("true");

        config.setHistory(HistoryLevel.HISTORY_LEVEL_FULL.getName());
        config.setJobExecutorActivate(true);
        config.setMetricsEnabled(false);
        final Logger logger = LoggerFactory.getLogger("History Event Handler");

        final HistoryEventHandler testHistoryEventHandler = new HistoryEventHandler() {

            @Override
            public void handleEvent(final HistoryEvent evt) {
                LOGGER.debug("handleEvent | " + evt.getProcessInstanceId() + " | "
                        + evt.toString());
            }

            @Override
            public void handleEvents(final List<HistoryEvent> events) {
                for (final HistoryEvent curEvent : events) {
                    handleEvent(curEvent);
                }
            }
        };

        config.setHistoryEventHandler(new CompositeHistoryEventHandler(Collections.singletonList(testHistoryEventHandler)));

        try {
            final Resource[] bpmnResources = resourceLoader.getResources("classpath:*.bpmn");
            final Resource[] dmnResources = resourceLoader.getResources("classpath:*.dmn");
            config.setDeploymentResources(addAll(bpmnResources, dmnResources));
        } catch (final IOException exception) {
            exception.printStackTrace();
            LOGGER.error("An error occurred while trying to deploy BPMN and DMN files", exception);
        }

        return config;
    }
}