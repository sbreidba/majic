package com.sri.vt.majic.mojo.cmake;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;

/**
 * Executes CTest to run unit tests.
 */
@Mojo(name="cmake-test", defaultPhase= LifecyclePhase.TEST, requiresProject=true)
public class CTestMojo extends CMakeMojo
{
    @Parameter(alias = "executable", defaultValue = "ctest")
    private String exeName;

    @Parameter(defaultValue = "true")
    boolean verbose;

    @Parameter(defaultValue = "skipTests")
    boolean skip;

    @Override
    protected boolean getSkip()
    {
        if (super.getSkip() || skip) return true;

        File testConfigFile = new File(getWorkingDirectory(), "CTestTestfile.cmake");
        if (!testConfigFile.exists())
        {
            getLog().info("No test config file (" + testConfigFile.getAbsolutePath() + ") - skipping tests");
            return true;
        }

        return false;
    }

    @Override
    protected String getCommandlineArgs()
    {
        StringBuilder builder = new StringBuilder();

        String config = getCurrentConfig();
        if ((config != null) && (config.length() != 0))
        {
            builder.append(" --build-config ");
            builder.append(config);
        }

        if (verbose)
        {
            builder.append(" --verbose");
        }

        return builder.toString();
    }

    @Override
    protected String getExecutable()
    {
        return exeName;
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        execute(ExecutionMode.ExecutionPerConfig);
    }
}
