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

@Mojo(name="cmake-untar-dependencies", defaultPhase = LifecyclePhase.INITIALIZE, requiresProject = true, requiresDependencyResolution = ResolutionScope.TEST)
public class UntarDependenciesMojo extends UntarMojo
{
    // Not used - computed instead.
    @Parameter(defaultValue = "", readonly = true)
    private File tarFile;

    // fallback for unknown scope
    @Parameter(defaultValue = CMakeDirectories.CMAKE_PROJECT_BIN_DIRECTORY_DEFAULT)
    private File outputDirectory;

    @Parameter(defaultValue = CMakeDirectories.CMAKE_PROJECT_BIN_DIRECTORY_DEFAULT)
    private File testScopeOutputDirectory;

    @Parameter(defaultValue = CMakeDirectories.CMAKE_EXPORT_ROOT_DEFAULT)
    private File compileScopeOutputDirectory;

    @Parameter(defaultValue = CMakeDirectories.CMAKE_PACKAGE_ROOT_DEFAULT)
    private File runtimeScopeOutputDirectory;

    // TODO! select the types to be extracted!

    private Artifact currentArtifact;

    @Override
    protected boolean shouldStripRootDirectory()
    {
        // For source artifacts, we keep the directory
        String classifier = currentArtifact.getClassifier();
        if ((classifier != null) && classifier.equalsIgnoreCase("sources"))
        {
            return false;
        }
        
        return super.shouldStripRootDirectory();
    }

    protected void setCurrentArtifact(Artifact artifact)
    {
        currentArtifact = artifact;
    }

    protected File getOutputDirectory()
    {
        if (currentArtifact.getScope().equalsIgnoreCase(Artifact.SCOPE_TEST))
        {
            return testScopeOutputDirectory;
        }

        if (currentArtifact.getScope().equalsIgnoreCase(Artifact.SCOPE_COMPILE))
        {
            return compileScopeOutputDirectory;
        }

        if (currentArtifact.getScope().equalsIgnoreCase(Artifact.SCOPE_RUNTIME))
        {
            // these are external packages
            return runtimeScopeOutputDirectory;
        }

        return outputDirectory;
    }

    @Override
    protected File getMarkersDirectory()
    {
        return new File(getOutputDirectory(), "cmake-untar-dependencies/markers");
    }

    @Override
    protected File getTarFile()
    {
        return currentArtifact.getFile();
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
                setCurrentArtifact(artifact);
                if (getOutputDirectory() == null)
                {
                    getLog().info("Ignoring dependency " + artifact.toString());
                    continue;
                }

                getLog().info("Extracting dependency " + artifact.toString() + " to " + getOutputDirectory());
                super.execute();
            }
        }
    }
}