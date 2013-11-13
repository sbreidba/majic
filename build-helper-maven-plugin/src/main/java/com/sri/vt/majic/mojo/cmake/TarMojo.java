package com.sri.vt.majic.mojo.cmake;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;

import static com.sri.vt.majic.mojo.util.Logging.error;

@Mojo(name="cmake-tar", defaultPhase = LifecyclePhase.PACKAGE, requiresProject=true)
public class TarMojo extends CMakeCommandMojo
{
    @Parameter(defaultValue = "tar cjf", required = true)
    private String command;

    @Parameter(defaultValue = "${cmake.project.packagedir}")
    private File workingDirectory;

    @Parameter(defaultValue = "${cmake.project.installdir}")
    private File installDirectory;

    @Parameter(defaultValue = "${project.artifactId}-${project.version}.tar.bz2")
    private String outputName;

    @Parameter(defaultValue = "${cmake.project.bindir}")
    private File outputDirectory;
    
    protected String getCommand()
    {
        return command;
    }

    // This is the directory that tar will execute in, meaning that everything in this directory
    // will get packaged up.
    protected File getWorkingDirectory()
    {
        File root = workingDirectory;
        if (workingDirectory == null)
        {
            try
            {
                workingDirectory = getCMakeDirectories().getProjectPackagedir();
            }
            catch (IOException e)
            {
                error(this, "Failed to get project packagedir");
                return null;
            }
        }
        
        return workingDirectory;
    }

    protected File getInstallDirectory()
    {
        File root = installDirectory;
        if (installDirectory == null)
        {
            try
            {
                installDirectory = getCMakeDirectories().getProjectInstalldir();
            }
            catch (IOException e)
            {
                error(this, "Failed to get project installdir");
                return null;
            }
        }

        return installDirectory;
    }

    protected File getTarFile()
    {
        File root = outputDirectory;
        if (root == null)
        {
            try
            {
                root = getCMakeDirectories().getProjectBindir();
            }
            catch (IOException e)
            {
                error(this, "Failed to get project bin dir");
            }
        }
        
        return new File(root, outputName);
    }
    
    @Override
    protected String getCommandlineArgs()
    {
        StringBuilder builder = new StringBuilder();
        builder.append(super.getCommandlineArgs());
        builder.append(" ");
        builder.append(getTarFile().getAbsolutePath());
        builder.append(" ");
        builder.append(getInstallDirectory());

        return builder.toString();
    }
}
