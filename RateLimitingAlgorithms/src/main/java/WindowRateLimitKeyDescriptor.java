import java.time.Instant;

public class WindowRateLimitKeyDescriptor {
    private final String key;
    private int rateLimitCapacityUsed;
    private Instant lastWindowTimestamp;

    public WindowRateLimitKeyDescriptor(String key, int rateLimitCapacityUsed, Instant lastWindowTimestamp) {
        this.key = key;
        this.rateLimitCapacityUsed = rateLimitCapacityUsed;
        this.lastWindowTimestamp = lastWindowTimestamp;
    }

    public String getKey() {
        return key;
    }

    public int getRateLimitCapacityUsed() {
        return rateLimitCapacityUsed;
    }

    public void setRateLimitCapacityUsed(int rateLimitCapacityUsed) {
        this.rateLimitCapacityUsed = rateLimitCapacityUsed;
    }

    public void incrementRateLimitCapacityUsed() {
        this.rateLimitCapacityUsed++;
    }

    public Instant getLastWindowTimestamp() {
        return lastWindowTimestamp;
    }

    public void setLastWindowTimestamp(Instant lastWindowTimestamp) {
        this.lastWindowTimestamp = lastWindowTimestamp;
    }
}
