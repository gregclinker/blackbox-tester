package com.essexboy;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@JsonPropertyOrder({"startTime", "finishTime", "loadResults", "testSuite", "results"})
public class TestReport {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final Map<String, TestResults> results = new ConcurrentHashMap<>();
    protected HttpTestSuite testSuite;
    protected Date startTime;
    protected Date finishTime;

    public TestReport(HttpTestSuite testSuite) {
        startTime = Calendar.getInstance().getTime();
        this.testSuite = testSuite;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Map<String, TestResults> getResults() {
        if (testSuite.isLoadTest()) {
            SortedMap<String, TestResults> sortedMap = new TreeMap<>();
            for (HttpTest httpTest : testSuite.getHttpTests()) {
                sortedMap.put(httpTest.getDescription(), results.get(httpTest.getDescription()));
            }
            return sortedMap;
        }
        return null;
    }

    public void add(HttpTest httpTest, HttpTestResult httpTestResult) {
        if (testSuite.isLoadTest()) {
            LoadTestResult loadTestResult = new LoadTestResult(httpTest.getDescription(), httpTest.isGood(httpTestResult), httpTestResult.getExecutionTime());
            if (results.get(loadTestResult.getTestKey()) == null) {
                results.put(loadTestResult.getTestKey(), new TestResults());
            }
            results.get(loadTestResult.getTestKey()).add(loadTestResult);
        }
    }

    public void report() throws IOException {
        finishTime = Calendar.getInstance().getTime();
        ObjectMapper objectMapper = new ObjectMapper();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd:HH:mm");
        objectMapper.setDateFormat(df);
        System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(this));
    }
}
