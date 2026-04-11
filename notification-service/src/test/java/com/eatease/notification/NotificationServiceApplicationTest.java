package com.eatease.notification;

import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectPackages("com.eatease.notification")
class NotificationServiceApplicationTest {
    static {
        System.setProperty("net.bytebuddy.experimental", "true");
    }
}
