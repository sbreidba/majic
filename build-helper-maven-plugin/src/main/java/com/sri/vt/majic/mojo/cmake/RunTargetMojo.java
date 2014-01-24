package com.sri.vt.majic.mojo.cmake;

import org.apache.commons.lang3.SystemUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * The RunTarget mojo allows arbitrary --build --target {target} commands to be executed.<br/>
 * Several convenience mojos (doc, install, etc) are also defined for the sake of simplicity
 * and readability.
 */
@Mojo(name="cmake-runtarget", requiresProject=true)
public class RunTargetMojo extends CMakeMojo
{
    /**
     * The target to run, i.e. --target {target}
     */
    @Parameter(defaultValue = "")
    private String target;

    /**
     * Any extra arguments that should be added to the command line can be specified here.
     * Note: it is up to the caller to pass the -- if desired.
     */
    @Parameter(defaultValue = "")
    private String extraArgs;

    /**
     * The number of jobs to execute simultaneously, i.e. the value passed via -j.
     */
    @Parameter(defaultValue = "4", property = "cmake.jobs")
    private Integer jobs;

    @Override
    protected String getCommandlineArgs()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("--build ."); // always ., since we set the working dir at exec time

        String target = getTarget();
        if ((target != null) && (target.length() != 0))
        {
            builder.append(" --target ");
            builder.append(getTarget());
        }

        if (SystemUtils.IS_OS_WINDOWS)
        {
            String config = getCurrentConfig();
            if ((config != null) && (config.length() != 0))
            {
                builder.append(" --config ");
                builder.append(config);
            }
        }

        String extra = getExtraArgs();
        if ((extra != null) && (extra.length() != 0))
        {
            builder.append(" ");
            builder.append(extra);
        }

        return builder.toString();
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

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        execute(ExecutionMode.ExecutionPerConfig);
    }
}

