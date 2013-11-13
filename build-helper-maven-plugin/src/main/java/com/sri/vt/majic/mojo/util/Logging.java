package com.sri.vt.majic.mojo.util;

public class Logging
{
    public static void info(ILoggable loggable, String message)
    {
        if (loggable.getLog() != null) loggable.getLog().info(loggable.getClass().getSimpleName() + ": " + message);
    }

    public static void warn(ILoggable loggable, String message)
    {
        if (loggable.getLog() != null) loggable.getLog().warn(loggable.getClass().getSimpleName() + ": " + message);
    }

    public static void error(ILoggable loggable, String message)
    {
        if (loggable.getLog() != null) loggable.getLog().error(loggable.getClass().getSimpleName() + ": " + message);
    }
}
