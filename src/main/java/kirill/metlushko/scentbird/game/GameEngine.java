package kirill.metlushko.scentbird.game;

import kirill.metlushko.scentbird.game.api.GameState;
import kirill.metlushko.scentbird.game.api.Message;

public interface GameEngine {

    Message sync(GameState state);

    Message acceptState(GameState state);

    Message makeMove();

    void finish(GameState state);
}
