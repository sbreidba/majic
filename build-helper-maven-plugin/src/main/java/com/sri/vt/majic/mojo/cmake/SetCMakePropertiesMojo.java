package com.sri.vt.majic.mojo.cmake;

import com.sri.vt.majic.mojo.util.CMakeDirectories;
import com.sri.vt.majic.mojo.util.ILoggable;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.IOException;

import static com.sri.vt.majic.mojo.util.Logging.info;

// use this mojo to proactively declare needed cmake properties in advance.
// this can be helpful for debugging, though it's not usable for non-Windows
// multi-config builds that use the cmake project bindir.

@Mojo(name="cmake-set-properties", defaultPhase=LifecyclePhase.VALIDATE, requiresProject=true)
public class SetCMakePropertiesMojo extends AbstractMojo implements ILoggable
{
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    protected MavenProject project;

    @Parameter(defaultValue = "${session}", readonly = true, required = true)
    protected MavenSession session;

    @Component(hint = "")
    protected BuildPluginManager pluginManager;

    @Parameter(defaultValue = "debug")
    private String config;

    @Parameter(defaultValue = "false")
    private boolean verbose;

    protected String getConfig()
    {
        return config;
    }

    protected boolean isVerbose()
    {
        return verbose;
    }
    
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        CMakeDirectories cmakeDirectories = new CMakeDirectories(project);
        if (isVerbose())
        {
            cmakeDirectories.log(getLog());
        }

        try
        {
            cmakeDirectories.setProjectProperties(getConfig());
        }
        catch (IOException e)
        {
            throw new MojoExecutionException(e.getMessage());
        }
    }
}
