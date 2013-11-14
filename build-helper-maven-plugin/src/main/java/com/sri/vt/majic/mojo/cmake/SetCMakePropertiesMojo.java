package com.sri.vt.majic.mojo.cmake;

import com.sri.vt.majic.mojo.util.CMakeDirectories;
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
import java.util.List;

// use this mojo to proactively declare needed cmake properties in advance.
// note that ${cmake.project.bindir} is a "config-neutral" directory
// when configurations are specified, other bindir properties
// in the form ${cmake.project.bindir}.<config> are also set.

@Mojo(name="cmake-set-properties", defaultPhase=LifecyclePhase.VALIDATE, requiresProject=true)
public class SetCMakePropertiesMojo extends AbstractMojo
{
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    protected MavenProject project;

    @Parameter(defaultValue = "${session}", readonly = true, required = true)
    protected MavenSession session;

    @Component(hint = "")
    protected BuildPluginManager pluginManager;

    @Parameter(defaultValue = "")
    private List<String> configs;

    @Parameter(defaultValue = "false")
    private boolean verbose;

    protected List<String> getConfigs()
    {
        return configs;
    }

    protected MavenProject getProject()
    {
        return project;
    }

    public void execute() throws MojoExecutionException, MojoFailureException
    {
        CMakeDirectories cmakeDirectories = new CMakeDirectories(getProject(), getLog());

        getProject().getProperties().setProperty(
                CMakeDirectories.CMAKE_BUILD_ROOT_PROPERTY,
                cmakeDirectories.getBuildRoot().getAbsolutePath());

        getProject().getProperties().setProperty(
                CMakeDirectories.CMAKE_PACKAGE_ROOT_PROPERTY,
                cmakeDirectories.getPackageRoot().getAbsolutePath());

        getProject().getProperties().setProperty(
                CMakeDirectories.CMAKE_EXPORT_ROOT_PROPERTY,
                cmakeDirectories.getExportRoot().getAbsolutePath());

        getProject().getProperties().setProperty(
                CMakeDirectories.CMAKE_BINDIR_ROOT_PROPERTY,
                cmakeDirectories.getBindirRoot().getAbsolutePath());

        try
        {
            getProject().getProperties().setProperty(
                    CMakeDirectories.CMAKE_PROJECT_BINDIR,
                    cmakeDirectories.getProjectBindir().getAbsolutePath());

            if (getConfigs() != null)
            {
                for (String config : getConfigs())
                {
                    getProject().getProperties().setProperty(
                            CMakeDirectories.CMAKE_PROJECT_BINDIR + "." + config,
                            cmakeDirectories.getProjectBindir(config).getAbsolutePath());
                }
            }

            getProject().getProperties().setProperty(
                    CMakeDirectories.CMAKE_PROJECT_INSTALLDIR,
                    cmakeDirectories.getProjectInstalldir().getAbsolutePath());
        }
        catch (IOException e)
        {
            throw new MojoExecutionException(e.getMessage());
        }
    }
}
