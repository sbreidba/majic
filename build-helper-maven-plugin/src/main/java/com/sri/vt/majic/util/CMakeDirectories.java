package com.sri.vt.majic.util;

import org.apache.commons.io.FileSystemUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.maven.model.Build;

import java.io.File;
import java.io.IOException;

public class CMakeDirectories
{
    public static final String CMAKE_PROJECT_BIN_DIRECTORY_DEFAULT = "${project.build.directory}";
    public static final String CMAKE_PROJECT_EXPORT_DIR_DEFAULT = CMAKE_PROJECT_BIN_DIRECTORY_DEFAULT + "/exports";
    public static final String CMAKE_PROJECT_PACKAGE_DIR_DEFAULT = CMAKE_PROJECT_BIN_DIRECTORY_DEFAULT + "/pkg";
    public static final String CMAKE_PROJECT_INSTALL_DIR_DEFAULT = CMAKE_PROJECT_BIN_DIRECTORY_DEFAULT + "/exports/${project.artifactId}-${project.version}";

    public CMakeDirectories()
    {
    }

    public void updateProperties(PropertyCache updatedProperties) throws IOException
    {
        File topLevel = updateTopLevel(updatedProperties);
        if (topLevel != null)
        {
            File buildRoot = updateBuildRoot(topLevel, updatedProperties);
            if (buildRoot != null)
            {
                updateProjectBindir(buildRoot, updatedProperties);
            }
        }
    }

    public File updateTopLevel(PropertyCache propertyCache) throws IOException
    {
        if (propertyCache.getProject().getProperties().contains(BuildEnvironment.Properties.CMAKE_TOPLEVEL_PROJECT_DIRECTORY))
        {
            propertyCache.getLog().debug("Found existing toplevel directory property");
            return new File(propertyCache.getProject().getProperties().getProperty(BuildEnvironment.Properties.CMAKE_TOPLEVEL_PROJECT_DIRECTORY));
        }

        File currentPath = propertyCache.getProject().getBasedir();
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
        }

        if (lastPomFound != null)
        {
            propertyCache.setProperty(BuildEnvironment.Properties.CMAKE_TOPLEVEL_PROJECT_DIRECTORY, lastPomFound.getAbsolutePath());
            return lastPomFound.getAbsoluteFile();
        }

        propertyCache.getLog().error("Could not determine " + BuildEnvironment.Properties.CMAKE_TOPLEVEL_PROJECT_DIRECTORY);
        return null;
    }

    public File updateBuildRoot(File topLevel, PropertyCache propertyCache) throws IOException
    {
        File updatedBuildRoot = null;
        String userBuildRootString = propertyCache.getProject().getProperties().getProperty(BuildEnvironment.Properties.CMAKE_BUILD_ROOT, "");
        if (userBuildRootString.length() != 0)
        {
            File userBuildRoot = new File(propertyCache.getProject().getProperties().getProperty(BuildEnvironment.Properties.CMAKE_BUILD_ROOT));
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

            BuildEnvironment buildEnvironment = new BuildEnvironment(propertyCache.getProject());
            updatedBuildRoot = new File(path, buildEnvironment.getPackageClassifier());
        }

        propertyCache.setProperty(
                BuildEnvironment.Properties.CMAKE_BUILD_ROOT,
                FilenameUtils.separatorsToUnix(updatedBuildRoot.getAbsolutePath()));

        String relPath = PathUtils.getRelativePath(
                updatedBuildRoot.getPath(),
                propertyCache.getProject().getBasedir().getAbsolutePath(),
                File.separator);
        propertyCache.setProperty(
                BuildEnvironment.Properties.CMAKE_BUILD_ROOT_RELATIVE,
                FilenameUtils.separatorsToUnix(relPath));

        return updatedBuildRoot.getAbsoluteFile();
    }

    public File updateProjectBindir(File buildRoot, PropertyCache propertyCache) throws IOException
    {
        File projectBin = new File(buildRoot, propertyCache.getProject().getArtifactId());
        propertyCache.setProperty(
                BuildEnvironment.Properties.CMAKE_PROJECT_BIN_DIRECTORY,
                FilenameUtils.separatorsToUnix(projectBin.getAbsolutePath()));

        String relPath = PathUtils.getRelativePath(
                projectBin.getPath(),
                propertyCache.getProject().getBasedir().getAbsolutePath(),
                File.separator);
        propertyCache.setProperty(
                BuildEnvironment.Properties.CMAKE_PROJECT_BIN_DIRECTORY_RELATIVE,
                FilenameUtils.separatorsToUnix(relPath));

        return projectBin.getAbsoluteFile();
    }
}
