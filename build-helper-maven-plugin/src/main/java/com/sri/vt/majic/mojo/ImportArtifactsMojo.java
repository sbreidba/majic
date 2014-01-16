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

    public void execute() throws MojoExecutionException, MojoFailureException
    {
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
        for (File subFile : directory.listFiles())
        {
            File artifactFile = new File(subFile, inputFileName);
            if (artifactFile.exists())
            {
                getLog().info("importing from " + artifactFile);
                importArtifacts(subFile);
            }
            
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