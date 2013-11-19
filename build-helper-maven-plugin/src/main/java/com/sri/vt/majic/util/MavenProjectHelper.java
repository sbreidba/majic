package com.sri.vt.majic.util;

import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.logging.Logger;

public class MavenProjectHelper
{
    public static void setPropertyIfNotSet(MavenProject project, Logger log, String key, String value)
    {
        String existing = project.getProperties().getProperty(key);
        if ((existing != null) && (existing.length() > 0))
        {
            log.debug("For project " + project.getArtifact() + ": setting property " + key + " already set - skipping.");
            return;
        }

        log.debug("For project " + project.getArtifact() + ": setting property " + key + " to " + value);
        project.getProperties().setProperty(key, value);
    }
}
