package com.sri.vt.majic.mojo.cmake;

import com.sri.vt.majic.mojo.ExecMojo;
import com.sri.vt.majic.mojo.util.CMakeDirectories;
import com.sri.vt.majic.mojo.util.OperatingSystemInfo;
import org.apache.commons.lang3.SystemUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;

import static com.sri.vt.majic.mojo.util.Logging.info;

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
    @Parameter(defaultValue = "debug")
    private String config;

    @Parameter(defaultValue = "")
    private File buildRoot;

    private CMakeDirectories cmakeDirectories;
    
/*    protected OperatingSystemInfo operatingSystemInfo = null;

    CMakeMojo()
    {
        try
        {
            init();
        }
        catch(IOException e)
        {
            // TODO this won't work - getLog() isn't usable in constructor!
            getLog().error("Could not load operating system info");
        }
    }
*/
    protected void init() throws IOException
    {
        //operatingSystemInfo = new OperatingSystemInfo();
        cmakeDirectories = new CMakeDirectories(project);
    }

    protected CMakeDirectories getCMakeDirectories()
    {
        assert(cmakeDirectories != null);
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

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        try
        {
            init();
        }
        catch (IOException e)
        {
            throw new MojoExecutionException(e.getMessage());
        }
        
        super.execute();
    }

    protected String getConfig()
    {
        return config;
    }

    /*
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
    }*/
}
