package io.example.datasource;

import io.example.caseobj.TestCase;

import java.util.Collections;
import java.util.List;

public class SqlDataSource implements TestCaseDataSource {

    @Override
    public List<TestCase> getTestCases() {
        // read from JDC connection, etc
        return Collections.emptyList();
    }

}
