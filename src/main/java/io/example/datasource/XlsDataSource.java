package io.example.datasource;

import io.example.caseobj.TestCase;
import io.example.caseobj.TestCase.TestAction;
import io.example.caseobj.TestCase.TestAction.Keyword;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class XlsDataSource implements TestCaseDataSource {

    private final String fileName;

    public XlsDataSource(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public List<TestCase> getTestCases() {
        // map csv file to test cases
        List<TestCase> testCases = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line = reader.readLine();
            while (line != null) {
                if (!line.startsWith("#")) {
                    List<TestAction> actions = new ArrayList<>();
                    TestCase testCase = new TestCase(line, actions);
                    line = reader.readLine();
                    while (line != null && line.contains(",")) {
                        if (!line.startsWith("#")) {
                            String[] columns = line.split(",");
                            List<String> params = new ArrayList<>(Arrays.asList(columns).subList(1, columns.length));
                            actions.add(new TestAction(Keyword.valueOf(columns[0]), params));
                        }
                        line = reader.readLine();
                    }
                    testCases.add(testCase);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return testCases;
    }

}
