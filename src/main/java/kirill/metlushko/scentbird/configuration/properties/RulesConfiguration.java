package kirill.metlushko.scentbird.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

@ConfigurationProperties("game.rules")
public record RulesConfiguration(
        Duration timeToMove
) {

    public long delay() {
        return timeToMove.get(ChronoUnit.SECONDS);
    }

    public TimeUnit unit() {
        return TimeUnit.SECONDS;
    }
}
