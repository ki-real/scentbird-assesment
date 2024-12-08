package kirill.metlushko.scentbird.game;

import kirill.metlushko.scentbird.game.api.Board;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Tests for random move selection service")
class RandomMoveSelectionServiceTest {

    private final MoveSelectionService moveSelectionService = new RandomMoveSelectionService();

    private static Stream<Arguments> boardPositions() {
        return Stream.of(
                Arguments.of("X        "),
                Arguments.of("XX       "),
                Arguments.of("XXX      "),
                Arguments.of("XXXX     "),
                Arguments.of("XXXXX    "),
                Arguments.of("XXXXXX   "),
                Arguments.of("XXXXXXX  "),
                Arguments.of("XXXXXXXX ")
        );
    }

    @ParameterizedTest
    @MethodSource("boardPositions")
    @DisplayName("Selects index of cell with no mark in it")
    void doesNotSelectOccupiedCells(String boardPosition) {
        // given
        Board board = new Board(boardPosition);

        // when
        int nextMoveIndex = moveSelectionService.getNextMoveIndex(board);

        // then
        assertEquals(' ', boardPosition.charAt(nextMoveIndex));
    }

    @Test
    @DisplayName("")
    void throwsExceptionOnFilledBoard() {
        // given
        Board board = new Board("X".repeat(9));

        // when
        Executable call = () -> moveSelectionService.getNextMoveIndex(board);

        // then
        assertThrows(IllegalArgumentException.class, call);
    }
}
