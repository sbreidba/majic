package com.sri.vt.majic.util;

import org.apache.commons.lang3.SystemUtils;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.logging.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Locale;

public class OperatingSystemInfo
{
    private static String DISTRO_RED_HAT_ENTERPRISE_DETECTED = "redhatent";
    private static String DISTRO_CENTOS_DETECTED = "centos";

    private static String DISTRO_RED_HAT_ENTERPRISE = "rhel";
    private static String DISTRO_CENTOS = "centos";

    public OperatingSystemInfo() throws IOException
    {
        name = SystemUtils.OS_NAME;
        arch = SystemUtils.OS_ARCH;
        distro = "unknown";

        if (SystemUtils.IS_OS_WINDOWS)
        {
            if (SystemUtils.IS_OS_WINDOWS_7) distro = "win7";
            else if (SystemUtils.IS_OS_WINDOWS_XP) distro = "winxp";
            else distro = "win";
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
                if (distFull.toLowerCase(Locale.ENGLISH).startsWith(DISTRO_RED_HAT_ENTERPRISE_DETECTED))
                {
                    dist.append(DISTRO_RED_HAT_ENTERPRISE);
                }
                else if (distFull.toLowerCase(Locale.ENGLISH).startsWith(DISTRO_CENTOS_DETECTED))
                {
                    dist.append(DISTRO_CENTOS);
                }
                else
                {
                    // centos is "CentOS", so we can use it as-is
                    dist.append(distFull.toLowerCase(Locale.ENGLISH));
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

    public void setProperties(MavenProject project, Logger log)
    {
        PropertyUtils.setPropertyIfNotSet(project, log, BuildEnvironment.Properties.OPERATING_SYSTEM_NAME, getName());
        PropertyUtils.setPropertyIfNotSet(project, log, BuildEnvironment.Properties.OPERATING_SYSTEM_ARCHITECTURE, getArch());
        PropertyUtils.setPropertyIfNotSet(project, log, BuildEnvironment.Properties.OPERATING_SYSTEM_DISTRIBUTION, getDistro());
        PropertyUtils.setPropertyIfNotSet(project, log, BuildEnvironment.Properties.OPERATING_SYSTEM_CLASSIFIER, getClassifier());
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

    public String getClassifier()
    {
        // In general, we can default to this being the same as the distro,
        // but for simplicity's sake we map RHEL to Centos.
        String classifier = getDistro();
        if (getDistro().startsWith(DISTRO_RED_HAT_ENTERPRISE))
        {
            classifier = classifier.replaceFirst(DISTRO_RED_HAT_ENTERPRISE, DISTRO_CENTOS);
        }

        return classifier;
    }

    private String name;
    private String arch;
    private String distro;
}
