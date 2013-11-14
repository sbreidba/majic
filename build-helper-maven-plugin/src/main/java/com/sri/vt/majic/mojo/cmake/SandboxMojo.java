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

@Mojo(name="sandbox", defaultPhase=LifecyclePhase.VALIDATE, requiresProject=true)
public class SandboxMojo extends AbstractMojo implements ILoggable
{
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    protected MavenProject project;

    @Parameter(defaultValue = "${session}", readonly = true, required = true)
    protected MavenSession session;

    @Component(hint = "")
    protected BuildPluginManager pluginManager;

    public void execute() throws MojoExecutionException, MojoFailureException
    {
        CMakeDirectories cmakeDirectories = new CMakeDirectories(project);

        info(this, "cmake build root = " + cmakeDirectories.getBuildRoot().getAbsolutePath());
        info(this, "cmake package root = " + cmakeDirectories.getPackageRoot());
        info(this, "cmake export root = " + cmakeDirectories.getExportRoot());
        info(this, "cmake bindir root = " + cmakeDirectories.getBindirRoot());
        
        try
        {
            info(this, "cmake project debug bindir = " + cmakeDirectories.getProjectBindir("debug"));
            info(this, "cmake project release bindir = " + cmakeDirectories.getProjectBindir("release"));
            info(this, "cmake project installdir = " + cmakeDirectories.getProjectInstalldir());
        }
        catch (IOException e)
        {
            throw new MojoExecutionException(e.getMessage());
        }
    }
}
