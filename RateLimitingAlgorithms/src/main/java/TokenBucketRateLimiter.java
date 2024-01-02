import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class TokenBucketRateLimiter implements RateLimiter {
    final private Map<String, BucketRateLimitKeyDescriptor> rateLimitKeyDescriptorMap = new HashMap<>();
    private final int rateLimit;
    private final int refillsPerInterval;
    private final long refillIntervalMilliseconds;
    private final TimestampProvider timestampProvider;

    public TokenBucketRateLimiter(int rateLimit, int refillsPerInterval, long refillIntervalMilliseconds, TimestampProvider timestampProvider) {
        this.rateLimit = rateLimit;
        this.refillsPerInterval = refillsPerInterval;
        this.refillIntervalMilliseconds = refillIntervalMilliseconds;
        this.timestampProvider = timestampProvider;
    }

    @Override
    public boolean isRequestAllowed(String key) {
        if (!rateLimitKeyDescriptorMap.containsKey(key)) {
            insertRateLimitDescriptor(key);
        }

        BucketRateLimitKeyDescriptor rateLimitKeyDescriptor = rateLimitKeyDescriptorMap.get(key);
        Instant currentTimestamp = timestampProvider.get();

        refillBucket(rateLimitKeyDescriptor, currentTimestamp);
        if (rateLimitKeyDescriptor.getAvailableCapacity() == 0) {
            return false;
        }

        rateLimitKeyDescriptor.decrementAvailableCapacity();
        return true;
    }

    private int getRefillsIntervals(BucketRateLimitKeyDescriptor rateLimitKeyDescriptor, Instant currentTimestamp) {
        return (int) ((currentTimestamp.toEpochMilli() - rateLimitKeyDescriptor.getLastAccessedTime().toEpochMilli()) / refillIntervalMilliseconds);
    }

    private void refillBucket(BucketRateLimitKeyDescriptor rateLimitKeyDescriptor, Instant currentTimestamp) {
        int refillsIntervals = getRefillsIntervals(rateLimitKeyDescriptor, currentTimestamp);
        rateLimitKeyDescriptor.setAvailableCapacity(Math.min(rateLimitKeyDescriptor.getAvailableCapacity() + (refillsIntervals * rateLimitKeyDescriptor.getRefillsPerInterval()), rateLimit));
        if (refillsIntervals > 0) {
            rateLimitKeyDescriptor.setLastAccessedTime(currentTimestamp);
        }
    }

    private void insertRateLimitDescriptor(String key) {
        rateLimitKeyDescriptorMap.put(key, new BucketRateLimitKeyDescriptor(key, rateLimit, refillsPerInterval, timestampProvider.get()));
    }

}
