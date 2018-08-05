package examplebeans.configuration;

import javax.sql.DataSource;
import lombok.AllArgsConstructor;
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
    flyway.setLocations("filesystem:D:\\java_projects\\folders\\src\\main\\webapp\\db\\migration");
    flyway.setBaselineOnMigrate(true);
    return flyway;
  }

}
