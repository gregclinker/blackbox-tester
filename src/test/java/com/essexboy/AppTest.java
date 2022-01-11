package com.essexboy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AppTest {

    private static final Logger logger = LoggerFactory.getLogger(BlackBoxTestRunner.class);

    @Test
    public void test1() throws Exception {
        BlackBoxTestRunner blackBoxTestRunner = new BlackBoxTestRunner(getClass().getResourceAsStream("/testInput2.yaml"));
        blackBoxTestRunner.runTests();
        HttpTestSuite httpTestSuite = blackBoxTestRunner.getHttpTestSuite();
        assertTrue(httpTestSuite.isVerbose());
        HttpTest httpTest1 = httpTestSuite.getHttpTests().get(0);
        assertEquals(200, httpTest1.getHttpTestResult().getResponseStatusCode());
        assertTrue(httpTest1.isGood());
    }
}


