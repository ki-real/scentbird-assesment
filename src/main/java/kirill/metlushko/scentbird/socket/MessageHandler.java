package kirill.metlushko.scentbird.socket;

import com.fasterxml.jackson.databind.json.JsonMapper;
import kirill.metlushko.scentbird.configuration.properties.RulesConfiguration;
import kirill.metlushko.scentbird.events.SessionInterruptedEvent;
import kirill.metlushko.scentbird.game.GameEngine;
import kirill.metlushko.scentbird.game.api.Message;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import static kirill.metlushko.scentbird.game.api.Action.STOP;
import static org.springframework.web.socket.CloseStatus.GOING_AWAY;
import static org.springframework.web.socket.CloseStatus.NORMAL;

@Slf4j
@Setter
@RequiredArgsConstructor
@Service
public class MessageHandler extends TextWebSocketHandler {

    private final JsonMapper objectMapper;
    private final SessionHolder sessionHolder;
    private final ApplicationEventPublisher eventPublisher;
    private final RulesConfiguration rulesConfiguration;
    private final GameEngine gameManager;

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) {
        sessionHolder.setCurrentSession(session);
        log.info("Connection established");
    }

    @Override
    @SneakyThrows
    protected void handleTextMessage(@NonNull WebSocketSession session, TextMessage message) {
        var parsedMessage = objectMapper.readValue(message.getPayload(), Message.class);
        var state = parsedMessage.payload();
        if (parsedMessage.action() == STOP) {
            gameManager.finish(state);
            session.close(NORMAL);
            return;
        }
        Supplier<Message> reaction = switch (parsedMessage.action()) {
            case SYNC -> () -> gameManager.sync(state);
            case ACK -> gameManager::makeMove;
            case MOVE -> () -> gameManager.acceptState(state);
            default -> throw new IllegalStateException("Unexpected value: " + parsedMessage.action());
        };
        CompletableFuture.supplyAsync(reaction, CompletableFuture.delayedExecutor(rulesConfiguration.delay(), rulesConfiguration.unit()))
                .thenApply(this::wrapWithTextMessage)
                .thenAccept(textMessage -> {
                    try {
                        log.info("Send a message: {}", textMessage);
                        session.sendMessage(textMessage);
                    } catch (IOException e) {
                        throw new RuntimeException("Can't send message to opponent", e);
                    }
                });
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus closeStatus) {
        sessionHolder.removeCurrentSession();
        if (closeStatus.equalsCode(GOING_AWAY) || closeStatus.equalsCode(NORMAL)) {
            log.info("Connection closed");
        } else {
            log.info("Connection was unexpectedly closed with a status: {}. Try to reconnect", closeStatus);
            eventPublisher.publishEvent(new SessionInterruptedEvent());
        }
    }

    @SneakyThrows
    public TextMessage wrapWithTextMessage(Message message) {
        return new TextMessage(objectMapper.writeValueAsString(message));
    }
}
