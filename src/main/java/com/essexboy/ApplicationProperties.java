package com.essexboy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

public enum ApplicationProperties {
    INSTANCE;

    private static final Logger logger = LoggerFactory.getLogger(ApplicationProperties.class);

    private final Properties properties;

    ApplicationProperties() {
        properties = new Properties();
        try {
            properties.load(getClass().getClassLoader().getResourceAsStream("application.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getDevKeyStoreName() {
        return properties.getProperty("dgateway.keystore.name");
    }

    public String getProdKeyStoreName() {
        return properties.getProperty("gateway.keystore.name");
    }

    public String getKeyStorePassword() {
        return properties.getProperty("keystore.password");
    }

    public String getCertificateAlias() {
        return properties.getProperty("certificate.alias");
    }
}