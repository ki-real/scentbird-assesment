package kirill.metlushko.scentbird.game.api;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static kirill.metlushko.scentbird.game.api.Winner.DRAW;

public record Board(
        String position
) {

    public static final String INITIAL_POSITION = " ".repeat(9);
    private static final Set<Character> ALLOWED_CHARACTERS = Set.of('X', 'O', ' ');

    @JsonIgnore
    public Optional<Winner> getWinner() {
        var chars = position.toCharArray();
        if (chars[0] != ' ' && chars[0] == chars[4] && chars[4] == chars[8]) { // left diagonal check
            return Optional.of(Winner.valueOf(String.valueOf(chars[0])));
        }
        if (chars[2] != ' ' && chars[2] == chars[4] && chars[4] == chars[6]) { // right diagonal check
            return Optional.of(Winner.valueOf(String.valueOf(chars[2])));
        }
        for (int i = 0; i < 3; i++) { // columns check
            if (chars[i] != ' ' && chars[i] == chars[i + 3] && chars[i] == chars[i + 6]) {
                return Optional.of(Winner.valueOf(String.valueOf(chars[i])));
            }
        }
        for (int i = 0; i < 3; i++) { // rows check
            if (chars[i] != ' ' && chars[3 * i] == chars[3 * i + 1] && chars[3 * i] == chars[3 * i + 2]) {
                return Optional.of(Winner.valueOf(String.valueOf(chars[i])));
            }
        }
        return position.contains(" ") ? Optional.empty() : Optional.of(DRAW);
    }

    public boolean isValidTransition(String newPosition) {
        if (position.length() != newPosition.length()) {
            return false;
        }
        var newPositionChars = newPosition.chars()
                .mapToObj(i -> (char) i)
                .collect(Collectors.toSet());
        if (!ALLOWED_CHARACTERS.containsAll(newPositionChars)) {
            return false;
        }

        var currentSpacesNumber = position.replaceAll("\\w", "").length();
        var newSpacesNumber = newPosition.replaceAll("\\w", "").length();
        if (currentSpacesNumber - 1 != newSpacesNumber) {
            return false;
        }
        var pos = position.toCharArray();
        var newPos = newPosition.toCharArray();
        for (int i = 0; i < pos.length; i++) {
            if (pos[i] != ' ' && newPos[i] != pos[i]) {
                return false;
            }
        }
        return true;
    }
}
