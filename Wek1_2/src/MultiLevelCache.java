import java.util.*;

public class MultiLevelCache {

    // -------------------------------
    // LRU Cache using LinkedHashMap
    // -------------------------------
    class LRUCache<K, V> extends LinkedHashMap<K, V> {
        private int capacity;

        public LRUCache(int capacity) {
            super(capacity, 0.75f, true);
            this.capacity = capacity;
        }

        protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
            return size() > capacity;
        }
    }

    private LRUCache<String, String> L1;
    private LRUCache<String, String> L2;

    private Map<String, String> L3; // simulated DB
    private Map<String, Integer> accessCount;

    private int l1Hits = 0, l2Hits = 0, l3Hits = 0;

    public MultiLevelCache(int l1Size, int l2Size) {
        L1 = new LRUCache<>(l1Size);
        L2 = new LRUCache<>(l2Size);
        L3 = new HashMap<>();
        accessCount = new HashMap<>();

        // preload database
        L3.put("video_123", "Video Data 123");
        L3.put("video_999", "Video Data 999");
    }

    // -------------------------------
    // Get Video
    // -------------------------------
    public String getVideo(String videoId) {

        // L1
        if (L1.containsKey(videoId)) {
            l1Hits++;
            return "L1 HIT → " + L1.get(videoId);
        }

        // L2
        if (L2.containsKey(videoId)) {
            l2Hits++;
            String value = L2.get(videoId);

            // Promote to L1
            L1.put(videoId, value);
            return "L2 HIT → Promoted to L1";
        }

        // L3 (DB)
        if (L3.containsKey(videoId)) {
            l3Hits++;
            String value = L3.get(videoId);

            // Track access count
            accessCount.put(videoId, accessCount.getOrDefault(videoId, 0) + 1);

            // Add to L2
            L2.put(videoId, value);

            return "L3 HIT → Added to L2";
        }

        return "Video not found";
    }

    // -------------------------------
    // Stats
    // -------------------------------
    public String getStats() {
        int total = l1Hits + l2Hits + l3Hits;

        double l1Rate = total == 0 ? 0 : (l1Hits * 100.0 / total);
        double l2Rate = total == 0 ? 0 : (l2Hits * 100.0 / total);
        double l3Rate = total == 0 ? 0 : (l3Hits * 100.0 / total);

        return "L1 Hit Rate: " + String.format("%.2f", l1Rate) + "%\n" +
                "L2 Hit Rate: " + String.format("%.2f", l2Rate) + "%\n" +
                "L3 Hit Rate: " + String.format("%.2f", l3Rate) + "%";
    }

    // -------------------------------
    // Test
    // -------------------------------
    public static void main(String[] args) {
        MultiLevelCache cache = new MultiLevelCache(2, 3);

        System.out.println(cache.getVideo("video_123")); // L3
        System.out.println(cache.getVideo("video_123")); // L2 → L1
        System.out.println(cache.getVideo("video_123")); // L1

        System.out.println(cache.getVideo("video_999")); // L3

        System.out.println("\nStats:");
        System.out.println(cache.getStats());
    }
}