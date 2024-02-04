package com.cr4sh.nhlauncher.bridge;

import androidx.annotation.NonNull;

import java.util.Objects;

/**
 * @author kiva
 */
public class SessionId {
    /**
     * Created a new session.
     */
    public static final SessionId NEW_SESSION = SessionId.of("new");

    private final String sessionId;

    SessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public static SessionId of(String sessionId) {
        return new SessionId(sessionId);
    }

    public String getSessionId() {
        return sessionId;
    }

    @NonNull
    @Override
    public String toString() {
        return "TerminalSession { id = " + sessionId + " }";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SessionId sessionId1 = (SessionId) o;
        return Objects.equals(sessionId, sessionId1.sessionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sessionId);
    }
}
