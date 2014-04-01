package com.sri.vt.majic.util;

import java.util.ArrayList;
import java.util.List;

public class StringCombinations
{
    public StringCombinations(String seed)
    {
        strings.add(seed);
    }

    public List<String> getStrings()
    {
        return strings;
    }

    public StringCombinations combineWith(String value)
    {
        List<String> additions = new ArrayList<String>();
        for (String s : strings)
        {
            additions.add(s + value);
        }

        strings.addAll(additions);
        return this;
    }

    private List<String> strings = new ArrayList<String>();
}