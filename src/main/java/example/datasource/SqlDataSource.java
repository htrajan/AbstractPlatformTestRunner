package example.datasource;

import example.caseobj.TestCase;

import java.util.Collections;
import java.util.List;

public class SqlDataSource implements TestCaseDataSource {

    @Override
    public List<TestCase> getTestCases() {
        // read from JDC connection, etc
        return Collections.emptyList();
    }

}
