package com.essexboy;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class AppTest {

    private static final Logger logger = LoggerFactory.getLogger(BlackBoxTestRunner.class);

    @Test
    public void test0() throws IOException {
        HttpTestSuite httpTestSuite = new HttpTestSuite();
        httpTestSuite.getContstants().add(new BBNameValuePair("name", "value"));
        httpTestSuite.getContstants().add(new BBNameValuePair("name", "value"));
        new ObjectMapper().writeValueAsString(httpTestSuite);
    }

    @Test
    public void test1() throws IOException {
        List<HttpTest> httpTestList = new ArrayList<>();
        HttpTest httpTest = new HttpTest();
        httpTest.setMethod("GET");
        httpTest.setUrl("http://wso2dev02.bank.local:9081/testlogsummary/search/findFirst2ByTestSetUpIdOrderByTestStartTimeDesc?testSetUpId=4");
        httpTest.setExpected(new Expected());
        httpTest.getExpected().setHttpStatus(200);
        httpTest.getExpected().getContains().add("testFunctionURL");
        httpTest.getExpected().getContains().add("testAccountNo");
        httpTest.getHeaders().add(new HttpTestHeader("key", "value"));
        httpTestList.add(httpTest);
    }

    @Test
    public void test2() throws Exception {

        HttpTestSuite httpTestSuite = new HttpTestSuite();
        HttpTest httpTest2 = new HttpTest();
        httpTest2.setUrl("http://wso2dev02.bank.local:9081/testlogsummary/search/findFirst2ByTestSetUpIdOrderByTestStartTimeDesc?testSetUpId=4");
        httpTest2.getHeaders().add(new HttpTestHeader("header1", "value1"));
        httpTest2.getHeaders().add(new HttpTestHeader("header2", "value2"));
        httpTest2.setMethod("GET");
        Expected expected = httpTest2.getExpected();
        expected.setHttpStatus(200);
        expected.getContains().add("this this");
        expected.getContains().add("and this");
        httpTestSuite.getHttpTests().add(httpTest2);
    }

    @Test
    public void test7() {
        HttpTestSuite httpTestSuite = new HttpTestSuite();

        httpTestSuite.setTokenLifeTime("1m");
        assertEquals(60, httpTestSuite.getTokenLifeTimeSeconds());

        httpTestSuite.setTokenLifeTime("1h");
        assertEquals(60 * 60, httpTestSuite.getTokenLifeTimeSeconds());

        httpTestSuite.setTokenLifeTime("1d");
        assertEquals(24 * 60 * 60, httpTestSuite.getTokenLifeTimeSeconds());

        httpTestSuite.setTokenLifeTime("1w");
        assertEquals(24 * 60 * 60 * 7, httpTestSuite.getTokenLifeTimeSeconds());
    }
}

