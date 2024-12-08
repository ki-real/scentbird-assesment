package kirill.metlushko.scentbird.game.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static kirill.metlushko.scentbird.game.api.Player.O;
import static kirill.metlushko.scentbird.game.api.Player.X;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Tests for game state model")
class GameStateTest {

    @Nested
    class GameCreation {

        @Test
        @DisplayName("New game created with empty board")
        void newGameBoardState() {
            // when
            GameState actualResult = GameState.newGameForPlayer(X);

            // then
            assertEquals(" ".repeat(9), actualResult.getBoardState().position());
        }

        @ParameterizedTest
        @EnumSource(Player.class)
        @DisplayName("X moves first")
        void compareBoardsWhenComparingStates(Player gameFor) {
            // given
            GameState newGame = GameState.newGameForPlayer(gameFor);

            // when
            var actualResult = newGame.getTurn();

            // then
            assertEquals(X, actualResult);
            assertEquals(gameFor, newGame.getMySide());
        }
    }

    @Nested
    class GameStateComparison {

        @Test
        @DisplayName("Check board position when state comparing")
        void compareBoardsWhenComparingStates() {
            // given
            GameState gameOne = GameState.newGameForPlayer(X);
            GameState gameTwo = new GameState(gameOne.getBoardState(), gameOne.getTurn(), O);

            // when
            var actualResult = gameOne.isSameState(gameTwo);

            // then
            assertTrue(actualResult);
        }

        @Test
        @DisplayName("Check players turn when state comparing")
        void compareTurnsWhenComparingStates() {
            // given
            GameState gameOne = GameState.newGameForPlayer(X);
            GameState gameTwo = new GameState(gameOne.getBoardState(), O, O);

            // when
            var actualResult = gameOne.isSameState(gameTwo);

            // then
            assertFalse(actualResult);
        }

        @Test
        @DisplayName("Check sides when state comparing")
        void compareSidesWhenComparingStates() {
            // given
            GameState gameOne = GameState.newGameForPlayer(X);
            GameState gameTwo = new GameState(gameOne.getBoardState(), gameOne.getTurn(), X);

            // when
            var actualResult = gameOne.isSameState(gameTwo);

            // then
            assertFalse(actualResult);
        }
    }
}
