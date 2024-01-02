import java.time.Instant;

public interface TimestampProvider {
    Instant get();
}
