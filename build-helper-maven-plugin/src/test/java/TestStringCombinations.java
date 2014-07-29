import com.sri.vt.majic.util.StringCombinations;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.LinkedHashSet;
import java.util.Set;

public class TestStringCombinations
{
    @Test()
    public void getUniqueVars()
    {
        StringCombinations combinations = new StringCombinations("foo");
        Assert.assertEquals(combinations.getStrings().size(), 1);

        combinations.combineWith("-bar");
        Assert.assertEquals(combinations.getStrings().size(), 2);

        combinations.combineWith("-baz");
        Assert.assertEquals(combinations.getStrings().size(), 4);

        combinations.combineWith("-wahoo");
        Assert.assertEquals(combinations.getStrings().size(), 8);

        Set<String> uniques = new LinkedHashSet<String>(combinations.getStrings());
        Assert.assertEquals(uniques.size(), 8);
    }
}
