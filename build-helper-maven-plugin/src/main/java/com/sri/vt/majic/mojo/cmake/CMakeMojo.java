package com.sri.vt.majic.mojo.cmake;

import com.sri.vt.majic.mojo.ExecMojo;
import com.sri.vt.majic.util.CMakeDirectories;
import org.apache.commons.lang3.SystemUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
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

    private String currentConfig;

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
        return configs;
    }

    protected void execute(String config) throws MojoExecutionException, MojoFailureException
    {
        setCurrentConfig(config);
        super.execute();
    }
    
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        if (configs == null)
        {
            getLog().info("Executing with no config specified.");
            execute(null);
        }
        else if (configs.isEmpty())
        {
            getLog().info("Skipping execution - an empty configs list was specified.");
        }
        else
        {
            for (String config : getConfigs())
            {
                if ((config == null) || (config.length() == 0))
                {
                    getLog().debug("Skipping a null/empty config.");
                    continue;
                }

                getLog().info("Executing the " + config + " config.");
                execute(config);
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
