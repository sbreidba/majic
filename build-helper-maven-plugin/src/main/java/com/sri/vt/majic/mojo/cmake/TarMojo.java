package com.sri.vt.majic.mojo.cmake;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
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
    private File tarDirectory;

    protected String getCommand()
    {
        return command;
    }

    // This deserves some explanation. when execute() is run, the tarDirectory
    // is one lower than where we want to CD to when tar starts doing its thing.
    //
    // This creates the tar structure from that point in the tree. However, the semantics of
    // "working directory" are where we place the tarball. It's handy to use the parent for
    // this since the os/config directory information is computed for us here.

    protected File getWorkingDirectory()
    {
        return new File(tarDirectory, "/..");
    }

    protected File getTarFile()
    {
        return new File(super.getWorkingDirectory(), tarDirectory.getName() + ".tar.bz2");
    }
    
    @Override
    protected String getCommandlineArgs()
    {
        StringBuilder builder = new StringBuilder();
        builder.append(super.getCommandlineArgs());
        builder.append(" ");
        builder.append(getTarFile().getAbsolutePath());
        builder.append(" ");
        builder.append(tarDirectory);

        return builder.toString();
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        getTarFile().getParentFile().mkdirs();
        super.execute();
    }
}
