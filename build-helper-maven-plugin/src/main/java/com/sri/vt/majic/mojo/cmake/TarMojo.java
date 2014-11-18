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

    @Parameter(defaultValue = CMakeDirectories.CMAKE_PROJECT_EXPORT_DIR_DEFAULT)
    private File workingDirectory;

    // Note: tar.bz2 is not replaced here or in 'type' with ${majic.package.extension}
    // After all, this is the TarMojo. A future ZipMojo would set defaults differently.

    @Parameter(defaultValue = "${project.artifactId}-${project.version}.tar.bz2")
    private String outputName;

    /**
     * If set, include all subdirectories of the exports directory in tar file.
     */
    @Parameter(defaultValue = "false", property = "majic.cmake.tar.alldirs")
    private boolean allDirs;

    // If !allDirs, then just do projectName
    @Parameter(defaultValue = "${project.artifactId}-${project.version}")
    private String projectName;

    @Parameter(defaultValue = CMakeDirectories.CMAKE_PROJECT_EXPORT_DIR_DEFAULT)
    private File outputDirectory;

    @Parameter(defaultValue = "true")
    private boolean attachArtifact;

    @Parameter(defaultValue = "tar.bz2")
    private String type;

    /**
     * If not set, this is derived from Maven's global debug flag (i.e. -X).
     */
    @Parameter(defaultValue = "false", property = "majic.cmake.tar.verbose")
    private Boolean verbose;

    @Override
    protected boolean isVerbose()
    {
        return (verbose || getLog().isDebugEnabled());
    }

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

        if (allDirs) {
            for (File file : getWorkingDirectory().listFiles())
                {
                    if (file.isDirectory())
                        {
                            String relativePath = PathUtils.toRelative(getWorkingDirectory(), file.getAbsolutePath());

                            builder.append(" ");
                            builder.append(relativePath);

                        }
                }
        } else {
            builder.append(" ");
            builder.append(projectName);
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
