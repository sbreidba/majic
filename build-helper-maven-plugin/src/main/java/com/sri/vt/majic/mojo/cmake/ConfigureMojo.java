package com.sri.vt.majic.mojo.cmake;

import com.google.common.collect.ImmutableMap;
import com.sri.vt.majic.util.BuildEnvironment;
import com.sri.vt.majic.util.CMakeDirectories;
import org.apache.commons.lang3.SystemUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Executes the CMake configuration step.
 */
@Mojo(name="cmake-configure", defaultPhase=LifecyclePhase.PROCESS_SOURCES, requiresProject=true)
public class ConfigureMojo extends CMakeMojo
{
    @Parameter(defaultValue = "${basedir}")
    private File sourceDirectory;

    /**
     * The CMake generator to use (i.e. <code>cmake -G generator</code>)
     * Left blank, this will be computed automatically from cmake.generator and cmake.arch.
     * If set, it will override the use of those variables.
     */
    @Parameter(defaultValue = "", property = "cmake.generator")
    private String generator;

    /**
     * The package dir is the location that "external" packages are unpacked to.
     * It is only used if addCMakePrefixPath is enabled.
     */
    @Parameter(defaultValue = CMakeDirectories.CMAKE_PACKAGE_ROOT_DEFAULT)
    private File packageRoot;

    /**
     * The export dir is the location that "internal" packages are unpacked to.
     * It is only used if addCMakePrefixPath is enabled.
     */
    @Parameter(defaultValue = CMakeDirectories.CMAKE_EXPORT_ROOT_DEFAULT)
    private File exportRoot;

    /**
     * The project install dir defines where CMake INSTALL(...) commands copy files to.
     * It is only used if addCMakeInstallPrefix is enabled.
     */
    @Parameter(defaultValue = CMakeDirectories.CMAKE_PROJECT_INSTALLDIR_DEFAULT)
    private File projectInstallDir;

    /**
     * A map of options to pass to CMake, in the form <code>-Dkey=value</code>
     */
    @Parameter(defaultValue = "")
    private Map<String, String> options;

    /**
     * If enabled, automatically adds <code>-DCPACK_PACKAGE_VERSION(_MAJOR|_MINOR|_PATCH)</code> to the configuration.
     */
    @Parameter(defaultValue = "true")
    private boolean addCPackPackageVersion;

    /**
     * If enabled, automatically adds <code>-DCMAKE_PREFIX_PATH</code> to the configuration. The package root
     * and export root are both included.
     */
    @Parameter(defaultValue = "true")
    private boolean addCMakePrefixPath;

    /**
     * If enabled, automatically adds <code>-DCMAKE_INSTALL_PREFIX=projectInstallDir</code> to the configuration.
     */
    @Parameter(defaultValue = "true")
    private boolean addCMakeInstallPrefix;

    /**
     * If enabled, for single-config (e.g. not Windows) builds, this will add <code>-DCMAKE_BUILD_TYPE="..."</code>
     * to the configuration.
     */
    @Parameter(defaultValue = "true")
    private boolean addCMakeBuildType;

    /**
     * If enabled, for multi-config builds, this will add <code>-DCMAKE_CONFIGURATION_TYPES="..."</code>
     * to the configuration.
     */
    @Parameter(defaultValue = "true")
    private boolean addCMakeConfigurationTypes;

    /**
     * If set, the configuration step will be skipped.
     */
    @Parameter(defaultValue = "false", property = "skipCMakeConfig")
    private boolean skip;

    static final Map<BuildEnvironment.Compiler, String> mapCompilerToGeneratorPrefix =
            ImmutableMap.<BuildEnvironment.Compiler, String>builder()
            .put(BuildEnvironment.Compiler.gcc, "Unix Makefiles")
            .put(BuildEnvironment.Compiler.vc2008, "Visual Studio 8")
            .put(BuildEnvironment.Compiler.vc2009, "Visual Studio 9")
            .put(BuildEnvironment.Compiler.vc2010, "Visual Studio 10")
            .put(BuildEnvironment.Compiler.vc2012, "Visual Studio 11")
            .build();

    @Override
    protected boolean getSkip()
    {
        File cmakeScript = new File(sourceDirectory, "CMakeLists.txt");
        if (!cmakeScript.exists())
        {
            getLog().info("CMakeLists.txt not found -- skipping execution.");
            return true;
        }
        
        return skip;
    }

    protected String getCMakeGenerator()
    {
        BuildEnvironment.Compiler compiler = BuildEnvironment.getCompiler(getProject());
        if (compiler == null)
        {
            getLog().error("Could not determine compiler type");
            return null;
        }

        BuildEnvironment.Arch arch = BuildEnvironment.getArchitecture(getProject());
        if (arch == null)
        {
            getLog().error("Could not determine compiler architecture");
            return null;
        }

        String cmakeGenerator = mapCompilerToGeneratorPrefix.get(compiler);
        if (SystemUtils.IS_OS_WINDOWS)
        {
            cmakeGenerator += " Win" + arch.toString();
        }

        return cmakeGenerator;
    }

    protected Map<String, String> getCMakeOptions()
    {
        return options;
    }

    protected File getSourceDirectory()
    {
        return sourceDirectory;
    }

    private void appendDashD(List<String> list, String key, String value)
    {
        if ((value != null) && (value.length() != 0))
        {
            list.add(new StringBuilder("-D").append(key).append("=").append(value).toString());
        }
    }

    public List<String> getArguments()
    {
        List<String> arguments = super.getArguments();

        if (arguments == null) arguments = new ArrayList<String>();

        if (getCMakeGenerator() != null)
        {
            arguments.add(new StringBuilder("-G").append(getCMakeGenerator()).toString());
        }

        if (getCMakeOptions() != null)
        {
            for (String optionsKey : getCMakeOptions().keySet())
            {
                appendDashD(arguments, optionsKey, options.get(optionsKey));
            }
        }
        
        if (addCPackPackageVersion)
        {
            appendDashD(arguments, "CPACK_PACKAGE_VERSION", getProject().getVersion());

            String[] versionComponents = getProject().getVersion().split("\\.");

            if (versionComponents.length > 0)
            {
                appendDashD(arguments, "CPACK_PACKAGE_VERSION_MAJOR", versionComponents[0]);
            }

            if (versionComponents.length > 1)
            {
                appendDashD(arguments, "CPACK_PACKAGE_VERSION_MINOR", versionComponents[1]);
            }
            
            if (versionComponents.length > 2)
            {
                appendDashD(arguments, "CPACK_PACKAGE_VERSION_PATCH", versionComponents[2]);
            }
        }

        if (addCMakePrefixPath)
        {
            appendDashD(arguments, "CMAKE_PREFIX_PATH", packageRoot.getAbsolutePath() + ";" + exportRoot.getAbsolutePath());
        }

        if (addCMakeInstallPrefix)
        {
            appendDashD(arguments, "CMAKE_INSTALL_PREFIX", projectInstallDir.getAbsolutePath());
        }

        if (addCMakeConfigurationTypes && SystemUtils.IS_OS_WINDOWS)
        {
            appendDashD(arguments, "CMAKE_CONFIGURATION_TYPES", getCurrentConfig());
        }

        arguments.add(getSourceDirectory().getAbsolutePath());

        return arguments;
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        ExecutionMode mode = ExecutionMode.ExecutionPerConfig;
        if (SystemUtils.IS_OS_WINDOWS)
        {
            mode = ExecutionMode.ExecutionAsSemicolonSeparatedList;
        }

        execute(mode);
    }
}
