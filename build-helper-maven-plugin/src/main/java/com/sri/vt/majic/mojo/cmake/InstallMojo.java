package com.sri.vt.majic.mojo.cmake;

import com.sri.vt.majic.Utils;
import com.sri.vt.majic.mojo.OperatingSystemInfo;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.Xpp3Dom;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import static org.twdata.maven.mojoexecutor.MojoExecutor.*;
import static org.twdata.maven.mojoexecutor.MojoExecutor.element;

@Mojo(name="cmake-install", defaultPhase=LifecyclePhase.PREPARE_PACKAGE, requiresProject=true)
public class InstallMojo extends AbstractMojo
{
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    @Parameter(defaultValue = "${session}", readonly = true, required = true)
    private MavenSession session;

    @Component()
    private BuildPluginManager pluginManager;

    @Parameter(defaultValue = "cmake")
    private String cmakeExeName;

    @Parameter(defaultValue = "install")
    private String target;

    @Parameter(defaultValue = "Release")
    private String config;

    @Parameter(alias = "buildDirectory", defaultValue = "${project.build.directory}")
    private File configuredBuildDirectory;

    // Append an operating-system-specific sub-directory to the buildDirectory
    @Parameter(defaultValue = "true")
    private boolean useOSBuildSubdirectory;

    public void execute() throws MojoExecutionException, MojoFailureException
    {
        try
        {
            // TODO: refactor - shares too much with ConfigureMojo
            Plugin execPlugin = Utils.getExecPlugin(this, project);
            getLog().info("Using exec plugin version: " + execPlugin.getVersion());

            OperatingSystemInfo info = new OperatingSystemInfo();

            File buildDirectory = configuredBuildDirectory;
            if (useOSBuildSubdirectory)
            {
                buildDirectory = new File(configuredBuildDirectory, info.getDistro());
            }
            buildDirectory.mkdirs();

            Xpp3Dom configElements = configuration(
                element(name("workingDirectory"), buildDirectory.getAbsolutePath()),
                element(name("executable"), cmakeExeName),
                element(name("commandlineArgs"), "--build . --target " + target + " --config " + config)
            );

            getLog().info("Executing cmake install:");
            getLog().info(configElements.toString());

            executeMojo(
                    plugin(
                            groupId("org.codehaus.mojo"),
                            artifactId("exec-maven-plugin"),
                            version(execPlugin.getVersion())
                    ),
                    goal("exec"),
                    configElements,
                    executionEnvironment(
                            project,
                            session,
                            pluginManager
                    )
            );
        }
        catch (IOException e)
        {
            throw new MojoExecutionException(e.getMessage());
        }
    }
}
