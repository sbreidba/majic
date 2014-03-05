package com.sri.vt.majic.util;

import java.util.ArrayList;
import java.util.List;

public class Version
{
    public static Version parse(String versionString)
    {
        Version version = new Version();

        String[] versionComponents = versionString.split("\\.");
        for (String component : versionComponents)
        {
            try
            {
                int value = Integer.parseInt(component);
                version.addComponent(value);
            }
            catch(NumberFormatException e)
            {
                // The first failure is the last value followed by the suffix
                String[] finalComponents = component.split("-");
                try
                {
                    int value = Integer.parseInt(finalComponents[0]);
                    version.addComponent(value);

                    // re-build from the remainder
                    StringBuilder suffix = new StringBuilder();
                    for (int i = 1; i < finalComponents.length; ++i)
                    {
                        suffix.append("-");
                        suffix.append(finalComponents[i]);
                    }

                    version.setSuffix(suffix.toString());
                }
                catch(NumberFormatException e2)
                {
                    if (version.getCount() == 0)
                    {
                        // If we got nothing so far, then the entire string is the suffix
                        if (versionString.length() > 0) version.setSuffix(versionString);
                    }
                    else
                    {
                        // Otherwise, we failed because the first part of the last dot-separated component
                        // was not a number, so we'll pretend it was intended to be a suffix.
                        version.setSuffix("." + component);
                    }
                }

                break;
            }
        }

        return version;
    }

    /**
     * @return the number of version elements, not including the suffix
     */
    public int getCount()
    {
        return components.size();
    }
    
    public Integer getMajor()
    {
        if (getCount() > 0) return components.get(0);
        else return null;
    }

    public Integer getMinor()
    {
        if (getCount() > 1) return components.get(1);
        else return null;
    }

    public Integer getPatch()
    {
        if (getCount() > 2) return components.get(2);
        else return null;
    }

    public String getSuffix()
    {
        return suffix;
    }

    public void setSuffix(String suffix)
    {
        this.suffix = suffix;
    }

    public void addComponent(int value)
    {
        components.add(value);
    }
    
    private List<Integer> components = new ArrayList<Integer>();
    private String suffix;
}
