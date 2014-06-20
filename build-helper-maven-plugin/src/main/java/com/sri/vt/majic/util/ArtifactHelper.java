package com.sri.vt.majic.util;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.StringUtils;

import java.io.File;
import java.util.List;

public class ArtifactHelper
{
    public static File getRepoExtractDirectory(List<MavenProject> reactorArtifacts, Artifact artifact)
    {
        boolean isReactorArtifact = false;
        Artifact reactorArtifact = getArtifactFromReactor(reactorArtifacts, artifact);
        if (reactorArtifact != null)
        {
            isReactorArtifact = true;
            artifact = reactorArtifact;
        }

        if (isReactorArtifact)
        {
            String path = artifact.getFile().getAbsolutePath();
            if (path.endsWith("." + artifact.getType()))
            {
                path = path.substring(0, path.length() - artifact.getType().length() - 1);
            }
            else
            {
                return null;
            }

            return new File(path);
        }


        File outDir = artifact.getFile().getParentFile();
        if ((artifact.getClassifier() != null) && (artifact.getClassifier().length() > 0))
        {
            return new File(outDir, artifact.getClassifier());
        }
        else
        {
            return outDir;
        }
    }

    /**
     * Null-safe compare of two artifacts based on groupId, artifactId, version, type and classifier.
     *
     * @param a the first artifact.
     * @param b the second artifact.
     * @return <code>true</code> if and only if the two artifacts have the same groupId, artifactId, version,
     *         type and classifier.
     */
    public static boolean equals( Artifact a, Artifact b )
    {
        return a == b || !( a == null || b == null )
            && StringUtils.equals(a.getGroupId(), b.getGroupId())
            && StringUtils.equals(a.getArtifactId(), b.getArtifactId())
            && StringUtils.equals(a.getVersion(), b.getVersion())
            && StringUtils.equals(a.getType(), b.getType())
            && StringUtils.equals(a.getClassifier(), b.getClassifier());
    }

    /**
     * Returns <code>true</code> if the artifact has a file.
     *
     * @param artifact the artifact (may be null)
     * @return <code>true</code> if and only if the artifact is non-null and has a file.
     */
    public static boolean hasFile(Artifact artifact)
    {
        return artifact != null && artifact.getFile() != null && artifact.getFile().isFile();
    }

    public static Artifact getArtifactFromReactor(List<MavenProject> reactorProjects, Artifact artifact)
    {
        if (reactorProjects == null)
        {
            return null;
        }

        for (MavenProject mavenProject : reactorProjects)
        {
            // check the main artifact
            if (equals(artifact, mavenProject.getArtifact()) && hasFile(mavenProject.getArtifact()))
            {
                return mavenProject.getArtifact();
            }

            // check any attached artifacts
            for (Artifact attachedArtifact : mavenProject.getAttachedArtifacts())
            {
                if (equals(artifact, attachedArtifact) && hasFile(attachedArtifact))
                {
                    return attachedArtifact;
                }
            }
        }

        return null;
    }
}
