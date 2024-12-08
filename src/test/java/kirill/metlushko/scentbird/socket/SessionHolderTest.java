package kirill.metlushko.scentbird.socket;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("Tests for session holder")
class SessionHolderTest {

    SessionHolder sessionHolder = new SessionHolderImpl();

    @Test
    @DisplayName("Get open session returns nothing if holder is empty")
    void getSessionIfEmpty() {
        // when
        sessionHolder.getOpenSession();

        // then
        assertTrue(sessionHolder.getOpenSession().isEmpty());
    }

    @Test
    @DisplayName("Get open session doesn't return closed already session")
    void getOpenSession() {
        // given
        WebSocketSession session = Mockito.mock(WebSocketSession.class);
        sessionHolder.setCurrentSession(session);
        when(session.isOpen()).thenReturn(false);

        // when
        sessionHolder.getOpenSession();

        // then
        assertTrue(sessionHolder.getOpenSession().isEmpty());
    }

    @Test
    @DisplayName("Close existing open session while setting a new one")
    void closeExistingSession() throws IOException {
        // given
        WebSocketSession oldSession = Mockito.mock(WebSocketSession.class);
        when(oldSession.isOpen()).thenReturn(true);
        sessionHolder.setCurrentSession(oldSession);
        when(oldSession.getId()).thenReturn("old");
        WebSocketSession newSession = Mockito.mock(WebSocketSession.class);
        when(newSession.getId()).thenReturn("new");
        when(newSession.isOpen()).thenReturn(true);

        // when
        sessionHolder.setCurrentSession(newSession);

        // then
        assertTrue(sessionHolder.getOpenSession().isPresent());
        assertEquals("new", sessionHolder.getOpenSession().get().getId());
        verify(oldSession, times(1)).close(CloseStatus.GOING_AWAY);
    }

    @Test
    @DisplayName("Remove session stored in a holder")
    void removeSession() {
        // given
        WebSocketSession session = Mockito.mock(WebSocketSession.class);
        sessionHolder.setCurrentSession(session);

        // when
        sessionHolder.removeCurrentSession();

        // then
        assertTrue(sessionHolder.getOpenSession().isEmpty());
    }
}
