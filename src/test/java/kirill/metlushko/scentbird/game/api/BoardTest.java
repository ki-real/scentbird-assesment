package kirill.metlushko.scentbird.game.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static kirill.metlushko.scentbird.game.api.Board.INITIAL_POSITION;
import static kirill.metlushko.scentbird.game.api.Winner.DRAW;
import static kirill.metlushko.scentbird.game.api.Winner.O;
import static kirill.metlushko.scentbird.game.api.Winner.X;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Tests for board model")
class BoardTest {

    @Nested
    @DisplayName("Calculate winner with the given board position")
    class WinnerCalculation {
        private static Stream<Arguments> clearWinnersPositions() {
            return Stream.of(
                    Arguments.of("XXXOO    ", X),
                    Arguments.of("XO XO X  ", X),
                    Arguments.of("OOOXX X  ", O),
                    Arguments.of("OX OXXO  ", O),
                    Arguments.of("XOO X   X", X),
                    Arguments.of("OXX OX  O", O),
                    Arguments.of("OOX X X  ", X),
                    Arguments.of("XXOXO O  ", O)
            );
        }

        @ParameterizedTest
        @MethodSource("clearWinnersPositions")
        @DisplayName("Winner calculation with clear winner position")
        void winnerCalculation(String boardPosition, Winner expectedWinner) {
            // given
            Board board = new Board(boardPosition);

            // when
            var actualResult = board.getWinner();

            // then
            assertTrue(actualResult.isPresent());
            assertEquals(expectedWinner, actualResult.get());
        }

        @Test
        @DisplayName("Winner can't be declared with uncertain position and at least one empty cell")
        void noWinnerYet() {
            // given
            Board board = new Board("OOXXXOOX ");

            // when
            var winner = board.getWinner();

            // then
            assertTrue(winner.isEmpty());
        }

        @Test
        @DisplayName("Draw should be declared if there is no clear winner and there are no empty cells left")
        void noWinnerAndNoEmptyCells() {
            // given
            Board board = new Board("OOXXXOOXO");

            // when
            var actualResult = board.getWinner();

            // then
            assertTrue(actualResult.isPresent());
            assertEquals(DRAW, actualResult.get());
        }
    }

    @Nested
    @DisplayName("Validate possible position transition")
    class TransitionValidation {

        @Test
        @DisplayName("No characters other then ' ', 'X' and 'O' should be allowed")
        void unknownCharacters() {
            // given
            Board board = new Board(INITIAL_POSITION);

            // when
            var actualResult = board.isValidTransition(" ".repeat(8) + "Y");

            // then
            assertFalse(actualResult);
        }

        @Test
        @DisplayName("Position length must stay the same as initial position length")
        void differentPositionLength() {
            // given
            Board board = new Board(INITIAL_POSITION);

            // when
            var actualResult = board.isValidTransition(" ".repeat(8) + "XXXX");

            // then
            assertFalse(actualResult);
        }

        @Test
        @DisplayName("Initial position is fully empty")
        void emptinessOfInitialPosition() {
            // given
            Board board = new Board(INITIAL_POSITION);

            // when
            var actualResult = board.position();

            // then
            assertTrue(actualResult.isBlank());
        }

        @Test
        @DisplayName("Initial position is fully empty")
        void lengthOfInitialPosition() {
            // given
            Board board = new Board(INITIAL_POSITION);

            // when
            var actualResult = board.position().length();

            // then
            assertEquals(9, actualResult);
        }

        @Test
        @DisplayName("Characters replacement on board is not allowed")
        void noReplacement() {
            // given
            Board board = new Board("X        ");

            // when
            var actualResult = board.isValidTransition("O        ");

            // then
            assertFalse(actualResult);
        }

        @Test
        @DisplayName("Number of empty cells should be reduced by one")
        void emptyCellShouldBeFilled() {
            // given
            Board board = new Board("X        ");

            // when
            var actualResult = board.isValidTransition("X        ");

            // then
            assertFalse(actualResult);
        }

        @Test
        @DisplayName("No characters shuffling allowed")
        void charactersShuffling() {
            // given
            Board board = new Board("X        ");

            // when
            var actualResult = board.isValidTransition("OX       ");

            // then
            assertFalse(actualResult);
        }

        @Test
        @DisplayName("Valid move")
        void happyPath() {
            // given
            Board board = new Board(INITIAL_POSITION);

            // when
            var actualResult = board.isValidTransition("X        ");

            // then
            assertTrue(actualResult);
        }
    }
}
