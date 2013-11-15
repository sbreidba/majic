package com.sri.vt.majic.mojo;

import com.sri.vt.majic.mojo.util.OperatingSystemInfo;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.IOException;

/**
 * This goal sets maven properties with information about the current O/S:
 * ${os.name}, ${os.arch}, and ${os.distro}
 */
@Mojo(name="set-os-properties", defaultPhase=LifecyclePhase.VALIDATE, requiresProject=true)
public class SetOsPropertiesMojo extends AbstractMojo
{
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    public void execute() throws MojoExecutionException, MojoFailureException
    {
        try
        {
            OperatingSystemInfo info = new OperatingSystemInfo();
            getProject().getProperties().setProperty("os.name", info.getName());
            getProject().getProperties().setProperty("os.arch", info.getArch());
            getProject().getProperties().setProperty("os.distro", info.getDistro());
        }
        catch (IOException e)
        {
            throw new MojoExecutionException(e.getMessage());
        }
    }

    protected MavenProject getProject()
    {
        return project;
    }
}
