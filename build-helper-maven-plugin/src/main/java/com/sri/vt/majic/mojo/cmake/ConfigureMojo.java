package com.sri.vt.majic.mojo.cmake;

import com.google.common.collect.ImmutableMap;
import com.sri.vt.majic.util.ArtifactHelper;
import com.sri.vt.majic.util.BuildEnvironment;
import com.sri.vt.majic.util.CMakeDirectories;
import com.sri.vt.majic.util.Version;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Executes the CMake configuration step.
 */
@Mojo(name="cmake-configure", defaultPhase=LifecyclePhase.PROCESS_SOURCES, requiresProject=true, requiresDependencyResolution = ResolutionScope.TEST)
public class ConfigureMojo extends CMakeMojo
{
    @Parameter( defaultValue = "${reactorProjects}", readonly = true )
    protected List<MavenProject> reactorProjects;

    @Parameter(defaultValue = "${basedir}")
    private File sourceDirectory;

    /**
     * The CMake generator to use (i.e. <code>cmake -G generator</code>)
     * Left blank, this will be computed automatically from cmake.generator and cmake.arch.
     * If set, it will override the use of those variables.
     */
    @Parameter(defaultValue = "", property = "cmake.generator")
    private String generator;

    /**
     * The project install dir defines where CMake INSTALL(...) commands copy files to.
     * It is only used if addCMakeInstallPrefix is enabled.
     */
    @Parameter(defaultValue = CMakeDirectories.CMAKE_PROJECT_INSTALL_DIR_DEFAULT)
    private File projectInstallDir;

    /**
     * A map of options to pass to CMake, in the form <code>-Dkey=value</code>
     */
    @Parameter(defaultValue = "")
    private Map<String, String> options;

    /**
     * If not set, this is derived from Maven's global debug flag (i.e. -X).
     */
    @Parameter(defaultValue = "false", property = "majic.configure.verbose")
    private boolean verbose;

    /**
     * If enabled, automatically adds <code>-DCPACK_PACKAGE_VERSION(_MAJOR|_MINOR|_PATCH)</code> to the configuration.
     */
    @Parameter(defaultValue = "true")
    private boolean addCPackPackageVersion;

    /**
     * If enabled, automatically adds <code>-DCMAKE_PREFIX_PATH</code> to the configuration. The package root
     * and export root are both included.
     */
    @Parameter(defaultValue = "true")
    private boolean addCMakePrefixPath;

    /**
     * If enabled, automatically adds <code>-DCMAKE_INSTALL_PREFIX=projectInstallDir</code> to the configuration.
     */
    @Parameter(defaultValue = "true")
    private boolean addCMakeInstallPrefix;

    /**
     * If enabled, for multi-config builds, this will add <code>-DCMAKE_CONFIGURATION_TYPES="..."</code>
     * to the configuration.
     */
    @Parameter(defaultValue = "true")
    private boolean addCMakeConfigurationTypes;

    /**
     * If enabled, for single-config (e.g. not Windows) builds, this will add <code>-DCMAKE_BUILD_TYPE="..."</code>
     * to the configuration.
     */
    @Parameter(defaultValue = "true")
    private boolean addCMakeBuildType;

    /**
     * If enabled, add the contents of any variables of the form
     * ${cmake.vars(.{majic.os.classifier})(.{cmake.compiler})(.${cmake.arch}}
     * to the configuration line.
     */
    @Parameter(defaultValue = "true")
    private boolean addAdditionalPredefined;

    /**
     * If set, the configuration step will be skipped.
     */
    @Parameter(defaultValue = "false", property = "skipCMakeConfig")
    private boolean skip;

    static final Map<BuildEnvironment.Compiler, String> mapCompilerToGeneratorPrefix =
            ImmutableMap.<BuildEnvironment.Compiler, String>builder()
            .put(BuildEnvironment.Compiler.gcc, "Unix Makefiles")
            .put(BuildEnvironment.Compiler.vc2008, "Visual Studio 8")
            .put(BuildEnvironment.Compiler.vc2009, "Visual Studio 9")
            .put(BuildEnvironment.Compiler.vc2010, "Visual Studio 10")
            .put(BuildEnvironment.Compiler.vc2012, "Visual Studio 11")
            .build();

    @Override
    protected boolean getSkip()
    {
        File cmakeScript = new File(sourceDirectory, "CMakeLists.txt");
        if (!cmakeScript.exists())
        {
            getLog().info("CMakeLists.txt not found -- skipping execution.");
            return true;
        }
        
        return skip;
    }
    
