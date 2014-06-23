package com.sri.vt.majic.mojo.cmake;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Executes the cmake doc target.
 */
@Mojo(name="cmake-doc", defaultPhase=LifecyclePhase.PREPARE_PACKAGE, requiresProject=true)
public class DocMojo extends RunTargetMojo
{
    @Parameter(alias = "target", defaultValue = "doc")
    private String installTarget;

    /**
     * If not set, this is derived from Maven's global debug flag (i.e. -X).
     */
    @Parameter(defaultValue = "false", property = "majic.cmake.doc.verbose")
    private Boolean verbose;

    @Override
    protected boolean isVerbose()
    {
        return (verbose || getLog().isDebugEnabled());
    }
    protected String getTarget()
    {
        return installTarget;
    }
}
