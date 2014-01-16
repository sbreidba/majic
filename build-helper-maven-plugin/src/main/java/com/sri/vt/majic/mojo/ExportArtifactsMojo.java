package com.sri.vt.majic.mojo;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.wagon.PathUtils;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomWriter;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;

@Mojo(name="export-artifacts", defaultPhase = LifecyclePhase.VERIFY)
public class ExportArtifactsMojo extends AbstractMojo
{
    @Parameter(defaultValue = "false", property = "exportArtifacts.skip")
    boolean skip;
    
    @Parameter(defaultValue = "false", property = "exportArtifacts.includeSelf")
    boolean includeSelf;

    @Parameter(defaultValue = "artifacts.xml", property = "exportArtifacts.outputFileName")
    String outputFileName;

    @Parameter(defaultValue = "${project.build.directory}", property = "exportArtifacts.outputDirectory")
    File outputDirectory;
    
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    MavenProject project;

    public void execute() throws MojoExecutionException, MojoFailureException
    {
        if (skip)
        {
            getLog().info("Skipping execution");
            return;
        }

        Collection<Artifact> artifacts = new ArrayList<Artifact>();

        if (includeSelf)
        {
            artifacts.add(project.getArtifact());
        }

        artifacts.addAll(project.getAttachedArtifacts());

        Writer writer;
        try
        {
            File outputFile = new File(outputDirectory, outputFileName);
            writer = new PrintWriter(outputFile, "UTF-8");
        }
        catch (FileNotFoundException e)
        {
            throw new MojoExecutionException("Could not create output file", e);
        }
        catch (UnsupportedEncodingException e)
        {
            throw new MojoExecutionException("Could not create output file: Unsupported Encoding: ", e);
        }

        Xpp3Dom dom = new Xpp3Dom("artifacts");
        for (Artifact artifact : artifacts)
        {
            getLog().debug("Found artifact " + artifact);
            Xpp3Dom xmlArtifact = new Xpp3Dom("artifact");

            {
                Xpp3Dom xmlType = new Xpp3Dom("type");
                xmlType.setValue(artifact.getType());
                xmlArtifact.addChild(xmlType);
            }
            
            {
                Xpp3Dom xmlClassifier = new Xpp3Dom("classifier");
                xmlClassifier.setValue(artifact.getClassifier());
                xmlArtifact.addChild(xmlClassifier);
            }

            {
                Xpp3Dom xmlFile = new Xpp3Dom("file");
                if (artifact.getFile() != null)
                {
                    xmlFile.setValue(PathUtils.toRelative(outputDirectory, artifact.getFile().getAbsolutePath()));
                    xmlArtifact.addChild(xmlFile);
                }
                else
                {
                    getLog().error("Null artifact file: " + artifact);
                }
            }

            dom.addChild(xmlArtifact);
        }

        Xpp3DomWriter.write(writer, dom);
        
        try
        {
            writer.close();
        }
        catch (IOException e)
        {
            throw new MojoExecutionException("Could not close output file", e);
        }
    }
}