package com.challenge.comercia.config.db;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

/**
 * Database initialization.
 */
@Configuration
public class DatabaseInitConfig {

  @Bean
  public DataSourceInitializer dataSourceInitializer(DataSource dataSource) {
    DataSourceInitializer dataSourceInitializer = new DataSourceInitializer();

    ResourceDatabasePopulator resourceDatabasePopulator =
        new ResourceDatabasePopulator();
    dataSourceInitializer.setDatabasePopulator(resourceDatabasePopulator);
    dataSourceInitializer.setDataSource(dataSource);

    return dataSourceInitializer;
  }
}


