package example.runner;

import example.datasource.TestCaseDataSource;
import example.datasource.XlsDataSource;
import example.platform.IosPlatform;
import example.platform.Platform;
import example.repository.PropertyReader;

import java.util.List;

import static example.platform.Platform.DeviceType.SIMULATOR;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

public class TestRunner {

    private final List<Platform> platforms;

    private final TestCaseDataSource dataSource;

    private TestRunner(TestCaseDataSource dataSource, List<Platform> platforms) {
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
            platforms = singletonList(new IosPlatform(
                SIMULATOR, new PropertyReader(singletonList("login"))));
            //platforms = singletonList(new MDotWebPlatform());
            //platforms = ImmutableList.of(new MDotWebPlatform(), new WebPlatform());
        } catch (Exception e) {
            e.printStackTrace();
            platforms = emptyList();
        }
        TestRunner runner = new TestRunner(new XlsDataSource(args[0]), platforms);
        runner.runTests();
    }
}
