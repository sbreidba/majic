package com.sri.vt.majic.util;

import org.codehaus.plexus.interpolation.AbstractValueSource;

import java.util.Map;

public class PropertiesValueSource extends AbstractValueSource
{
    private final Map<String, String> properties;

    public PropertiesValueSource(final Map<String, String> properties)
    {
        super(false);
        this.properties = properties;
    }

    public Object getValue(final String expression)
    {
        return properties.get(expression);
    }
}
