package io.example.runner;

import io.example.componentfinder.XlsAliasComponentFinder;
import io.example.datasource.TestCaseDataSource;
import io.example.datasource.XlsDataSource;
import io.example.platform.Platform;
import io.example.platform.WebPlatform;

import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;

/**
 * Copyright 1/1/2021 KT Rajan
 * All rights reserved
 */

public class TR {

    private static final String COPYRIGHT = "Copyright 1/1/2021 KT Rajan. All rights reserved";

    private final List<Platform> platforms;
    private final TestCaseDataSource dataSource;
    private Optional<XlsAliasComponentFinder> componentFinder = Optional.empty();

    public TR(TestCaseDataSource dataSource, List<Platform> platforms) {
        this.dataSource = dataSource;
        this.platforms = platforms;
    }

    public TR(TestCaseDataSource dataSource, XlsAliasComponentFinder componentFinder, List<Platform> platforms) {
        this.dataSource = dataSource;
        this.componentFinder = Optional.of(componentFinder);
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
                        platform.executeTestAction(action, componentFinder);
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
        TR runner;
        if (args.length == 1) {
            runner = new TR(new XlsDataSource(args[0]), platforms);
        } else if (args.length == 2) {
            runner = new TR(new XlsDataSource(args[0]), new XlsAliasComponentFinder(args[1]), platforms);
        } else {
            throw new IllegalStateException("TR args must be of length 1 or 2.");
        }
        runner.runTests();
    }
}
