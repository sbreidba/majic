package com.sri.vt.majic.mojo;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.twdata.maven.mojoexecutor.MojoExecutor.*;

@Mojo(name="exec", requiresProject=true)
public class ExecMojo extends AbstractExecutorMojo
{
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

    @Override
    protected boolean shouldFailIfPluginNotFound()
    {
        return true;
    }

    @Override
    protected String getPluginGroupId()
    {
        return "org.codehaus.mojo";
    }

    @Override
    protected String getPluginArtifactId()
    {
        return "exec-maven-plugin";
    }

    @Override
    protected String getGoal()
    {
        return "exec";
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
            getLog().info("Skipping execution - skip is set.");
            return;
        }

        if (isUpToDate())
        {
            getLog().info("Skipping execution - target is up-to-date.");
            return;
        }

        super.execute();
    }
}
