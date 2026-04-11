package com.eatease.offer;

import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectPackages("com.eatease.offer")
class OfferServiceApplicationTest {
    static {
        System.setProperty("net.bytebuddy.experimental", "true");
    }
}
