import java.util.*;
import java.util.concurrent.*;

public class AnalyticsDashboard {

    // page -> total visits
    private ConcurrentHashMap<String, Integer> pageVisits;

    // page -> unique users
    private ConcurrentHashMap<String, Set<String>> uniqueVisitors;

    // source -> count
    private ConcurrentHashMap<String, Integer> sourceCount;

    public AnalyticsDashboard() {
        pageVisits = new ConcurrentHashMap<>();
        uniqueVisitors = new ConcurrentHashMap<>();
        sourceCount = new ConcurrentHashMap<>();
    }

    // Process incoming event
    public void processEvent(String url, String userId, String source) {

        // Count total visits
        pageVisits.put(url, pageVisits.getOrDefault(url, 0) + 1);

        // Track unique visitors
        uniqueVisitors.putIfAbsent(url, ConcurrentHashMap.newKeySet());
        uniqueVisitors.get(url).add(userId);

        // Count traffic source
        sourceCount.put(source, sourceCount.getOrDefault(source, 0) + 1);
    }

    // Get top 10 pages
    public List<String> getTopPages() {
        PriorityQueue<Map.Entry<String, Integer>> minHeap =
                new PriorityQueue<>(Comparator.comparingInt(Map.Entry::getValue));

        for (Map.Entry<String, Integer> entry : pageVisits.entrySet()) {
            minHeap.offer(entry);
            if (minHeap.size() > 10) {
                minHeap.poll();
            }
        }

        List<String> result = new ArrayList<>();
        while (!minHeap.isEmpty()) {
            Map.Entry<String, Integer> entry = minHeap.poll();
            String url = entry.getKey();
            int visits = entry.getValue();
            int unique = uniqueVisitors.getOrDefault(url, Collections.emptySet()).size();

            result.add(url + " - " + visits + " views (" + unique + " unique)");
        }

        Collections.reverse(result); // highest first
        return result;
    }

    // Get traffic source stats
    public Map<String, Integer> getSourceStats() {
        return new HashMap<>(sourceCount);
    }

    // Dashboard output
    public void getDashboard() {
        System.out.println("=== Top Pages ===");
        for (String page : getTopPages()) {
            System.out.println(page);
        }

        System.out.println("\n=== Traffic Sources ===");
        for (Map.Entry<String, Integer> entry : sourceCount.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }

    // Test
    public static void main(String[] args) {
        AnalyticsDashboard dashboard = new AnalyticsDashboard();

        dashboard.processEvent("/article/breaking-news", "user_1", "google");
        dashboard.processEvent("/article/breaking-news", "user_2", "facebook");
        dashboard.processEvent("/sports/championship", "user_1", "google");
        dashboard.processEvent("/article/breaking-news", "user_1", "direct");

        dashboard.getDashboard();
    }
}