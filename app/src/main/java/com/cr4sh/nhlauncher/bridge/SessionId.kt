package com.cr4sh.nhlauncher.bridge

import java.util.Objects

/**
 * @author kiva
 */
class SessionId internal constructor(val sessionId: String) {

    override fun toString(): String {
        return "TerminalSession { id = $sessionId }"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val sessionId1 = other as SessionId
        return sessionId == sessionId1.sessionId
    }

    override fun hashCode(): Int {
        return Objects.hash(sessionId)
    }

    companion object {
        /**
         * Created a new session.
         */
        val NEW_SESSION = of("new")
        fun of(sessionId: String): SessionId {
            return SessionId(sessionId)
        }
    }
}
