package com.sri.vt.majic.mojo.cmake;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

import java.io.File;
import java.util.Set;

@Mojo(name="cmake-untar-dependencies", defaultPhase = LifecyclePhase.INITIALIZE, requiresProject = true, requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class UntarDependenciesMojo extends UntarMojo
{
    // Not used - computed instead.
    @Parameter(defaultValue = "", readonly = true)
    private File tarFile;

    // Normally, this is computed, but can be overridden if desired
    @Parameter(defaultValue = "")
    private File outputDirectory;

    private File currentOutputDirectory;
    
    protected void setCurrentTarFile(File file)
    {
        tarFile = file;
    }

    protected void setCurrentOutputDirectory(String scope)
    {
        if ((outputDirectory != null) && (outputDirectory.length() != 0))
        {
            currentOutputDirectory = outputDirectory;
            return;
        }

        if (scope.equalsIgnoreCase(Artifact.SCOPE_COMPILE))
        {
            // these are internal packages
            currentOutputDirectory = getCMakeDirectories().getExportRoot();
            return;
        }

        if (scope.equalsIgnoreCase(Artifact.SCOPE_RUNTIME))
        {
            // these are external packages
            currentOutputDirectory = getCMakeDirectories().getPackageRoot();
            return;
        }

        currentOutputDirectory = null;
    }

    @Override
    protected File getOutputDirectory()
    {
        return currentOutputDirectory;
    }
    
    @Override
    protected File getTarFile()
    {
        return tarFile;
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        Set artifacts = getProject().getArtifacts();
        if ((artifacts != null) && (!artifacts.isEmpty()))
        {
            for (Object object : artifacts)
            {
                Artifact artifact = (Artifact)object;
                setCurrentOutputDirectory(artifact.getScope());
                if (getOutputDirectory() == null)
                {
                    getLog().info("Ignoring dependency " + artifact.toString() + " with scope " + artifact.getScope());
                    continue;
                }

                getLog().info("Extracting dependency " + artifact.toString() + " with scope " + artifact.getScope() + " to " + getOutputDirectory());
                setCurrentTarFile(artifact.getFile());
                super.execute();
            }
        }
    }
}