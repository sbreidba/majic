package com.sri.vt.majic.mojo.cmake;

import com.sri.vt.majic.util.CMakeDirectories;
import com.sri.vt.majic.util.clean.Cleaner;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;

/**
 * Removes temporary build outputs.
 */
@Mojo(name="cmake-clean", defaultPhase= LifecyclePhase.CLEAN, requiresProject=true)
public class CleanMojo extends AbstractMojo
{
    @Parameter(defaultValue = CMakeDirectories.CMAKE_PROJECT_BIN_DIRECTORY_DEFAULT)
    private File cleanDirectory;

    /**
     * If not set, this is derived from Maven's global debug flag (i.e. -X).
     */
    @Parameter(defaultValue = "false", property = "majic.clean.verbose")
    private Boolean verbose;

    private boolean isVerbose()
    {
        return (verbose || getLog().isDebugEnabled());
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        Cleaner cleaner = new Cleaner(getLog(), isVerbose());
        try
        {
            cleaner.delete(cleanDirectory, null, false, true, true);
        }
        catch (IOException e)
        {
            throw new MojoFailureException("Could not clean " + cleanDirectory + ": " + e.getMessage());
        }
    }
}
