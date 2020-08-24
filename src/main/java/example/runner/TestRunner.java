package example.runner;

import example.datasource.TestCaseDataSource;
import example.datasource.XlsDataSource;
import example.platform.IosPlatform;
import example.platform.Platform;
import example.platform.WebPlatform;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static example.platform.Platform.DeviceType.SIMULATOR;

public class TestRunner {

    private static List<Platform> platforms;

    static {
        try {
            platforms = Collections.singletonList(/*new IosPlatform(SIMULATOR)*/ new WebPlatform());
        } catch (IOException e) {
            e.printStackTrace();
            platforms = Collections.emptyList();
        }
    }

    private final TestCaseDataSource dataSource;

    private TestRunner(TestCaseDataSource dataSource) {
        this.dataSource = dataSource;
    }

    private void runTests() {
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
        TestRunner runner = new TestRunner(new XlsDataSource("loginWeb.csv"));
        runner.runTests();
    }
}
