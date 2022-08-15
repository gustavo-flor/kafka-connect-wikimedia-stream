package com.github.gustavoflor.kafkaconnectwikimediastream.exception;

import org.apache.kafka.common.config.ConfigException;

public class RequiredConfigException extends ConfigException {
    public RequiredConfigException(final String name) {
        super("Please fill out the value for configuration " + name);
    }
}
