package kirill.metlushko.scentbird.socket;

import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.util.Optional;

import static org.springframework.web.socket.CloseStatus.GOING_AWAY;

@Service
public class SessionHolderImpl implements SessionHolder {

    private Optional<WebSocketSession> currentSession = Optional.empty();

    @Override
    public Optional<WebSocketSession> getOpenSession() {
        if (currentSession.isEmpty() || !currentSession.get().isOpen()) {
            currentSession = Optional.empty();
            return currentSession;
        }
        return currentSession;
    }

    @SneakyThrows
    public void setCurrentSession(WebSocketSession newSession) {
        if (currentSession.isPresent() && currentSession.get().isOpen()) {
            currentSession.get().close(GOING_AWAY);
        }
        currentSession = Optional.of(newSession);
    }

    public void removeCurrentSession() {
        currentSession = Optional.empty();
    }
}
