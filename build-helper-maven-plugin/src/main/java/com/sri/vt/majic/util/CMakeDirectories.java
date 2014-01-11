package com.sri.vt.majic.util;

import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.logging.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

public class CMakeDirectories
{
    public static final String CMAKE_BUILD_ROOT_DEFAULT = "${" + BuildEnvironment.Properties.CMAKE_BUILD_ROOT + "}";

    public static final String CMAKE_PACKAGE_ROOT_DEFAULT = "${" + BuildEnvironment.Properties.CMAKE_BUILD_ROOT + "}/pkg";
    public static final String CMAKE_EXPORT_ROOT_DEFAULT = "${" + BuildEnvironment.Properties.CMAKE_BUILD_ROOT + "}/exports";

    public static final String CMAKE_PROJECT_BIN_DIRECTORY_DEFAULT = "${" + BuildEnvironment.Properties.CMAKE_BUILD_ROOT + "}/${project.artifactId}";
    public static final String CMAKE_PROJECT_PACKAGEDIR_DEFAULT = CMAKE_PROJECT_BIN_DIRECTORY_DEFAULT + "/exports";
    public static final String CMAKE_PROJECT_INSTALLDIR_DEFAULT = CMAKE_PROJECT_BIN_DIRECTORY_DEFAULT + "/exports/${project.artifactId}-${project.version}";

    public CMakeDirectories(MavenProject project, Logger log)
    {
        _project = project;
        _log = log;
    }

    public void setProperties() throws IOException
    {
        File topLevel = updateTopLevel();
        if (topLevel != null)
        {
            File buildRoot = updateBuildRoot(topLevel);
            if (buildRoot != null)
            {
                updateProjectBindir(buildRoot);
            }
        }
    }

    public File updateTopLevel() throws IOException
    {
        if (getProperties().contains(BuildEnvironment.Properties.CMAKE_TOPLEVEL_PROJECT_DIRECTORY))
        {
            getLog().debug("Found existing toplevel directory property");
            return new File(getProperties().getProperty(BuildEnvironment.Properties.CMAKE_TOPLEVEL_PROJECT_DIRECTORY));
        }

        File currentPath = getProject().getBasedir();
        int levels = 0;
        File lastPomFound = null;

        while(true)
        {
            File parentPom = new File(currentPath, "pom.xml");
            if (!parentPom.exists())
            {
                break;
            }

            lastPomFound = currentPath;
            currentPath = currentPath.getParentFile();
            ++levels;
        }

        if (lastPomFound != null)
        {
            levels--;
            getLog().debug("found a top-level parent pom " + levels + " levels up");
            getLog().debug("Setting " + BuildEnvironment.Properties.CMAKE_TOPLEVEL_PROJECT_DIRECTORY + " to " + lastPomFound.getAbsolutePath());
            getProperties().setProperty(BuildEnvironment.Properties.CMAKE_TOPLEVEL_PROJECT_DIRECTORY, lastPomFound.getAbsolutePath());
            return lastPomFound.getAbsoluteFile();
        }

        getLog().error("Could not determine " + BuildEnvironment.Properties.CMAKE_TOPLEVEL_PROJECT_DIRECTORY);
        return null;
    }

    public File updateBuildRoot(File topLevel) throws IOException
    {
        File updatedBuildRoot = null;
        String userBuildRootString = getProperties().getProperty(BuildEnvironment.Properties.CMAKE_BUILD_ROOT, "");
        if (userBuildRootString.length() != 0)
        {
            File userBuildRoot = new File(getProperties().getProperty(BuildEnvironment.Properties.CMAKE_BUILD_ROOT));
            if (userBuildRoot.isAbsolute())
            {
                // nothing to set - already there
                return userBuildRoot;
            }

            // non-empty user build root, but it's relative. Make it relative to topLevel and update
            updatedBuildRoot = new File(topLevel, userBuildRootString);
        }
        else
        {
            // empty build root, set defaults
            String path = topLevel.getAbsolutePath() + "-build";
            updatedBuildRoot = new File(path, BuildEnvironment.getClassifier(getProject()));
        }

        getLog().debug("Setting " + BuildEnvironment.Properties.CMAKE_BUILD_ROOT + " to " + updatedBuildRoot.getAbsolutePath());
        getProperties().setProperty(
                BuildEnvironment.Properties.CMAKE_BUILD_ROOT,
                updatedBuildRoot.getAbsolutePath());
        return updatedBuildRoot.getAbsoluteFile();
    }

    public File updateProjectBindir(File buildRoot) throws IOException
    {
        File projectBin = new File(buildRoot, getProject().getArtifactId());
        getLog().debug("Setting " + BuildEnvironment.Properties.CMAKE_PROJECT_BIN_DIRECTORY + " to " + projectBin.getAbsolutePath());
        getProperties().setProperty(
                BuildEnvironment.Properties.CMAKE_PROJECT_BIN_DIRECTORY,
                projectBin.getAbsolutePath());
        return projectBin.getAbsoluteFile();
    }

    private Logger _log;
    private MavenProject _project;

    protected MavenProject getProject()
    {
        return _project;
    }

    protected Properties getProperties()
    {
        return getProject().getProperties();
    }

    public Logger getLog()
    {
        return _log;
    }
}
