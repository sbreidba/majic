package com.sri.vt.majic.mojo.cmake;

import com.sri.vt.majic.util.CMakeDirectories;
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

    // fallback for unknown scope
    @Parameter(defaultValue = CMakeDirectories.CMAKE_PROJECT_BIN_DIRECTORY_DEFAULT)
    private File outputDirectory;

    @Parameter(defaultValue = "${project.build.directory}/temp-sources")
    private File sourceOutputDirectory;

    @Parameter(defaultValue = CMakeDirectories.CMAKE_EXPORT_ROOT_DEFAULT)
    private File compileScopeOutputDirectory;

    @Parameter(defaultValue = CMakeDirectories.CMAKE_PACKAGE_ROOT_DEFAULT)
    private File runtimeScopeOutputDirectory;

    // TODO! select the types to be extracted!
    
    private File currentOutputDirectory;
    
    protected void setCurrentTarFile(File file)
    {
        tarFile = file;
    }

    protected void setCurrentOutputDirectory(Artifact artifact)
    {
        if (artifact.getClassifier().equalsIgnoreCase("sources"))
        {
            currentOutputDirectory = sourceOutputDirectory;
            return;
        }
        
        if (artifact.getScope().equalsIgnoreCase(Artifact.SCOPE_COMPILE))
        {
            currentOutputDirectory = compileScopeOutputDirectory;
            return;
        }

        if (artifact.getScope().equalsIgnoreCase(Artifact.SCOPE_RUNTIME))
        {
            // these are external packages
            currentOutputDirectory = runtimeScopeOutputDirectory;
            return;
        }

        currentOutputDirectory = outputDirectory;
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
                setCurrentOutputDirectory(artifact);
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