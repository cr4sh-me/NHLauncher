package com.cr4sh.nhlauncher.bridge

import android.content.ComponentName
import android.content.Intent
import java.util.Objects

/**
 * @author kiva
 */
class Bridge private constructor() {
    init {
        throw IllegalAccessException()
    }

    companion object {
        private const val ACTION_EXECUTE = "neoterm.action.remote.execute"
        private const val EXTRA_COMMAND = "neoterm.extra.remote.execute.command"
        private const val EXTRA_EXECUTABLE = "neoterm.extra.remote.execute.executable"
        private const val EXTRA_SESSION_ID = "neoterm.extra.remote.execute.session"
        private const val EXTRA_FOREGROUND = "neoterm.extra.remote.execute.foreground"
        private const val NEOTERM_PACKAGE = "com.offsec.nhterm"
        private const val NEOTERM_REMOTE_INTERFACE =
            "com.offsec.nhterm.ui.term.NeoTermRemoteInterface"
        private val NEOTERM_COMPONENT = ComponentName(NEOTERM_PACKAGE, NEOTERM_REMOTE_INTERFACE)

        // Usual callable intents
        @JvmOverloads
        fun createExecuteIntent(
            sessionId: SessionId,
            executablePath: String,
            command: String,
            foreground: Boolean = true
        ): Intent {
            Objects.requireNonNull(executablePath, "executablePath")
            Objects.requireNonNull(command, "command")
            Objects.requireNonNull(sessionId, "session id")
            val intent = Intent(ACTION_EXECUTE)
            intent.setComponent(NEOTERM_COMPONENT)
            intent.putExtra(EXTRA_EXECUTABLE, executablePath) // Used for like /system/xbin/bash
            intent.putExtra(
                EXTRA_COMMAND,
                command
            ) // Example: "-c lscpu", This here allows us to add extra cmd after executable as shown in example
            intent.putExtra(EXTRA_SESSION_ID, sessionId.sessionId)
            intent.putExtra(EXTRA_FOREGROUND, foreground)
            return intent
        }

        fun createExecuteIntent(
            executablePath: String,
            command: String,
        ): Intent {
            return createExecuteIntent(SessionId.NEW_SESSION, executablePath, command)
        }
    }
}
