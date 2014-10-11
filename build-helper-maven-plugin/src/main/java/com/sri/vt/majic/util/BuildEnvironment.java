package com.sri.vt.majic.util;

import org.apache.commons.lang.SystemUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;


public class BuildEnvironment
{
    // This class contains all of the properties that can be set in a pom file, or will be set
    // at build time.
    public class Properties
    {
        // The toplevel project directory property is determined automatically (no override) and can be used in plugins.
        // If you have the following module structure:
        // ~/Devel/
        //    foo/pom.xml
        //       bar/pom.xml
        //       baz/pom.xml
        // Then the toplevel project directory will be ~/Devel/foo for foo, as well as the bar and baz modules.
        // This is computed by walking the directory tree up from any given module until no more pom files are found.
        public static final String CMAKE_TOPLEVEL_PROJECT_DIRECTORY = "majic.toplevel.project.directory";

        // The CMake project build root (CMAKE_BINARY_DIR) is set by default by to:
        //      ${toplevel.project.directory}-build/${majic.package.classifier}/${project.artifactId}
        //
        // So if your source code is in ~/Devel/foo, you'll get a build directory like:
        //      ~/Devel/foo-build/win7-vc2010-64/<projects>
        //
        // If you are Phil Miller and don't like that, override this value.
        // - if you specify a relative path, it will always be relative to ${majic.toplevel.project.directory}.
        // - if you specify an absolute path, then you are the master of your own destiny. Be careful with mvn clean!
        //   You can use macros in your path, for example, something like this should work well:
        //     C:/majic-builds/${project.groupId}/${majic.package.classifier}/${project.artifactId}
        //   This is quite similar to the default, with two changes:
        //     - An absolute path, with volume, is specified.
        //     - The groupId is included. While optional, this serves to prevent build collisions.
        //       That's not necessary in the default case, since the output path is based on directory names (which
        //       can't collide by definition, or you'd have two projects in one directory), not artifactId.

        // The project bin directory maps to the CMake variable CMAKE_BINARY_DIR. It's effectively a per-project
        // sandbox. By default it is set to ${cmake.build.root}/${project.artifactId}
        // While this is a computable property, it is set in the Maven project since it's used fairly often.
        public static final String CMAKE_PROJECT_BIN_DIRECTORY = "cmake.project.bin.directory";

        // The cmake compiler is used as a shorthand/hint for the cmake generator in combination with the cmake.arch
        // property. It is also used to form the package classifier.
        // The Compiler enum lists the possible values.
        public static final String CMAKE_COMPILER = "cmake.compiler";

        // The cmake architecture is used to define the cmake generator in combination with the cmake.compiler
        // property. It is also used to form the package classifier.
        // The Arch enum lists the possible values.
        public static final String CMAKE_ARCH = "cmake.arch";

        // The CMAKE_SIZEOF_VOID_P value: 8 when building 64-bit, 4 when building 32-bit.
        // Useful when cross-compiling.
        public static final String CMAKE_SIZEOF_VOID_P = "cmake.sizeof.void.p";

        // This is a simple way to add CMake variables to a given build. This variable, ${cmake.vars}
        // is used, but is also a prefix, and a larger set of variables is examined. The full set of variables
        // is cmake.vars(.os)(.compiler)(.arch). Each component is optional. So while the contents of ${cmake.vars} \
        // is always added, ${cmake.vars.win7} is added for all win7 builds, ${cmake.vars.32} is added for all
        // 32-bit builds, ${cmake.vars.vc2010.64} is added for 64-bit visual studio 2010 builds, etc.
        public static final String CMAKE_VARS = "cmake.vars";

        // The package classifier is set by default to ${majic.os.classifier}-${cmake.compiler}-${cmake.arch}.
        // Note that for dependencies, only properties defined in the parent (such as this one) can be used -
        // interpolated properties cannot be used for dependency resolution.
        public static final String PACKAGE_CLASSIFIER = "majic.package.classifier";

        // The package extension is the maven "type", e.g. tar.bz2 or zip
        // This variable is set by default to "tar.bz2". Note that this is just a convenience for pom file usage.
        // Changing it does not alter plugin behavior. It's not used in this code base, but it's defined in
        // the Majic parent pom, so it's here for documentation purposes.
        public static final String PACKAGE_EXTENSION = "majic.package.extension";

        // The OS classifier should be used when forming artifact classifiers as it collapses compatible
        // distros such as centos and rhel.
        public static final String OPERATING_SYSTEM_CLASSIFIER = "majic.os.classifier";

        // The compiler version according to nmake, e.g. 1400 for Visual Studio 2008
        // This follows the ${cmake.compiler} variable.
        public static final String NMAKE_MSVCVER = "nmake.msvcver";

        // The toolset used when compiling with bjam.
        // This follows the ${cmake.compiler} variable.
        public static final String BJAM_TOOLSET = "bjam.toolset";

        // This is the common tools directory that corresponds to the selected compiler
        // (if Windows) e.g. the contents of VS80COMNTOOLS, VS90COMNTOOLS, etc.
        public static final String VISUAL_STUDIO_COMNTOOLS_DIR = "visual.studio.comntools.dir";

        // This is the architecture as known by vcvarsall.bat. This follows the ${cmake.arch}
        // variable.
        public static final String VCVARS_ARCH = "vcvars.arch";
    }

    public enum Compiler
    {
        gcc("gcc"),
        vc2008("vc2008"),
        vc2009("vc2009"),
        vc2010("vc2010"),
        vc2012("vc2012");

        Compiler(String value) { this.value = value; }

        private final String value;

        @Override
        public String toString() { return value; }

