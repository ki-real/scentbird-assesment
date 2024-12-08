package kirill.metlushko.scentbird.service;

import kirill.metlushko.scentbird.game.api.GameState;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static kirill.metlushko.scentbird.game.api.Player.X;

@Service
public class GameStorageImpl implements GameStorage {

    private Optional<GameState> game = Optional.empty();

    @Override
    public GameState createGame() {
        game = Optional.of(GameState.newGameForPlayer(X));
        return game.get();
    }

    @Override
    public Optional<GameState> getGame() {
        return game;
    }

    @Override
    public GameState updateGame(GameState newState) {
        game = Optional.of(newState);
        return game.get();
    }

    @Override
    public void removeGame() {
        game = Optional.empty();
    }
}
