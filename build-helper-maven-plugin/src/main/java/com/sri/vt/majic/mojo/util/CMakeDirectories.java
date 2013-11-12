package com.sri.vt.majic.mojo.util;

import org.apache.commons.lang3.SystemUtils;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import static com.sri.vt.majic.mojo.util.Logging.error;
import static com.sri.vt.majic.mojo.util.Logging.info;

public class CMakeDirectories implements ILoggable
{
    private static String CMAKE_BUILD_ROOT_ENV = "env.cmake.build.root";
    private static String CMAKE_BUILD_ROOT_PROPERTY = "cmake.build.root";

    private static String CMAKE_PACKAGE_ROOT_PROPERTY = "cmake.pkg.root";
    private static String CMAKE_EXPORT_ROOT_PROPERTY = "cmake.export.root";
    private static String CMAKE_BINDIR_ROOT_PROPERTY = "cmake.bindir.root";

    private static String CMAKE_PROJECT_BINDIR = "cmake.project.bindir";
    private static String CMAKE_PROJECT_INSTALLDIR = "cmake.project.installdir";

    public CMakeDirectories(MavenProject project)
    {
        _project = project;
        _log = null;

        mapSuffixes = new HashMap<String, String>();
        mapSuffixes.put(CMAKE_PACKAGE_ROOT_PROPERTY, "pkg");
        mapSuffixes.put(CMAKE_EXPORT_ROOT_PROPERTY, "exports");
        mapSuffixes.put(CMAKE_BINDIR_ROOT_PROPERTY, "binary_dirs");
    }

    public CMakeDirectories log(Log log)
    {
        _log = log;
        return this;
    }

    // cmake.build.root: the absolute path to the top-level build directory
    // rules for setting this are as follows:
    //     1. If it is already set, do nothing. Projects that wish a nested structure
    //        can define:
    //          <properties>
    //             <cmake.build.root>${basedir}</cmake.build.root>
    //          </properties>
    //     2. Is there an environment variable called ${env.cmake.build.root}?
    //        If so, use that. Example: given a pom (likely a parent pom) that contains:
    //          <properties>
    //             <env.cmake.build.root>MYPROJECT_ROOT</env.cmake.build.root>
    //          </properties>
    //        cmake.build.root will get set to ${env.MYPROJECT_ROOT}.
    //     3. Starting from ${basedir}, traverse the directory structure upwards,
    //        looking for the last parent directory that contains a pom.xml and
    //        append "-build" to this directory.

