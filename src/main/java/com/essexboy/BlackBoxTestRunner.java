package com.essexboy;

import lombok.Getter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class BlackBoxTestRunner {

    final static Logger logger = LoggerFactory.getLogger(BlackBoxTestRunner.class);

    private HttpTestSuite httpTestSuite;
    private String inputFile;
    private Map<String, List<LoadTestResult>> loadResults = new ConcurrentHashMap<>();
    private BBAPIClient alRayanAPIClient;

    private Date loadTestStart;
    private int accessTokenReUseCount = 0;

    public BlackBoxTestRunner(String inputFile) throws Exception {
        this(new FileInputStream(inputFile));
        this.inputFile = inputFile;
    }

    public BlackBoxTestRunner(InputStream inputStream) throws Exception {
        String jsonInput = IOUtils.toString(inputStream, Charset.defaultCharset());
        httpTestSuite = new ObjectMapper().readValue(jsonInput, HttpTestSuite.class);
        for (BBNameValuePair nameValuePair : httpTestSuite.getContstants()) {
            if (nameValuePair.getName().equals("accessToken")) {
                httpTestSuite.setAccessToken(nameValuePair.getValue());
            }
        }
        alRayanAPIClient = new BBAPIClient(httpTestSuite.getGatewayAlrayanbank(), httpTestSuite.getClientId(), httpTestSuite.getClientSecret(), httpTestSuite.getRedirectUrl(), httpTestSuite.getTokenLifeTimeSeconds());
        alRayanAPIClient.setVerbose(httpTestSuite.isVerbose());
        if (httpTestSuite.getGatewayAlrayanbank().contains("dgateway")) {
            alRayanAPIClient.production = false;
        }
        for (HttpTest httpTest : httpTestSuite.getHttpTests()) {
            httpTest.setUrl(httpTest.getUrl().replaceAll("\\$\\{gatewayAlrayanbank\\}", httpTestSuite.getGatewayAlrayanbank()));
            for (BBNameValuePair constant : httpTestSuite.getContstants()) {
                httpTest.setUrl(httpTest.getUrl().replaceAll("\\$\\{" + constant.getName() + "\\}", constant.getValue()));
            }
        }
    }

    private void resetAccessToken() throws Exception {
        if (httpTestSuite.getRefreshToken() != null) {
            String token = alRayanAPIClient.getToken(httpTestSuite.getRefreshToken());
            httpTestSuite.setAccessToken(token);
        }
    }

    public void runReport() {
        if (isLoadTest()) {
            return;
        }
        int passed = 0;
        int failed = 0;
        long totalExecutionTime = 0;
        long averageExecutionTime = 0;
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(Inclusion.NON_NULL);
        objectMapper.setSerializationInclusion(Inclusion.NON_EMPTY);
        for (HttpTest httpTest : httpTestSuite.getHttpTests()) {
            if (httpTest.isGood()) {
                System.out.println(httpTest.getSummary() + " in " + httpTest.getHttpTestResult().getExecutionTime() + "ms" + " - OK");
                totalExecutionTime += httpTest.getHttpTestResult().getExecutionTime();
                passed++;
            }
        }
        System.out.println("\n");
        for (HttpTest httpTest : httpTestSuite.getHttpTests()) {
            if (!httpTest.isGood()) {
                String asString = null;
                try {
                    asString = objectMapper.writeValueAsString(httpTest);
                } catch (IOException e) {
                    asString = httpTest.toString();
                }
                System.out.println(httpTest.getDescription() + " - FAILED\n" + asString + "\n");
                failed++;
            }
        }
        int total = failed + passed;
        if (passed > 0) {
            averageExecutionTime = totalExecutionTime / passed;
        }
        System.out.println("ran=" + total + ", passed=" + passed + "(aveage execution time=" + averageExecutionTime + "ms)" + ", failed=" + failed);
    }

    public void runLoadReport() throws IOException {
        if (!isLoadTest()) {
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        Date endDate = Calendar.getInstance().getTime();
        long elapsed = (endDate.getTime() - loadTestStart.getTime()) / (1000 * 60);
        stringBuilder.append("test started at " + loadTestStart + ", test ended at " + endDate + ", ran for " + elapsed + " mins\n");
        long overAllFailed = 0;
        long overAllPassed = 0;
        long overAllTotalTests = 0;
        for (String testKey : loadResults.keySet()) {
            long failed = 0;
            long passed = 0;
            long totalTests = 0;
            long minTime = 1000000;
            long maxTime = 0;
            long executionTime = 0;
            long totalExecutionTime = 0;
            long averageTime = 0;
            long percentagePass = 0;
            long percentageFail = 0;
            long greaterThan1000ms = 0;
            long greaterThan500ms = 0;
            long greaterThan0ms = 0;
            long percentageGreaterThan1000ms = 0;
            long percentageGreaterThan500ms = 0;
            long percentageGreaterThan0ms = 0;
            for (LoadTestResult loadTestResult : loadResults.get(testKey)) {
                totalTests++;
                overAllTotalTests++;
                executionTime = loadTestResult.getExecutionTime();
                totalExecutionTime += executionTime;
                if (executionTime < minTime) {
                    minTime = executionTime;
                }
                if (executionTime > maxTime) {
                    maxTime = executionTime;
                }
                if (loadTestResult.isPassed()) {
                    passed++;
                    overAllPassed++;
                } else {
                    failed++;
                    overAllFailed++;
                }
                if (executionTime > 1000l) {
                    greaterThan1000ms++;
                } else if (executionTime > 500l) {
                    greaterThan500ms++;
                } else {
                    greaterThan0ms++;
                }
            }
            if (totalTests > 0) {
                percentagePass = (long) (((float) passed / (float) totalTests) * 100);
                percentageFail = (long) (((float) failed / (float) totalTests) * 100);
                averageTime = (long) ((float) totalExecutionTime / (float) totalTests);
                percentageGreaterThan1000ms = (long) (((float) greaterThan1000ms / (float) totalTests) * 100);
                percentageGreaterThan500ms = (long) (((float) greaterThan500ms / (float) totalTests) * 100);
                percentageGreaterThan0ms = (long) (((float) greaterThan0ms / (float) totalTests) * 100);
            }
            stringBuilder.append(testKey + " ran=" + totalTests + ", passed=" + passed + "(" + percentagePass + "%), failed=" + failed + "(" + percentageFail + "%), min=" + minTime + "ms, max=" + maxTime + "ms, average=" + averageTime + "ms");
            stringBuilder.append(", >1000ms=" + greaterThan1000ms + "(" + percentageGreaterThan1000ms + "%), >500ms=" + greaterThan500ms + "(" + percentageGreaterThan500ms + "%), <500ms=" + greaterThan0ms + "(" + percentageGreaterThan0ms + "%)");
            stringBuilder.append("\n");
        }
        if (overAllTotalTests > 0) {
            long overAllPercentagePass = (long) (((float) overAllPassed / (float) overAllTotalTests) * 100);
            long overAllPercentageFail = (long) (((float) overAllFailed / (float) overAllTotalTests) * 100);
            stringBuilder.append("total test ran=" + overAllTotalTests + ", passed=" + overAllPassed + "(" + overAllPercentagePass + "%), failed=" + overAllFailed + "(" + overAllPercentageFail + "%)\n");
        }
        System.out.println("\n" + stringBuilder.toString());
        if (httpTestSuite.getDescription() != null) {
            FileUtils.writeStringToFile(new File(httpTestSuite.getDescription().replaceAll("\\s+", "") + ".out"), httpTestSuite.getSummary() + "\n\n" + stringBuilder.toString(), Charset.defaultCharset());
        }
    }

    public HttpTestSuite runTests() throws Exception {
        System.out.print(httpTestSuite.getSummary());
        resetAccessToken();
        Random r = new Random();
        loadTestStart = Calendar.getInstance().getTime();
        logger.debug(httpTestSuite.getSummary());
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < httpTestSuite.getThreads(); i++) {
            if (isLoadTest()) {
                Thread.sleep((long) (r.nextInt(6) * 1000));
            }
            System.out.print("\nstarting thread " + i + ", Running " + httpTestSuite.getHttpTests().size() + " tests ");
            BlackBoxTest blackBoxTest = new BlackBoxTest(httpTestSuite, loadResults, i);
            Thread thread = new Thread(blackBoxTest);
            threads.add(thread);
            thread.start();
        }
        for (Thread thread : threads) {
            thread.join();
        }
        System.out.println("\nfinished all threads");
        return httpTestSuite;
    }

    private boolean isLoadTest() {
        return httpTestSuite.getRepeat() > 1 || httpTestSuite.getThreads() > 1;
    }

    public void getConsentUrl() throws Exception {
        System.out.println("consentUrl=" + alRayanAPIClient.getConsentUrl());
    }

    public void getRefreshToken(String code) throws Exception {
        String refreshToken = alRayanAPIClient.getRefreshToken(code);
        System.out.println("\nrefresh token=" + refreshToken);
        changeRefreshToken(refreshToken);
    }

    public void changeRefreshToken(String refreshToken) throws IOException {
        String jsonInput = IOUtils.toString(new FileInputStream(inputFile), Charset.defaultCharset());
        String newJson = jsonInput.replaceAll("\"refreshToken\": \".*\"", "\"refreshToken\": \"" + refreshToken + "\"");
        FileUtils.writeStringToFile(new File(inputFile), newJson, Charset.defaultCharset());
    }

    public void getToken() throws Exception {
        System.out.println("\naccess token=" + alRayanAPIClient.getToken(httpTestSuite.getRefreshToken()));
    }
}
