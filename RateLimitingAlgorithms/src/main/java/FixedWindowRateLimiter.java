import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class FixedWindowRateLimiter implements RateLimiter {
    private final int rateLimit;

    private final long windowMillisecondsInterval;

    private final TimestampProvider timestampProvider;

    private final Map<String, WindowRateLimitKeyDescriptor> rateLimitKeyDescriptorMap = new HashMap<>();

    public FixedWindowRateLimiter(int rateLimit, long windowMillisecondsInterval, TimestampProvider timestampProvider) {
        this.rateLimit = rateLimit;
        this.windowMillisecondsInterval = windowMillisecondsInterval;
        this.timestampProvider = timestampProvider;
    }

    public boolean isRequestAllowed(String key) {
        if (!rateLimitKeyDescriptorMap.containsKey(key)) {
            insertRateLimitDescriptor(key);
        }

        WindowRateLimitKeyDescriptor rateLimitKeyDescriptor = rateLimitKeyDescriptorMap.get(key);
        Instant currentTimestamp = timestampProvider.get();
        if(isWindowExpired(rateLimitKeyDescriptor, currentTimestamp)) {
            resetKeyDescriptorUsage(rateLimitKeyDescriptor, currentTimestamp);
        }

        rateLimitKeyDescriptor.incrementRateLimitCapacityUsed();
        return rateLimitKeyDescriptor.getRateLimitCapacityUsed() <= rateLimit;
    }

    private void insertRateLimitDescriptor(String key) {
        rateLimitKeyDescriptorMap.put(key, new WindowRateLimitKeyDescriptor(key, 0, timestampProvider.get()));
    }

    private boolean isWindowExpired(WindowRateLimitKeyDescriptor rateLimitKeyDescriptor, Instant currentTimestamp) {
        return currentTimestamp.toEpochMilli() - rateLimitKeyDescriptor.getLastWindowTimestamp().toEpochMilli() > windowMillisecondsInterval;
    }

    private void resetKeyDescriptorUsage(WindowRateLimitKeyDescriptor rateLimitKeyDescriptor, Instant currentTimestamp) {
        rateLimitKeyDescriptor.setRateLimitCapacityUsed(0);
        rateLimitKeyDescriptor.setLastWindowTimestamp(currentTimestamp);
    }
}
