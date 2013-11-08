package com.sri.vt.majic.mojo.cmake;

import org.apache.commons.lang3.SystemUtils;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

@Mojo(name="cmake-runtarget", requiresProject=true)
public class RunTargetMojo extends CMakeMojo
{
    @Parameter(defaultValue = "")
    private String target;

    @Parameter(defaultValue = "")
    private String config;

    // Note: it is up to the caller to pass the -- if desired.
    @Parameter(defaultValue = "")
    private String extraArgs;

    @Parameter(defaultValue = "4")
    private Integer jobs;

    @Override
    protected String getCommandlineArgs()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("--build ."); // always ., since we set the working dir at exec time
        builder.append(" --target ");
        builder.append(getTarget());

        String config = getConfig();
        if ((config != null) && (config.length() != 0))
        {
            builder.append(" --config ");
            builder.append(config);
        }

        String extra = getExtraArgs();
        if ((extra != null) && (extra.length() != 0))
        {
            builder.append(" ");
            builder.append(extra);
        }

        return builder.toString();
    }

    protected String getConfig()
    {
        return config;
    }

    protected String getExtraArgs()
    {
        boolean hasDoubleDash = false;
        StringBuilder extra = new StringBuilder();
        if (extraArgs != null)
        {
            extra.append(extraArgs);
            hasDoubleDash = (extraArgs.contains("--"));
        }

		if (SystemUtils.IS_OS_LINUX) 
		{
			if (jobs != null) 
			{
				if (!hasDoubleDash) 
				{
					extra.append(" -- ");
				}
				extra.append("-j");
				extra.append(jobs);
			}
		}

        return extra.toString();
    }
    
    protected String getTarget()
    {
        return target;
    }
}

