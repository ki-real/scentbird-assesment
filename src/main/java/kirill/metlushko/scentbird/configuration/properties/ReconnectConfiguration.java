package kirill.metlushko.scentbird.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties("game.reconnect")
public record ReconnectConfiguration(
        Short attempts,
        Duration coolDown
) {
}
