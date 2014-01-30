package com.sri.vt.majic.mojo.cmake;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.clean.Cleaner;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;

/**
 * Unpacks the specified tar file.
 */
@Mojo(name="cmake-untar", defaultPhase = LifecyclePhase.GENERATE_SOURCES, requiresProject = true)
public class UntarMojo extends CMakeCommandMojo
{
    /**
     * The command to pass to <code>cmake -E</code>
     */
    @Parameter(defaultValue = "tar xjf", required = true)
    private String command;

    /**
     * The file to untar.
     */
    @Parameter(defaultValue = "", required = true)
    private File tarFile;

    /**
     * The directory that contains small marker files used to determine when tarballs need to be unpacked.
     */
    @Parameter(defaultValue = "${cmake.build.root}/cmake-untar/markers")
    private File markersDirectory;

    /**
     * If set, moves the contents of the first directory found up one level. Often useful for
     * dealing with standard tarballs where the root directory recapitulates the tar file name.
     */
    @Parameter(defaultValue = "true")
    private boolean stripRootDirectory;

    /**
     * The directory to extract the tar's contents to.
     */
    @Parameter(defaultValue = "", required = true)
    private File outputDirectory;

    /**
     * If not set, this is derived from Maven's global debug flag (i.e. -X).
     */
    @Parameter(defaultValue = "false", property = "majic.untar.verbose")
    private Boolean verbose;

    private boolean isVerbose()
    {
        return ( verbose || getLog().isDebugEnabled());
    }

    protected String getCommand()
    {
        return command;
    }

    protected File getTarFile()
    {
        return tarFile;
    }

    // The extract dir becomes the working directory for purposes of exec()
    // getTemporaryExtractDir exists since it's more expressive when used elsewhere.

    protected File getWorkingDirectory()
    {
        return getTemporaryExtractDir();
    }

    protected File getOutputDirectory()
    {
        return outputDirectory;
    }

    protected File getTemporaryExtractDir()
    {
        // If we do the tar enumeration approach, this will go away... if not
        // we may want to make it a property instead of making assumptions here.
        return new File(getMarkersDirectory(), "temp");
    }

    protected File getMarkersDirectory()
    {
        return markersDirectory;
    }

    @Override
    protected boolean isUpToDate()
    {
        getLog().debug("Checking up-to-date: " + getTarFile() + " vs. " + getMarkerFile());

        return (
            getMarkerFile().exists()
            && (getTarFile().lastModified() <= getMarkerFile().lastModified())
        );
    }

    protected File getMarkerFile()
    {
        return new File(getMarkersDirectory(), getTarFile().getName() + ".marker");
    }

    protected void moveFiles() throws IOException
    {
        if (shouldStripRootDirectory())
        {
            for (File subDir : getTemporaryExtractDir().listFiles())
            {
                if (subDir.isDirectory())
                {
                    moveContents(subDir);
                }

                Cleaner cleaner = new Cleaner(getLog(), isVerbose());
                try
                {
                    // don't follow symlinks, don't fail on error, do retry.
                    // this is not critical to clean up.
                    cleaner.delete(subDir, null, false, false, true);
                }
                catch(IOException e)
                {
                    getLog().warn("Could not clean up directory " + subDir + ": " + e.getMessage());
                }
            }
        }
        else
        {
            moveContents(getTemporaryExtractDir());
        }
    }

    protected void moveContents(File sourceDirectory) throws IOException
    {
        /* TODO: moving directories doesn't really work very well.

           First, FileUtils.moveXXX will complain if the destination already exists.
           Second, even if it didn't, the java implementations seem to revert to copy & delete, so
           successive extractions will be slow(er).

           Possible fixes:
           1. Upgrade to Java 1.7 and see if we can't do better with nio.Files.
           2. Use a library to examine the tarball when stripRootDirectory is turned on.
              Then we can selectively extract the contents of the tarball directly to the output directory.
              This is probably our best option if it's practical.
           3. Do a recursive move, but checking to see if the target exists before the move, deleting it first if so.
        */

        FileUtils.copyDirectory(sourceDirectory, getOutputDirectory());

        Cleaner cleaner = new Cleaner(getLog(), isVerbose());
        cleaner.delete(sourceDirectory, null, false, false, true);

        /*for (File containedFile : sourceDirectory.listFiles())
        {
            if (dryRunMove)
            {
                getLog().info("I would have moved " + containedFile + " to " + getOutputDirectory());
            }
            else
            {
                FileUtils.moveToDirectory(containedFile, getOutputDirectory(), true);
            }
        }*/
    }

    @Override
    protected String getCommandlineArgs()
    {
        StringBuilder builder = new StringBuilder();
        builder.append(super.getCommandlineArgs());
        builder.append(" ");
        builder.append(getTarFile().getAbsolutePath());

        return builder.toString();
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        getTemporaryExtractDir().mkdirs();
        getMarkersDirectory().mkdirs();

        super.execute();

        try
        {
            moveFiles();
            FileUtils.touch(getMarkerFile());
        }
        catch(IOException e)
        {
            throw new MojoExecutionException(e.getMessage());
        }
    }

    protected boolean shouldStripRootDirectory()
    {
        return stripRootDirectory;
    }
}