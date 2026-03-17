import java.util.*;

public class PlagiarismDetector {

    private static final int N = 5; // n-gram size

    // n-gram -> set of document IDs
    private Map<String, Set<String>> index;

    // documentId -> list of n-grams
    private Map<String, List<String>> documentMap;

    public PlagiarismDetector() {
        index = new HashMap<>();
        documentMap = new HashMap<>();
    }

    // Add document to system
    public void addDocument(String docId, String content) {
        List<String> ngrams = generateNGrams(content);
        documentMap.put(docId, ngrams);

        for (String gram : ngrams) {
            index.computeIfAbsent(gram, k -> new HashSet<>()).add(docId);
        }
    }

    // Analyze a new document
    public void analyzeDocument(String docId, String content) {
        List<String> ngrams = generateNGrams(content);
        System.out.println("Extracted " + ngrams.size() + " n-grams");

        // Count matches per document
        Map<String, Integer> matchCount = new HashMap<>();

        for (String gram : ngrams) {
            if (index.containsKey(gram)) {
                for (String existingDoc : index.get(gram)) {
                    matchCount.put(existingDoc,
                            matchCount.getOrDefault(existingDoc, 0) + 1);
                }
            }
        }

        // Find most similar document
        String bestMatch = null;
        int maxMatches = 0;

        for (Map.Entry<String, Integer> entry : matchCount.entrySet()) {
            if (entry.getValue() > maxMatches) {
                maxMatches = entry.getValue();
                bestMatch = entry.getKey();
            }
        }

        if (bestMatch == null) {
            System.out.println("No similar documents found.");
            return;
        }

        int total = ngrams.size();
        double similarity = (maxMatches * 100.0) / total;

        System.out.println("Most similar: " + bestMatch);
        System.out.println("Matching n-grams: " + maxMatches);
        System.out.println("Similarity: " + String.format("%.2f", similarity) + "%");

        if (similarity > 30) {
            System.out.println("⚠️ Possible plagiarism detected!");
        }
    }

    // Generate n-grams
    private List<String> generateNGrams(String text) {
        List<String> result = new ArrayList<>();

        String[] words = text.toLowerCase().split("\\s+");

        for (int i = 0; i <= words.length - N; i++) {
            StringBuilder gram = new StringBuilder();

            for (int j = 0; j < N; j++) {
                gram.append(words[i + j]).append(" ");
            }

            result.add(gram.toString().trim());
        }

        return result;
    }

    // Test
    public static void main(String[] args) {
        PlagiarismDetector pd = new PlagiarismDetector();

        pd.addDocument("essay_1", "this is a sample essay for testing plagiarism detection system");
        pd.addDocument("essay_2", "this is another sample essay used for plagiarism detection");

        pd.analyzeDocument("essay_3",
                "this is a sample essay for plagiarism detection system testing");
    }
}