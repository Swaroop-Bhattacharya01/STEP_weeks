import java.util.*;

public class AutocompleteSystem {

    // Trie Node
    class TrieNode {
        Map<Character, TrieNode> children = new HashMap<>();
        Map<String, Integer> frequencyMap = new HashMap<>();
    }

    private TrieNode root;
    private Map<String, Integer> globalFrequency;

    public AutocompleteSystem() {
        root = new TrieNode();
        globalFrequency = new HashMap<>();
    }

    // Insert or update query
    public void addQuery(String query) {
        globalFrequency.put(query, globalFrequency.getOrDefault(query, 0) + 1);

        TrieNode node = root;
        for (char c : query.toCharArray()) {
            node.children.putIfAbsent(c, new TrieNode());
            node = node.children.get(c);

            node.frequencyMap.put(query, globalFrequency.get(query));
        }
    }

    // Search top 10 suggestions
    public List<String> search(String prefix) {
        TrieNode node = root;

        // Traverse prefix
        for (char c : prefix.toCharArray()) {
            if (!node.children.containsKey(c)) {
                return new ArrayList<>();
            }
            node = node.children.get(c);
        }

        // Min-heap for top 10
        PriorityQueue<Map.Entry<String, Integer>> minHeap =
                new PriorityQueue<>(Comparator.comparingInt(Map.Entry::getValue));

        for (Map.Entry<String, Integer> entry : node.frequencyMap.entrySet()) {
            minHeap.offer(entry);
            if (minHeap.size() > 10) {
                minHeap.poll();
            }
        }

        List<String> result = new ArrayList<>();
        while (!minHeap.isEmpty()) {
            result.add(minHeap.poll().getKey());
        }

        Collections.reverse(result);
        return result;
    }

    // Test
    public static void main(String[] args) {
        AutocompleteSystem ac = new AutocompleteSystem();

        ac.addQuery("java tutorial");
        ac.addQuery("java tutorial");
        ac.addQuery("java stream");
        ac.addQuery("java string");
        ac.addQuery("javascript basics");

        System.out.println(ac.search("jav")); // suggestions

        ac.addQuery("java tutorial"); // update frequency

        System.out.println(ac.search("java"));
    }
}