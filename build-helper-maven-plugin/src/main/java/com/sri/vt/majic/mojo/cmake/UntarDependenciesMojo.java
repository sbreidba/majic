package com.sri.vt.majic.mojo.cmake;

import com.sri.vt.majic.util.ArtifactHelper;
import com.sri.vt.majic.util.CMakeDirectories;
import com.sri.vt.majic.util.clean.Cleaner;
import org.apache.commons.lang3.SystemUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Untars dependencies to specific directories based on dependency scope.
 */
@Mojo(name="cmake-untar-dependencies", defaultPhase = LifecyclePhase.GENERATE_SOURCES, requiresProject = true, requiresDependencyResolution = ResolutionScope.TEST)
public class UntarDependenciesMojo extends UntarMojo
{
    // Not settable by the user - always computed instead.
    @Parameter(defaultValue = "", readonly = true)
    private File tarFile;

    /**
     * If set, symlinks are created that point to the untarred dependencies.
     */
    @Parameter(defaultValue = "true", property = "cmake.untar.create.symlinks")
    private boolean createSymbolicLinks;

    /**
     * If symbolic links are being created, they will be output to this directory.
     */
    @Parameter(defaultValue = CMakeDirectories.CMAKE_PROJECT_PACKAGE_DIR_DEFAULT, property = "cmake.untar.symlink.directory")
    private File getSymbolicLinkDirectory;

    // This is now determined automatically and cannot be overridden.
    @Parameter(defaultValue = "", readonly = true)
    private File outputDirectory;

    // TODO! select the types to be extracted!

    private Artifact currentArtifact;

    @Parameter( defaultValue = "${reactorProjects}", readonly = true )
    protected List<MavenProject> reactorProjects;

    protected void setCurrentArtifact(Artifact artifact)
    {
        currentArtifact = artifact;
    }

    protected File getOutputDirectory()
    {
        return ArtifactHelper.getRepoExtractDirectory(reactorProjects, currentArtifact);
    }

    protected boolean getCreateSymbolicLinks()
    {
        return createSymbolicLinks;
    }
    
    protected File getSymbolicLinkDirectory()
    {
        return getSymbolicLinkDirectory;
    }
    
    @Override
    protected File getMarkersDirectory()
    {
        File baseDir = currentArtifact.getFile().getParentFile();
        return new File(baseDir, "cmake-untar-dependencies/markers");
    }

    @Override
    protected File getTarFile()
    {
        return currentArtifact.getFile();
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        if (getCreateSymbolicLinks())
        {
            if (getSymbolicLinkDirectory() == null)
            {
                throw new MojoExecutionException("Must specify symlink destination directory");
            }

            if (getSymbolicLinkDirectory().exists())
            {
                Cleaner cleaner = new Cleaner(getLog(), isVerbose());
                try
                {
                    cleaner.delete(getSymbolicLinkDirectory(), null, false, true, true);
                }
                catch (IOException e)
                {
                    throw new MojoExecutionException(e.getMessage());
                }
            }
        }

        Set<Artifact> reactorArtifacts = new HashSet<>();

        Set artifacts = getProject().getArtifacts();
        if ((artifacts != null) && (!artifacts.isEmpty()))
        {
            for (Object object : artifacts)
            {
                boolean isReactorArtifact = false;
                Artifact artifact = (Artifact)object;

                Artifact reactorArtifact = ArtifactHelper.getArtifactFromReactor(reactorProjects, artifact);
                if (reactorArtifact != null)
                {
                    isReactorArtifact = true;
                    artifact = reactorArtifact;
                    reactorArtifacts.add(reactorArtifact);
                }

                setCurrentArtifact(artifact);
                if (getOutputDirectory() == null)
                {
                    getLog().warn("Could not determine output directory for " + artifact.toString() + ". Ignoring.");
                    continue;
                }

                // We don't bother cleaning/extracting if this is a reactor artifact -
                // what we need is available on-disk.
                if (!isReactorArtifact)
                {
                    Cleaner cleaner = new Cleaner(getLog(), isVerbose());
                    if (!getSkip() && !isUpToDate())
                    {
                        try
                        {
                            // Since this artifact was out of date, we may be extracting to the same location.
                            // We'll clean up what was there before extracting again.
                            cleaner.delete(getOutputDirectory(), null, false, true, true);
                        }
                        catch(IOException e)
                        {
                            throw new MojoFailureException("Could not clean up directory during untar: " + e.getMessage());
                        }
                    }

                    super.execute();
                }

                if (getCreateSymbolicLinks())
                {
                    java.nio.file.Path target = getOutputDirectory().toPath();

                    File symLink = ArtifactHelper.getSymlinkDirectory(getSymbolicLinkDirectory(), artifact);

                    if (symLink.exists())
                    {
                        getLog().error("Symlink " + symLink + " already exists.");
                    }

//                    if (!symLink.exists())
                 //   {
                        java.nio.file.Path symLinkPath = symLink.toPath();

                        try
                        {
                            if (isVerbose()) getLog().info("Creating symlink from " + symLinkPath + " to " + target);

                            boolean created = false;
                            int[] delays = { 0, 50, 250, 750, 1500, 5000, 7500 };
                            for ( int i = 0; !created && i < delays.length; i++ )
                            {
                                try
                                {
                                    if (SystemUtils.IS_OS_WINDOWS)
                                    {
                                        // try to release any locks held by non-closed files
                                        System.gc();
                                    }

                                    getSymbolicLinkDirectory().mkdirs();
                                    Thread.sleep(delays[i]);
                                    java.nio.file.Files.createSymbolicLink(symLinkPath, target);
                                    created = symLink.exists();
                                }
                                catch (InterruptedException e)
                                {
                                    // ignore
                                }
                                catch (IOException e)
                                {
                                    // when this is related to admin rights, the specific class will be a
                                    // java.nio.file.FileSystemException, and the message is "A required
                                    // privilege is not held by the client."

                                    // eat exception unless we're done trying
                                    if (i == (delays.length - 1)) throw e;

                                    getLog().warn("Symlink creation failed with " + e.toString() + ". Retrying in " + delays[i + 1] + " milliseconds.");
                                }
                            }
                        }
                        catch(SecurityException e)
                        {
                            throw new MojoExecutionException(
                                    "Security exception occurred when creating symlink. " +
                                    "Either run as Administrator or set cmake.untar.create.symlinks to false. " +
                                    e.getMessage());
                        }
                        catch(IOException e)
                        {
                            throw new MojoExecutionException("Could not create dependency symlink: " + e.getMessage());
                        }
                    //}
                }
            }
        }

        getLog().info("Dependency Summary:");
        getLog().info("");
        getLog().info("Local repository/artifact cache:");
        logArtifacts(artifacts, reactorArtifacts);
        getLog().info("Reactor artifacts:");
        logArtifacts(reactorArtifacts, null);
    }

    private void logArtifacts(Set artifacts, Set excludeArtifacts)
    {
        if (artifacts.size() == 0)
        {
            getLog().info("   (none)");
        }
        else
        {
            for (Object artifactObject : artifacts)
            {
                Artifact artifact = (Artifact)artifactObject;
                if ((excludeArtifacts == null) || !excludeArtifacts.contains(artifact)) getLog().info("   " + artifact);
            }
        }
    }
}