    protected boolean isVerbose()
    {
        return (verbose || getLog().isDebugEnabled());
    }

    protected String getCMakeGenerator() throws MojoExecutionException
    {
        BuildEnvironment.Compiler compiler = getBuildEnvironment().getCompiler();
        BuildEnvironment.Arch arch = getBuildEnvironment().getArchitecture();
        if (arch == null)
        {
            getLog().error("Could not determine compiler architecture");
            return null;
        }

        String cmakeGenerator = mapCompilerToGeneratorPrefix.get(compiler);
        if ((SystemUtils.IS_OS_WINDOWS) && (arch == BuildEnvironment.Arch.bits64))
        {
            cmakeGenerator += " Win64";
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

    private void appendDashD(List<String> list, String key, String value, boolean convertToUnixPath)
    {
        if ((value != null) && (value.length() != 0))
        {
            if (convertToUnixPath)
            {
                value = FilenameUtils.separatorsToUnix(value);
            }
            list.add(new StringBuilder("-D").append(key).append("=").append(value).toString());
        }
    }

    private void appendDashD(List<String> list, String key, String value)
    {
        appendDashD(list, key, value, false);
    }

    public List<String> getArguments() throws MojoExecutionException
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

            Version version = Version.parse(getProject().getVersion());

            appendDashD(arguments, "CPACK_PACKAGE_VERSION_COUNT", Integer.toString(version.getCount()));
            
            if (version.getMajor() != null)
            {
                appendDashD(arguments, "CPACK_PACKAGE_VERSION_MAJOR", version.getMajor().toString());
            }

            if (version.getMinor() != null)
            {
                appendDashD(arguments, "CPACK_PACKAGE_VERSION_MINOR", version.getMinor().toString());
            }
            
            if (version.getPatch() != null)
            {
                appendDashD(arguments, "CPACK_PACKAGE_VERSION_PATCH", version.getPatch().toString());
            }

            if (version.getSuffix() != null)
            {
                appendDashD(arguments, "CPACK_PACKAGE_VERSION_SUFFIX", version.getSuffix());
            }
        }

        if (addCMakePrefixPath)
        {
            StringBuilder prefixPath = new StringBuilder();

            Set artifacts = getProject().getArtifacts();
            if ((artifacts != null) && (!artifacts.isEmpty()))
            {
                for (Object object : artifacts)
                {
                    if (prefixPath.length() > 0)
                    {
                        prefixPath.append(";");
                    }

                    // Note that we can't use the symlink location in Windows - cmake doesn't seem to be able to
                    // traverse it
                    Artifact artifact = (Artifact)object;
                    File file = ArtifactHelper.getRepoExtractDirectory(reactorProjects, artifact);

                    if (file == null)
                    {
                        getLog().warn("Could not determine output directory for " + artifact.toString() + ". Not adding to CMAKE_PREFIX_PATH.");
                        continue;
                    }

                    prefixPath.append(file.getAbsolutePath());
                }
            }

            appendDashD(arguments, "CMAKE_PREFIX_PATH", prefixPath.toString(), true);
        }

        if (addCMakeInstallPrefix)
        {
            appendDashD(arguments, "CMAKE_INSTALL_PREFIX", projectInstallDir.getAbsolutePath(), true);
        }

        if (addCMakeConfigurationTypes && SystemUtils.IS_OS_WINDOWS)
        {
            appendDashD(arguments, "CMAKE_CONFIGURATION_TYPES", getCurrentConfig());
        }

        if (addCMakeBuildType && !SystemUtils.IS_OS_WINDOWS)
        {
            appendDashD(arguments, "CMAKE_BUILD_TYPE", getCurrentConfig());
        }

        // now add any of the ${cmake.vars*} variables.
        if (addAdditionalPredefined)
        {
            StringBuilder additionalArgBuilder = new StringBuilder();
            List<String> otherList = getBuildEnvironment().getAdditionalCMakeVariables();
            for (String var : otherList)
            {
                if (isVerbose())
                {
                    getLog().info("Examining ${" + var + "} for additional CMake options");
                }

                String value = getProject().getProperties().getProperty(var);
                if ((value != null) && (value.length() > 0))
                {
                    if (additionalArgBuilder.length() > 0) additionalArgBuilder.append(" ");
                    additionalArgBuilder.append(value);
                }
            }

            if (additionalArgBuilder.length() > 0)
            {
                arguments.add(additionalArgBuilder.toString());
            }
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
