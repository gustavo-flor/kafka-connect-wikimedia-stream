package com.github.gustavoflor.kafkaconnectwikimediastream;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.source.SourceConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.github.gustavoflor.kafkaconnectwikimediastream.util.VersionUtil.getVersion;
import static org.apache.kafka.common.config.ConfigDef.Importance.HIGH;
import static org.apache.kafka.common.config.ConfigDef.Type.STRING;

public class WikimediaStreamSourceConnector extends SourceConnector {
    private static final Logger LOGGER = LoggerFactory.getLogger(WikimediaStreamSourceConnector.class);
    public static final String WIKIMEDIA_EVENT_CONFIG = "wikimedia.event";
    private static final String WIKIMEDIA_EVENT_DOC = "Name of the event stream from Wikimedia.";
    public static final String TOPIC_CONFIG = "topic";
    private static final String TOPIC_DOC = "Target topic to sent the message";

    private Map<String, String> properties;

    @Override
    public void start(final Map<String, String> props) {
        LOGGER.info("Starting {}", getClass().getSimpleName());
        properties = props;
    }

    @Override
    public Class<? extends Task> taskClass() {
        return WikimediaStreamSourceTask.class;
    }

    @Override
    public List<Map<String, String>> taskConfigs(int maxTasks) {
        return Collections.singletonList(properties);
    }

    @Override
    public void stop() {
        LOGGER.info("Stopping {}", getClass().getSimpleName());
    }

    @Override
    public ConfigDef config() {
        return new ConfigDef()
                .define(WIKIMEDIA_EVENT_CONFIG, STRING, HIGH, WIKIMEDIA_EVENT_DOC)
                .define(TOPIC_CONFIG, STRING, HIGH, TOPIC_DOC);
    }

    @Override
    public String version() {
        return getVersion();
    }
}
