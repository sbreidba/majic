package com.sri.vt.majic.mojo.cmake;

import com.sri.vt.majic.mojo.util.OperatingSystemInfo;
import org.apache.commons.lang3.SystemUtils;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;

public class CMakeMojo extends ExecMojo
{
    @Parameter(alias = "executable", defaultValue = "cmake")
    private String cmakeExeName;

    // Append an operating-system-specific sub-directory to the workingDirectory
    @Parameter(defaultValue = "true")
    private boolean appendOSBuildSubdirectory;

    @Parameter(defaultValue = BuildConfigDirectoryHandling.Constants.BY_OS_VALUE)
    private BuildConfigDirectoryHandling appendConfigDirectory;

    @Parameter(defaultValue = "")
    private String config;

    protected OperatingSystemInfo operatingSystemInfo = null;

    CMakeMojo()
    {
        try
        {
            init();
        }
        catch(IOException e)
        {
            getLog().error("Could not load operating system info");
        }
    }

    protected void init() throws IOException
    {
        operatingSystemInfo = new OperatingSystemInfo();
    }

    @Override
    protected String getExecutable()
    {
        return cmakeExeName;
    }

    @Override
    protected File getWorkingDirectory()
    {
        return prepareBuildDirectory();
    }

    protected String getConfig()
    {
        return config;
    }
    
    // Generates the full build (working) directory, creating it if needed
    protected File prepareBuildDirectory()
    {
        File buildDirectory = super.getWorkingDirectory();
        if (appendOSBuildSubdirectory)
        {
            buildDirectory = new File(super.getWorkingDirectory(), operatingSystemInfo.getDistro());
        }

        boolean appendConfig;
        switch(appendConfigDirectory)
        {
            case ALWAYS:
                appendConfig = true;
                break;

            case BY_OS:
                appendConfig = (!SystemUtils.IS_OS_WINDOWS);
                break;

            case NEVER:
                appendConfig = true;
                break;

            default:
                assert(false);
                appendConfig = false;
                break;
        }

        if (appendConfig && (config != null) && (config.length() != 0))
        {
            buildDirectory = new File(buildDirectory, getConfig());
        }

        buildDirectory.mkdirs();

        return buildDirectory;
    }
}
