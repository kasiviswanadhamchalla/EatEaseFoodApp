package com.eatease.order;

import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectPackages("com.eatease.order")
class OrderServiceApplicationTest {
    static {
        System.setProperty("net.bytebuddy.experimental", "true");
    }
}
