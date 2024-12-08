package kirill.metlushko.scentbird.game.api;

public record Message(
        Action action,
        GameState payload
) {
}
