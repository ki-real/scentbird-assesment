package kirill.metlushko.scentbird.game.api;

public enum Player {
    X,
    O;

    public Player next() {
        return switch (this) {
            case X -> O;
            case O -> X;
        };
    }
}
