package com.sri.vt.majic.mojo.cmake;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Mojo(name="cmake-tar", defaultPhase = LifecyclePhase.PACKAGE, requiresProject=true)
public class TarMojo extends CMakeCommandMojo
{
    @Parameter(defaultValue = "tar cjf", required = true)
    private String command;

    @Parameter(defaultValue = "", required = true)
    private File tarFile;

    @Parameter(defaultValue = "", required = true)
    private ArrayList<String> items;

    protected String getCommand()
    {
        return command;
    }

    protected File getTarFile()
    {
        return tarFile;
    }
    
    protected List<String> getItems()
    {
        return items;
    }

    @Override
    protected String getCommandlineArgs()
    {
        StringBuilder builder = new StringBuilder();
        builder.append(super.getCommandlineArgs());
        builder.append(" ");
        builder.append(getTarFile().getAbsolutePath());

        for (String item : items)
        {
            builder.append(" ");
            builder.append(item);
        }

        return builder.toString();
    }
}
