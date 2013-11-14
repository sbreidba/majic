package com.sri.vt.majic.mojo;

import com.sri.vt.majic.mojo.util.OperatingSystemInfo;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.IOException;

/**
 * This goal displays information about the current O/S.
 */
@Mojo(name="show-os-info")
public class ShowOsInfoMojo extends AbstractMojo
{
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        try
        {
            OperatingSystemInfo info = new OperatingSystemInfo();

            getLog().info("OS Name: " + info.getName());
            getLog().info("OS Arch: " + info.getArch());
            getLog().info("OS Distro: " + info.getDistro());
        }
        catch (IOException e)
        {
            throw new MojoExecutionException(e.getMessage());
        }
    }
}
