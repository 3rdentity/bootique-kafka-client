package io.bootique.kafka.client_0_8.consumer;

import kafka.consumer.*;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.message.MessageAndMetadata;
import kafka.serializer.Decoder;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.function.BiConsumer;

/**
 * A helper class to consumer a singke topic from a Kafka 0.8 stream.
 */
public class TopicConsumer<K, V> implements AutoCloseable {

    private ConsumerConnector connector;
    private Decoder<K> keyDecoder;
    private Decoder<V> valueDecoder;
    private String topic;
    private int numberOfThreads;

    private TopicConsumer() {
    }

    public static <K, V> Builder<K, V> builder(Decoder<K> keyDecoder, Decoder<V> valueDecoder) {
        return new Builder<>(keyDecoder, valueDecoder);
    }

    public void consume(ExecutorService executorService, BiConsumer<K, V> handler) {
        getStreams().forEach(s -> executorService.submit(() -> {

            for (MessageAndMetadata<K, V> m : s) {
                handler.accept(m.key(), m.message());
            }
        }));
    }

    @Override
    public void close() {
        connector.shutdown();
    }

    protected List<KafkaStream<K, V>> getStreams() {

        Map<String, Integer> topicCountMap = Collections.singletonMap(topic, numberOfThreads);
        Map<String, List<KafkaStream<K, V>>> map = connector
                .createMessageStreams(topicCountMap, keyDecoder, valueDecoder);

        return Objects.requireNonNull(map.get(topic));
    }

    public static class Builder<K, V> {

        private TopicConsumer<K, V> consumer;
        private String configName;
        private String group;

        private Builder(Decoder<K> keyDecoder, Decoder<V> valueDecoder) {
            this.consumer = new TopicConsumer<>();
            this.consumer.keyDecoder = Objects.requireNonNull(keyDecoder);
            this.consumer.valueDecoder = Objects.requireNonNull(valueDecoder);
            this.consumer.numberOfThreads = 2;
        }

        public Builder<K, V> group(String group) {
            this.group = group;
            return this;
        }

        public Builder<K, V> configName(String configName) {
            this.configName = configName;
            return this;
        }

        public Builder<K, V> topic(String topic) {
            this.consumer.topic = topic;
            return this;
        }

        public Builder<K, V> threads(int threads) {
            this.consumer.numberOfThreads = threads;
            return this;
        }

        public TopicConsumer<K, V> build(ConsumerFactory factory) {

            Objects.requireNonNull(group);
            Objects.requireNonNull(consumer.topic);

            ConsumerConfig configOverride = new ConsumerConfig();
            configOverride.setGroup(group);

            consumer.connector = createConnector(factory, configOverride);
            return consumer;
        }

        private ConsumerConnector createConnector(ConsumerFactory factory, ConsumerConfig configOverride) {
            return configName != null
                    ? factory.newConsumerConnector(configName, configOverride)
                    : factory.newConsumerConnector(configOverride);
        }
    }

}