package com.github.gustavoflor.kafkaconnectwikimediastream.util;

import org.apache.kafka.connect.errors.ConnectException;

import java.io.InputStream;
import java.util.Properties;

public class VersionUtil {
    private static final String CONNECTOR_PROPERTIES_FILE = "/connector.properties";
    private static final Properties CONNECTOR_PROPERTIES;
    private static final String VERSION_PROPERTY_KEY = "version";

    static {
        try (InputStream inputStream = VersionUtil.class.getResourceAsStream(CONNECTOR_PROPERTIES_FILE)) {
            CONNECTOR_PROPERTIES = new Properties();
            CONNECTOR_PROPERTIES.load(inputStream);
        } catch (Exception e) {
            throw new ConnectException(e);
        }
    }

    private VersionUtil() {
    }

    public static String getVersion() {
        return CONNECTOR_PROPERTIES.getProperty(VERSION_PROPERTY_KEY);
    }
}
