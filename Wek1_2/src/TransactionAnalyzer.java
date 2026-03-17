import java.util.*;

public class TransactionAnalyzer {

    static class Transaction {
        int id;
        int amount;
        String merchant;
        long timestamp; // in milliseconds
        String account;

        Transaction(int id, int amount, String merchant, long timestamp, String account) {
            this.id = id;
            this.amount = amount;
            this.merchant = merchant;
            this.timestamp = timestamp;
            this.account = account;
        }
    }

    // -------------------------------
    // 1. Classic Two-Sum
    // -------------------------------
    public List<int[]> findTwoSum(List<Transaction> transactions, int target) {
        Map<Integer, Transaction> map = new HashMap<>();
        List<int[]> result = new ArrayList<>();

        for (Transaction t : transactions) {
            int complement = target - t.amount;

            if (map.containsKey(complement)) {
                result.add(new int[]{map.get(complement).id, t.id});
            }
            map.put(t.amount, t);
        }

        return result;
    }

    // -------------------------------
    // 2. Two-Sum with Time Window (1 hour)
    // -------------------------------
    public List<int[]> findTwoSumWithTime(List<Transaction> transactions, int target) {
        List<int[]> result = new ArrayList<>();
        Map<Integer, List<Transaction>> map = new HashMap<>();

        for (Transaction t : transactions) {
            int complement = target - t.amount;

            if (map.containsKey(complement)) {
                for (Transaction prev : map.get(complement)) {
                    if (Math.abs(t.timestamp - prev.timestamp) <= 3600000) {
                        result.add(new int[]{prev.id, t.id});
                    }
                }
            }

            map.computeIfAbsent(t.amount, k -> new ArrayList<>()).add(t);
        }

        return result;
    }

    // -------------------------------
    // 3. K-Sum (generalized)
    // -------------------------------
    public List<List<Integer>> findKSum(List<Transaction> transactions, int k, int target) {
        List<List<Integer>> result = new ArrayList<>();
        backtrack(transactions, k, target, 0, new ArrayList<>(), result);
        return result;
    }

    private void backtrack(List<Transaction> txs, int k, int target, int start,
                           List<Integer> path, List<List<Integer>> result) {

        if (k == 0 && target == 0) {
            result.add(new ArrayList<>(path));
            return;
        }

        if (k == 0 || target < 0) return;

        for (int i = start; i < txs.size(); i++) {
            Transaction t = txs.get(i);

            path.add(t.id);
            backtrack(txs, k - 1, target - t.amount, i + 1, path, result);
            path.remove(path.size() - 1);
        }
    }

    // -------------------------------
    // 4. Duplicate Detection
    // Same amount + merchant, different accounts
    // -------------------------------
    public List<String> detectDuplicates(List<Transaction> transactions) {
        Map<String, Set<String>> map = new HashMap<>();
        List<String> result = new ArrayList<>();

        for (Transaction t : transactions) {
            String key = t.amount + "_" + t.merchant;

            map.putIfAbsent(key, new HashSet<>());
            map.get(key).add(t.account);

            if (map.get(key).size() > 1) {
                result.add("Duplicate found: amount=" + t.amount +
                        ", merchant=" + t.merchant +
                        ", accounts=" + map.get(key));
            }
        }

        return result;
    }

    // -------------------------------
    // Test
    // -------------------------------
    public static void main(String[] args) {
        TransactionAnalyzer ta = new TransactionAnalyzer();

        List<Transaction> txs = Arrays.asList(
                new Transaction(1, 500, "StoreA", System.currentTimeMillis(), "acc1"),
                new Transaction(2, 300, "StoreB", System.currentTimeMillis(), "acc2"),
                new Transaction(3, 200, "StoreC", System.currentTimeMillis(), "acc3"),
                new Transaction(4, 500, "StoreA", System.currentTimeMillis(), "acc2")
        );

        System.out.println("Two Sum:");
        for (int[] pair : ta.findTwoSum(txs, 500)) {
            System.out.println(Arrays.toString(pair));
        }

        System.out.println("\nTwo Sum (Time Window):");
        for (int[] pair : ta.findTwoSumWithTime(txs, 500)) {
            System.out.println(Arrays.toString(pair));
        }

        System.out.println("\nK-Sum:");
        System.out.println(ta.findKSum(txs, 3, 1000));

        System.out.println("\nDuplicates:");
        for (String s : ta.detectDuplicates(txs)) {
            System.out.println(s);
        }
    }
}