package com.sri.vt.majic.util;

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

        if (SystemUtils.IS_OS_WINDOWS)
        {
            if (SystemUtils.IS_OS_WINDOWS_7) distro = "win7";
            else if (SystemUtils.IS_OS_WINDOWS_XP) distro = "winxp";
            else if (SystemUtils.IS_OS_WINDOWS) distro = "win";
        }
        else if (SystemUtils.IS_OS_LINUX)
        {
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
                // include only the major version number
                String[] versionComponents = versionFull.split("\\.");
                dist.append(versionComponents[0]);
            }

            distro = dist.toString();
        }
        else if (SystemUtils.IS_OS_MAC_OSX)
        {
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

    private String name;
    private String arch;
    private String distro;
}
