package com.eatease.restaurant;

import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectPackages("com.eatease.restaurant")
class RestaurantServiceApplicationTest {
    static {
        System.setProperty("net.bytebuddy.experimental", "true");
    }
}
