package kirill.metlushko.scentbird.controller.mapper;

import kirill.metlushko.scentbird.game.api.Board;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class BoardMapper {

    public String drawBoard(Board board) {
        var position = board.position();
        List<String> rows = new ArrayList<>(3);
        int index = 0;
        while (index < position.length()) {
            rows.add(position.substring(index, Math.min(index + 3, position.length())));
            index += 3;
        }
        StringBuilder sb = new StringBuilder();
        sb.repeat('-', 13).append("\n");
        for (var row : rows) {
            for (var cell : row.toCharArray()) {
                sb.append("| ").append(cell).append(" ");
            }
            sb.append("|").append("\n");
            sb.repeat('-', 13).append("\n");
        }
        return sb.toString();
    }
}
