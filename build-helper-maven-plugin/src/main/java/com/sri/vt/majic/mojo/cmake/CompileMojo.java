package com.sri.vt.majic.mojo.cmake;

import com.sri.vt.majic.util.CMakeDirectories;
import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;

@Mojo(name="cmake-compile", defaultPhase=LifecyclePhase.COMPILE, requiresProject=true)
public class CompileMojo extends RunTargetMojo
{
    @Parameter(alias = "target", defaultValue = "")
    private String compileTarget;

    @Parameter(defaultValue = "true")
    private boolean cleanPackageDirBeforeBuilding;
    
    @Parameter(defaultValue = CMakeDirectories.CMAKE_PROJECT_PACKAGEDIR_DEFAULT)
    private File projectPackageDir;

    protected String getTarget()
    {
        return compileTarget;
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        if (cleanPackageDirBeforeBuilding)
        {
            getLog().info("Cleaning " + projectPackageDir);
            try
            {
                FileUtils.deleteDirectory(projectPackageDir);
            }
            catch (IOException e)
            {
                getLog().warn("Could not clean " + projectPackageDir + ": " + e.getMessage());
            }
        }

        super.execute();
    }
}
