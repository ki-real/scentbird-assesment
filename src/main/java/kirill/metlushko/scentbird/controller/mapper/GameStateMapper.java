package kirill.metlushko.scentbird.controller.mapper;

import kirill.metlushko.scentbird.game.api.GameState;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GameStateMapper {

    private final BoardMapper boardMapper;

    public String displayGameState(GameState gameState) {
        var boardAsString = boardMapper.drawBoard(gameState.getBoardState());

        return "Player turn: " + gameState.getTurn() + System.lineSeparator() +
                "I'm playing: " + gameState.getMySide() + System.lineSeparator() +
                boardAsString + System.lineSeparator();
    }
}
