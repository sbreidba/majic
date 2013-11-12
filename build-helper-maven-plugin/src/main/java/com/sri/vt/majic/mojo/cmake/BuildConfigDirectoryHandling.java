package com.sri.vt.majic.mojo.cmake;

public enum BuildConfigDirectoryHandling
{
    ALWAYS(Constants.ALWAYS_VALUE),
    BY_OS(Constants.BY_OS_VALUE),
    NEVER(Constants.NEVER_VALUE);

    private final String id;

    BuildConfigDirectoryHandling(String id)
    {
        this.id = id;
    }

    public String id()
    {
        return this.id;
    }

    public static class Constants
    {
        public static final String ALWAYS_VALUE = "ALWAYS";
        public static final String BY_OS_VALUE = "BY_OS";
        public static final String NEVER_VALUE = "NEVER";
    }
}