    public File getBuildRoot()
    {
        File root = null;

        root = getPropertyAsFile(CMAKE_BUILD_ROOT_PROPERTY);
        if (root != null)
        {
            info(this, "discovered property " + CMAKE_BUILD_ROOT_PROPERTY);
            return root;
        }

        root = getPropertyAsFile(CMAKE_BUILD_ROOT_ENV);
        if (root != null)
        {
            info(this, "discovered property " + CMAKE_BUILD_ROOT_ENV);
            return root;
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
        levels--;

        if (lastPomFound != null)
        {
            info(this, "found a top-level parent pom " + levels + " levels up");

            String path = lastPomFound.getAbsolutePath();
            path += "-build";

            return new File(path);
        }

        error(this, "could not configure cmake.build.root");
        return null;
    }

    // The package root is where "package" (typically external) dependencies will be placed
    // If ${cmake.pkg.root} is defined, that is returned, otherwise it is computed as
    // getBuildRoot()/pkg (i.e. ${cmake.build.root}/pkg). Typically, this would be included
    // in CMAKE_PREFIX_PATH.

    public File getPackageRoot()
    {
        return getRootRelativeDirectory(CMAKE_PACKAGE_ROOT_PROPERTY);
    }

    // The export root is where "export" (typically project-internal) dependencies will be placed
    // If ${cmake.export.root} is defined, that is returned, otherwise it is computed as
    // getBuildRoot()/pkg (i.e. ${cmake.build.root}/exports). Typically, this would be included
    // in CMAKE_PREFIX_PATH.

    public File getExportRoot()
    {
        return getRootRelativeDirectory(CMAKE_EXPORT_ROOT_PROPERTY);
    }

    // The bindir root is the directory where individual project binary dirs live
    // If ${cmake.bindir.root} is defined, that is returned, otherwise it is computed as
    // getBuildRoot()/binary_dirs (i.e. ${cmake.build.root}/binary_dirs)

    public File getBindirRoot()
    {
        return getRootRelativeDirectory(CMAKE_BINDIR_ROOT_PROPERTY);
    }

    // The project bindir is a specific project's cmake binary dir (where configure is run).
    // If ${cmake.project.bindir} is defined, that is returned, otherwise it is computed as
    // getBinDirRoot()/${project.artifactId} (i.e. ${cmake.bindir.root}/${project.artifactId}
    // There are two variants of this method; one which is sensitive to build configuration
    // (e.g. debug or release) and one which is invariant.

    public File getProjectBindir() throws IOException
    {
        return getProjectBindir(null);
    }

    public File getProjectBindir(String config) throws IOException
    {
        File root = getPropertyAsFile(CMAKE_PROJECT_BINDIR);
        if (root != null)
        {
            info(this, "discovered property " + CMAKE_PROJECT_BINDIR);
            return root;
        }

        root = getBindirRoot();
        if (root == null)
        {
            return null;
        }

        OperatingSystemInfo operatingSystemInfo = new OperatingSystemInfo();
        root = new File(root, getProject().getArtifactId() + "/" + operatingSystemInfo.getDistro());

        if (!SystemUtils.IS_OS_WINDOWS)
        {
            if ((config != null) && (config.length() != 0))
            {
                root = new File(root, config);
            }
        }
        
        return root;
    }
    
    // The project installdir is a specific project's export directory; this is often used
    // as the source for a project's packaging.
    // If ${cmake.project.installdir} is defined, that is returned, otherwise it is computed as
    // getBinDirRoot()/exports/${project.artifactId}-${project.version}
    // (i.e. ${cmake.bindir.root}/${project.artifactId}-${project.version}
    // Note that the build config is *always* ignored here. Debug and release targets
    // are assumed to be merged for packaging during installation.

    public File getProjectInstalldir() throws IOException
    {
        File root = getPropertyAsFile(CMAKE_PROJECT_INSTALLDIR);
        if (root != null)
        {
            info(this, "discovered property " + CMAKE_PROJECT_INSTALLDIR);
            return root;
        }

        root = getProjectBindir();
        if (root == null)
        {
            return null;
        }

        return new File(root, "exports/" + getProject().getArtifactId() + "-" + getProject().getVersion());
    }

    // This helper method builds common paths out of the build root plus a suffix.
    protected File getRootRelativeDirectory(String property)
    {
        File root = getPropertyAsFile(property);
        if (root != null)
        {
            info(this, "discovered property " + property);
            return root;
        }

        root = getBuildRoot();
        if (root == null) return null;

        return new File(root, mapSuffixes.get(property));
    }

    protected File getPropertyAsFile(String property)
    {
        if (getProject().getProperties().containsKey(property))
        {
            Object buildRootObj = getProject().getProperties().get(property);
            if (buildRootObj.getClass().equals(File.class))
            {
                return (File)buildRootObj;
            }
            else
            {
                assert(buildRootObj.getClass().equals(String.class));
                String buildRoot = (String)buildRootObj;
                return new File(buildRoot).getAbsoluteFile();
            }
        }

        return null;
    }

    private MavenProject _project;
    private Log _log;
    private HashMap<String, String> mapSuffixes;

    protected MavenProject getProject()
    {
        return _project;
    }

    public Log getLog()
    {
        return _log;
    }
}
