package com.essexboy;

import lombok.Setter;
import lombok.SneakyThrows;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Setter
public class BlackBoxTest extends BBHttpClient implements Runnable {

    private Logger logger;

    private HttpTestSuite httpTestSuite;
    private TestReport testReport;

    public BlackBoxTest(HttpTestSuite httpTestSuite, TestReport testReport, int id) throws Exception {
        this.httpTestSuite = httpTestSuite;
        this.testReport = testReport;
        this.id = id;
        getHttpClient();
        logger = LoggerFactory.getLogger(BlackBoxTest.class.getSimpleName() + id);
    }

    @SneakyThrows
    public void run() {
        int count = 0;
        for (int i = 0; i < httpTestSuite.getRepeat(); i++) {
            for (HttpTest httpTest : httpTestSuite.getHttpTests()) {
                HttpTestResult httpTestResult = null;
                count++;
                if (httpTestSuite.isVerbose()) {
                    System.out.print("\ntread " + id + ", running : " + httpTest.getSummary());
                } else if (count % httpTestSuite.getSlice() == 0 && httpTestSuite.isLoadTest()) {
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
                if (httpTestSuite.isLog()) {
                    logger.debug(httpTest.getSummary() + " in " + httpTestResult.getExecutionTime() + "ms" + ", status=" + httpTestResult.getResponseStatusCode());
                    if (httpTestResult.getResponseStatusCode() != httpTest.getExpected().getHttpStatus()) {
                        logger.error(httpTest + "\n" + httpTestResult);
                    }
                }
                if (httpTestSuite.isLoadTest()) {
                    testReport.add(httpTest, httpTestResult);
                } else {
                    httpTest.setHttpTestResult(httpTestResult);
                }
            }
        }
    }

    private HttpTestResult runTest(HttpTest httpTest) throws Exception {
        HttpRequestBase request;
        List<NameValuePair> params = null;
        switch (httpTest.getMethod()) {
            case "GET":
                request = new HttpGet(httpTest.getUrl());
                break;
            case "POST":
                request = new HttpPost(httpTest.getUrl());
                if (httpTest.getBody() != null) {
                    ((HttpPost) request).setEntity(new StringEntity(httpTest.getBody()));
                }
                break;
            default:
                throw new RuntimeException("invalid method : " + httpTest.getMethod());
        }
        for (Header header : httpTest.getHeaders()) {
            request.addHeader(header);
        }
        log(request, params);
        long start = System.currentTimeMillis();
        HttpResponse response = execute(request);
        long finish = System.currentTimeMillis();
        String responseBody = null;
        try {
            responseBody = EntityUtils.toString(response.getEntity());
        } catch (Exception e) {
            //do nothing
        }
        HttpTestResult httpTestResult = new HttpTestResult();
        httpTestResult.setExecutionTime(finish - start);
        httpTestResult.setResponseStatusCode(response.getStatusLine().getStatusCode());
        log(responseBody);
        httpTestResult.setResponseBody(responseBody);
        return httpTestResult;
    }
}
