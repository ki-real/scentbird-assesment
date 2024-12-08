package kirill.metlushko.scentbird.socket;

import org.springframework.web.socket.WebSocketSession;

import java.util.Optional;

public interface SessionHolder {

    Optional<WebSocketSession> getOpenSession();

    void setCurrentSession(WebSocketSession session);

    void removeCurrentSession();
}
