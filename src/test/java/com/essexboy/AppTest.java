package com.essexboy;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AppTest {

    private static final Logger logger = LoggerFactory.getLogger(BlackBoxTestRunner.class);

    @Test
    public void test1() throws Exception {
        List<HttpTest> httpTestList = new ArrayList<>();
        HttpTest httpTest = new HttpTest();
        httpTest.setMethod("GET");
        httpTest.setUrl("https://api-3scale-apicast-production.3scale2.apps.ocp-dev-nvan.dev-globalrelay.net:443/test");
        httpTest.setExpected(new Expected());
        httpTest.getExpected().setHttpStatus(200);
        httpTestList.add(httpTest);

        BlackBoxTestRunner blackBoxTestRunner = new BlackBoxTestRunner(getClass().getResourceAsStream("/testInput.json"));
        blackBoxTestRunner.runTests();
        HttpTest httpTest1 = blackBoxTestRunner.getHttpTestSuite().getHttpTests().get(0);
        assertTrue(httpTest1.isGood());

        System.out.println(blackBoxTestRunner.getHttpTestSuite().getSlice());
    }

    @Test
    public void test2() throws Exception {

        BlackBoxTestRunner blackBoxTestRunner = new BlackBoxTestRunner(getClass().getResourceAsStream("/testInput.json"));
        blackBoxTestRunner.runTests();
        HttpTest httpTest1 = blackBoxTestRunner.getHttpTestSuite().getHttpTests().get(0);
        assertTrue(httpTest1.isGood());

        System.out.println(blackBoxTestRunner.getHttpTestSuite().getSlice());
    }

    @Test
    public void test23() throws Exception {
        List<Integer> list = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            list.add(i);
        }

        assertEquals(99, percentile(list, 99).intValue());
        assertEquals(90, percentile(list, 90).intValue());
        assertEquals(89, percentile(list, 89).intValue());
        assertEquals(80, percentile(list, 80).intValue());
        assertEquals(79, percentile(list, 79).intValue());

        list = new ArrayList<>();
        for (int i = 1; i <= 200; i++) {
            list.add(i);
        }

        assertEquals(198, percentile(list, 99).intValue());
        assertEquals(180, percentile(list, 90).intValue());
        assertEquals(160, percentile(list, 80).intValue());
    }

    private Integer percentile(List<Integer> list, Integer percentile) {
        int limit = ((Double) (((double) list.size() / 100.0) * (double) percentile)).intValue();
        System.out.println(limit);
        return list.stream().sorted().limit(limit).max(Integer::compare).get();
    }

    @Test
    public void test3() {
        int v = (99 * 9 * 2) / 100;
        System.out.println("xxxx=" + v);

    }
}


