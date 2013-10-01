#include "artidep.h"
#include "gtest/gtest.h"

TEST(add2_test, with2)
{
    int x = add2(2);
    EXPECT_EQ(x, 4);
}


TEST(add2_test, with4)
{
    int x = add2(4);
    EXPECT_EQ(x, 6);
}