        public static Compiler fromString(String text)
        {
            if (text != null)
            {
                for (Compiler e : Compiler.values())
                {
                    if (text.equalsIgnoreCase(e.toString())) return e;
                }
            }

            return null;
        }
    }

    public enum Arch
    {
        bits32("32"),
        bits64("64");

        Arch(String value) { this.value = value; }

        private final String value;

        @Override
        public String toString() { return value; }

        public static Arch fromString(String text)
        {
            if (text != null)
            {
                for (Arch e : Arch.values())
                {
                    if (text.equalsIgnoreCase(e.toString())) return e;
                }
            }

            return null;
        }
    }

    private MavenProject project;

    public BuildEnvironment(MavenProject project)
    {
        this.project = project;
    }

    protected MavenProject getProject()
    {
        return project;
    }

    public String getPackageClassifier() throws IOException
    {
        return getProject().getProperties().getProperty(Properties.PACKAGE_CLASSIFIER);
    }

    public String getPackageType() throws IOException
    {
        return getProject().getProperties().getProperty(Properties.PACKAGE_EXTENSION);
    }

    public String getOperatingSystemClassifier()
    {
        return getProject().getProperties().getProperty(Properties.OPERATING_SYSTEM_CLASSIFIER);
    }

    public String getVCVarsArch()
    {
        return getProject().getProperties().getProperty(Properties.VCVARS_ARCH);
    }
    
    public Compiler getCompiler() throws MojoExecutionException
    {
        String compilerStr = getProject().getProperties().getProperty(Properties.CMAKE_COMPILER);
        if ((compilerStr == null) || (compilerStr.length() == 0))
        {
            throw new MojoExecutionException("cmake.compiler is not set - have you included the majic parent pom?");
        }

        Compiler compiler = Compiler.fromString(compilerStr);
        if (compiler == null)
        {
            throw new MojoExecutionException("cmake.compiler could not be set from " + compilerStr);
        }

        return compiler;
    }

    public String getVisualStudioCommonToolsDir() throws MojoExecutionException
    {
        if (!SystemUtils.IS_OS_WINDOWS)
        {
            throw new MojoExecutionException("Trying to compute visual studio common tools dir on a non-windows platform.");
        }

        Map<String, String> env = System.getenv();

        switch (getCompiler())
        {
            case vc2008:
                return env.get("VS80COMNTOOLS");

            case vc2009:
                return env.get("VS90COMNTOOLS");

            case vc2010:
                return env.get("VS100COMNTOOLS");

            case vc2012:
                return env.get("VS110COMNTOOLS");

            default:
                throw new MojoExecutionException("Could not get visual studio common tools dir - unknown compiler type");
        }
    }

    public File getVisualStudioVCVarsAllFile() throws MojoExecutionException
    {
        File toolsPath = new File(getVisualStudioCommonToolsDir());
        return new File(toolsPath, "..\\..\\VC\\vcvarsall.bat");
    }

    public Arch getArchitecture() throws MojoExecutionException
    {
        String archStr = getProject().getProperties().getProperty(Properties.CMAKE_ARCH);
        if (archStr == null)
        {
            throw new MojoExecutionException("cmake.arch could not be set from " + archStr);
        }

        return Arch.fromString(archStr);
    }

    public List<String> getAdditionalCMakeVariables() throws MojoExecutionException
    {
        StringCombinations combinations = new StringCombinations(Properties.CMAKE_VARS)
                .combineWith("." + getOperatingSystemClassifier())
                .combineWith("." + getCompiler().toString())
                .combineWith("." + getArchitecture().toString());

        return combinations.getStrings();
    }

    public void updateProperties(PropertyCache propertyCache) throws MojoExecutionException
    {
        if (SystemUtils.IS_OS_WINDOWS)
        {
            propertyCache.setProperty(
                    Properties.VISUAL_STUDIO_COMNTOOLS_DIR,
                    getVisualStudioCommonToolsDir());
        }

        switch (getCompiler())
        {
            case gcc:
                propertyCache.setProperty(Properties.NMAKE_MSVCVER, "unknown");
                propertyCache.setProperty(Properties.BJAM_TOOLSET, "gcc");
                break;

            case vc2008:
                propertyCache.setProperty(Properties.NMAKE_MSVCVER, "1400");
                propertyCache.setProperty(Properties.BJAM_TOOLSET, "msvc-8.0");
                break;

            case vc2009:
                propertyCache.setProperty(Properties.NMAKE_MSVCVER, "1500");
                propertyCache.setProperty(Properties.BJAM_TOOLSET, "msvc-9.0");
                break;

            case vc2010:
                propertyCache.setProperty(Properties.NMAKE_MSVCVER, "1600");
                propertyCache.setProperty(Properties.BJAM_TOOLSET, "msvc-10.0");
                break;

            case vc2012:
                propertyCache.setProperty(Properties.NMAKE_MSVCVER, "1700");
                propertyCache.setProperty(Properties.BJAM_TOOLSET, "msvc-11.0");
                break;

            default:
                propertyCache.getLog().error("Could not detect compiler to use.");
                break;
        }

        switch (getArchitecture())
        {
            case bits32:
                propertyCache.setProperty(Properties.VCVARS_ARCH, "x86");
                propertyCache.setProperty(Properties.CMAKE_SIZEOF_VOID_P, "4");
                break;

            case bits64:
                propertyCache.setProperty(Properties.VCVARS_ARCH, "x64");
                propertyCache.setProperty(Properties.CMAKE_SIZEOF_VOID_P, "8");
                break;

            default:
                propertyCache.setProperty(Properties.VCVARS_ARCH, "unknown");
                propertyCache.setProperty(Properties.CMAKE_SIZEOF_VOID_P, "unknown");
                break;
        }
    }
}
