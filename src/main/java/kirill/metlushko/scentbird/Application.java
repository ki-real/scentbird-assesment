package kirill.metlushko.scentbird;

import kirill.metlushko.scentbird.configuration.properties.OpponentConfiguration;
import kirill.metlushko.scentbird.configuration.properties.ReconnectConfiguration;
import kirill.metlushko.scentbird.configuration.properties.RulesConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(value = {OpponentConfiguration.class, RulesConfiguration.class, ReconnectConfiguration.class})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
