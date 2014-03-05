import com.sri.vt.majic.util.Version;

import org.testng.Assert;
import org.testng.annotations.Test;

public class TestVersionParse
{
    @Test()
    public void testFullSnapshot()
    {
        Version version = Version.parse("1.20.4-013-SNAPSHOT");

        Assert.assertEquals(version.getCount(), 3);

        Assert.assertNotNull(version.getMajor());
        Assert.assertEquals(version.getMajor(), new Integer(1));

        Assert.assertNotNull(version.getMinor());
        Assert.assertEquals(version.getMinor(), new Integer(20));

        Assert.assertNotNull(version.getPatch());
        Assert.assertEquals(version.getPatch(), new Integer(4));

        Assert.assertNotNull(version.getSuffix());
        Assert.assertEquals(version.getSuffix(), "-013-SNAPSHOT");
    }

    @Test()
    public void testFullRelease()
    {
        Version version = Version.parse("1.20.4-013");

        Assert.assertEquals(version.getCount(), 3);

        Assert.assertNotNull(version.getMajor());
        Assert.assertEquals(version.getMajor(), new Integer(1));

        Assert.assertNotNull(version.getMinor());
        Assert.assertEquals(version.getMinor(), new Integer(20));

        Assert.assertNotNull(version.getPatch());
        Assert.assertEquals(version.getPatch(), new Integer(4));

        Assert.assertNotNull(version.getSuffix());
        Assert.assertEquals(version.getSuffix(), "-013");
    }

    @Test()
    public void testNoPatchSnapshot()
    {
        Version version = Version.parse("1.20-013-SNAPSHOT");

        Assert.assertEquals(version.getCount(), 2);

        Assert.assertNotNull(version.getMajor());
        Assert.assertEquals(version.getMajor(), new Integer(1));

        Assert.assertNotNull(version.getMinor());
        Assert.assertEquals(version.getMinor(), new Integer(20));

        Assert.assertNull(version.getPatch());

        Assert.assertNotNull(version.getSuffix());
        Assert.assertEquals(version.getSuffix(), "-013-SNAPSHOT");
    }

    @Test()
    public void testNoPatchRelease()
    {
        Version version = Version.parse("1.20");

        Assert.assertEquals(version.getCount(), 2);

        Assert.assertNotNull(version.getMajor());
        Assert.assertEquals(version.getMajor(), new Integer(1));

        Assert.assertNotNull(version.getMinor());
        Assert.assertEquals(version.getMinor(), new Integer(20));

        Assert.assertNull(version.getPatch());

        Assert.assertNull(version.getSuffix());
    }

    @Test()
    public void testNoMinorSnapshot()
    {
        Version version = Version.parse("1-013-SNAPSHOT");

        Assert.assertEquals(version.getCount(), 1);

        Assert.assertNotNull(version.getMajor());
        Assert.assertEquals(version.getMajor(), new Integer(1));

        Assert.assertNull(version.getMinor());

        Assert.assertNull(version.getPatch());

        Assert.assertNotNull(version.getSuffix());
        Assert.assertEquals(version.getSuffix(), "-013-SNAPSHOT");
    }

    @Test()
    public void testNoMinorRelease()
    {
        Version version = Version.parse("1");

        Assert.assertEquals(version.getCount(), 1);

        Assert.assertNotNull(version.getMajor());
        Assert.assertEquals(version.getMajor(), new Integer(1));

        Assert.assertNull(version.getMinor());

        Assert.assertNull(version.getPatch());

        Assert.assertNull(version.getSuffix());
    }

    @Test()
    public void testModeratelyMalformed()
    {
        Version version = Version.parse("1.2.A-SNAPSHOT");

        Assert.assertEquals(version.getCount(), 2);

        Assert.assertNotNull(version.getMajor());
        Assert.assertEquals(version.getMajor(), new Integer(1));

        Assert.assertNotNull(version.getMinor());
        Assert.assertEquals(version.getMinor(), new Integer(2));

        Assert.assertNull(version.getPatch());

        Assert.assertNotNull(version.getSuffix());
        Assert.assertEquals(version.getSuffix(), ".A-SNAPSHOT");
    }

    @Test()
    public void testBadlyMalformed()
    {
        Version version = Version.parse("A-1.3.7-SNAPSHOT");

        Assert.assertEquals(version.getCount(), 0);

        Assert.assertNull(version.getMajor());
        Assert.assertNull(version.getMinor());
        Assert.assertNull(version.getPatch());

        Assert.assertNotNull(version.getSuffix());
        Assert.assertEquals(version.getSuffix(), "A-1.3.7-SNAPSHOT");
    }
}
