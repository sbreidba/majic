package com.sri.vt.majic;

import com.sri.vt.majic.util.BuildEnvironment;
import com.sri.vt.majic.util.CMakeDirectories;
import com.sri.vt.majic.util.OperatingSystemInfo;
import org.apache.maven.AbstractMavenLifecycleParticipant;
import org.apache.maven.MavenExecutionException;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.logging.Logger;
import org.sonatype.aether.graph.Dependency;

import java.io.IOException;

@Component(role = AbstractMavenLifecycleParticipant.class, hint = "majic")
public class MajicExtension extends AbstractMavenLifecycleParticipant
{
    @Requirement
    private Logger logger;

    protected Logger getLogger()
    {
        return logger;
    }

    @Override
    public void afterProjectsRead(MavenSession session) throws MavenExecutionException
    {
        getLogger().info("Building with Majic.");

        // TODO: loop through dependencies and replaces PACKAGE_CLASSIFIER macros with actual value.
        
        try
        {
            OperatingSystemInfo operatingSystemInfo = new OperatingSystemInfo();

            for (MavenProject project : session.getProjects())
            {
                UpdateProperties(operatingSystemInfo, project);
                UpdateDependencies(project);
            }
        }
        catch (IOException e)
        {
            throw new MavenExecutionException(e.getMessage(), session.getTopLevelProject().getFile());
        }

        super.afterProjectsRead(session);
    }

    private void UpdateDependencies(MavenProject project) throws IOException
    {
        String classifier = BuildEnvironment.getClassifier(project);
        String replacementText = "\\$\\{" + BuildEnvironment.Properties.PACKAGE_CLASSIFIER + "\\}";
        for(org.apache.maven.model.Dependency dependency : project.getModel().getDependencies())
        {
            String originalClassifier = dependency.getClassifier();
            if (originalClassifier != null)
            {
                String updatedClassifier = originalClassifier.replaceAll(replacementText, classifier);
                dependency.setClassifier(updatedClassifier);
            }
        }
    }

    private void UpdateProperties(OperatingSystemInfo operatingSystemInfo, MavenProject project) throws IOException
    {
        operatingSystemInfo.setProperties(project, getLogger());

        CMakeDirectories directories = new CMakeDirectories(project, getLogger());
        directories.setProperties();

        BuildEnvironment.setProperties(project, getLogger());
    }
}
