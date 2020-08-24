package example.runner;

import example.datasource.TestCaseDataSource;
import example.datasource.XlsDataSource;
import example.platform.Platform;
import example.platform.MDotWebPlatform;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class TestRunner {

    private static List<Platform> platforms;

    static {
        try {
            platforms = Collections.singletonList(/*new IosPlatform(SIMULATOR)*/ new MDotWebPlatform());
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
