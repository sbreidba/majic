package com.sri.vt.majic.mojo;

import com.sri.vt.majic.util.ArtifactHelper;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.util.List;
import java.util.Set;

/**
 * Imports a list of currently attached artifacts from XML files. This mojo is generally
 * only used by the build system for multi-os build coordination.
 */
@Mojo(name="dependency-properties", defaultPhase = LifecyclePhase.INITIALIZE, requiresProject = true, requiresDependencyResolution = ResolutionScope.TEST)
public class DependencyPropertiesMojo extends AbstractMojo
{
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    @Parameter(defaultValue = "${reactorProjects}", readonly = true)
    private List<MavenProject> reactorProjects;

    /**
     * The prefix to apply to the property created.
     */
    @Parameter(defaultValue = "majic.dependency")
    private String prefix;

    /**
     * If not set, this is derived from Maven's global debug flag (i.e. -X).
     */
    @Parameter(defaultValue = "false", property = "majic.dependency.properties.verbose")
    private Boolean verbose;

    protected boolean isVerbose()
    {
        return ( verbose || getLog().isDebugEnabled());
    }

    public void execute() throws MojoExecutionException, MojoFailureException
    {
        Set artifacts = project.getArtifacts();
        if ((artifacts != null) && (!artifacts.isEmpty()))
        {
            for (Object object : artifacts)
            {
                Artifact artifact = (Artifact)object;

                StringBuilder propertyBuilder = new StringBuilder();
                propertyBuilder.append(prefix);
                if (propertyBuilder.length() > 0) propertyBuilder.append(".");
                propertyBuilder.append(artifact.getGroupId());
                propertyBuilder.append(".");
                propertyBuilder.append(artifact.getArtifactId());
                propertyBuilder.append(".path");

                File directory = ArtifactHelper.getRepoExtractDirectory(reactorProjects, artifact);
                String property = propertyBuilder.toString();
                String value = directory.getAbsolutePath();
                if (isVerbose()) getLog().info("Setting " + property + " to " + value);
                project.getProperties().setProperty(property, value);
            }
        }
    }
}