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
 * Executes the cmake install target.
 */
@Mojo(name="cmake-install", defaultPhase=LifecyclePhase.PREPARE_PACKAGE, requiresProject=true)
public class InstallMojo extends RunTargetMojo
{
    @Parameter(alias = "target", defaultValue = "install")
    private String installTarget;

    @Parameter(defaultValue = "true")
    private boolean cleanExportDirBeforeInstalling;

    @Parameter(defaultValue = CMakeDirectories.CMAKE_PROJECT_EXPORT_DIR_DEFAULT)
    private File projectExportDir;

    /**
     * If not set, this is derived from Maven's global debug flag (i.e. -X).
     */
    @Parameter(defaultValue = "false", property = "majic.install.verbose")
    private Boolean verbose;

    private boolean isVerbose()
    {
        return (verbose || getLog().isDebugEnabled());
    }

    protected String getTarget()
    {
        return installTarget;
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        if (cleanExportDirBeforeInstalling)
        {
            Cleaner cleaner = new Cleaner(getLog(), isVerbose());
            try
            {
                // don't follow symlinks, do fail on error, do retry.
                // this must be cleaned up or we run the risk of getting cruft.
                cleaner.delete(projectExportDir, null, false, true, true);
            }
            catch (IOException e)
            {
                throw new MojoFailureException("Could not clean " + projectExportDir + ": " + e.getMessage());
            }
        }

        super.execute();
    }
}
