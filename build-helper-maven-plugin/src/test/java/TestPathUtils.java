import com.sri.vt.majic.util.PathUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TestPathUtils
{
    @Test
    public void testEqualPaths()
    {
        String target = "C:\\Foo\\baz";
        String base = "C:\\Foo\\baz";
        Assert.assertEquals(PathUtils.getRelativePath(target, base, "\\"), ".");
    }

    @Test
    public void testEqualPathsTrailingBaseSlash()
    {
        String target = "C:\\Foo\\baz";
        String base = "C:\\Foo\\baz\\";
        Assert.assertEquals(PathUtils.getRelativePath(target, base, "\\"), ".");
    }

    @Test
    public void testEqualPathsTrailingBaseTarget()
    {
        String target = "C:\\Foo\\baz\\";
        String base = "C:\\Foo\\baz";
        Assert.assertEquals(PathUtils.getRelativePath(target, base, "\\"), ".");
    }

    @Test
    public void testGetRelativePathsUnix()
    {
        Assert.assertEquals("stuff/xyz.dat", PathUtils.getRelativePath("/var/data/stuff/xyz.dat", "/var/data/", "/"));
        Assert.assertEquals("../../b/c", PathUtils.getRelativePath("/a/b/c", "/a/x/y/", "/"));
        Assert.assertEquals("../../b/c", PathUtils.getRelativePath("/m/n/o/a/b/c", "/m/n/o/a/x/y/", "/"));
    }

    @Test
    public void testGetRelativePathFileToFile()
    {
        String target = "C:\\Windows\\Boot\\Fonts\\chs_boot.ttf";
        String base = "C:\\Windows\\Speech\\Common\\sapisvr.exe";

        String relPath = PathUtils.getRelativePath(target, base, "\\");
        Assert.assertEquals("..\\..\\Boot\\Fonts\\chs_boot.ttf", relPath);
    }

    @Test
    public void testGetRelativePathDirectoryToFile()
    {
        String target = "C:\\Windows\\Boot\\Fonts\\chs_boot.ttf";
        String base = "C:\\Windows\\Speech\\Common\\";

        String relPath = PathUtils.getRelativePath(target, base, "\\");
        Assert.assertEquals("..\\..\\Boot\\Fonts\\chs_boot.ttf", relPath);
    }

    @Test
    public void testGetRelativePathFileToDirectory()
    {
        String target = "C:\\Windows\\Boot\\Fonts";
        String base = "C:\\Windows\\Speech\\Common\\foo.txt";

        String relPath = PathUtils.getRelativePath(target, base, "\\");
        Assert.assertEquals("..\\..\\Boot\\Fonts", relPath);
    }

    @Test
    public void testGetRelativePathDirectoryToDirectory()
    {
        String target = "C:\\Windows\\Boot\\";
        String base = "C:\\Windows\\Speech\\Common\\";
        String expected = "..\\..\\Boot";

        String relPath = PathUtils.getRelativePath(target, base, "\\");
        Assert.assertEquals(expected, relPath);
    }

    @Test
    public void testGetRelativePathDifferentDriveLetters()
    {
        String target = "D:\\sources\\recovery\\RecEnv.exe";
        String base = "C:\\Java\\workspace\\AcceptanceTests\\Standard test data\\geo\\";

        String relPath = PathUtils.getRelativePath(target, base, "\\");
        Assert.assertEquals(relPath, target);
    }
}
