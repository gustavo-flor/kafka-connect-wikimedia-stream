package com.github.gustavoflor.kafkaconnectwikimediastream;

import com.launchdarkly.eventsource.EventHandler;
import org.apache.kafka.connect.errors.ConnectException;

public interface WikimediaStreamEventHandler extends EventHandler {
    @Override
    default void onOpen() {
    }

    @Override
    default void onClosed() {
    }

    @Override
    default void onComment(String comment) {
    }

    @Override
    default void onError(Throwable throwable) {
        throw new ConnectException(throwable);
    }
}
