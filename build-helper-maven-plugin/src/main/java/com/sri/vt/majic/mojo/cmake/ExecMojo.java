package com.sri.vt.majic.mojo.cmake;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.twdata.maven.mojoexecutor.MojoExecutor.*;

public class ExecMojo extends AbstractMojo
{
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    protected MavenProject project;

    @Parameter(defaultValue = "${session}", readonly = true, required = true)
    protected MavenSession session;

    @Component(hint = "")
    protected BuildPluginManager pluginManager;

    @Parameter(alias = "workingDirectory", defaultValue = "${project.build.directory}")
    private File workingDirectory;

    @Parameter(defaultValue = "")
    private String executable;

    @Parameter(defaultValue = "")
    private String commandlineArgs;

    @Parameter(defaultValue = "")
    private List<String> arguments;

    @Parameter(defaultValue = "false")
    private boolean skip;

    @Parameter(defaultValue = "")
    private File outputFile;

    protected Plugin getExecPlugin(MavenProject project) throws MojoExecutionException
    {
        String execComponentKey = Plugin.constructKey("org.codehaus.mojo", "exec-maven-plugin");
        Plugin execPlugin = project.getPluginManagement().getPluginsAsMap().get(execComponentKey);
        if (execPlugin == null)
        {
            throw new MojoExecutionException(this, "Exec-maven-plugin version missing", "Could not determine exec plugin version. Please declare exec-maven-plugin in PluginManagement");
        }

        return execPlugin;
    }

    protected List<String> getArguments()
    {
        return arguments;
    }

    protected String getExecutable()
    {
        return executable;
    }

    protected File getWorkingDirectory()
    {
        return workingDirectory;
    }

    protected String getCommandlineArgs()
    {
        return commandlineArgs;
    }

    protected File getOutputFile()
    {
        return null;
    }

    protected boolean getSkip()
    {
        return skip;
    }

    protected boolean isUpToDate()
    {
        return false;
    }

    protected void append(List<Element> elements, String name, File value)
    {
        if (value != null)
        {
        	append(elements, name, value.getAbsolutePath());
        }
    }

    protected void append(List<Element> elements, String name, String value)
    {
        getLog().info(getClass().getSimpleName() + ": " + name + " is " + ((value == null) ? "(not set)" : "[" + value + "]"));
		if (value != null)
		{
			elements.add(element(name, value));
		}
    }

    protected void append(List<Element> elements, String elementName, String childName, List<String> values)
    {
        if ((values == null) || (values.size() == 0)) 
        	return;

        List<Element> childElements = new ArrayList<Element>();
        for (String value : values)
        {
            append(childElements, childName, value);
        }

        Element[] childArray = new Element[childElements.size()];
        childElements.toArray(childArray);

        elements.add(new Element(elementName, childArray));
    }

    protected Element[] getConfigurationElements()
    {
        List<Element> elements = new ArrayList<Element>();
        append(elements, "workingDirectory", getWorkingDirectory());
        append(elements, "executable", getExecutable());
        append(elements, "commandlineArgs", getCommandlineArgs());
        append(elements, "arguments", "argument", getArguments());
        append(elements, "skip", Boolean.toString(getSkip()));
        append(elements, "outputFile", getOutputFile());

        Element[] elementArray = new Element[elements.size()];
        elements.toArray(elementArray);
        return elementArray;
    }

    public void execute() throws MojoExecutionException, MojoFailureException
    {
        if (getSkip())
        {
            getLog().info(getClass().getSimpleName() + ": Skipping execution - skip is set.");
            return;
        }

        if (isUpToDate())
        {
            getLog().info(getClass().getSimpleName() + ": Skipping execution - target is up-to-date.");
        }

        Plugin execPlugin = getExecPlugin(project);
        getLog().info("Using exec plugin version: " + execPlugin.getVersion());

        executeMojo(
            plugin(
                groupId("org.codehaus.mojo"),
                artifactId("exec-maven-plugin"),
                version(execPlugin.getVersion())
            ),
            goal("exec"),
            configuration(getConfigurationElements()),
            executionEnvironment(
                project,
                session,
                pluginManager
            )
        );
    }
}
