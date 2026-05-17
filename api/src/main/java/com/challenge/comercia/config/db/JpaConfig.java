package com.challenge.comercia.config.db;

import java.util.Objects;

import javax.sql.DataSource;

import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * JPA configuration.
 */
@Configuration
@EnableJpaRepositories(basePackages = "com.challenge.comercia.repository",
    entityManagerFactoryRef = "entityManagerFactory",
    transactionManagerRef = "transactionManager")
public class JpaConfig {

  @Bean
  public LocalContainerEntityManagerFactoryBean entityManagerFactory(
      DataSource dataSource, EntityManagerFactoryBuilder builder) {
    return builder.dataSource(dataSource)
        .packages("com.challenge.comercia.entity").build();
  }

  @Bean
  public PlatformTransactionManager transactionManager(
      LocalContainerEntityManagerFactoryBean entityManagerFactory) {
    return new JpaTransactionManager(
        Objects.requireNonNull(entityManagerFactory.getObject()));
  }
}


