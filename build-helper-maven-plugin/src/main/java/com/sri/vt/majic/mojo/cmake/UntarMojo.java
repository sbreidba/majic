package com.sri.vt.majic.mojo.cmake;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

@Mojo(name="cmake-untar", defaultPhase = LifecyclePhase.INITIALIZE, requiresProject=true)
public class UntarMojo extends CMakeCommandMojo
{
    @Parameter(defaultValue = "tar xjf", required = true)
    private String command;

    @Parameter(defaultValue = "", required = true)
    private File tarFile;

    @Parameter(defaultValue = "${project.build.directory}/cmake-untar/markers")
    private File markersDirectory;

    @Parameter(defaultValue = "${project.build.directory}/cmake-untar/tempExtract")
    private File temporaryExtractDir;

    @Parameter(defaultValue = "true")
    private boolean stripRootDirectory;

    @Parameter(defaultValue = "false")
    private boolean dryRunMove;

    protected String getCommand()
    {
        return command;
    }

    protected File getTarFile()
    {
        return tarFile;
    }

    protected File getOutputDirectory()
    {
        // TODO: there needs to be a better way to share the auto-computed working directory
        // TODO: perhaps export it as a variable?
        return new File(getWorkingDirectory(), "pkg");
    }

    protected File getMarkersDirectory()
    {
        return markersDirectory;
    }

    protected File getTemporaryExtractDir()
    {
        return temporaryExtractDir;
    }

    @Override
    protected boolean isUpToDate()
    {
        getLog().debug("Comparing modification dates: " + getTarFile() + " vs. " + getMarkerFile());

        if (!getMarkerFile().exists())
        {
            return false;
        }
        
        return (getTarFile().lastModified() < getMarkerFile().lastModified());
    }

    protected File getMarkerFile()
    {
        return new File(getMarkersDirectory(), getTarFile().getName() + ".marker");
    }

    protected void moveFiles() throws IOException
    {
        if (stripRootDirectory)
        {
            for (File subDir : getTemporaryExtractDir().listFiles())
            {
                if (subDir.isDirectory())
                {
                    moveContents(subDir);
                }

                if (dryRunMove)
                {
                    getLog().info("I would have deleted " + subDir + " during cleanup.");
                }
                else
                {
                    if (!subDir.delete())
                    {
                        getLog().warn("Could not clean up directory " + subDir);
                    }
                }
            }
        }
        else
        {
            moveContents(getTemporaryExtractDir());
        }
    }

    protected void moveContents(File file) throws IOException
    {
        for (File containedFile : file.listFiles())
        {
            if (dryRunMove)
            {
                getLog().info("I would have moved " + containedFile + " to " + getOutputDirectory());
            }
            else
            {
                FileUtils.moveToDirectory(containedFile, getOutputDirectory(), true);
            }
        }
    }

    @Override
    protected String getCommandlineArgs()
    {
        StringBuilder builder = new StringBuilder();
        builder.append(super.getCommandlineArgs());
        builder.append(" ");
        builder.append(getTarFile().getAbsolutePath());

        return builder.toString();
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        if (!getTemporaryExtractDir().mkdirs())
        {
            throw new MojoExecutionException("Could not create temporary extraction subdirectory: " + getTemporaryExtractDir());
        }

        if (!getMarkersDirectory().mkdirs())
        {
            throw new MojoExecutionException("Could not create markers subdirectory: " + getMarkersDirectory());
        }

        super.execute();

        try
        {
            moveFiles();
            FileUtils.touch(getMarkerFile());
        }
        catch(IOException e)
        {
            throw new MojoExecutionException(e.getMessage());
        }
    }
}