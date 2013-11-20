package com.sri.vt.majic.util;

import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.logging.Logger;

import java.io.File;
import java.io.IOException;

public class CMakeDirectories
{
    // cmake build root is an oddity to maven - it's a path that's relative to some fixed point
    // in the tree. This is computed automatically by following the directory chain up and looking
    // for the highest-level parent. It can also be set directly via a pom file (though care
    // must be taken when doing so!)
    //
    // Rules for setting this are as follows:
    //     1. If it is already set and absolute, do nothing. Projects that wish a nested structure
    //        can define:
    //          <properties>
    //             <cmake.build.root>${basedir}</cmake.build.root>
    //          </properties>
    //        Projects are also welcome to use an environment variable here:
    //          <properties>
    //             <cmake.build.root>${env.MY_PROJECT_ROOT}</cmake.build.root>
    //          </properties>
    //     2. If it is already set, *and relative*, append it to the auto-computed path. Note that many (most?)
    //        Maven file properties automatically expand to their absolute paths.
    //     3. Auto-computed path: Starting from ${basedir}, traverse the directory structure upwards,
    //        looking for the last parent directory that contains a pom.xml and
    //        append "-build" to this directory.

    public static final String CMAKE_RELATIVE_BUILD_ROOT_PROPERTY = "cmake.relative.build.root";

    public static final String CMAKE_BUILD_ROOT_PROPERTY = "cmake.build.root";
    public static final String CMAKE_BUILD_ROOT_DEFAULT = "${" + CMAKE_BUILD_ROOT_PROPERTY + "}";
    public static final String CMAKE_PACKAGE_ROOT_DEFAULT = CMAKE_BUILD_ROOT_DEFAULT + "/pkg";
    public static final String CMAKE_EXPORT_ROOT_DEFAULT = CMAKE_BUILD_ROOT_DEFAULT + "/exports";

    public static final String CMAKE_PROJECT_BIN_DIRECTORY_PROPERTY = "cmake.project.bin.directory";
    public static final String CMAKE_PROJECT_BIN_DIRECTORY_DEFAULT = "${" + CMAKE_PROJECT_BIN_DIRECTORY_PROPERTY + "}";
    public static final String CMAKE_PROJECT_PACKAGEDIR_DEFAULT = CMAKE_PROJECT_BIN_DIRECTORY_DEFAULT + "/exports";
    public static final String CMAKE_PROJECT_INSTALLDIR_DEFAULT = CMAKE_PROJECT_BIN_DIRECTORY_DEFAULT + "/exports/${project.artifactId}-${project.version}";

    public CMakeDirectories(MavenProject project, Logger log)
    {
        _project = project;
        _log = log;
    }

    public void setProperties(MavenProject project, Logger log) throws IOException
    {
        MavenProjectHelper.setPropertyIfNotSet(project, log, CMAKE_BUILD_ROOT_PROPERTY, getBuildRoot().getAbsolutePath());
        MavenProjectHelper.setPropertyIfNotSet(project, log, CMAKE_PROJECT_BIN_DIRECTORY_PROPERTY, getProjectBindir().getAbsolutePath());
    }

    public File getBuildRoot() throws IOException
    {
        String relativeBuildRoot = getProject().getProperties().getProperty(CMAKE_RELATIVE_BUILD_ROOT_PROPERTY, "");

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
        levels--;

        if (lastPomFound != null)
        {
            getLog().debug("found a top-level parent pom " + levels + " levels up");

            String path = lastPomFound.getAbsolutePath();
            if (relativeBuildRoot.length() == 0) // pick a parallel build directory
            {
                path += "-build";
            }

            return new File(path, relativeBuildRoot).getCanonicalFile();
        }

        getLog().error("Could not configure ${" + CMAKE_BUILD_ROOT_PROPERTY + "}");
        return null;
    }

    public File getProjectBindir() throws IOException
    {
        File root = getBuildRoot();

        OperatingSystemInfo operatingSystemInfo = new OperatingSystemInfo();
        root = new File(root, getProject().getArtifactId() + "/" + operatingSystemInfo.getDistro());

        return root.getCanonicalFile();
    }

    private MavenProject _project;
    private Logger _log;

    protected MavenProject getProject()
    {
        return _project;
    }

    public Logger getLog()
    {
        return _log;
    }
}
