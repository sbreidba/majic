package com.sri.vt.majic.mojo.cmake;

import com.sri.vt.majic.util.CMakeDirectories;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;

/**
 * Executes the cmake install target.
 */
@Mojo(name="cmake-install", defaultPhase=LifecyclePhase.PREPARE_PACKAGE, requiresProject=true)
public class InstallMojo extends RunTargetMojo
{
    @Parameter(alias = "target", defaultValue = "install")
    private String installTarget;

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
}
