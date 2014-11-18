package com.sri.vt.majic.util;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;

public class CMakeDirectories
{
    public static final String CMAKE_PROJECT_BIN_DIRECTORY_DEFAULT = "${project.build.directory}";
    public static final String CMAKE_PROJECT_EXPORT_DIR_DEFAULT = CMAKE_PROJECT_BIN_DIRECTORY_DEFAULT + "/exports";
    public static final String CMAKE_PROJECT_PACKAGE_DIR_DEFAULT = CMAKE_PROJECT_BIN_DIRECTORY_DEFAULT + "/pkg";
    public static final String CMAKE_PROJECT_INSTALL_NAME_DEFAULT = "${project.artifactId}-${project.version}";
    public static final String CMAKE_PROJECT_INSTALL_DIR_DEFAULT = CMAKE_PROJECT_BIN_DIRECTORY_DEFAULT + "/exports/" + CMAKE_PROJECT_INSTALL_NAME_DEFAULT;

    public CMakeDirectories()
    {
    }

    public void updateProperties(PropertyCache updatedProperties) throws IOException
    {
        File topLevel = updateTopLevel(updatedProperties);
        if (topLevel != null)
        {
            updateProjectBinDirectory(topLevel, updatedProperties);
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

    public File updateProjectBinDirectory(File topLevel, PropertyCache propertyCache) throws IOException
    {
        File binDir = null;
        String userBinDirString = propertyCache.getProject().getProperties().getProperty(BuildEnvironment.Properties.CMAKE_PROJECT_BIN_DIRECTORY, "");
        if (userBinDirString.length() != 0)
        {
            binDir = new File(propertyCache.getProject().getProperties().getProperty(BuildEnvironment.Properties.CMAKE_PROJECT_BIN_DIRECTORY));
            if (!binDir.isAbsolute())
            {
                // non-empty user build root, but it's relative. We'll assume the intent was to make it relative
                // to the discovered toplevel directory
                binDir = new File(topLevel, userBinDirString);
            }
        }
        else
        {
            // empty build root, set defaults
            String path = topLevel.getAbsolutePath() + "-build";

            BuildEnvironment buildEnvironment = new BuildEnvironment(propertyCache.getProject());
            File intermediate = new File(path, buildEnvironment.getPackageClassifier());
            binDir = new File(intermediate, propertyCache.getProject().getArtifactId());
        }

        String relBinPath = PathUtils.getRelativePath(
                binDir.getPath(),
                propertyCache.getProject().getBasedir().getAbsolutePath(),
                File.separator);
        propertyCache.setProperty(
                BuildEnvironment.Properties.CMAKE_PROJECT_BIN_DIRECTORY,
                FilenameUtils.separatorsToUnix(relBinPath));

        File absoluteFile = new File(relBinPath);
        if (!absoluteFile.isAbsolute()) {
            absoluteFile = new File(propertyCache.getProject().getBasedir(), relBinPath);
        }
        
        propertyCache.setProperty(
                BuildEnvironment.Properties.CMAKE_PROJECT_BIN_DIRECTORY_ABSOLUTE,
                FilenameUtils.separatorsToUnix(absoluteFile.getCanonicalPath()));

        return binDir.getAbsoluteFile();
    }
}
