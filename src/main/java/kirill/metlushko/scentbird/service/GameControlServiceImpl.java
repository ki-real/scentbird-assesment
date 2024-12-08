package kirill.metlushko.scentbird.service;

import kirill.metlushko.scentbird.configuration.properties.OpponentConfiguration;
import kirill.metlushko.scentbird.configuration.properties.ReconnectConfiguration;
import kirill.metlushko.scentbird.events.SessionInterruptedEvent;
import kirill.metlushko.scentbird.game.api.GameState;
import kirill.metlushko.scentbird.game.api.Message;
import kirill.metlushko.scentbird.socket.MessageHandler;
import kirill.metlushko.scentbird.socket.SessionHolder;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.socket.client.WebSocketClient;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static kirill.metlushko.scentbird.game.api.Action.STOP;
import static kirill.metlushko.scentbird.game.api.Action.SYNC;
import static org.springframework.http.HttpStatus.CONFLICT;

@Slf4j
@RequiredArgsConstructor
@Service
public class GameControlServiceImpl implements GameControlService {

    private final OpponentConfiguration opponentConfiguration;
    private final ReconnectConfiguration reconnectConfiguration;
    private final WebSocketClient webSocketClient;
    private final MessageHandler messageHandler;
    private final GameStorage gameStorage;
    private final SessionHolder sessionHolder;

    @Override
    public Optional<GameState> startGame() {
        if (gameStorage.getGame().isPresent()) {
            throw new ResponseStatusException(CONFLICT, "Can't start a new game while another game is in progress");
        }
        var game = gameStorage.createGame();
        return syncGames(game)
                .thenApplyAsync(Optional::of)
                .exceptionally(e -> Optional.empty())
                .join();
    }

    @Override
    public Optional<GameState> getGameState() {
        return gameStorage.getGame();
    }

    @Override
    public void stopGame() {
        var game = gameStorage.getGame();
        if (game.isPresent()) {
            gameStorage.removeGame();
            var message = new Message(STOP, game.get());
            var textMessage = messageHandler.wrapWithTextMessage(message);
            sessionHolder.getOpenSession()
                    .ifPresent(s -> {
                        try {
                            s.sendMessage(textMessage);
                        } catch (IOException e) {
                            throw new RuntimeException("Can't send message to opponent to stop the game", e);
                        }
                    });
        }
    }

    @SneakyThrows
    @EventListener(SessionInterruptedEvent.class)
    private void resumeGame() {
        var game = gameStorage.getGame();
        if (game.isEmpty()) {
            log.info("No active game to resume");
            return;
        }
        if (game.get().getTurn() != game.get().getMySide()) {
            log.info("It's not my turn, wait for request to resume from the opponent");
            return;
        }
        GameState result = null;
        short counter = reconnectConfiguration.attempts();
        while (result == null && counter-- > 0) {
            try {
                result = syncGames(game.get())
                        .join();
            } catch (CompletionException e) {
                log.info("Couldn't resume game, retry", e);
                Thread.sleep(reconnectConfiguration.coolDown());
            }
        }
        if (result == null) {
            log.error("Couldn't resume game, in {} tries", reconnectConfiguration.attempts());
        }
    }

    private CompletableFuture<GameState> syncGames(GameState game) {
        return webSocketClient.execute(messageHandler, opponentConfiguration.buildUriTemplate())
                .thenApplyAsync(session -> {
                    var message = new Message(SYNC, game);
                    var textMessage = messageHandler.wrapWithTextMessage(message);
                    try {
                        session.sendMessage(textMessage);
                    } catch (IOException e) {
                        throw new RuntimeException("Can't send message to opponent to sync game state", e);
                    }
                    return game;
                });
    }
}
