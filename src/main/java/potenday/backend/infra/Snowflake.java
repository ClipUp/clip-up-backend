package potenday.backend.infra;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import potenday.backend.application.IdProvider;

import java.util.random.RandomGenerator;

@Component
@Scope("prototype")
class Snowflake implements IdProvider {

    private static final int NODE_ID_BITS = 10;
    private static final int SEQUENCE_BITS = 12;

    private static final long maxNodeId = (1L << NODE_ID_BITS) - 1;
    private static final long maxSequence = (1L << SEQUENCE_BITS) - 1;

    private final long nodeId = RandomGenerator.getDefault().nextLong(maxNodeId + 1);
    // UTC = 2025-01-01T00:00:00Z
    private final long startTimeMillis = 1735689600000L;

    private long lastTimeMillis = startTimeMillis;
    private long sequence = 0L;

    @Override
    public synchronized String nextId() {
        long currentTimeMillis = System.currentTimeMillis();

        if (currentTimeMillis < lastTimeMillis) {
            throw new IllegalStateException("Invalid Time");
        }

        if (currentTimeMillis == lastTimeMillis) {
            sequence = (sequence + 1) & maxSequence;
            if (sequence == 0) {
                currentTimeMillis = waitNextMillis(currentTimeMillis);
            }
        } else {
            sequence = 0;
        }

        lastTimeMillis = currentTimeMillis;

        return String.valueOf(((currentTimeMillis - startTimeMillis) << (NODE_ID_BITS + SEQUENCE_BITS))
            | (nodeId << SEQUENCE_BITS)
            | sequence);
    }

    private long waitNextMillis(long currentTimestamp) {
        while (currentTimestamp <= lastTimeMillis) {
            currentTimestamp = System.currentTimeMillis();
        }
        return currentTimestamp;
    }

}
