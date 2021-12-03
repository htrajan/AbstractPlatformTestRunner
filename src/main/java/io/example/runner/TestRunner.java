package io.example.runner;

import io.example.datasource.TestCaseDataSource;
import io.example.datasource.XlsDataSource;
import io.example.platform.IosPlatform;
import io.example.platform.Platform;
import io.example.platform.WebPlatform;
import io.example.repository.PropertyReader;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

/**
 * Copyright 1/1/2021 KT Rajan
 * All rights reserved
 */

public class TestRunner {

    private static final String COPYRIGHT = "Copyright 1/1/2021 KT Rajan. All rights reserved";

    private final List<Platform> platforms;

    private final TestCaseDataSource dataSource;

    public TestRunner(TestCaseDataSource dataSource, List<Platform> platforms) {
        this.dataSource = dataSource;
        this.platforms = platforms;
    }

    public void runTests() {
        platforms.forEach(platform -> {
            System.out.println("Platform: " + platform.getPlatformName());
            System.out.println("-------------------------------------");
            dataSource.getTestCases().forEach(testCase -> {
                System.out.println("Test case: " + testCase.getName());
                System.out.println("-------------------------------------");
                try {
                    testCase.getActions().forEach(action -> {
                        System.out.println(action);
                        platform.executeTestAction(action);
                    });
                } finally {
                    try {
                        platform.cleanUp();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("-------------------------------------");
            });
        });
    }

    public static void main(String[] args) {
        List<Platform> platforms;
        try {
//            platforms = singletonList(new IosPlatform(
//                Platform.DeviceType.SIMULATOR, new PropertyReader(singletonList("login"))));
            //platforms = singletonList(new MDotWebPlatform());
            platforms = List.of(new WebPlatform());
        } catch (Exception e) {
            e.printStackTrace();
            platforms = emptyList();
        }
        TestRunner runner = new TestRunner(new XlsDataSource(args[0]), platforms);
        runner.runTests();
    }
}
