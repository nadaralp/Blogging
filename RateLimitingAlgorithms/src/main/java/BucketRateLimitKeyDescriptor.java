import java.time.Instant;

public class BucketRateLimitKeyDescriptor {
    private final String key;
    private int availableCapacity;
    private int refillsPerInterval;
    private Instant lastAccessedTime;

    public BucketRateLimitKeyDescriptor(String key, int availableCapacity, int refillsPerInterval, Instant lastAccessedTime) {
        this.key = key;
        this.availableCapacity = availableCapacity;
        this.refillsPerInterval = refillsPerInterval;
        this.lastAccessedTime = lastAccessedTime;
    }

    public String getKey() {
        return key;
    }

    public int getAvailableCapacity() {
        return availableCapacity;
    }

    public void setAvailableCapacity(int availableCapacity) {
        this.availableCapacity = availableCapacity;
    }

    public void decrementAvailableCapacity() {
        this.availableCapacity--;
    }


    public int getRefillsPerInterval() {
        return refillsPerInterval;
    }

    public Instant getLastAccessedTime() {
        return lastAccessedTime;
    }

    public void setLastAccessedTime(Instant lastAccessedTime) {
        this.lastAccessedTime = lastAccessedTime;
    }
}
