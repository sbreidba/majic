package com.sri.vt.majic.component.cmake;

import com.sri.vt.majic.util.OperatingSystemInfo;
import org.apache.maven.AbstractMavenLifecycleParticipant;
import org.apache.maven.MavenExecutionException;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;

import java.io.IOException;
import java.util.logging.Logger;

@Component(role = AbstractMavenLifecycleParticipant.class, hint = "cmake-majic")
public class CMakeDirectoriesExtension extends AbstractMavenLifecycleParticipant
{
    @Requirement
    private Logger logger;

    protected Logger getLogger()
    {
        return logger;
    }

    public void execute() throws MojoExecutionException, MojoFailureException
    {
    }

    
    @Override
    public void afterProjectsRead(MavenSession session) throws MavenExecutionException
    {
        super.afterProjectsRead(session);

        try
        {
            OperatingSystemInfo info = new OperatingSystemInfo();

            for (MavenProject project : session.getProjects())
            {
                project.getProperties().setProperty("os.name", info.getName());
                project.getProperties().setProperty("os.arch", info.getArch());
                project.getProperties().setProperty("os.distro", info.getDistro());

                project.getProperties().setProperty("os.name", info.getName());
                project.getProperties().setProperty("os.arch", info.getArch());
                project.getProperties().setProperty("os.distro", info.getDistro());
            }
        }
        catch (IOException e)
        {
            throw new MavenExecutionException(e.getMessage(), session.getTopLevelProject().getFile());
        }
    }
}
