package com.sri.vt.majic.cmake;

import com.sri.vt.majic.OperatingSystemInfo;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.twdata.maven.mojoexecutor.MojoExecutor;

import javax.lang.model.element.*;
import javax.lang.model.element.Element;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import static org.twdata.maven.mojoexecutor.MojoExecutor.*;

@Mojo(name="cmake-configure", defaultPhase=LifecyclePhase.PROCESS_SOURCES, requiresProject=true)
public class ConfigureMojo extends AbstractMojo
{
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    @Parameter(defaultValue = "${session}", readonly = true, required = true)
    private MavenSession session;

    @Component()
    private BuildPluginManager pluginManager;

    @Parameter(defaultValue = "${basedir}")
    private File sourceDirectory;

    @Parameter(alias = "buildDirectory", defaultValue = "${project.build.directory}")
    private File configuredBuildDirectory;

    // Append an operating-system-specific sub-directory to the buildDirectory
    @Parameter(defaultValue = "true")
    private boolean useOperatingSystemSpecificBuildDirectory;

    // Left blank, this will be computed automatically
    @Parameter()
    private String generator;
    
    @Parameter()
    private Map<String, String> options;

    @Parameter(defaultValue = "cmake")
    private String cmakeExeName;

    public void execute() throws MojoExecutionException, MojoFailureException
    {
        try
        {
            String execComponentKey = Plugin.constructKey("org.codehaus.mojo", "exec-maven-plugin");
            Plugin execPlugin = project.getPluginManagement().getPluginsAsMap().get(execComponentKey);
            if (execPlugin == null)
            {
                throw new MojoExecutionException(this, "Exec-maven-plugin version missing", "Could not determine exec plugin version. Please declare exec-maven-plugin in PluginManagement");
            }
            
            getLog().info("Using exec plugin version: " + execPlugin.getVersion());

            OperatingSystemInfo info = new OperatingSystemInfo();

            File buildDirectory = configuredBuildDirectory;
            if (useOperatingSystemSpecificBuildDirectory)
            {
                buildDirectory = new File(configuredBuildDirectory, info.getDistro());
            }
            
            buildDirectory.mkdirs();

            if ((generator == null) || (generator.length() == 0))
            {
                generator = info.getCMakeGenerator();
            }

            org.twdata.maven.mojoexecutor.MojoExecutor.Element[] argElements;
            {
                ArrayList<MojoExecutor.Element> args = new ArrayList<MojoExecutor.Element>();

                args.add(element("argument", new StringBuilder("-G").append(generator).toString()));

                for (String optionsKey : options.keySet())
                {
                    args.add(element("argument", new StringBuilder("-D").append(optionsKey).append("=").append(options.get(optionsKey)).toString()));
                }

                args.add(element("argument", sourceDirectory.getAbsolutePath()));

                argElements = new MojoExecutor.Element[args.size()];
                args.toArray(argElements);
            }
            
            getLog().info(argElements.toString());

            executeMojo(
                    plugin(
                            groupId("org.codehaus.mojo"),
                            artifactId("exec-maven-plugin"),
                            version(execPlugin.getVersion())
                    ),
                    goal("exec"),
                    configuration(
                            element(name("workingDirectory"), buildDirectory.getAbsolutePath()),
                            element(name("executable"), cmakeExeName),
                            element(name("arguments"), argElements)
                    ),
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
