package com.sri.vt;

import org.apache.commons.lang3.SystemUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

public class OperatingSystemInfo
{
    public OperatingSystemInfo() throws IOException
    {
        m_name = SystemUtils.OS_NAME;
        m_arch = SystemUtils.OS_ARCH;
        m_distro = "unsupported";

        if (SystemUtils.IS_OS_WINDOWS)
        {
            if (SystemUtils.IS_OS_WINDOWS_7) m_distro = "win7";
            else if (SystemUtils.IS_OS_WINDOWS_XP) m_distro = "winxp";
            else if (SystemUtils.IS_OS_WINDOWS) m_distro = "win";
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
                dist.append(versionFull.replace(".", ""));
            }

            m_distro = dist.toString();
        }
    }

    public String getName()
    {
        return m_name;
    }

    public String getArch()
    {
        return m_arch;
    }

    public String getDistro()
    {
        return m_distro;
    }

    private String m_name;
    private String m_arch;
    private String m_distro;
}
