package com.sri.vt.majic;

import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

public class Utils {
    static public Plugin getExecPlugin(Object source, MavenProject project) throws MojoExecutionException
    {
        String execComponentKey = Plugin.constructKey("org.codehaus.mojo", "exec-maven-plugin");
        Plugin execPlugin = project.getPluginManagement().getPluginsAsMap().get(execComponentKey);
        if (execPlugin == null)
        {
            throw new MojoExecutionException(source, "Exec-maven-plugin version missing", "Could not determine exec plugin version. Please declare exec-maven-plugin in PluginManagement");
        }

        return execPlugin;
    }
}
