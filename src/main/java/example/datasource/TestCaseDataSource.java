package example.datasource;

import example.caseobj.TestCase;

import java.io.FileNotFoundException;
import java.util.List;

public interface TestCaseDataSource {

    List<TestCase> getTestCases();

}
