package com.sri.vt.majic.mojo.cmake;

import com.sri.vt.majic.mojo.ExecMojo;
import com.sri.vt.majic.mojo.util.CMakeDirectories;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;

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
    @Parameter(defaultValue = "Debug")
    private String config;

    @Parameter(defaultValue = "")
    private File buildRoot;

    private CMakeDirectories cmakeDirectories;

    protected CMakeDirectories getCMakeDirectories()
    {
        if (cmakeDirectories == null)
        {
            cmakeDirectories = new CMakeDirectories(getProject());
        }

        return cmakeDirectories;
    }

    @Override
    protected String getExecutable()
    {
        return cmakeExeName;
    }

    @Override
    protected File getWorkingDirectory()
    {
        File dir = buildRoot;
        if (dir == null)
        {
            try
            {
                dir = getCMakeDirectories().getProjectBindir(getConfig());
            }
            catch (IOException e)
            {
                return null;
            }
        }
        return dir;
    }

    protected String getConfig()
    {
        return config;
    }
}
