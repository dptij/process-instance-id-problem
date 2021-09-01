package org.example;

import org.camunda.bpm.engine.impl.cfg.ProcessEnginePlugin;
import org.camunda.bpm.engine.spring.SpringProcessEngineConfiguration;
import org.camunda.bpm.spring.boot.starter.util.SpringBootProcessEnginePlugin;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@EnableConfigurationProperties
public class CamundaDb {
    @Bean(name = "camundaBpmDataSource")
    @ConfigurationProperties(prefix = "com.camunda.datasource")
    public DataSource dsCamunda() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "camundaTxManager")
    public PlatformTransactionManager camundaTransactionManager(@Qualifier("camundaBpmDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
    @Bean
    public ProcessEnginePlugin fixCamundaTransactionManager(@Qualifier("camundaTxManager") PlatformTransactionManager camundaTxManager) {
        return new SpringBootProcessEnginePlugin() {
            @Override
            public void preInit(final SpringProcessEngineConfiguration configuration) {
                configuration.setTransactionManager(camundaTxManager);
            }
        };
    }
}
