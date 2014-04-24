package com.sri.vt.majic.mojo.cmake;

import com.sri.vt.majic.util.CMakeDirectories;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProjectHelper;
import org.apache.maven.wagon.PathUtils;

import java.io.File;
import java.io.IOException;

/**
 * Creates a tar file from the contents of a given directory.
 */
@Mojo(name="cmake-tar", defaultPhase = LifecyclePhase.PACKAGE, requiresProject=true)
public class TarMojo extends CMakeCommandMojo
{
    @Component()
    private MavenProjectHelper projectHelper;

    @Parameter(defaultValue = "tar cjf", required = true)
    private String command;

    @Parameter(defaultValue = CMakeDirectories.CMAKE_PROJECT_PACKAGEDIR_DEFAULT)
    private File workingDirectory;

    // Note: tar.bz2 is not replaced here or in 'type' with ${majic.package.extension}
    // After all, this is the TarMojo. A future ZipMojo would set defaults differently.

    @Parameter(defaultValue = "${project.artifactId}-${project.version}.tar.bz2")
    private String outputName;

    @Parameter(defaultValue = CMakeDirectories.CMAKE_PROJECT_PACKAGEDIR_DEFAULT)
    private File outputDirectory;

    @Parameter(defaultValue = "true")
    private boolean attachArtifact;

    @Parameter(defaultValue = "tar.bz2")
    private String type;

    protected String getCommand()
    {
        return command;
    }

    // This is the directory that tar will execute in, meaning that everything in this directory
    // will get packaged up.
    protected File getWorkingDirectory()
    {
        return workingDirectory;
    }

    protected File getTarFile()
    {
        return new File(outputDirectory, outputName);
    }
    
    @Override
    protected String getCommandlineArgs()
    {
        StringBuilder builder = new StringBuilder();
        builder.append(super.getCommandlineArgs());
        builder.append(" ");
        builder.append(getTarFile().getAbsolutePath());
        builder.append(" ");

        boolean bFirst = true;
        for (File file : getWorkingDirectory().listFiles())
        {
            if (file.isDirectory())
            {
                String relativePath = PathUtils.toRelative(getWorkingDirectory(), file.getAbsolutePath());

                if (!bFirst) builder.append(" ");
                builder.append(relativePath);

                bFirst = false;
            }
        }

        return builder.toString();
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        if (!getWorkingDirectory().exists())
        {
            getLog().warn("Skipping tar step: directory " + getWorkingDirectory() + " does not exist.");
            return;
        }

        super.execute();

        if (getSkip() || isUpToDate())
        {
            return;
        }
        
        if (shouldAttachArtifact())
        {
            try
            {
                getProjectHelper().attachArtifact(getProject(), getType(), getBuildEnvironment().getPackageClassifier(), getTarFile());
            }
            catch (IOException e)
            {
                throw new MojoExecutionException("Could not determine classifier: " + e.getMessage());
            }
        }
    }

    protected MavenProjectHelper getProjectHelper()
    {
        return projectHelper;
    }

    public boolean shouldAttachArtifact()
    {
        return attachArtifact;
    }

    public String getType()
    {
        return type;
    }
}
