package com.sri.vt.majic.mojo.cmake;

import com.sri.vt.majic.util.CMakeDirectories;
import com.sri.vt.majic.util.OperatingSystemInfo;
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

@Mojo(name="cmake-tar", defaultPhase = LifecyclePhase.PACKAGE, requiresProject=true)
public class TarMojo extends CMakeCommandMojo
{
    @Component()
    private MavenProjectHelper projectHelper;
    
    @Parameter(defaultValue = "tar cjf", required = true)
    private String command;

    @Parameter(defaultValue = CMakeDirectories.CMAKE_PROJECT_PACKAGEDIR_DEFAULT)
    private File workingDirectory;

    @Parameter(defaultValue = "${project.artifactId}-${project.version}.tar.bz2")
    private String outputName;

    @Parameter(defaultValue = CMakeDirectories.CMAKE_PROJECT_BIN_DIRECTORY_DEFAULT)
    private File outputDirectory;

    @Parameter(defaultValue = "true")
    private boolean attachTarArtifact;

    @Parameter(defaultValue = "${os.classifier}")
    private String classifier;

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

        for (File file : getWorkingDirectory().listFiles())
        {
            if (file.isDirectory())
            {
                String relativePath = PathUtils.toRelative(getWorkingDirectory(), file.getAbsolutePath());
                builder.append(relativePath);
            }
        }

        return builder.toString();
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        super.execute();

        if (shouldAttachTarArtifact())
        {
            getProjectHelper().attachArtifact(getProject(), getType(), getClassifier(), getTarFile());
        }
    }

    protected MavenProjectHelper getProjectHelper()
    {
        return projectHelper;
    }

    public boolean shouldAttachTarArtifact()
    {
        return attachTarArtifact;
    }

    public String getClassifier()
    {
        if (classifier != null)
        {
            return classifier;
        }

        OperatingSystemInfo operatingSystemInfo = null;
        try
        {
            operatingSystemInfo = new OperatingSystemInfo();
            return operatingSystemInfo.getDistro();
        }
        catch (IOException e)
        {
            return null;
        }
    }

    public String getType()
    {
        return type;
    }
}
