package com.essexboy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Getter
public class BlackBoxTestRunner {

    final static Logger logger = LoggerFactory.getLogger(BlackBoxTestRunner.class);

    private final HttpTestSuite httpTestSuite;
    private final TestReport testReport;
    private final BBHttpClient bbHttpClient = new BBHttpClient();
    private String inputFile;

    public BlackBoxTestRunner(String inputFile) throws Exception {
        this(new FileInputStream(inputFile));
        this.inputFile = inputFile;
    }

    public BlackBoxTestRunner(InputStream inputStream) throws Exception {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER));
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        httpTestSuite = mapper.readValue(inputStream, HttpTestSuite.class);
        testReport = new TestReport(httpTestSuite);
        bbHttpClient.setVerbose(httpTestSuite.isVerbose());
    }

    public void runReport() throws IOException {
        testReport.report();
    }

    public void runTests() throws Exception {
        System.out.print(httpTestSuite.getSummary() + "\n");
        logger.debug(httpTestSuite.getSummary());
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < httpTestSuite.getThreads(); i++) {
            System.out.print("starting thread " + i + ", Running " + httpTestSuite.getHttpTests().size() + " tests\n");
            BlackBoxTest blackBoxTest = new BlackBoxTest(httpTestSuite, testReport, i);
            Thread thread = new Thread(blackBoxTest);
            threads.add(thread);
        }
        if (httpTestSuite.isLoadTest()) {
            System.out.println(httpTestSuite.getProgressBar());
        }
        for (Thread thread : threads) {
            thread.start();
        }
        for (Thread thread : threads) {
            thread.join();
        }
        System.out.println("\nfinished all threads");
    }
}
