import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class TokenBucketRateLimiterTest {
    private final TimestampProvider timestampProvider = Mockito.mock(TimestampProvider.class);

    @Test
    void isRequestAllowed_shouldAllowRequest_whenAvailableCapacityExists() {
        Mockito.when(timestampProvider.get()).thenReturn(Instant.now());
        TokenBucketRateLimiter tokenBucketRateLimiter = new TokenBucketRateLimiter(10, 5, 1000, timestampProvider);
        String key = "user1";

        assertTrue(tokenBucketRateLimiter.isRequestAllowed(key));
        assertTrue(tokenBucketRateLimiter.isRequestAllowed(key));
    }

    @Test
    void isRequestAllowed_shouldDenyRequest_whenNoAvailableCapacity() {
        Mockito.when(timestampProvider.get()).thenReturn(Instant.now());
        TokenBucketRateLimiter tokenBucketRateLimiter = new TokenBucketRateLimiter(1, 5, 1000, timestampProvider);
        String key = "user1";

        assertTrue(tokenBucketRateLimiter.isRequestAllowed(key));
        assertFalse(tokenBucketRateLimiter.isRequestAllowed(key));
    }

    @Test
    void isRequestAllowed_shouldAllowRequest_whenBucketWasRefilled() {
        Mockito.when(timestampProvider.get()).thenReturn(Instant.ofEpochMilli(0));
        TokenBucketRateLimiter tokenBucketRateLimiter = new TokenBucketRateLimiter(1, 1, 1000, timestampProvider);
        String key = "user1";

        assertTrue(tokenBucketRateLimiter.isRequestAllowed(key));
        Mockito.when(timestampProvider.get()).thenReturn(Instant.ofEpochMilli(1000)); // should refill bucket
        assertTrue(tokenBucketRateLimiter.isRequestAllowed(key));
    }

    @Test
    void isRequestAllowed_shouldRefillBucketWith2Intervals() {
        Mockito.when(timestampProvider.get()).thenReturn(Instant.ofEpochMilli(0));
        TokenBucketRateLimiter tokenBucketRateLimiter = new TokenBucketRateLimiter(2, 1, 1000, timestampProvider);
        String key = "user1";

        assertTrue(tokenBucketRateLimiter.isRequestAllowed(key));
        assertTrue(tokenBucketRateLimiter.isRequestAllowed(key));
        Mockito.when(timestampProvider.get()).thenReturn(Instant.ofEpochMilli(2000)); // should refill bucket twice because two seconds passed
        assertTrue(tokenBucketRateLimiter.isRequestAllowed(key));
        assertTrue(tokenBucketRateLimiter.isRequestAllowed(key));

        assertFalse(tokenBucketRateLimiter.isRequestAllowed(key)); // this one should fail since no more tokens left
    }

    @Test
    void isRequestAllowed_shouldAllowBurstOfRequests_withLowRefillRate() {
        Mockito.when(timestampProvider.get()).thenReturn(Instant.ofEpochMilli(0));
        TokenBucketRateLimiter tokenBucketRateLimiter = new TokenBucketRateLimiter(50, 5, 1000, timestampProvider);
        String key = "user1";
        for (int i = 0; i < 50; i++) {
            assertTrue(tokenBucketRateLimiter.isRequestAllowed(key));
        }

        assertFalse(tokenBucketRateLimiter.isRequestAllowed(key)); // this should fail since all tokens are exhausted
    }
}