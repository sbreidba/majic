package com.sri.vt.majic.util;

import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.logging.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

public class CMakeDirectories
{

    // TODO! docs in the paragraph below are out of date!
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

    // This variable is determined automatically (no override) and can be used in plugins.
    public static final String CMAKE_TOPLEVEL_PROJECT_DIRECTORY_PROPERTY = "toplevel.project.directory";

//    public static final String CMAKE_TOPLEVEL_RELATIVE_BUILD_ROOT_PROPERTY = "cmake.toplevel-relative.build.root";

    // This variable is set by default by setting it to look like:
    //      ${toplevel.project.directory}-build/${os.classifier}
    // If you are Phil, and don't like that, override this value. If you specify a relative path,
    // it will always be relative to ${toplevel.project.directory}.
    // If you specify an absolute path, then you are the master of your own destiny.
    // ... careful with mvn clean!
    public static final String CMAKE_BUILD_ROOT_PROPERTY = "cmake.build.root";
//    public static final String CMAKE_BUILD_ROOT_DEFAULT = "${" + CMAKE_TOPLEVEL_RELATIVE_BUILD_ROOT_PROPERTY + "}";
    public static final String CMAKE_PACKAGE_ROOT_DEFAULT = "${" + CMAKE_BUILD_ROOT_PROPERTY + "/pkg";
    public static final String CMAKE_EXPORT_ROOT_DEFAULT = "${" + CMAKE_BUILD_ROOT_PROPERTY + "}/exports";

    public static final String CMAKE_PROJECT_BIN_DIRECTORY_PROPERTY = "cmake.project.bin.directory";
    public static final String CMAKE_PROJECT_BIN_DIRECTORY_DEFAULT = "${cmake.build.root}/${project.artifactId}";
    public static final String CMAKE_PROJECT_PACKAGEDIR_DEFAULT = CMAKE_PROJECT_BIN_DIRECTORY_DEFAULT + "/exports";
    public static final String CMAKE_PROJECT_INSTALLDIR_DEFAULT = CMAKE_PROJECT_BIN_DIRECTORY_DEFAULT + "/exports/${project.artifactId}-${project.version}";

    public CMakeDirectories(MavenProject project, Logger log)
    {
        _project = project;
        _log = log;
    }

    public void setProperties() throws IOException
    {
        OperatingSystemInfo operatingSystemInfo = new OperatingSystemInfo();
        String distro = operatingSystemInfo.getDistro();

        File topLevel = updateTopLevel();
        if (topLevel != null)
        {
            File buildRoot = updateBuildRoot(topLevel, distro);
            if (buildRoot != null)
            {
                updateProjectBindir(buildRoot);
            }
        }
    }

    public File updateTopLevel() throws IOException
    {
        if (getProperties().contains(CMAKE_TOPLEVEL_PROJECT_DIRECTORY_PROPERTY))
        {
            getLog().debug("Found existing toplevel directory property");
            return new File(getProperties().getProperty(CMAKE_TOPLEVEL_PROJECT_DIRECTORY_PROPERTY));
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
            getProperties().setProperty(CMAKE_TOPLEVEL_PROJECT_DIRECTORY_PROPERTY, lastPomFound.getAbsolutePath());
            return lastPomFound.getAbsoluteFile();
        }

        return null;
    }

    public File updateBuildRoot(File topLevel, String distro) throws IOException
    {
        File updatedBuildRoot = null;
        String userBuildRootString = getProperties().getProperty(CMAKE_BUILD_ROOT_PROPERTY, "");
        if (userBuildRootString.length() != 0)
        {
            File userBuildRoot = new File(getProperties().getProperty(CMAKE_BUILD_ROOT_PROPERTY));
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
            updatedBuildRoot = new File(path, distro);

        }

        getProperties().setProperty(CMAKE_BUILD_ROOT_PROPERTY, updatedBuildRoot.getAbsolutePath());
        return updatedBuildRoot.getAbsoluteFile();
    }

    public File updateProjectBindir(File buildRoot) throws IOException
    {
        File projectBin = new File(buildRoot, getProject().getArtifactId());
        getProperties().setProperty(CMAKE_PROJECT_BIN_DIRECTORY_PROPERTY, projectBin.getAbsolutePath());
        return projectBin.getAbsoluteFile();
    }

    private Properties _properties;
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
