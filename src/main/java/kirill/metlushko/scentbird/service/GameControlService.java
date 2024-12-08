package kirill.metlushko.scentbird.service;

import kirill.metlushko.scentbird.game.api.GameState;

import java.util.Optional;

public interface GameControlService {

    Optional<GameState> startGame();

    Optional<GameState> getGameState();

    void stopGame();
}
