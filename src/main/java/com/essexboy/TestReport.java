package com.essexboy;

import lombok.Getter;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@JsonPropertyOrder({"startTime", "finishTime", "loadResults", "testSuite"})
public class TestReport {
    protected HttpTestSuite testSuite;
    protected Date startTime;
    protected Date finishTime;
    private Map<String, TestResults> results = new ConcurrentHashMap<>();

    public TestReport(HttpTestSuite testSuite) {
        startTime = Calendar.getInstance().getTime();
        this.testSuite = testSuite;
    }

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    public Map<String, TestResults> getResults() {
        SortedMap<String, TestResults> sortedMap = new TreeMap<>();
        for (HttpTest httpTest : testSuite.getHttpTests()) {
            sortedMap.put(httpTest.getDescription(), results.get(httpTest.getDescription()));
        }
        return sortedMap;
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
