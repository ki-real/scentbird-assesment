package kirill.metlushko.scentbird.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("game.opponent")
public record OpponentConfiguration(
        String host,
        Integer port,
        String wsPath
) {

    public String buildUriTemplate() {
        return host + ":" + port + wsPath;
    }
}
