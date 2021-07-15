package com.essexboy;

import org.junit.Before;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ClientTest {

    private BBAPIClient bbapiClient;

    @Before
    public void init() {
        bbapiClient.setVerbose(true);
    }
}
