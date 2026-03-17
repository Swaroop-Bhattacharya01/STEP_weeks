import java.util.*;
import java.util.concurrent.*;

public class DNSCache {

    // Entry class
    class DNSEntry {
        String domain;
        String ip;
        long expiryTime;

        DNSEntry(String domain, String ip, long ttlMillis) {
            this.domain = domain;
            this.ip = ip;
            this.expiryTime = System.currentTimeMillis() + ttlMillis;
        }

        boolean isExpired() {
            return System.currentTimeMillis() > expiryTime;
        }
    }

    private final int capacity;

    // LRU Cache using LinkedHashMap
    private final LinkedHashMap<String, DNSEntry> cache;

    private int hits = 0;
    private int misses = 0;

    public DNSCache(int capacity) {
        this.capacity = capacity;

        this.cache = new LinkedHashMap<String, DNSEntry>(capacity, 0.75f, true) {
            protected boolean removeEldestEntry(Map.Entry<String, DNSEntry> eldest) {
                return size() > DNSCache.this.capacity;
            }
        };

        // Start cleanup thread
        startCleanupTask();
    }

    // Resolve domain
    public synchronized String resolve(String domain) {
        DNSEntry entry = cache.get(domain);

        if (entry != null) {
            if (!entry.isExpired()) {
                hits++;
                return "Cache HIT → " + entry.ip;
            } else {
                cache.remove(domain);
            }
        }

        // Cache miss → simulate upstream DNS call
        misses++;
        String newIp = queryUpstreamDNS(domain);

        // Add to cache with TTL = 5 seconds (example)
        cache.put(domain, new DNSEntry(domain, newIp, 5000));

        return "Cache MISS → " + newIp;
    }

    // Simulated upstream DNS
    private String queryUpstreamDNS(String domain) {
        // Fake IP generator
        return "172.217.14." + new Random().nextInt(255);
    }

    // Background cleanup
    private void startCleanupTask() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleAtFixedRate(() -> {
            synchronized (DNSCache.this) {
                Iterator<Map.Entry<String, DNSEntry>> it = cache.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<String, DNSEntry> entry = it.next();
                    if (entry.getValue().isExpired()) {
                        it.remove();
                    }
                }
            }
        }, 5, 5, TimeUnit.SECONDS);
    }

    // Cache stats
    public synchronized String getCacheStats() {
        int total = hits + misses;
        double hitRate = total == 0 ? 0 : (hits * 100.0 / total);

        return "Hits: " + hits +
                ", Misses: " + misses +
                ", Hit Rate: " + String.format("%.2f", hitRate) + "%";
    }

    // Test
    public static void main(String[] args) throws InterruptedException {
        DNSCache dns = new DNSCache(3);

        System.out.println(dns.resolve("google.com")); // MISS
        System.out.println(dns.resolve("google.com")); // HIT

        Thread.sleep(6000); // wait for TTL expiry

        System.out.println(dns.resolve("google.com")); // MISS again

        System.out.println(dns.getCacheStats());
    }
}