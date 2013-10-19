package com.sri.vt;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.IOException;

/**
 * The distro goal sets maven properties with information about the current O/S:
 *    ${distro.name}, ${distro.arch}, and ${distro.distro}
 */
@Mojo(name="set-properties", defaultPhase=LifecyclePhase.INITIALIZE, requiresProject=true)
public class SetPropertiesMojo extends AbstractMojo
{
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject m_project;

    public void execute() throws MojoExecutionException, MojoFailureException
    {
        if (m_project == null)
        {
            throw new MojoExecutionException("Maven project not set");
        }

        try
        {
            OperatingSystemInfo info = new OperatingSystemInfo();
            m_project.getProperties().setProperty("distro.name", info.getName());
            m_project.getProperties().setProperty("distro.arch", info.getArch());
            m_project.getProperties().setProperty("distro.distro", info.getDistro());
        }
        catch (IOException e)
        {
            throw new MojoExecutionException(e.getMessage());
        }
    }
}
