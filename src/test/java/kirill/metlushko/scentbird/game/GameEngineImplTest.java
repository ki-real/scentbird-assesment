package kirill.metlushko.scentbird.game;

import kirill.metlushko.scentbird.game.api.Board;
import kirill.metlushko.scentbird.game.api.GameState;
import kirill.metlushko.scentbird.game.api.Player;
import kirill.metlushko.scentbird.game.api.Winner;
import kirill.metlushko.scentbird.service.GameStorage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static kirill.metlushko.scentbird.game.api.Action.ACK;
import static kirill.metlushko.scentbird.game.api.Action.MOVE;
import static kirill.metlushko.scentbird.game.api.Action.STOP;
import static kirill.metlushko.scentbird.game.api.Board.INITIAL_POSITION;
import static kirill.metlushko.scentbird.game.api.Player.O;
import static kirill.metlushko.scentbird.game.api.Player.X;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests for game engine")
class GameEngineImplTest {

    @Mock
    private GameStorage gameStorage;
    @Mock
    private MoveSelectionService moveSelectionService;

    @InjectMocks
    private GameEngineImpl engine;

    @Nested
    @DisplayName("Test for game state sync mechanism")
    class SyncLogic {

        @Test
        @DisplayName("Sync happens if there is no active game")
        void successfulSyncWhenNoGame() {
            // given
            GameState stateToSyncWith = GameState.newGameForPlayer(X);
            GameState gameStateUpdate = new GameState(stateToSyncWith.getBoardState(), stateToSyncWith.getTurn(), O);

            when(gameStorage.getGame()).thenReturn(Optional.empty());
            when(gameStorage.updateGame(gameStateUpdate)).thenReturn(gameStateUpdate);

            // when
            var actualResult = engine.sync(stateToSyncWith);

            // then
            assertNotNull(actualResult);
            assertEquals(ACK, actualResult.action());
            assertNull(actualResult.payload());
        }

        @Test
        @DisplayName("Sync happens if there is an active game with the same state")
        void successfulSyncWhenGameWithSameState() {
            // given
            GameState stateToSyncWith = GameState.newGameForPlayer(X);
            GameState existingGame = new GameState(stateToSyncWith.getBoardState(), stateToSyncWith.getTurn(), O);
            GameState gameStateUpdate = new GameState(stateToSyncWith.getBoardState(), stateToSyncWith.getTurn(), O);

            when(gameStorage.getGame()).thenReturn(Optional.of(existingGame));
            when(gameStorage.updateGame(gameStateUpdate)).thenReturn(gameStateUpdate);

            // when
            var actualResult = engine.sync(stateToSyncWith);

            // then
            assertNotNull(actualResult);
            assertEquals(ACK, actualResult.action());
            assertNull(actualResult.payload());
        }

        @Test
        @DisplayName("Sync fails if existing game state differs")
        void failOnSyncWithDifferentGameState() {
            // given
            GameState stateToSyncWith = GameState.newGameForPlayer(X);
            GameState existingGame = new GameState(new Board("XO       "), stateToSyncWith.getTurn(), O);

            when(gameStorage.getGame()).thenReturn(Optional.of(existingGame));

            // expect
            Executable call = () -> engine.sync(stateToSyncWith);

            // then
            assertThrows(RuntimeException.class, call);
        }

        @Test
        @DisplayName("Active game cleanup on sync if existing game state differs")
        void activeGameCleanUpOnSyncFail() {
            // given
            GameState stateToSyncWith = GameState.newGameForPlayer(X);
            GameState existingGame = new GameState(new Board("XO       "), stateToSyncWith.getTurn(), O);

            when(gameStorage.getGame()).thenReturn(Optional.of(existingGame));

            // expect
            Executable call = () -> engine.sync(stateToSyncWith);

            // then
            assertThrows(RuntimeException.class, call);
            verify(gameStorage, times(1)).removeGame();
        }
    }

    @Nested
    @DisplayName("Test for move making mechanism")
    class MoveLogic {

        @Test
        @DisplayName("No move can be made if there is no active game")
        void noMoveIfNoGame() {
            // given
            when(gameStorage.getGame()).thenReturn(Optional.empty());

            // when
            Executable call = () -> engine.makeMove();

            // then
            assertThrows(RuntimeException.class, call);
        }

        @ParameterizedTest
        @EnumSource(Player.class)
        @DisplayName("The turn order changes after a move has been made")
        void turnOrderChangesAfterAMove(Player startForPlayer) {
            // given
            GameState currentGame = new GameState(new Board(INITIAL_POSITION), startForPlayer, startForPlayer);

            when(gameStorage.getGame()).thenReturn(Optional.of(currentGame));
            when(moveSelectionService.getNextMoveIndex(currentGame.getBoardState())).thenReturn(3);

            // when
            var actualResult = engine.makeMove();

            // then
            assertNotNull(actualResult);
            assertEquals(MOVE, actualResult.action());
            assertEquals(startForPlayer.next(), actualResult.payload().getTurn());
            assertEquals(startForPlayer, actualResult.payload().getMySide());
        }

