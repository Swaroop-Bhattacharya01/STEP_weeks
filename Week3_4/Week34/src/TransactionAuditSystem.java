import java.util.*;

class Transaction {
    String id;
    double fee;
    String timestamp; // HH:MM format

    Transaction(String id, double fee, String timestamp) {
        this.id = id;
        this.fee = fee;
        this.timestamp = timestamp;
    }

    public String toString() {
        return id + ": fee=" + fee + ", ts=" + timestamp;
    }
}

public class TransactionAuditSystem {

    // 🔄 Bubble Sort (by fee)
    static void bubbleSortByFee(ArrayList<Transaction> list) {
        int n = list.size();
        int passes = 0, swaps = 0;

        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            passes++;

            for (int j = 0; j < n - i - 1; j++) {
                if (list.get(j).fee > list.get(j + 1).fee) {
                    // swap
                    Transaction temp = list.get(j);
                    list.set(j, list.get(j + 1));
                    list.set(j + 1, temp);

                    swaps++;
                    swapped = true;
                }
            }

            if (!swapped) break; // early termination
        }

        System.out.println("Bubble Sort Result (by Fee):");
        printList(list);
        System.out.println("Passes: " + passes + ", Swaps: " + swaps);
    }

    // 📥 Insertion Sort (by fee + timestamp)
    static void insertionSortByFeeAndTime(ArrayList<Transaction> list) {
        int n = list.size();

        for (int i = 1; i < n; i++) {
            Transaction key = list.get(i);
            int j = i - 1;

            while (j >= 0 &&
                    (list.get(j).fee > key.fee ||
                            (list.get(j).fee == key.fee &&
                                    list.get(j).timestamp.compareTo(key.timestamp) > 0))) {

                list.set(j + 1, list.get(j));
                j--;
            }

            list.set(j + 1, key);
        }

        System.out.println("\nInsertion Sort Result (by Fee + Timestamp):");
        printList(list);
    }

    // 🚨 Outlier Detection
    static void detectHighFees(ArrayList<Transaction> list) {
        System.out.println("\nHigh-Fee Outliers (>50):");
        boolean found = false;

        for (Transaction t : list) {
            if (t.fee > 50) {
                System.out.println(t);
                found = true;
            }
        }

        if (!found) {
            System.out.println("None");
        }
    }

    // 🖨️ Print List
    static void printList(ArrayList<Transaction> list) {
        for (Transaction t : list) {
            System.out.println(t);
        }
    }

    // 🚀 Main Method
    public static void main(String[] args) {

        ArrayList<Transaction> transactions = new ArrayList<>();

        // Sample Input
        transactions.add(new Transaction("id1", 10.5, "10:00"));
        transactions.add(new Transaction("id2", 25.0, "09:30"));
        transactions.add(new Transaction("id3", 5.0, "10:15"));

        int size = transactions.size();

        // Algorithm selection based on batch size
        if (size <= 100) {
            bubbleSortByFee(transactions);
        } else if (size <= 1000) {
            insertionSortByFeeAndTime(transactions);
        } else {
            System.out.println("Large dataset: Use advanced sorting (not implemented)");
        }

        // Detect high-fee outliers
        detectHighFees(transactions);
    }
}