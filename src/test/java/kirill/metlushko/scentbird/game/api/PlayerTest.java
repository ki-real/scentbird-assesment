package kirill.metlushko.scentbird.game.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static kirill.metlushko.scentbird.game.api.Player.O;
import static kirill.metlushko.scentbird.game.api.Player.X;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Tests for player model")
class PlayerTest {

    @ParameterizedTest
    @EnumSource(Player.class)
    @DisplayName("Choose next player")
    void nextPlayer(Player current) {
        // when
        var actualResult = current.next();

        // then
        assertEquals(current == O ? X : O, actualResult);
    }
}
