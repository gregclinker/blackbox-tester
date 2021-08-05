package com.essexboy;

import lombok.Getter;
import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.*;

@Getter
public class BlackBoxTestRunner {

    final static Logger logger = LoggerFactory.getLogger(BlackBoxTestRunner.class);

    private HttpTestSuite httpTestSuite;
    private String inputFile;
    private TestReport testReport;
    private BBHttpClient bbHttpClient = new BBHttpClient();

    public BlackBoxTestRunner(String inputFile) throws Exception {
        this(new FileInputStream(inputFile));
        this.inputFile = inputFile;
    }

    public BlackBoxTestRunner(InputStream inputStream) throws Exception {
        String jsonInput = IOUtils.toString(inputStream, Charset.defaultCharset());
        httpTestSuite = new ObjectMapper().readValue(jsonInput, HttpTestSuite.class);
        testReport = new TestReport(httpTestSuite);
        bbHttpClient.setVerbose(httpTestSuite.isVerbose());
    }

    public void runReport() throws IOException {
        testReport.report();
    }

    public HttpTestSuite runTests() throws Exception {
        System.out.print(httpTestSuite.getSummary() + "\n");
        Random r = new Random();
        logger.debug(httpTestSuite.getSummary());
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < httpTestSuite.getThreads(); i++) {
            System.out.print("starting thread " + i + ", Running " + httpTestSuite.getHttpTests().size() + " tests\n");
            BlackBoxTest blackBoxTest = new BlackBoxTest(httpTestSuite, testReport, i);
            Thread thread = new Thread(blackBoxTest);
            threads.add(thread);
        }
        System.out.println(httpTestSuite.getProgressBar());
        for (Thread thread : threads) {
            thread.start();
        }
        for (Thread thread : threads) {
            thread.join();
        }
        System.out.println("\nfinished all threads");
        return httpTestSuite;
    }
}
