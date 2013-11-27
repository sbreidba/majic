package com.sri.vt.majic.mojo.cmake;

import com.sri.vt.majic.mojo.AbstractExecutorMojo;
import com.sri.vt.majic.mojo.ExecMojo;
import com.sri.vt.majic.util.CMakeDirectories;
import org.apache.commons.lang3.SystemUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.wagon.PathUtils;
import org.twdata.maven.mojoexecutor.MojoExecutor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.twdata.maven.mojoexecutor.MojoExecutor.*;
import static org.twdata.maven.mojoexecutor.MojoExecutor.executionEnvironment;

@Mojo(name="cmake-clean", defaultPhase= LifecyclePhase.CLEAN, requiresProject=true)
public class CleanMojo extends AbstractExecutorMojo
{
    // Always cleaned
    @Parameter(defaultValue = CMakeDirectories.CMAKE_BUILD_ROOT_DEFAULT)
    private File cleanDirectory;

    // Excluded unless reallyClean is specified
    @Parameter(defaultValue = CMakeDirectories.CMAKE_PACKAGE_ROOT_DEFAULT)
    private File reallyCleanDirectory;

    // Always cleaned
    @Parameter(defaultValue = CMakeDirectories.CMAKE_PROJECT_BIN_DIRECTORY_DEFAULT)
    private File binDir;

    @Parameter(defaultValue = "${realClean}")
    private boolean reallyClean;

    @Override
    protected boolean shouldFailIfPluginNotFound()
    {
        return false;
    }

    @Override
    protected String getPluginGroupId()
    {
        return "org.apache.maven.plugins";
    }

    @Override
    protected String getPluginArtifactId()
    {
        return "maven-clean-plugin";
    }

    @Override
    protected String getGoal()
    {
        return "clean";
    }

    @Override
    protected Element[] getConfigurationElements()
    {
        List<Element> buildRootFileSet = new ArrayList<Element>();
        append(buildRootFileSet, "directory", cleanDirectory);
        if (!reallyClean)
        {
            List<Element> excludes = new ArrayList<Element>();
            String relativePath = PathUtils.toRelative(cleanDirectory, reallyCleanDirectory.getAbsolutePath());
            append(excludes, "exclude", relativePath + "/**");
            append(buildRootFileSet, "excludes", excludes);
        }

        List<Element> projectBinFileSet = new ArrayList<Element>();
        append(projectBinFileSet, "directory", binDir);

        List<Element> fileSets = new ArrayList<Element>();
        append(fileSets, "fileset", buildRootFileSet);
        append(fileSets, "fileset", projectBinFileSet);

        List<Element> elements = new ArrayList<Element>();
        append(elements, "filesets", fileSets);

        Element[] elementArray = new Element[elements.size()];
        elements.toArray(elementArray);
        return elementArray;
    }
}
