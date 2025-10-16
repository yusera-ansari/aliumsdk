package com.dwao.alium.services;

public class Logger {
    public enum LogLevel {
        DEBUG, INFO, WARN, ERROR
    }
    private static final int MAX_TAG_LENGTH = 23;
    public static boolean isLoggingEnabled = true;
    public static LogLevel minLogLevel = LogLevel.DEBUG;

    private static final String LOG_PREFIX = "Alium";
    private static String safeTag(String tag){
        return (tag.length() >MAX_TAG_LENGTH)?tag.substring(0, MAX_TAG_LENGTH):tag;
    }

    public static void log(LogLevel level, String tag, String message) {
        if (!isLoggingEnabled || level.ordinal() < minLogLevel.ordinal()) return;

        String safeTag = safeTag(LOG_PREFIX + "." + tag);

        switch (level) {
            case DEBUG:
                android.util.Log.d(safeTag, message);
                break;
            case INFO:
                android.util.Log.i(safeTag, message);
                break;
            case WARN:
                android.util.Log.w(safeTag, message);
                break;
            case ERROR:
                android.util.Log.e(safeTag, message);
                break;
        }
    }
}
