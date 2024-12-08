package kirill.metlushko.scentbird.game;

import kirill.metlushko.scentbird.game.api.Board;
import kirill.metlushko.scentbird.game.api.GameState;
import kirill.metlushko.scentbird.game.api.Message;
import kirill.metlushko.scentbird.game.api.Player;
import kirill.metlushko.scentbird.game.api.Winner;
import kirill.metlushko.scentbird.service.GameStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static kirill.metlushko.scentbird.game.api.Action.ACK;
import static kirill.metlushko.scentbird.game.api.Action.MOVE;
import static kirill.metlushko.scentbird.game.api.Action.STOP;
import static kirill.metlushko.scentbird.game.api.Winner.DRAW;

@Slf4j
@RequiredArgsConstructor
@Service
public class GameEngineImpl implements GameEngine {

    private final GameStorage gameStorage;
    private final MoveSelectionService moveSelectionService;

    @Override
    public Message sync(GameState state) {
        var game = gameStorage.getGame();
        if (game.isEmpty() || game.get().isSameState(state)) {
            game = Optional.of(state);
            Player mySide = state.getMySide().next();
            game.get().setMySide(mySide);
            gameStorage.updateGame(game.get());
            return new Message(ACK, null);
        } else {
            gameStorage.removeGame();
            throw new RuntimeException("Can't sync with opponent state. Stop the game");
        }
    }

    @Override
    public Message makeMove() {
        var game = gameStorage.getGame();
        if (game.isEmpty()) {
            throw new RuntimeException("Can't make move, no active game at the moment");
        }

        var placeToMove = moveSelectionService.getNextMoveIndex(game.get().getBoardState());
        var positionAsChars = game.get().getBoardState().position().toCharArray();
        positionAsChars[placeToMove] = game.get().getMySide().name().charAt(0);

        var newBoardPosition = new String(positionAsChars);
        game.get().setBoardState(new Board(newBoardPosition));

        var nextTurn = game.get().getTurn().next();
        game.get().setTurn(nextTurn);

        gameStorage.updateGame(game.get());
        return new Message(MOVE, game.get());
    }

    @Override
    public Message acceptState(GameState state) {
        var game = gameStorage.getGame();
        if (game.isEmpty()) {
            throw new RuntimeException("Can't accept any moves, no active game at the moment");
        }

        if (!game.get().getBoardState().isValidTransition(state.getBoardState().position())) {
            log.warn("Opponent broke the rules!");
            gameStorage.removeGame();
            return new Message(STOP, game.get());
        }

        game.get().setBoardState(state.getBoardState());
        game.get().setTurn(state.getTurn());
        gameStorage.updateGame(game.get());

        if (game.get().getBoardState().getWinner().isPresent()) {
            finish(game.get());
            return new Message(STOP, game.get());
        }
        return makeMove();
    }

    @Override
    public void finish(GameState state) {
        var game = gameStorage.getGame();
        if (game.isEmpty()) {
            throw new RuntimeException("No active game at the moment to finish");
        } else {
            var winner = state.getBoardState().getWinner();
            if (winner.isEmpty()) {
                log.info("Opponent requested to stop the game");
            } else {
                logGameResult(winner.get(), game.get().getMySide());
            }
        }
        gameStorage.removeGame();
    }

    private void logGameResult(Winner winner, Player mySide) {
        if (winner == DRAW) {
            log.info("It's a draw");
        } else {
            if (winner.name().equals(mySide.name())) {
                log.info("I win :)");
            } else {
                log.info("I lost :(");
            }
        }
    }
}
