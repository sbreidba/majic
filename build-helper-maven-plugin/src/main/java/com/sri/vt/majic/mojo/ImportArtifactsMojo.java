package com.sri.vt.majic.mojo;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.*;

/**
 * Imports a list of currently attached artifacts from XML files. This mojo is generally
 * only used by the build system for multi-os build coordination.
 */
@Mojo(name="import-artifacts", defaultPhase = LifecyclePhase.PREPARE_PACKAGE)
public class ImportArtifactsMojo extends AbstractMojo
{
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    MavenProject project;

    @Parameter(defaultValue = "artifacts.xml", property = "importArtifacts.inputFileName")
    String inputFileName;

    @Parameter(defaultValue = "${project.build.directory}", property = "importArtifacts.inputDirectory")
    File inputDirectory;

    @Parameter(defaultValue = "true", property = "importArtifacts.recurse")
    boolean recurse;

    @Component()
    private MavenProjectHelper projectHelper;

    /**
     * If not set, this is derived from Maven's global debug flag (i.e. -X).
     */
    @Parameter(defaultValue = "false", property = "majic.artifact.import.verbose")
    private Boolean verbose;

    protected boolean isVerbose()
    {
        return (verbose || getLog().isDebugEnabled());
    }

    public void execute() throws MojoExecutionException, MojoFailureException
    {
        if (isVerbose())
        {
            getLog().info("Importing from " + inputDirectory);
        }

        if (!recurse)
        {
            importArtifacts(inputDirectory);
        }
        else
        {
            recursiveImport(inputDirectory);
        }
    }

    public void recursiveImport(File directory) throws MojoExecutionException
    {
        if (!directory.isDirectory())
        {
            getLog().error(directory + " is not a directory.");
            return;
        }

        File artifactFile = new File(directory, inputFileName);
        if (artifactFile.exists())
        {
            importArtifacts(directory);
        }

        for (File subFile : directory.listFiles())
        {
            if (subFile.isDirectory())
            {
                recursiveImport(subFile);
            }
        }
    }

    public void importArtifacts(File directory) throws MojoExecutionException
    {
        Reader reader;
        try
        {
            File inputFile = new File(directory, inputFileName);
            if (isVerbose())
            {
                getLog().info("Opening " + inputFile);
            }
            FileInputStream inputStream = new FileInputStream(inputFile);
            reader = new InputStreamReader(inputStream, "UTF-8");
        }
        catch (FileNotFoundException e)
        {
            throw new MojoExecutionException("Could not open file", e);
        }
        catch (UnsupportedEncodingException e)
        {
            throw new MojoExecutionException("Could not open file: Unsupported Encoding: ", e);
        }

        Xpp3Dom dom = null;
        try
        {
            dom = Xpp3DomBuilder.build(reader);
        }
        catch (XmlPullParserException e)
        {
            throw new MojoExecutionException("Error reading file: ", e);
        }
        catch (IOException e)
        {
            throw new MojoExecutionException("Error reading file: ", e);
        }

        if (isVerbose() && (dom.getChildCount() == 0))
        {
            getLog().warn("No artifacts found.");
        }

        for (Xpp3Dom artifact : dom.getChildren())
        {
            String type = artifact.getChild("type").getValue();
            String classifier = artifact.getChild("classifier").getValue();
            String file = artifact.getChild("file").getValue();

            File artifactFile = new File(directory, file);
            if (!artifactFile.exists())
            {
                getLog().error("Could not find " + artifactFile.getAbsolutePath() + " - skipping attachment");
            }

            getLog().info("Attaching " + artifactFile);
            projectHelper.attachArtifact(project, type, classifier, artifactFile);
        }

        try
        {
            reader.close();
        }
        catch (IOException e)
        {
            throw new MojoExecutionException("Could not close input file", e);
        }
    }
}