package com.sri.vt.majic.mojo.cmake;

import com.sri.vt.majic.mojo.ExecMojo;
import com.sri.vt.majic.util.CMakeDirectories;
import org.apache.commons.lang3.SystemUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CMakeMojo extends ExecMojo
{
    @Parameter(alias = "executable", defaultValue = "cmake")
    private String cmakeExeName;

    @Parameter(defaultValue = "")
    private List<String> configs;

    @Parameter(defaultValue = CMakeDirectories.CMAKE_PROJECT_BIN_DIRECTORY_DEFAULT)
    private File buildRoot;

    @Parameter(defaultValue = "false")
    private boolean skipRelease;

    @Parameter(defaultValue = "false")
    private boolean skipDebug;

    private String currentConfig;

    protected enum ExecutionMode
    {
        ExecutionPerConfig,
        ExecutionAsSemicolonSeparatedList
    }

    @Override
    protected String getExecutable()
    {
        return cmakeExeName;
    }

    protected File getWorkingDirectory()
    {
        String config = getCurrentConfig();
        if ((config != null) && (!SystemUtils.IS_OS_WINDOWS))
        {
            return new File(buildRoot, getCurrentConfig().toLowerCase(Locale.ENGLISH));
        }
        else
        {
            return buildRoot;
        }
    }

    protected List<String> getConfigs()
    {
        if (configs != null)
        {
            return configs;
        }

        ArrayList<String> defaultConfigs = new ArrayList<String>();
        if (SystemUtils.IS_OS_WINDOWS)
        {
            if (!skipDebug) defaultConfigs.add("Debug");
            if (!skipRelease) defaultConfigs.add("Release");
        }
        else
        {
            if (!skipRelease) defaultConfigs.add("Release");
        }

        return defaultConfigs;
    }

    protected void execute(String config) throws MojoExecutionException, MojoFailureException
    {
        setCurrentConfig(config);
        super.execute();
    }

    protected void execute(ExecutionMode mode) throws MojoExecutionException, MojoFailureException
    {
        List<String> configs = getConfigs();
        assert(configs != null);

        if (configs.isEmpty())
        {
            getLog().info("An empty configs list was specified. Executing with no config.");
            execute("");
        }
        else
        {
            String allConfigs = "";
            for (String config : getConfigs())
            {
                if ((config == null) || (config.length() == 0))
                {
                    getLog().debug("Skipping a null/empty config.");
                    continue;
                }

                if (mode == ExecutionMode.ExecutionAsSemicolonSeparatedList)
                {
                    if (allConfigs.length() > 0)
                    {
                        allConfigs += ";";
                    }

                    allConfigs += config;
                }
                else
                {
                    getLog().info("Executing the " + config + " config (per config mode).");
                    execute(config);
                }
            }

            if (mode == ExecutionMode.ExecutionAsSemicolonSeparatedList)
            {
                getLog().info("Executing the " + allConfigs + " config (list mode).");
                execute(allConfigs);
            }
        }
    }

    protected String getCurrentConfig()
    {
        return currentConfig;
    }

    protected void setCurrentConfig(String currentConfig)
    {
        this.currentConfig = currentConfig;
    }
}
