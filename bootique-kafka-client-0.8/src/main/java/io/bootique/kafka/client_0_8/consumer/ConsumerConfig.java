package io.bootique.kafka.client_0_8.consumer;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ConsumerConfig {

    private long zookeeperSessionTimeoutMs;
    private long zookeeperSyncTimeMs;
    private long autoCommitIntervalMs;
    private OffsetReset autoOffsetReset = OffsetReset.largest;
    private String group;

    public Map<String, String> createConsumerConfig() {

        Map<String, String> props = new HashMap<>();

        props.put("auto.offset.reset", Objects.requireNonNull(autoOffsetReset).name());

        if (group != null) {
            props.put("group.id", group);
        }

        if (zookeeperSessionTimeoutMs > 0) {
            props.put("zookeeper.session.timeout.ms", String.valueOf(zookeeperSessionTimeoutMs));
        }

        if (zookeeperSyncTimeMs > 0) {
            props.put("zookeeper.sync.time.ms", String.valueOf(zookeeperSyncTimeMs));
        }

        if (autoCommitIntervalMs > 0) {
            props.put("auto.commit.interval.ms", String.valueOf(autoCommitIntervalMs));
        }

        return props;
    }

    public void setAutoOffsetReset(OffsetReset autoOffsetReset) {
        this.autoOffsetReset = Objects.requireNonNull(autoOffsetReset);
    }

    public void setZookeeperSessionTimeoutMs(long zookeeperSessionTimeoutMs) {
        this.zookeeperSessionTimeoutMs = zookeeperSessionTimeoutMs;
    }

    public void setZookeeperSyncTimeMs(long zookeeperSyncTimeMs) {
        this.zookeeperSyncTimeMs = zookeeperSyncTimeMs;
    }

    public void setAutoCommitIntervalMs(long autoCommitIntervalMs) {
        this.autoCommitIntervalMs = autoCommitIntervalMs;
    }

    public void setGroup(String group) {
        this.group = group;
    }

}
