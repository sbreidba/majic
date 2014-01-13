package com.sri.vt.majic.util;

import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.interpolation.InterpolationException;
import org.codehaus.plexus.interpolation.Interpolator;
import org.codehaus.plexus.interpolation.StringSearchInterpolator;
import org.codehaus.plexus.logging.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

// The PropertyCache should be used whenever a maven property needs to be set from a Mojo instead
// of setting it directly. This allows values to be reinterpolated as required.
public class PropertyCache
{
    public PropertyCache(MavenProject project, Logger logger)
    {
        this.project = project;
        this.log = logger;
    }

    private MavenProject project;
    private Logger log;
    private Map<String, String> changedProperties = new HashMap<String, String>();

    public Map<String, String> getChangedProperties()
    {
        return changedProperties;
    }

    public MavenProject getProject()
    {
        return project;
    }

    public Logger getLog()
    {
        return log;
    }
    
    public void setProperty(String key, String value)
    {
        String existing = project.getProperties().getProperty(key);
        if ((existing != null) && existing.equals(value))
        {
            log.debug("For project " + project.getArtifact() + ": " + key + " already set to " + value + " - skipping update.");
            return;
        }

        log.debug("For project " + project.getArtifact() + ": " + key + " has been updated to " + value);
        project.getProperties().setProperty(key, value);
        changedProperties.put(key, value);
    }

    public void interpolate() throws IOException, InterpolationException
    {
        Interpolator interpolator = new StringSearchInterpolator();
        interpolator.addValueSource(new PropertiesValueSource(getChangedProperties()));

        for (Object objPropertyKey : getProject().getProperties().keySet())
        {
            String property = getProject().getProperties().getProperty((String)objPropertyKey);
            getProject().getProperties().setProperty((String)objPropertyKey, interpolator.interpolate(property));
        }

        for (org.apache.maven.model.Dependency dependency : getProject().getModel().getDependencies())
        {
            // Could interpolate other values here if needed
            dependency.setType(interpolator.interpolate(dependency.getType()));
            dependency.setClassifier(interpolator.interpolate(dependency.getClassifier()));
        }
    }
}
