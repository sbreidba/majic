package com.sri.vt.majic.util;

import java.io.File;
import org.apache.maven.artifact.Artifact;

public class ArtifactHelper
{
    public static File getRepoExtractDirectory(Artifact artifact)
    {
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
}
