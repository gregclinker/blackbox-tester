package com.essexboy;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Setter;
import lombok.SneakyThrows;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Setter
public class BlackBoxTest extends BBHttpClient implements Runnable {

    private Logger logger;

    private HttpTestSuite httpTestSuite;
    private Map<String, List<LoadTestResult>> loadResults;

    public BlackBoxTest(HttpTestSuite httpTestSuite, Map<String, List<LoadTestResult>> loadResults, int id) {
        this.httpTestSuite = httpTestSuite;
        this.loadResults = loadResults;
        verbose = httpTestSuite.isVerbose();
        logger = LoggerFactory.getLogger(BlackBoxTest.class.getSimpleName() + id);
    }

    @SneakyThrows
    public void run() {
        getKeyStore(); // warm up the keystore
        int count = 0;
        for (int i = 0; i < httpTestSuite.getRepeat(); i++) {
            for (HttpTest httpTest : httpTestSuite.getHttpTests()) {
                HttpTestResult httpTestResult = null;
                count++;
                if (httpTestSuite.isVerbose()) {
                    System.out.print("\nrunning : " + httpTest.getSummary());
                } else if (count % 100 == 0) {
                    System.out.print(count);
                } else {
                    System.out.print("*");
                }
                try {
                    Thread.sleep(httpTestSuite.getDelay());
                    httpTestResult = runTest(httpTest);
                } catch (Exception e) {
                    if (httpTestResult == null) {
                        httpTestResult = new HttpTestResult();
                    }
                    httpTestResult.setException(e.getMessage());
                }
                if (isLoadTest()) {
                    String testKey = httpTest.getDescription();
                    if (loadResults.get(testKey) == null) {
                        loadResults.put(testKey, Collections.synchronizedList(new ArrayList<>()));
                    }
                    loadResults.get(testKey).add(new LoadTestResult(httpTest.getDescription(), httpTest.isGood(httpTestResult), httpTestResult.getExecutionTime()));
                    logger.debug(httpTest.getSummary() + " in " + httpTestResult.getExecutionTime() + "ms" + ", status=" + httpTestResult.getResponseStatusCode());
                    if (httpTestResult.getResponseStatusCode() != 200) {
                        logger.error(httpTest + "\n" + httpTestResult);
                    }
                } else {
                    httpTest.setHttpTestResult(httpTestResult);
                }
            }
        }
    }

    private boolean isLoadTest() {
        return httpTestSuite.getRepeat() > 1 || httpTestSuite.getThreads() > 1;
    }

    private HttpTestResult runTest(HttpTest httpTest) throws Exception {
        HttpRequestBase request = null;
        List<NameValuePair> params = null;
        switch (httpTest.getMethod()) {
            case "GET":
                request = new HttpGet(httpTest.getUrl());
                break;
            case "POST":
                request = new HttpPost(httpTest.getUrl());
                if (httpTest.getBody() != null) {
                    ((HttpPost) request).setEntity(new StringEntity(httpTest.getBody()));
                } else if (httpTest.getBodyParams().size() > 0) {
                    params = new ArrayList<>(2);
                    for (NameValuePair nameValuePair : httpTest.getBodyParams()) {
                        String value = nameValuePair.getValue();
                        if (nameValuePair.getValue().equalsIgnoreCase("${getSignedJWT}")) {
                        }
                        if (nameValuePair.getValue().equalsIgnoreCase("${accessToken}")) {
                            value = httpTestSuite.getAccessToken();
                        }
                        params.add(new BasicNameValuePair(nameValuePair.getName(), value));
                    }
                    ((HttpPost) request).setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
                }
                break;
            default:
                throw new RuntimeException("invalid method : " + httpTest.getMethod());
        }
        for (Header header : httpTest.getHeaders()) {
            request.addHeader(header.getName(), header.getValue().replaceAll("\\$\\{accessToken\\}", httpTestSuite.getAccessToken()));
        }
        addAccessToken(request, httpTest.isWithToken());
        log(request, params);
        long start = System.currentTimeMillis();
        HttpResponse response = execute(request);
        long finish = System.currentTimeMillis();
        String responseBody = null;
        try {
            responseBody = EntityUtils.toString(response.getEntity());
            responseBody = responseBody.replaceAll("^\\{\\s+", "{").
                    replaceAll("\\s+\\}$", "}".
                            replaceAll("\\r\\s+", "").
                            replaceAll("\\s+:\\s+", ":").
                            replaceAll("\\s+\\{", "{"));
        } catch (Exception e) {
            //do nothing
        }
        HttpTestResult httpTestResult = new HttpTestResult();
        httpTestResult.setExecutionTime(finish - start);
        httpTestResult.setResponseStatusCode(response.getStatusLine().getStatusCode());
        log(responseBody);
        processTags(responseBody);
        httpTestResult.setResponseBody(responseBody);
        return httpTestResult;
    }

    private void addAccessToken(HttpRequestBase request, boolean isWithToken) throws Exception {
        // try and get a token, if it fails, use the last good one
        if (isWithToken) {
            request.setHeader("Authorization", "Bearer " + httpTestSuite.getAccessToken());
            request.setHeader("x-fapi-financial-id", "open-bank");
            //request.setHeader("Accept", "application/json");
            request.setHeader("Content-type", "application/json");
        }
    }

    private void processTags(String responseBody) {
        if (responseBody != null) {
            return;
        }
        try {
            JsonNode jsonNode = new com.fasterxml.jackson.databind.ObjectMapper().readTree(responseBody);
            httpTestSuite.setAccessToken(jsonNode.get("access_token").asText());
            if (verbose) {
                log("setting access_token to " + httpTestSuite.getAccessToken() + " to be used in later tests");
            }
        } catch (Exception e) {
            //do nothing
        }
    }
}
