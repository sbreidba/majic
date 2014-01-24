package com.sri.vt.majic.util;

import org.apache.maven.MavenExecutionException;
import org.apache.maven.project.MavenProject;

import java.io.IOException;

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

        // The CMake build root is set by default by to:
        //      ${toplevel.project.directory}-build/${package.classifier}
        //
        // So if your source code is in ~/Devel/foo, you'll get a build directory like:
        //      ~/Devel/foo-build/win7-vc2010-64
        //
        // If you are Phil Miller and don't like that, override this value.
        // - if you specify a relative path, it will always be relative to ${toplevel.project.directory}.
        // - if you specify an absolute path, then you are the master of your own destiny.
        //   ... but be careful with mvn clean!
        //   (Note that ${project.build.directory} is absolute, so you can revert to a maven-style per-project "target"
        //   build by using that variable as-is or with a suffix.)
        public static final String CMAKE_BUILD_ROOT = "cmake.build.root";

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

        // The package classifier is set by default to ${majic.os.classifier}-${cmake.compiler}-${cmake.arch}.
        // Note that for dependencies, this property must be used as it is the only one that is interpolated
        // at build time.
        public static final String PACKAGE_CLASSIFIER = "majic.package.classifier";

        // The package extension is the maven "type", e.g. tar.bz2 or zip
        // This variable is set by default to "tar.bz2". Note that this is just a convenience for pom file usage.
        // Changing it does not alter plugin behavior. It's not used in this code base, but it's defined in
        // the Majic parent pom, so it's here for documentation purposes.
        public static final String PACKAGE_EXTENSION = "majic.package.extension";

        // The OS name is a human-readable/friendly display name
        public static final String OPERATING_SYSTEM_NAME = "majic.os.name";

        // The OS architecture is the java os.arch property (values x64, etc).
        // Use care; this may not be correct in some circumstances.
        // See http://stackoverflow.com/questions/4748673/how-can-i-check-the-bitness-of-my-os-using-java-j2se-not-os-arch
        // for more details.
        public static final String OPERATING_SYSTEM_ARCHITECTURE = "majic.os.arch";

        // The OS distribution is debian, centos, rhel, win7, winxp, etc.
        public static final String OPERATING_SYSTEM_DISTRIBUTION = "majic.os.distro";

        // The OS classifier should be used when forming artifact classifiers as it collapses compatible
        // distros such as centos and rhel.
        public static final String OPERATING_SYSTEM_CLASSIFIER = "majic.os.classifier";
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
    
    public static String getPackageClassifier(MavenProject project) throws IOException
    {
        return project.getProperties().getProperty(Properties.PACKAGE_CLASSIFIER);
    }

    public static String getOperatingSystemClassifier(MavenProject project)
    {
        return project.getProperties().getProperty(Properties.OPERATING_SYSTEM_CLASSIFIER);
    }

    public static Compiler getCompiler(MavenProject project)
    {
        String compiler = project.getProperties().getProperty(Properties.CMAKE_COMPILER);
        return Compiler.fromString(compiler);
    }

    public static Arch getArchitecture(MavenProject project)
    {
        String arch = project.getProperties().getProperty(Properties.CMAKE_ARCH);
        return Arch.fromString(arch);
    }
}
