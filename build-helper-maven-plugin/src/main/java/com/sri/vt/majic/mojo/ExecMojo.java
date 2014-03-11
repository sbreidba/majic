package com.sri.vt.majic.mojo;

import com.sri.vt.majic.util.BuildEnvironment;
import org.apache.commons.lang.SystemUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
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
    @Parameter(defaultValue = "${project.build.directory}")
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

    /**
     * This enables the configuration of INCLUDE, LIB, and PATH environment variables for the given
     * compiler/SDK combination. An example of usage (Tip: Use all caps, or variables won't be expanded correctly.):
     * <pre>
     * {@code}
     * < compilerSDKs>
     *     < vc2012>${env.PROGRAMFILES(X86)}Microsoft SDKs\Windows\v7.1A</vc2012>
     * < /compilerSDKs>
     * </pre>
     * If found, a sub-directory named include is added to the INCLUDE env var; likewise with lib/LIB and
     * bin/PATH. Multiple sdk directories can be specified; separate them with semicolons.
     *
     * Note that this will append to the value of INCLUDE/LIB/PATH environment variables. If you need to
     * add other values in manually, use something like:
     *
     * <pre>
     * {@code}
     * < environmentVariables>
     *     < INSTALL>my_really_cool_path;${env.INSTALL}</INSTALL>
     * < /environmentVariables>
     * </pre>
     *
     * Likewise, if you need to completely replace the value of one of these environment variables, ignoring
     * the current environment, do something like:
     *
     * <pre>
     * {@code}
     * < environmentVariables>
     *     < INSTALL />
     * < /environmentVariables>
     * </pre>
     */
    @Parameter()
    private Map<String, String> compilerSDKs;

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

    protected List<String> getArguments() throws MojoExecutionException
    {
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
        modifiedArguments.add("\"");
        modifiedArguments.add(enquoteString(getBuildEnvironment().getVisualStudioVCVarsAllFile().getAbsolutePath()));
        modifiedArguments.add(getBuildEnvironment().getVCVarsArch());
        modifiedArguments.add("&&");
        modifiedArguments.add(enquoteString(getExecutable()));

        if (getArguments() != null)
        {
            for (String argument : getArguments())
            {
                modifiedArguments.add(enquoteString(argument));
            }
        }

        modifiedArguments.add("\"");

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
                    + " "
                    + getBuildEnvironment().getVCVarsArch()
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

    protected void PrependSDKDirectoryToEnvironment(Map<String, String> mapEnv, File sdkRoot, String subDirectory, String envVar)
    {
        File fullPath = new File(sdkRoot, subDirectory);
        if (!fullPath.exists())
        {
            getLog().warn("Configuring SDK: " + sdkRoot + ", but "
                    + subDirectory + " was not found. Skipping configuration of " + envVar + " env var.");
            return;
        }

        StringBuilder builder = new StringBuilder();

        // if the user has already specified an env var (or we've been called with the same key twice!)
        // then append what exists. If it doesn't exist, check the environment so we don't stomp on it.
        String existingEnv = mapEnv.get(envVar);
        if (existingEnv != null)
        {
            builder.append(mapEnv.get(envVar));
        }
        else
        {
            if (System.getenv(envVar) != null)
            {
                builder.append(System.getenv(envVar));
            }
        }

        if (builder.length() > 0) builder.insert(0, ";");
        builder.insert(0, fullPath.getAbsolutePath());

        mapEnv.put(envVar, builder.toString());
    }

    protected Map<String, String> getEnvironmentVariables()
    {
        if ((getCompilerSDKs() != null) && (getCompilerSDKs().size() > 0))
        {
            try
            {
                BuildEnvironment.Compiler compiler = getBuildEnvironment().getCompiler();
                if (getCompilerSDKs().containsKey(compiler.toString()))
                {
                    // Don't modify the source map - making a copy allows this to be called
                    // transparently many times
                    Map<String, String> envVars = new HashMap<String, String>();
                    if (environmentVariables != null)
                    {
                        envVars.putAll(environmentVariables);
                    }

                    String semiSep = getCompilerSDKs().get(compiler.toString());
                    String[] sdkValues = semiSep.split(";");
                    for (String sdk : sdkValues)
                    {
                        File sdkRoot = new File(sdk);
                        if (!sdkRoot.exists())
                        {
                            getLog().error("SDK directory " + sdk + " configured and active, but could not be found.");
                            continue;
                        }

                        PrependSDKDirectoryToEnvironment(envVars, sdkRoot, "include", "INCLUDE");
                        PrependSDKDirectoryToEnvironment(envVars, sdkRoot, "lib", "LIB");
                        PrependSDKDirectoryToEnvironment(envVars, sdkRoot, "bin", "PATH");
                    }

                    return envVars;
                }
            }
            catch (MojoExecutionException e)
            {
                getLog().warn("Exception caught while building SDK environment: " + e.getMessage());
            }
        }
        
        return environmentVariables;
    }

    protected Map<String, String> getCompilerSDKs()
    {
        return compilerSDKs;
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
        append(elements, "environmentVariables", getEnvironmentVariables());

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
