package examplebeans.configuration;

import javax.sql.DataSource;
import lombok.AllArgsConstructor;
import org.flywaydb.core.Flyway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
public class FlywayConfiguration {
  private DataSource dataSource;

  @Bean(initMethod = "migrate")
  public Flyway flyway() {
    Flyway flyway = new Flyway();
    flyway.setDataSource(dataSource);
    flyway.setLocations("filesystem:C:\\Users\\Artem_Kunats\\IdeaProjects\\folders\\src\\main\\webapp\\db\\migration");
    flyway.setBaselineOnMigrate(true);
    return flyway;
  }

}
