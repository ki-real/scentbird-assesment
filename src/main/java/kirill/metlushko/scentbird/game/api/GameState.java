package kirill.metlushko.scentbird.game.api;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static kirill.metlushko.scentbird.game.api.Board.INITIAL_POSITION;
import static kirill.metlushko.scentbird.game.api.Player.X;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class GameState {

    private Board boardState;
    private Player turn;
    private Player mySide;

    public static GameState newGameForPlayer(Player side) {
        return new GameState(new Board(INITIAL_POSITION), X, side);
    }

    public boolean isSameState(GameState other) {
        var sameBoardPosition = boardState.position().equalsIgnoreCase(other.getBoardState().position());
        var sameTurn = turn.equals(other.getTurn());
        var otherSide = !mySide.equals(other.getMySide());
        return sameBoardPosition && sameTurn && otherSide;
    }
}
