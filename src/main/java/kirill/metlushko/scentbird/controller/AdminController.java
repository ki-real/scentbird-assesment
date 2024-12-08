package kirill.metlushko.scentbird.controller;

import kirill.metlushko.scentbird.controller.mapper.GameStateMapper;
import kirill.metlushko.scentbird.service.GameControlService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.util.MimeTypeUtils.TEXT_PLAIN_VALUE;

@RestController
@RequestMapping(value = "/v1/admin/game")
@RequiredArgsConstructor
public class AdminController {

    private final GameControlService gameControlService;
    private final GameStateMapper gameStateMapper;

    @PostMapping(value = "/start", produces = TEXT_PLAIN_VALUE)
    public String startNewGame() {
        return gameControlService.startGame()
                .map(gameStateMapper::displayGameState)
                .orElseThrow(() -> new ResponseStatusException(INTERNAL_SERVER_ERROR, "Game was not created"));
    }

    @GetMapping(value = "/state", produces = TEXT_PLAIN_VALUE)
    public String getActiveGameState() {
        return gameControlService.getGameState()
                .map(gameStateMapper::displayGameState)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "No active game found"));
    }

    @DeleteMapping
    public ResponseEntity<Void> stopActiveGame() {
        gameControlService.stopGame();
        return ResponseEntity.noContent().build();
    }
}
