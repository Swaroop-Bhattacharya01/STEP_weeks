import java.util.*;
import java.util.concurrent.*;

public class Ecommerce {

    // productId -> stock count
    private ConcurrentHashMap<String, Integer> stockMap;

    // productId -> waiting queue (FIFO)
    private ConcurrentHashMap<String, Queue<Integer>> waitingListMap;

    public Ecommerce() {
        stockMap = new ConcurrentHashMap<>();
        waitingListMap = new ConcurrentHashMap<>();
    }

    // Add product with initial stock
    public void addProduct(String productId, int stock) {
        stockMap.put(productId, stock);
        waitingListMap.put(productId, new ConcurrentLinkedQueue<>());
    }

    // Check stock (O(1))
    public int checkStock(String productId) {
        return stockMap.getOrDefault(productId, 0);
    }

    // Purchase item (thread-safe)
    public String purchaseItem(String productId, int userId) {
        synchronized (productId.intern()) {

            int stock = stockMap.getOrDefault(productId, 0);

            if (stock > 0) {
                stockMap.put(productId, stock - 1);
                return "Success! Remaining stock: " + (stock - 1);
            } else {
                Queue<Integer> queue = waitingListMap.get(productId);
                queue.offer(userId);
                return "Out of stock. Added to waiting list. Position: " + queue.size();
            }
        }
    }

    // Restock and serve waiting list
    public void restock(String productId, int quantity) {
        synchronized (productId.intern()) {

            int currentStock = stockMap.getOrDefault(productId, 0);
            Queue<Integer> queue = waitingListMap.get(productId);

            // Serve waiting list first
            while (quantity > 0 && !queue.isEmpty()) {
                int userId = queue.poll();
                quantity--;
                System.out.println("User " + userId + " from waiting list purchased " + productId);
            }

            // Remaining stock goes back to inventory
            stockMap.put(productId, currentStock + quantity);
        }
    }

    // Get waiting list size
    public int getWaitingListSize(String productId) {
        return waitingListMap.get(productId).size();
    }

    // Test
    public static void main(String[] args) {
        Ecommerce ec = new Ecommerce();

        ec.addProduct("IPHONE15_256GB", 2);

        System.out.println(ec.purchaseItem("IPHONE15_256GB", 101));
        System.out.println(ec.purchaseItem("IPHONE15_256GB", 102));
        System.out.println(ec.purchaseItem("IPHONE15_256GB", 103)); // goes to waiting list

        System.out.println("Stock: " + ec.checkStock("IPHONE15_256GB"));
        System.out.println("Waiting: " + ec.getWaitingListSize("IPHONE15_256GB"));

        ec.restock("IPHONE15_256GB", 2);

        System.out.println("Final Stock: " + ec.checkStock("IPHONE15_256GB"));
    }
}