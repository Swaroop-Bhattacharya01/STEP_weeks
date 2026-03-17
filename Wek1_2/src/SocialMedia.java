import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SocialMedia {

    private ConcurrentHashMap<String, Integer> usernameMap;

    // Stores username -> attempt count
    private ConcurrentHashMap<String, Integer> attemptMap;

    public SocialMedia() {
        usernameMap = new ConcurrentHashMap<>();
        attemptMap = new ConcurrentHashMap<>();
    }

    // Register a username
    public boolean register(String username, int userId) {
        if (usernameMap.containsKey(username)) {
            return false;
        }
        usernameMap.put(username, userId);
        return true;
    }

    // Check availability (O(1))
    public boolean checkAvailability(String username) {
        if (usernameMap.containsKey(username)) {
            attemptMap.put(username, attemptMap.getOrDefault(username, 0) + 1);
            return false;
        }
        return true;
    }

    // Suggest alternatives
    public List<String> suggestAlternatives(String username) {
        List<String> suggestions = new ArrayList<>();

        // Try adding numbers
        for (int i = 1; i <= 100 && suggestions.size() < 5; i++) {
            String newUsername = username + i;
            if (!usernameMap.containsKey(newUsername)) {
                suggestions.add(newUsername);
            }
        }

        // Try adding underscore variations if needed
        if (suggestions.size() < 5) {
            String alt = username + "_";
            if (!usernameMap.containsKey(alt)) {
                suggestions.add(alt);
            }
        }

        return suggestions;
    }

    // Get most attempted username
    public String getMostAttempted() {
        String result = null;
        int max = 0;

        for (Map.Entry<String, Integer> entry : attemptMap.entrySet()) {
            if (entry.getValue() > max) {
                max = entry.getValue();
                result = entry.getKey();
            }
        }

        return result;
    }

    // (Optional) Debug print
    public void printUsers() {
        System.out.println(usernameMap);
    }

    // Main method for testing
    public static void main(String[] args) {
        SocialMedia sm = new SocialMedia();

        sm.register("john_doe", 1);
        sm.register("jane_smith", 2);

        System.out.println(sm.checkAvailability("john_doe"));   // false
        System.out.println(sm.checkAvailability("new_user"));   // true

        System.out.println(sm.suggestAlternatives("john_doe"));

        sm.checkAvailability("john_doe");
        sm.checkAvailability("john_doe");

        System.out.println(sm.getMostAttempted()); // john_doe
    }
}