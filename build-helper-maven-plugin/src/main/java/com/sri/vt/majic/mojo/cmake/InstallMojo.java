package com.sri.vt.majic.mojo.cmake;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

@Mojo(name="cmake-install", defaultPhase=LifecyclePhase.PREPARE_PACKAGE, requiresProject=true)
public class InstallMojo extends RunTargetMojo
{
    @Parameter(alias = "target", defaultValue = "install")
    private String installTarget;

    protected String getTarget()
    {
        return installTarget;
    }
}
