package kirill.metlushko.scentbird.game;

import kirill.metlushko.scentbird.game.api.Board;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class RandomMoveSelectionService implements MoveSelectionService {

    private static final Random RND = new Random();

    @Override
    public int getNextMoveIndex(Board currentBoardState) {
        if (currentBoardState.position().indexOf(' ') < 0) {
            throw new IllegalArgumentException("Can't select a cell to move on a filled board");
        }
        var currentBoardPosition = currentBoardState.position().toCharArray();
        var placeToMove = RND.nextInt(9);
        while (currentBoardPosition[placeToMove] != ' ') {
            placeToMove = RND.nextInt(9);
        }
        return placeToMove;
    }
}
