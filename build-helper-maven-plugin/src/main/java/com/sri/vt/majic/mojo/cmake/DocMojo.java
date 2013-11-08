package com.sri.vt.majic.mojo.cmake;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

@Mojo(name="cmake-doc", defaultPhase=LifecyclePhase.PREPARE_PACKAGE, requiresProject=true)
public class DocMojo extends RunTargetMojo
{
    @Parameter(alias = "target", defaultValue = "doc")
    protected String installTarget;

    protected String getTarget()
    {
        return installTarget;
    }
}
