package com.sri.vt.majic.mojo.cmake;

import com.sri.vt.majic.util.CMakeDirectories;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.IOException;

@Mojo(name="show-cmake-directories", defaultPhase=LifecyclePhase.VALIDATE, requiresProject=true)
public class ShowCMakeDirectoriesMojo extends AbstractMojo
{
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    protected MavenProject getProject()
    {
        return project;
    }
    
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        CMakeDirectories cmakeDirectories = new CMakeDirectories(getProject(), getLog());

        getLog().info("cmake build root = " + cmakeDirectories.getBuildRoot().getAbsolutePath());
        getLog().info("cmake package root = " + cmakeDirectories.getPackageRoot());
        getLog().info("cmake export root = " + cmakeDirectories.getExportRoot());
        getLog().info("cmake bindir root = " + cmakeDirectories.getBindirRoot());
        
        try
        {
            getLog().info("cmake project debug bindir = " + cmakeDirectories.getProjectBindir("debug"));
            getLog().info("cmake project release bindir = " + cmakeDirectories.getProjectBindir("release"));
            getLog().info("cmake project installdir = " + cmakeDirectories.getProjectInstalldir());
        }
        catch (IOException e)
        {
            throw new MojoExecutionException(e.getMessage());
        }
    }
}
