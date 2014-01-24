package com.sri.vt.majic.mojo.cmake;

import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Executes arbitary CMake commands, i.e <code>cmake -E.</code>
 */
@Mojo(name="cmake-command", requiresProject=true)
public class CMakeCommandMojo extends CMakeMojo
{
    @Parameter(defaultValue = "", required = true)
    private String command;

    protected String getCommand()
    {
        return command;
    }
    
    @Override
    protected String getCommandlineArgs()
    {
        String args = super.getCommandlineArgs();

        StringBuilder builder = new StringBuilder();
        if (args != null)
        {
            builder.append(args);
            builder.append(" ");
        }

        builder.append("-E ");
        builder.append(getCommand());

        return builder.toString();
    }
}
