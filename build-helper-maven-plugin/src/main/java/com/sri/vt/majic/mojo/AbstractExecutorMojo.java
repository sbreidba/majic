package com.sri.vt.majic.mojo;

import com.sri.vt.majic.util.BuildEnvironment;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.twdata.maven.mojoexecutor.MojoExecutor.*;

public abstract class AbstractExecutorMojo extends AbstractMojo
{
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    @Parameter(defaultValue = "${session}", readonly = true, required = true)
    private MavenSession session;

    @Component(hint = "")
    private BuildPluginManager pluginManager;

    /**
     * Not set until execute is called - project is unknown until then.
     */
    private BuildEnvironment buildEnvironment;

    protected Plugin getPlugin(String groupId, String artifactId) throws MojoExecutionException
    {
        String execComponentKey = Plugin.constructKey(groupId, artifactId);
        return getProject().getPluginManagement().getPluginsAsMap().get(execComponentKey);
    }

    protected void append(List<Element> elements, String name, File value)
    {
        if (value != null)
        {
        	append(elements, name, value.getAbsolutePath());
        }
    }

    protected void append(List<Element> elements, String name, String value)
    {
        if (isVerbose()) getLog().info(name + " is " + ((value == null) ? "(not set)" : "[" + value + "]"));
		if (value != null)
		{
			elements.add(element(name, value));
		}
    }

    protected void append(List<Element> elements, String elementName, String childName, List<String> values)
    {
        if ((values == null) || (values.size() == 0))
        {
        	return;
        }

        List<Element> childElements = new ArrayList<Element>();
        for (String value : values)
        {
            append(childElements, childName, value);
        }

        append(elements, elementName, childElements);
    }

    protected void append(List<Element> elements, String elementName, List<Element> children)
    {
        Element[] childArray = new Element[children.size()];
        children.toArray(childArray);
        elements.add(new Element(elementName, childArray));
    }

    protected void append(List<Element> elements, String elementName, Map<String, String> values)
    {
        if ((values == null) || (values.size() == 0))
        {
        	return;
        }

        List<Element> childElements = new ArrayList<Element>();
        for (String key : values.keySet())
        {
            append(childElements, key, values.get(key));
        }

        append(elements, elementName, childElements);
    }

    abstract protected boolean shouldFailIfPluginNotFound();
    abstract protected String getPluginGroupId();
    abstract protected String getPluginArtifactId();
    abstract protected String getGoal();
    abstract protected Element[] getConfigurationElements() throws MojoExecutionException;
    abstract protected boolean isVerbose();

    public void execute() throws MojoExecutionException, MojoFailureException
    {
        Plugin plugin = getPlugin(getPluginGroupId(), getPluginArtifactId());
        if (plugin == null)
        {
            if (shouldFailIfPluginNotFound())
            {
                throw new MojoExecutionException(this,
                        getPluginArtifactId() + " version missing",
                        "Could not determine plugin version - must be declared in <pluginManagement>");
            }
            else
            {
                getLog().warn(getPluginArtifactId() + " version missing - skipping execution.");
                return;
            }
        }
        
        getLog().debug("Using plugin : " + plugin.toString());

        executeMojo(
            plugin(
                groupId(getPluginGroupId()),
                artifactId(getPluginArtifactId()),
                version(plugin.getVersion())
            ),
            goal(getGoal()),
            configuration(getConfigurationElements()),
            executionEnvironment(
                getProject(),
                getSession(),
                getPluginManager()
            )
        );
    }

    protected BuildEnvironment getBuildEnvironment()
    {
        if (buildEnvironment == null)
        {
            buildEnvironment = new BuildEnvironment(getProject());
        }

        return buildEnvironment;
    }
    
    protected MavenProject getProject()
    {
        return project;
    }

    protected MavenSession getSession()
    {
        return session;
    }

    protected BuildPluginManager getPluginManager()
    {
        return pluginManager;
    }
}
