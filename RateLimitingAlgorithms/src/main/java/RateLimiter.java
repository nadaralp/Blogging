public interface RateLimiter {
    /**
     * Checks if a request is allowed based on the throttle policy.
     *
     * @param key the key for which the rate limit decision is made
     * @return true if request is allowed, false otherwise
     */
    boolean isRequestAllowed(String key);
}
