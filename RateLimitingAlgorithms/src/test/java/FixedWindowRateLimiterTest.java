import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FixedWindowRateLimiterTest {
    private final TimestampProvider timestampProvider = Mockito.mock(TimestampProvider.class);

    @Test
    void isRequestAllowed_shouldAllowRequest_whenWithinRateLimit() {
        Mockito.when(timestampProvider.get()).thenReturn(Instant.now());
        FixedWindowRateLimiter fixedWindowRateLimiter = new FixedWindowRateLimiter(100, Duration.ofSeconds(1).toMillis(), timestampProvider);
        String rateLimitKey = "user1";

        assertTrue(fixedWindowRateLimiter.isRequestAllowed(rateLimitKey));
        assertTrue(fixedWindowRateLimiter.isRequestAllowed(rateLimitKey));
        assertTrue(fixedWindowRateLimiter.isRequestAllowed(rateLimitKey));
    }

    @Test
    void isRequestAllowed_shouldDenyRequest_whenRateLimitExceeded() {
        Mockito.when(timestampProvider.get()).thenReturn(Instant.now());
        FixedWindowRateLimiter fixedWindowRateLimiter = new FixedWindowRateLimiter(1, Duration.ofSeconds(1).toMillis(), timestampProvider);
        String rateLimitKey = "user1";

        assertTrue(fixedWindowRateLimiter.isRequestAllowed(rateLimitKey));
        assertFalse(fixedWindowRateLimiter.isRequestAllowed(rateLimitKey)); // request should be throttled, assertFalse
    }

    @Test
    void isRequestAllowed_shouldAllowRequest_whenUsagePassedWindowExpiry() {
        Mockito.when(timestampProvider.get()).thenReturn(Instant.ofEpochMilli(0));
        FixedWindowRateLimiter fixedWindowRateLimiter = new FixedWindowRateLimiter(1, Duration.ofSeconds(1).toMillis(), timestampProvider);
        String rateLimitKey = "user1";

        assertTrue(fixedWindowRateLimiter.isRequestAllowed(rateLimitKey));
        Mockito.when(timestampProvider.get()).thenReturn(Instant.ofEpochMilli(2000)); // 2 seconds pass
        assertTrue(fixedWindowRateLimiter.isRequestAllowed(rateLimitKey)); // request shouldn't be throttled, as window reset
    }
}