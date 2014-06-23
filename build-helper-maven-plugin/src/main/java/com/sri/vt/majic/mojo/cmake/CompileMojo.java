package com.sri.vt.majic.mojo.cmake;

import com.sri.vt.majic.util.CMakeDirectories;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;

/**
 * Executes the CMake build step.
 */
@Mojo(name="cmake-compile", defaultPhase=LifecyclePhase.COMPILE, requiresProject=true)
public class CompileMojo extends RunTargetMojo
{
    @Parameter(alias = "target", defaultValue = "")
    private String compileTarget;

    @Parameter(defaultValue = CMakeDirectories.CMAKE_PROJECT_EXPORT_DIR_DEFAULT)
    private File projectExportDir;

    /**
     * If not set, this is derived from Maven's global debug flag (i.e. -X).
     */
    @Parameter(defaultValue = "false", property = "majic.compile.verbose")
    private Boolean verbose;

    protected String getTarget()
    {
        return compileTarget;
    }

    protected boolean isVerbose()
    {
        return (verbose || getLog().isDebugEnabled());
    }
}
