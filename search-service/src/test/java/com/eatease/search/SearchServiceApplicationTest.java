package com.eatease.search;

import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectPackages("com.eatease.search")
class SearchServiceApplicationTest {
    static {
        System.setProperty("net.bytebuddy.experimental", "true");
    }
}
