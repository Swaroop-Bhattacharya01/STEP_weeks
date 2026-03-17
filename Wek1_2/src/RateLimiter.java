import java.util.concurrent.*;

public class RateLimiter {

    // Token Bucket class
    class TokenBucket {
        double tokens;
        long lastRefillTime;
        final int maxTokens;
        final double refillRate; // tokens per millisecond

        TokenBucket(int maxTokens, int refillPerSecond) {
            this.maxTokens = maxTokens;
            this.tokens = maxTokens;
            this.refillRate = refillPerSecond / 1000.0;
            this.lastRefillTime = System.currentTimeMillis();
        }

        // Refill tokens based on elapsed time
        void refill() {
            long now = System.currentTimeMillis();
            long timeElapsed = now - lastRefillTime;

            double tokensToAdd = timeElapsed * refillRate;
            tokens = Math.min(maxTokens, tokens + tokensToAdd);

            lastRefillTime = now;
        }

        // Try to consume a token
        boolean allowRequest() {
            refill();

            if (tokens >= 1) {
                tokens -= 1;
                return true;
            }
            return false;
        }
    }

    // clientId -> TokenBucket
    private ConcurrentHashMap<String, TokenBucket> clientBuckets;

    private final int MAX_TOKENS;
    private final int REFILL_RATE;

    public RateLimiter(int maxRequestsPerHour) {
        this.MAX_TOKENS = maxRequestsPerHour;
        this.REFILL_RATE = maxRequestsPerHour / 3600; // per second
        this.clientBuckets = new ConcurrentHashMap<>();
    }

    // Check rate limit
    public String checkRateLimit(String clientId) {
        clientBuckets.putIfAbsent(clientId,
                new TokenBucket(MAX_TOKENS, REFILL_RATE));

        TokenBucket bucket = clientBuckets.get(clientId);

        synchronized (bucket) {
            if (bucket.allowRequest()) {
                return "Allowed (" + (int) bucket.tokens + " tokens remaining)";
            } else {
                return "Denied (Rate limit exceeded)";
            }
        }
    }

    // Get status
    public String getRateLimitStatus(String clientId) {
        TokenBucket bucket = clientBuckets.get(clientId);

        if (bucket == null) return "No data";

        synchronized (bucket) {
            bucket.refill();
            return "Tokens left: " + (int) bucket.tokens +
                    ", Max: " + bucket.maxTokens +
                    ", Last refill: " + bucket.lastRefillTime;
        }
    }

    // Test
    public static void main(String[] args) throws InterruptedException {
        RateLimiter rl = new RateLimiter(10); // 10 requests/hour (for testing)

        for (int i = 0; i < 12; i++) {
            System.out.println(rl.checkRateLimit("client_1"));
        }

        Thread.sleep(2000); // wait for refill

        System.out.println("\nAfter waiting:");
        System.out.println(rl.checkRateLimit("client_1"));

        System.out.println(rl.getRateLimitStatus("client_1"));
    }
}