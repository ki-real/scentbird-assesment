package kirill.metlushko.scentbird.service;

import kirill.metlushko.scentbird.game.api.GameState;

import java.util.Optional;

public interface GameStorage {

    GameState createGame();

    Optional<GameState> getGame();

    GameState updateGame(GameState newState);

    void removeGame();
}
