package kirill.metlushko.scentbird.configuration;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import kirill.metlushko.scentbird.socket.MessageHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import java.util.Objects;

@RequiredArgsConstructor
@Configuration
@EnableWebSocket
public class WebSocketConfiguration implements WebSocketConfigurer {

    private final ObjectProvider<MessageHandler> messageHandlerProvider;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        var messageHandler = Objects.requireNonNull(messageHandlerProvider.getIfAvailable());
        registry.addHandler(messageHandler, "/ws/game");
    }

    @Bean
    public JsonMapper jsonMapper() {
        return (JsonMapper) new JsonMapper()
                .registerModule(new Jdk8Module());
    }

    @Bean
    public WebSocketClient getWebSocketClient() {
        return new StandardWebSocketClient();
    }
}
