package com.sri.vt.majic.mojo.cmake;

import com.sri.vt.majic.mojo.ExecMojo;
import com.sri.vt.majic.mojo.util.CMakeDirectories;
import org.apache.commons.lang3.SystemUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class CMakeMojo extends ExecMojo
{
    @Parameter(alias = "executable", defaultValue = "cmake")
    private String cmakeExeName;

    /*
    // Append an operating-system-specific sub-directory to the workingDirectory
    @Parameter(defaultValue = "true")
    private boolean appendOSBuildSubdirectory;

    @Parameter(defaultValue = BuildConfigDirectoryHandling.Constants.BY_OS_VALUE)
    private BuildConfigDirectoryHandling appendConfigDirectory;
*/
    @Parameter(defaultValue = "")
    private List<String> configs;

    @Parameter(defaultValue = "")
    private File buildRoot;

    private CMakeDirectories cmakeDirectories;
    private String currentConfig;

    protected CMakeDirectories getCMakeDirectories()
    {
        // must assign on demand - getLog() isn't available in the constructor
        if (cmakeDirectories == null)
        {
            cmakeDirectories = new CMakeDirectories(getProject(), getLog());
        }

        return cmakeDirectories;
    }

    @Override
    protected String getExecutable()
    {
        return cmakeExeName;
    }

    protected File getWorkingDirectory()
    {
        String config = getCurrentConfig();

        if (buildRoot != null)
        {
            if ((config != null) && (!SystemUtils.IS_OS_WINDOWS))
            {
                return new File(buildRoot, getCurrentConfig());
            }
            else
            {
                return buildRoot;
            }
        }
        else
        {
            try
            {
                return getCMakeDirectories().getProjectBindir(config);
            }
            catch (IOException e)
            {
                getLog().error("Could not default to project bindir directory for config " + config);
                return null;
            }
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
        if ((configs == null) || (configs.size() == 0))
        {
            execute(null);
        }
        else
        {
            for (String config : getConfigs())
            {
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
