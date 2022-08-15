package com.github.gustavoflor.kafkaconnectwikimediastream;

import com.github.gustavoflor.kafkaconnectwikimediastream.exception.RequiredConfigException;
import com.launchdarkly.eventsource.EventSource;
import com.launchdarkly.eventsource.MessageEvent;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static com.github.gustavoflor.kafkaconnectwikimediastream.WikimediaStreamSourceConnector.TOPIC_CONFIG;
import static com.github.gustavoflor.kafkaconnectwikimediastream.WikimediaStreamSourceConnector.WIKIMEDIA_EVENT_CONFIG;
import static com.github.gustavoflor.kafkaconnectwikimediastream.util.VersionUtil.getVersion;
import static java.util.Collections.singletonMap;
import static java.util.Optional.ofNullable;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.apache.kafka.connect.data.Schema.STRING_SCHEMA;

public class WikimediaStreamSourceTask extends SourceTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(WikimediaStreamSourceConnector.class);
    private static final String WIKIMEDIA_STREAM_URL_PATTERN = "https://stream.wikimedia.org/v2/stream/%s";
    private static final String OFFSET_PROPERTY_NAME = "offset";
    
    private Map<String, String> properties;
    private EventSource eventSource;
    private BlockingQueue<SourceRecord> queue;
    private String topic;

    @Override
    public void start(final Map<String, String> props) {
        LOGGER.info("Starting {}", getClass().getSimpleName());
        properties = props;
        queue = new LinkedBlockingQueue<>();
        topic = getTopic();
        eventSource = getEventSource();
        eventSource.start();
    }

    @Override
    public synchronized List<SourceRecord> poll() throws InterruptedException {
        LOGGER.debug("Polling {}", getClass().getSimpleName());
        List<SourceRecord> records = new ArrayList<>();
        SourceRecord event = queue.poll(1L, SECONDS);
        if (event == null) {
            return records;
        }
        records.add(event);
        queue.drainTo(records);
        return records;
    }

    @Override
    public void stop() {
        LOGGER.info("Stopping {}", getClass().getSimpleName());
        eventSource.close();
    }

    @Override
    public String version() {
        return getVersion();
    }

    private String getTopic() {
        final var value = properties.get(TOPIC_CONFIG);
        return ofNullable(value).orElseThrow(() -> new RequiredConfigException(TOPIC_CONFIG));
    }

    private String getWikimediaEvent() {
        final var value = properties.get(WIKIMEDIA_EVENT_CONFIG);
        return ofNullable(value).orElseThrow(() -> new RequiredConfigException(WIKIMEDIA_EVENT_CONFIG));
    }
    
    private URI getWikimediaEventStreamURI() {
        return URI.create(String.format(WIKIMEDIA_STREAM_URL_PATTERN, getWikimediaEvent()));
    }

    private EventSource getEventSource() {
        return new EventSource.Builder(getEventHandler(), getWikimediaEventStreamURI()).build();
    }

    private WikimediaStreamEventHandler getEventHandler() {
        return (String event, MessageEvent messageEvent) -> {
            final var sourcePartition = singletonMap(WIKIMEDIA_EVENT_CONFIG, getWikimediaEvent());
            final var sourceOffset = singletonMap(OFFSET_PROPERTY_NAME, 0);
            final var data = messageEvent.getData();
            LOGGER.debug("Message received with data = {}", data);
            queue.add(new SourceRecord(sourcePartition, sourceOffset, topic, STRING_SCHEMA, data));
        };
    }
}
