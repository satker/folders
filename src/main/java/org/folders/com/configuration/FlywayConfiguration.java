package org.folders.com.configuration;

import javax.sql.DataSource;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlywayConfiguration {

  @Bean(initMethod = "migrate")
  public Flyway flyway(@Autowired DataSource dataSource) {
    Flyway flyway = new Flyway();
    flyway.setDataSource(dataSource);
    flyway.setBaselineOnMigrate(true);
    return flyway;
  }

}
