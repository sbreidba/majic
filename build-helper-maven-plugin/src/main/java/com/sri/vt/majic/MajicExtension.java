package com.sri.vt.majic;

import com.sri.vt.majic.util.BuildEnvironment;
import com.sri.vt.majic.util.CMakeDirectories;
import com.sri.vt.majic.util.PropertyCache;
import org.apache.maven.AbstractMavenLifecycleParticipant;
import org.apache.maven.MavenExecutionException;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.interpolation.InterpolationException;
import org.codehaus.plexus.logging.Logger;

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

        try
        {
            for (MavenProject project : session.getProjects())
            {
                BuildEnvironment.checkSanity(project);
                UpdateProperties(project);
            }
        }
        catch (IOException e)
        {
            throw new MavenExecutionException(e.getMessage(), session.getTopLevelProject().getFile());
        }
        catch (InterpolationException e)
        {
            throw new MavenExecutionException(e.getMessage(), session.getTopLevelProject().getFile());
        }

        super.afterProjectsRead(session);
    }

    private void UpdateProperties(MavenProject project) throws IOException, InterpolationException
    {
        PropertyCache propertyCache = new PropertyCache(project, getLogger());

        CMakeDirectories directories = new CMakeDirectories();
        directories.updateProperties(propertyCache);

        propertyCache.interpolate();
    }
}
