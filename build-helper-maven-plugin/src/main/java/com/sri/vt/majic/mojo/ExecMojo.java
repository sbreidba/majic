package com.sri.vt.majic.mojo;

import com.sri.vt.majic.util.BuildEnvironment;
import org.apache.commons.lang.SystemUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.twdata.maven.mojoexecutor.MojoExecutor.Element;

/**
 * An extension of the maven exec plugin with extended logging capabilities.
 */
@Mojo(name="exec", requiresProject=true)
public class ExecMojo extends AbstractExecutorMojo
{
    /**
     * See the maven exec plugin for details on this parameter.
     */
    @Parameter(alias = "workingDirectory", defaultValue = "${project.build.directory}")
    private File workingDirectory;

    /**
     * See the maven exec plugin for details on this parameter.
     */
    @Parameter(defaultValue = "")
    private String executable;

    /**
     * See the maven exec plugin for details on this parameter.
     */
    @Parameter(defaultValue = "")
    private String commandlineArgs;

    /**
     * See the maven exec plugin for details on this parameter.
     */
    @Parameter(defaultValue = "")
    private List<String> arguments;

    /**
     * See the maven exec plugin for details on this parameter.
     */
    @Parameter(defaultValue = "false")
    private boolean skip;

    /**
     * See the maven exec plugin for details on this parameter.
     */
    @Parameter(defaultValue = "")
    private File outputFile;

    /**
     * See the maven exec plugin for details on this parameter.
     */
    @Parameter()
    private Map<String, String> environmentVariables;

    /**
     * If enabled, all commands are effectively wrapped with
     *    cmd.exe /c vcvarsall.bat ${vcvars.arch} && [executable arguments]
     * This is only effective under Windows, so it is safe to set to true
     * even on projects expected to execute on Linux.
     */
    @Parameter(defaultValue = "false")
    private boolean enableWindowsVCVarsEnvironment;

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

    protected String getFinalExecutable()
    {
        if (getEnableWindowsCommandShellMode())
        {
            return "cmd.exe";
        }

        return getExecutable();
    }

    protected String getExecutable()
    {
        return executable;
    }

    /** issue:
     *
     * easy enough to modify arguments by prepending cmd /c cruft
     * but we need to add the executable to one of these
     *
     * plan:
     * modify getArguments if it's not null, or if getCommandLine args *is* null
     * modify getCommandlineArgs if it's not null
     *
     */

    protected List<String> getArguments() throws MojoExecutionException {
        return arguments;
    }

    protected List<String> getFinalArguments() throws MojoExecutionException
    {
        if (!getEnableWindowsCommandShellMode()) return getArguments();

        // Let commandline args win
        if (getCommandlineArgs() != null)
        {
            return getArguments();
        }

        List<String> modifiedArguments = new ArrayList<String>();
        modifiedArguments.add("/c");
        modifiedArguments.add(getBuildEnvironment().getVisualStudioVCVarsAllFile().getAbsolutePath());
        modifiedArguments.add("&&");
        modifiedArguments.add(getExecutable());

        if (getArguments() != null)
        {
            modifiedArguments.addAll(getArguments());
        }

        return modifiedArguments;
    }

    protected String getCommandlineArgs()
    {
        return commandlineArgs;
    }

    protected static String enquoteString(String string)
    {
        return "\"" + string + "\"";
    }

    protected String getFinalCommandlineArgs() throws MojoExecutionException
    {
        if (!getEnableWindowsCommandShellMode()) return getCommandlineArgs();

        // only modify this if it is in use already. if it's not,
        // we'll fall back to modifications in getArguments.
        String args = getCommandlineArgs();
        if (args != null)
        {
            args = "/c "
                    + enquoteString(getBuildEnvironment().getVisualStudioVCVarsAllFile().getAbsolutePath())
                    + " && "
                    + enquoteString(getExecutable())
                    + " "
                    + args;
        }

        return args;
    }

    protected File getWorkingDirectory()
    {
        return workingDirectory;
    }

    protected File getOutputFile()
    {
        return outputFile;
    }

    protected boolean getSkip()
    {
        return skip;
    }

    protected boolean isUpToDate()
    {
        return false;
    }

    protected boolean getEnableWindowsCommandShellMode()
    {
        if (!SystemUtils.IS_OS_WINDOWS)
        {
            return false;
        }

        return enableWindowsVCVarsEnvironment;
    }
    
    protected Element[] getConfigurationElements() throws MojoExecutionException
    {
        List<Element> elements = new ArrayList<Element>();
        append(elements, "workingDirectory", getWorkingDirectory());
        append(elements, "executable", getFinalExecutable());
        append(elements, "commandlineArgs", getFinalCommandlineArgs());
        append(elements, "arguments", "argument", getFinalArguments());
        append(elements, "skip", Boolean.toString(getSkip()));
        append(elements, "outputFile", getOutputFile());
        append(elements, "environmentVariables", environmentVariables);

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
