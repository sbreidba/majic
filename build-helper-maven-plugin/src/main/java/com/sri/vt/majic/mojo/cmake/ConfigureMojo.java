package com.sri.vt.majic.mojo.cmake;

import org.apache.commons.lang3.SystemUtils;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.sri.vt.majic.mojo.util.Logging.error;

@Mojo(name="cmake-configure", defaultPhase=LifecyclePhase.PROCESS_SOURCES, requiresProject=true)
public class ConfigureMojo extends CMakeMojo
{
    @Parameter(defaultValue = "${basedir}")
    private File sourceDirectory;

    // Left blank, this will be computed automatically
    @Parameter(defaultValue = "")
    private String generator;
    
    @Parameter(defaultValue = "")
    private Map<String, String> options;

    @Parameter(defaultValue = "true")
    private boolean addCPackPackageVersion;

    @Parameter(defaultValue = "true")
    private boolean addCMakePrefixPath;
    
    @Parameter(defaultValue = "true")
    private boolean addCMakeInstallPrefix;

    @Parameter(defaultValue = "true")
    private boolean addCMakeBuildTypeForSingleConfigBuilds;

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
        list.add(new StringBuilder("-D").append(key).append("=").append(value).toString());
    }

    public List<String> getArguments()
    {
        List<String> arguments = super.getArguments();

        if (arguments == null) arguments = new ArrayList<String>();

        if (getCMakeGenerator() != null)
        {
            arguments.add(new StringBuilder("-G").append(getCMakeGenerator()).toString());
        }

        for (String optionsKey : getCMakeOptions().keySet())
        {
            appendDashD(arguments, optionsKey, options.get(optionsKey));
        }

        if (addCPackPackageVersion)
        {
            appendDashD(arguments, "CPACK_PACKAGE_VERSION", project.getVersion());
        }

        if (addCMakePrefixPath)
        {
            appendDashD(arguments, "CMAKE_PREFIX_PATH", getCMakeDirectories().getPackageRoot().getAbsolutePath());
        }

        if (addCMakeInstallPrefix)
        {
            try
            {
                appendDashD(arguments, "CMAKE_INSTALL_PREFIX", getCMakeDirectories().getProjectInstalldir().getAbsolutePath());
            }
            catch (IOException e)
            {
                error(this, "Could not determine cmake project directory");
            }
        }

        if (addCMakeBuildTypeForSingleConfigBuilds)
        {
            if (!SystemUtils.IS_OS_WINDOWS)
            {
                appendDashD(arguments, "CMAKE_BUILD_TYPE", getConfig());
            }
        }

        arguments.add(getSourceDirectory().getAbsolutePath());

        return arguments;
    }
}