        @Test
        @DisplayName("Persist new state after a move has been made")
        void statePersistenceAfterMove() {
            // given
            GameState currentGame = GameState.newGameForPlayer(X);
            GameState gameStateAfterMoveMade = new GameState(
                    new Board(" ".repeat(3) + currentGame.getTurn().name().charAt(0) + " ".repeat(5)),
                    currentGame.getTurn().next(), currentGame.getMySide());

            when(gameStorage.getGame()).thenReturn(Optional.of(currentGame));
            when(moveSelectionService.getNextMoveIndex(currentGame.getBoardState())).thenReturn(3);

            // when
            engine.makeMove();

            // then
            verify(gameStorage, times(1)).updateGame(gameStateAfterMoveMade);
        }
    }

    @Nested
    @DisplayName("Test for move acceptance mechanism")
    class MoveAcceptanceLogic {

        @Test
        @DisplayName("No move can be accepted if there is no active game")
        void noMoveAcceptanceIfNoGame() {
            // given
            when(gameStorage.getGame()).thenReturn(Optional.empty());

            // when
            Executable call = () -> engine.acceptState(GameState.newGameForPlayer(X));

            // then
            assertThrows(RuntimeException.class, call);
        }

        @Test
        @DisplayName("Stop the game if opponent made invalid move")
        void stopTheGameOnInvalidMove() {
            // given
            GameState newGameState = GameState.newGameForPlayer(X);
            Board boardMock = Mockito.mock(Board.class);
            GameState currentState = new GameState(boardMock, X, O);

            when(gameStorage.getGame()).thenReturn(Optional.of(currentState));
            when(boardMock.isValidTransition(newGameState.getBoardState().position())).thenReturn(false);

            // when
            var actualResult = engine.acceptState(newGameState);

            // then
            assertNotNull(actualResult);
            assertEquals(STOP, actualResult.action());
            assertEquals(currentState, actualResult.payload());
        }

        @Test
        @DisplayName("Remove current game if opponent made invalid move")
        void removeCurrentGameOnInvalidMove() {
            // given
            GameState newGameState = GameState.newGameForPlayer(X);
            Board boardMock = Mockito.mock(Board.class);
            GameState currentState = new GameState(boardMock, X, O);

            when(gameStorage.getGame()).thenReturn(Optional.of(currentState));
            when(boardMock.isValidTransition(newGameState.getBoardState().position())).thenReturn(false);

            // when
            engine.acceptState(newGameState);

            // then
            verify(gameStorage, times(1)).removeGame();
        }

        @Test
        @DisplayName("Update state of the game if the move is valid")
        void updateGameState() {
            // given
            GameState newGameState = new GameState(new Board(" ".repeat(8) + "X"), O, X);
            Board boardMock = Mockito.mock(Board.class);
            GameState currentState = new GameState(boardMock, X, O);

            when(gameStorage.getGame()).thenReturn(Optional.of(currentState));
            when(boardMock.isValidTransition(newGameState.getBoardState().position())).thenReturn(true);

            // when
            engine.acceptState(newGameState);

            // then
            verify(gameStorage, times(2)).updateGame(any(GameState.class));
        }

        @Test
        @DisplayName("Declare winner if its clear")
        void declareWinner() {
            // given
            var winningPosition = "XXXOO    ";

            Board newStateBoardMock = Mockito.mock(Board.class);
            GameState newGameState = new GameState(newStateBoardMock, O, X);
            Board currStateBoardMock = Mockito.mock(Board.class);
            GameState currentState = new GameState(currStateBoardMock, X, O);

            GameState mergedState = new GameState(newGameState.getBoardState(), newGameState.getTurn(), O);
            when(gameStorage.getGame()).thenReturn(Optional.of(currentState));
            when(newStateBoardMock.position()).thenReturn(winningPosition);
            when(currentState.getBoardState().isValidTransition(winningPosition)).thenReturn(true);
            when(newStateBoardMock.getWinner()).thenReturn(Optional.of(Winner.X));
            when(gameStorage.updateGame(mergedState)).thenReturn(mergedState);

            // when
            var actualResult = engine.acceptState(newGameState);

            // then
            assertNotNull(actualResult);
            assertEquals(STOP, actualResult.action());

            verify(gameStorage, times(1)).updateGame(mergedState);
        }
    }

    @Nested
    @DisplayName("Test for game finish mechanism")
    class FinishLogic {

        @Test
        @DisplayName("Can't finish game if there isn't one")
        void cantFinishGameIfThereIsNotOne() {
            // given
            when(gameStorage.getGame()).thenReturn(Optional.empty());

            // when
            Executable call = () -> engine.finish(GameState.newGameForPlayer(X));

            // then
            assertThrows(RuntimeException.class, call);
        }

        @Test
        @DisplayName("Remove existing game on finish")
        void removeGameOnFinish() {
            // given
            GameState currentGameState = GameState.newGameForPlayer(X);
            when(gameStorage.getGame()).thenReturn(Optional.of(currentGameState));

            // when
            engine.finish(GameState.newGameForPlayer(O));

            // then
            verify(gameStorage, times(1)).removeGame();
        }

    }
}
