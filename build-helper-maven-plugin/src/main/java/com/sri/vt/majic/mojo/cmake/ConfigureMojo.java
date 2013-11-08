package com.sri.vt.majic.mojo.cmake;

import org.apache.commons.lang3.SystemUtils;
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
    @Parameter(defaultValue = "")
    private String generator;
    
    @Parameter(defaultValue = "")
    private Map<String, String> options;

    protected String getCMakeGenerator()
    {
        if ((generator != null) && (generator.length() != 0)) return generator;

        String cmakeGenerator = "unknown";
        if (SystemUtils.IS_OS_WINDOWS)
        {
            // TODO: this should be more sophisticated - check the arch, etc.
            cmakeGenerator = "Visual Studio 10 Win64";
        }
        else if (SystemUtils.IS_OS_LINUX)
        {
            cmakeGenerator = "Unix Makefiles";
        }
        else if (SystemUtils.IS_OS_MAC_OSX)
        {
            // TODO this is just for testing
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
            arguments.add(new StringBuilder("-D").append(optionsKey).append("=").append(options.get(optionsKey)).toString());
        }

        arguments.add(getSourceDirectory().getAbsolutePath());

        return arguments;
    }
}
