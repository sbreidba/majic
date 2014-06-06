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
import org.codehaus.plexus.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
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
     * Normally tarballs are expanded within the m2 repository.
     * If this is not desirable, override it here.
     *
     * Other goals such as config may have interactions with this behavior, so
     * it is suggested that the property be set instead of directly
     * configuring this value.
     */
    @Parameter(defaultValue = "true", property = "cmake.untar.inplace")
    private boolean extractInPlace;

    /**
     * If set, symlinks are created that point to the untarred dependencies.
     *
     * Other goals such as config may have interactions with this behavior, so
     * it is suggested that the property be set instead of directly
     * configuring this value.
     */
    @Parameter(defaultValue = "true", property = "cmake.untar.create.symlinks")
    private boolean createSymbolicLinks;

    /**
     * Only valid when extractInPlace is enabled. This is the location where symbolic
     * links to dependencies are created.
     *
     * Other goals such as config may have interactions with this behavior, so
     * it is suggested that the property be set instead of directly
     * configuring this value.
     */
    @Parameter(defaultValue = CMakeDirectories.CMAKE_PROJECT_PACKAGE_DIR_DEFAULT, property = "cmake.untar.symlink.directory")
    private File symbolicLinkDirectory;

    /**
     * The fallback output directory for unknown scopes.
     */
    @Parameter(defaultValue = CMakeDirectories.CMAKE_PROJECT_BIN_DIRECTORY_DEFAULT)
    private File outputDirectory;

    /**
     * The output directory for test scoped dependencies.
     */
    @Parameter(defaultValue = CMakeDirectories.CMAKE_PROJECT_BIN_DIRECTORY_DEFAULT)
    private File testScopeOutputDirectory;

    /**
     * The output directory for compile scoped dependencies.
     */
    @Parameter(defaultValue = CMakeDirectories.CMAKE_EXPORT_ROOT_DEFAULT)
    private File compileScopeOutputDirectory;

    /**
     * The output directory for runtime scoped dependencies.
     */
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

    @Parameter( defaultValue = "${reactorProjects}", readonly = true )
    protected List<MavenProject> reactorProjects;

    protected void setCurrentArtifact(Artifact artifact)
    {
        currentArtifact = artifact;
    }

    protected File getOutputDirectory()
    {
        if (getExtractInPlace())
        {
            return ArtifactHelper.getRepoExtractDirectory(currentArtifact);
        }
        else
        {
            getLog().warn("Deprecated functionality used. Only in-place extraction will be supported in future releases.");
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
    }

    protected boolean getCreateSymbolicLinks()
    {
        return createSymbolicLinks;
    }
    
    protected File getSymbolicLinkDirectory()
    {
        return symbolicLinkDirectory;
    }
    
    @Override
    protected File getMarkersDirectory()
    {
        File baseDir;
        if (getExtractInPlace())
        {
            baseDir = currentArtifact.getFile().getParentFile();
        }
        else
        {
            baseDir = getOutputDirectory();
        }
        
        return new File(baseDir, "cmake-untar-dependencies/markers");
    }

    @Override
    protected File getTarFile()
    {
        return currentArtifact.getFile();
    }

    /**
     * Null-safe compare of two artifacts based on groupId, artifactId, version, type and classifier.
     *
     * @param a the first artifact.
     * @param b the second artifact.
     * @return <code>true</code> if and only if the two artifacts have the same groupId, artifactId, version,
     *         type and classifier.
     */
    private static boolean equals( Artifact a, Artifact b )
    {
        return a == b || !( a == null || b == null )
            && StringUtils.equals(a.getGroupId(), b.getGroupId())
            && StringUtils.equals(a.getArtifactId(), b.getArtifactId())
            && StringUtils.equals(a.getVersion(), b.getVersion())
            && StringUtils.equals(a.getType(), b.getType())
            && StringUtils.equals(a.getClassifier(), b.getClassifier());
    }

    /**
     * Returns <code>true</code> if the artifact has a file.
     *
     * @param artifact the artifact (may be null)
     * @return <code>true</code> if and only if the artifact is non-null and has a file.
     */
     private static boolean hasFile(Artifact artifact)
     {
        return artifact != null && artifact.getFile() != null && artifact.getFile().isFile();
    }

    protected Artifact getArtifactFromReactor(Artifact artifact)
    {
        if (reactorProjects == null)
        {
            return null;
        }

        for (MavenProject mavenProject : reactorProjects)
        {
            // check the main artifact
            if (equals(artifact, mavenProject.getArtifact()) && hasFile(mavenProject.getArtifact()))
            {
                return mavenProject.getArtifact();
            }

            // check any attached artifacts
            for (Artifact attachedArtifact : mavenProject.getAttachedArtifacts())
            {
                if (equals(artifact, attachedArtifact) && hasFile(attachedArtifact))
                {
                    return attachedArtifact;
                }
            }
        }

        return null;
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
                File[] files = getSymbolicLinkDirectory().listFiles();
                for (File file : files)
                {
                    if (java.nio.file.Files.isSymbolicLink(file.toPath()))
                    {
                        if (isVerbose())
                        {
                            getLog().info("Removing symlink " + file.getPath());
                        }

                        file.delete();
                    }
                }

            }
        }

        Set<Artifact> reactorArtifacts = new HashSet<Artifact>();

        Set artifacts = getProject().getArtifacts();
        if ((artifacts != null) && (!artifacts.isEmpty()))
        {
            for (Object object : artifacts)
            {
                boolean isReactorArtifact = false;
                Artifact artifact = (Artifact)object;

                Artifact reactorArtifact = getArtifactFromReactor(artifact);
                if (reactorArtifact != null)
                {
                    isReactorArtifact = true;
                    artifact = reactorArtifact;
                    reactorArtifacts.add(reactorArtifact);
                }

                setCurrentArtifact(artifact);
                if (getOutputDirectory() == null)
                {
                    if (isVerbose()) getLog().info("Ignoring dependency " + artifact.toString());
                    continue;
                }

                // Extracting in-place is slightly safer as we can always remove the output dir contents
                // If we extract to module dirs, we end up merging directories, so we can't clean out
                // old cruft. Note that we don't bother cleaning/extracting if this is a reactor artifact -
                // what we need is available on-disk.
                if (!isReactorArtifact && getExtractInPlace())
                {
                    Cleaner cleaner = new Cleaner(getLog(), isVerbose());
                    if (!getSkip() && !isUpToDate())
                    {
                        try
                        {
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
                    getSymbolicLinkDirectory().mkdirs();

                    java.nio.file.Path target;
                    if (isReactorArtifact)
                    {
                        getLog().info("Artifact " + artifact + " was built with the reactor. The current build output will be used instead of unpacking from the local repository.");
                        String path = artifact.getFile().getAbsolutePath();
                        if (path.endsWith("." + artifact.getType()))
                        {
                            path = path.substring(0, path.length() - artifact.getType().length() - 1);
                        }
                        else
                        {
                            getLog().error("Could not determine reactor project directory from artifact name. Symlink will likely be incorrect.");
                        }

                        target = java.nio.file.Paths.get(path);
                    }
                    else
                    {
                        target = getOutputDirectory().toPath();
                    }

                    File symLink = new File(
                            getSymbolicLinkDirectory(),
                            artifact.getArtifactId() + "-" + artifact.getBaseVersion());

                    if (!symLink.exists())
                    {
                        java.nio.file.Path symLinkPath = symLink.toPath();

                        try
                        {
                            if (isVerbose()) getLog().info("Creating symlink from " + symLinkPath + " to " + target);

                            boolean created = false;
                            int[] delays = { 0, 50, 250, 750, 1500, 5000 };
                            for ( int i = 0; !created && i < delays.length; i++ )
                            {
                                try
                                {
                                    java.nio.file.Files.createSymbolicLink(symLinkPath, target);
                                    Thread.sleep(delays[i]);
                                    created = true;
                                }
                                catch (InterruptedException e)
                                {
                                    // ignore
                                }
                                catch (IOException e)
                                {
                                    // eat exception unless we're done trying
                                    if (i == (delays.length - 1)) throw e;

                                    if (SystemUtils.IS_OS_WINDOWS)
                                    {
                                        // try to release any locks held by non-closed files
                                        System.gc();
                                    }

                                    getLog().warn("Symlink creation failed. Retrying.");
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
                    }
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

    protected boolean getExtractInPlace()
    {
        return extractInPlace;
    }
}