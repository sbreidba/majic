package com.sri.vt.majic.mojo;

import lzma.streams.LzmaOutputStream;
import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.*;

/**
 *
 */
@Mojo(name="pack7zip", requiresProject=true)
public class Pack7ZipMojo extends AbstractMojo
{
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject m_project;

    @Parameter(readonly = true, required = true)
    private File sourceDirectory;

    @Parameter(readonly = true, required = true)
    private File outputFile;

    public void execute() throws MojoExecutionException, MojoFailureException
    {
        getLog().info("sourceDirectory = " + sourceDirectory);
        getLog().info("outputFile = " + outputFile);
        try
        {
            outputFile.createNewFile();
            getLog().info("created " + outputFile);
            OutputStream compressedOut = new LzmaOutputStream.Builder(
                    new BufferedOutputStream(
                            new FileOutputStream(outputFile))).build();
            getLog().info("opened " + outputFile + " as lzmaoutput");

            //note! compresses with one file - how about dirs?
            
            /*
            final LzmaOutputStream compressedOut = new LzmaOutputStream.Builder(
                    new BufferedOutputStream(new FileOutputStream(outputFile)))
                    .useMaximalDictionarySize()
                    .useEndMarkerMode(true)
                    .useBT4MatchFinder()
                    .build();*/

            addFiles(compressedOut, sourceDirectory.listFiles());
            compressedOut.close();
        }
        catch (IOException e)
        {
            throw new MojoExecutionException(e.getMessage());
        }
    }

    private void addFiles(OutputStream outputStream, File[] files) throws IOException
    {
        for (File file : files)
        {
            getLog().info("Adding file " + file);
            if (file.isDirectory())
            {
                addFiles(outputStream, file.listFiles());
            }
            else
            {
                getLog().info("Opening " + file);
                InputStream in = new BufferedInputStream(new FileInputStream(file));
                getLog().info("opened " + file);
                IOUtils.copy(in, outputStream);
                in.close();
            }

        }
    }


}
