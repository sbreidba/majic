package com.sri.vt.majic;

import org.apache.commons.lang3.SystemUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

public class OperatingSystemInfo
{
    public OperatingSystemInfo() throws IOException
    {
        name = SystemUtils.OS_NAME;
        arch = SystemUtils.OS_ARCH;
        distro = "unknown";
        cmakeGenerator = "unknown";

        if (SystemUtils.IS_OS_WINDOWS)
        {
            // TODO: this should be more sophisticated - check the arch, etc.
            cmakeGenerator = "Visual Studio 10 Win64";
            
            if (SystemUtils.IS_OS_WINDOWS_7) distro = "win7";
            else if (SystemUtils.IS_OS_WINDOWS_XP) distro = "winxp";
            else if (SystemUtils.IS_OS_WINDOWS) distro = "win";
        }
        else if (SystemUtils.IS_OS_LINUX)
        {
            cmakeGenerator = "Unix Makefiles";
            
            Process process = Runtime.getRuntime().exec(new String[]
                {
                    "lsb_release",
                    "-a"
                }
            );

            BufferedReader reader = new BufferedReader( new InputStreamReader( process.getInputStream() ) );

            String line = null;
            HashMap<String, String> mapKeyValue = new HashMap<String, String>();
            while ((line = reader.readLine()) != null)
            {
                String[] split = line.split(":", 2);
                mapKeyValue.put(split[0].trim(), split[1].trim());
            }

            StringBuilder dist = new StringBuilder();

            String distFull = mapKeyValue.get("Distributor ID");
            if (distFull != null)
            {
                if (distFull.toLowerCase().startsWith("redhatent"))
                {
                    dist.append("rhel");
                }
                else
                {
                    // centos is "CentOS", so we can use it as-is
                    dist.append(distFull.toLowerCase());
                }
            }
            else
            {
                dist.append("unknown");
            }


            String versionFull = mapKeyValue.get("Release");
            if (versionFull != null)
            {
                dist.append(versionFull.replace(".", ""));
            }

            distro = dist.toString();
        }
        else if (SystemUtils.IS_OS_MAC_OSX)
        {
            // TODO these are just for testing
            cmakeGenerator = "Unix Makefiles";
            distro = "osx";
        }
    }

    public String getName()
    {
        return name;
    }

    public String getArch()
    {
        return arch;
    }

    public String getDistro()
    {
        return distro;
    }

    public String getCMakeGenerator()
    {
        return cmakeGenerator;
    }

    private String name;
    private String arch;
    private String distro;
    private String cmakeGenerator;
}
