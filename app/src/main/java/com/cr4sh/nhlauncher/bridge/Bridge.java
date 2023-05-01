package com.cr4sh.nhlauncher.bridge;

import android.content.ComponentName;
import android.content.Intent;

import java.util.Objects;

/**
 * @author kiva
 */
public class Bridge {
    public static final String ACTION_EXECUTE = "neoterm.action.remote.execute";
    public static final String EXTRA_COMMAND = "neoterm.extra.remote.execute.command";
    public static final String EXTRA_EXECUTABLE = "neoterm.extra.remote.execute.executable";
    public static final String EXTRA_SESSION_ID = "neoterm.extra.remote.execute.session";
    public static final String EXTRA_FOREGROUND = "neoterm.extra.remote.execute.foreground";
    private static final String NEOTERM_PACKAGE = "com.offsec.nhterm";
    private static final String NEOTERM_REMOTE_INTERFACE = "com.offsec.nhterm.ui.term.NeoTermRemoteInterface";
    private static final ComponentName NEOTERM_COMPONENT = new ComponentName(NEOTERM_PACKAGE, NEOTERM_REMOTE_INTERFACE);

    private Bridge() throws IllegalAccessException {
        throw new IllegalAccessException();
    }

    public static Intent createExecuteIntent(SessionId sessionId,
                                             String executablePath,
                                             String command,
                                             boolean foreground) {
        Objects.requireNonNull(executablePath, "executablePath");
        Objects.requireNonNull(command, "command");
        Objects.requireNonNull(sessionId, "session id");

        Intent intent = new Intent(ACTION_EXECUTE);
        intent.setComponent(NEOTERM_COMPONENT);
        intent.putExtra(EXTRA_EXECUTABLE, executablePath); // Used for like /system/xbin/bash
        intent.putExtra(EXTRA_COMMAND, command); // Example: "-c lscpu", This here allows us to add extra cmd after executable as shown in example
        intent.putExtra(EXTRA_SESSION_ID, sessionId.getSessionId());
        intent.putExtra(EXTRA_FOREGROUND, foreground);
        return intent;
    }

    // Usual callable intents

    public static Intent createExecuteIntent(SessionId sessionId, String executablePath, String command) {
        return createExecuteIntent(sessionId, executablePath, command, true);
    }

    public static Intent createExecuteIntent(String executablePath, String command) {
        return createExecuteIntent(SessionId.NEW_SESSION, executablePath, command);
    }

}
