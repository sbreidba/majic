package com.sri.vt.majic.mojo.cmake;

import com.sri.vt.majic.mojo.util.OperatingSystemInfo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;

public class CMakeMojo extends ExecMojo
{
    @Parameter(alias = "executable", defaultValue = "cmake")
    protected String cmakeExeName;

    // Append an operating-system-specific sub-directory to the workingDirectory
    @Parameter(defaultValue = "true")
    protected boolean useOSBuildSubdirectory;

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

    // Generates the full build (working) directory, creating it if needed
    protected File prepareBuildDirectory()
    {
        File buildDirectory = super.getWorkingDirectory();
        if (useOSBuildSubdirectory)
        {
            buildDirectory = new File(super.getWorkingDirectory(), operatingSystemInfo.getDistro());
        }

        buildDirectory.mkdirs();

        return buildDirectory;
    }
}
