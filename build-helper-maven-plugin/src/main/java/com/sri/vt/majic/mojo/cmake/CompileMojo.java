package com.sri.vt.majic.mojo.cmake;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

@Mojo(name="cmake-compile", defaultPhase=LifecyclePhase.COMPILE, requiresProject=true)
public class CompileMojo extends RunTargetMojo
{
    @Parameter(alias = "target", defaultValue = "")
    protected String compileTarget;

    protected String getTarget()
    {
        return compileTarget;
    }
}
