package com.sri.vt.majic.mojo.cmake;

import com.sri.vt.majic.util.CMakeDirectories;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.clean.Cleaner;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;

/**
 * Executes the CMake build step.
 */
@Mojo(name="cmake-compile", defaultPhase=LifecyclePhase.COMPILE, requiresProject=true)
public class CompileMojo extends RunTargetMojo
{
    @Parameter(alias = "target", defaultValue = "")
    private String compileTarget;

    /**
     * Note that if this is enabled, then the clean step always occurs,
     * even if this goal is otherwise skipped. If this is not desirable,
     * tie this variable and your skip variable together.
     */
    @Parameter(defaultValue = "true")
    private boolean cleanPackageDirBeforeBuilding;
    
    @Parameter(defaultValue = CMakeDirectories.CMAKE_PROJECT_PACKAGEDIR_DEFAULT)
    private File projectPackageDir;

    /**
     * If not set, this is derived from Maven's global debug flag (i.e. -X).
     */
    @Parameter(defaultValue = "false", property = "majic.compile.verbose")
    private Boolean verbose;

    protected String getTarget()
    {
        return compileTarget;
    }

    private boolean isVerbose()
    {
        return ( verbose || getLog().isDebugEnabled());
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        if (cleanPackageDirBeforeBuilding)
        {
            Cleaner cleaner = new Cleaner(getLog(), isVerbose());
            try
            {
                // don't follow symlinks, do fail on error, do retry.
                // this must be cleaned up or we run the risk of getting cruft.
                cleaner.delete(projectPackageDir, null, false, true, true);
            }
            catch (IOException e)
            {
                throw new MojoFailureException("Could not clean " + projectPackageDir + ": " + e.getMessage());
            }
        }

        super.execute();
    }
}
