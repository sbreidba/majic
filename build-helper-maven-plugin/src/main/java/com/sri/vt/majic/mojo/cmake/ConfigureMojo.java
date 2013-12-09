package com.sri.vt.majic.mojo.cmake;

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

@Mojo(name="cmake-configure", defaultPhase=LifecyclePhase.PROCESS_SOURCES, requiresProject=true)
public class ConfigureMojo extends CMakeMojo
{
    @Parameter(defaultValue = "${basedir}")
    private File sourceDirectory;

    // Left blank, this will be computed automatically
    @Parameter(defaultValue = "", property = "cmake.generator")
    private String generator;

    @Parameter(defaultValue = CMakeDirectories.CMAKE_PACKAGE_ROOT_DEFAULT)
    private File packageRoot;

    @Parameter(defaultValue = CMakeDirectories.CMAKE_EXPORT_ROOT_DEFAULT)
    private File exportRoot;

    @Parameter(defaultValue = CMakeDirectories.CMAKE_PROJECT_INSTALLDIR_DEFAULT)
    private File projectInstallDir;

    @Parameter(defaultValue = "")
    private Map<String, String> options;

    @Parameter(defaultValue = "true")
    private boolean addCPackPackageVersion;

    @Parameter(defaultValue = "true")
    private boolean addCMakePrefixPath;
    
    @Parameter(defaultValue = "true")
    private boolean addCMakeInstallPrefix;

    // For single-config builds, this will add CMAKE_BUILD_TYPE="<config>"
    @Parameter(defaultValue = "true")
    private boolean addCMakeBuildType;

    // For multi-config builds, this will add CMAKE_CONFIGURATION_TYPES="<semi-colon separated configs>"
    @Parameter(defaultValue = "true")
    private boolean addCMakeConfigurationTypes;

    @Parameter(defaultValue = "${skipCMakeConfig}")
    private boolean skip;

    @Override
    protected boolean getSkip()
    {
        return skip;
    }

    protected String getCMakeGenerator()
    {
        if ((generator != null) && (generator.length() != 0)) return generator;

        String cmakeGenerator;
        if (SystemUtils.IS_OS_WINDOWS)
        {
            // TODO: this should be more sophisticated - check the arch, etc.
            cmakeGenerator = "Visual Studio 10 Win64";
        }
        else
        {
            cmakeGenerator = "Unix Makefiles";
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
