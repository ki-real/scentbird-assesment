package kirill.metlushko.scentbird.game;

import kirill.metlushko.scentbird.game.api.Board;

public interface MoveSelectionService {

    int getNextMoveIndex(Board currentBoardState);
}
