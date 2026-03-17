import java.util.*;

public class ParkingLot {

    enum Status {
        EMPTY, OCCUPIED
    }

    class Spot {
        String licensePlate;
        long entryTime;
        Status status;

        Spot() {
            this.status = Status.EMPTY;
        }
    }

    private Spot[] table;
    private int capacity;
    private int occupiedSpots = 0;
    private int totalProbes = 0;
    private int totalParked = 0;

    public ParkingLot(int capacity) {
        this.capacity = capacity;
        table = new Spot[capacity];
        for (int i = 0; i < capacity; i++) {
            table[i] = new Spot();
        }
    }

    // Hash function
    private int hash(String licensePlate) {
        return Math.abs(licensePlate.hashCode()) % capacity;
    }

    // Park vehicle
    public String parkVehicle(String licensePlate) {
        int index = hash(licensePlate);
        int probes = 0;

        while (table[index].status == Status.OCCUPIED) {
            index = (index + 1) % capacity;
            probes++;
        }

        table[index].licensePlate = licensePlate;
        table[index].entryTime = System.currentTimeMillis();
        table[index].status = Status.OCCUPIED;

        occupiedSpots++;
        totalProbes += probes;
        totalParked++;

        return "Assigned spot #" + index + " (" + probes + " probes)";
    }

    // Exit vehicle
    public String exitVehicle(String licensePlate) {
        int index = hash(licensePlate);
        int probes = 0;

        while (table[index].status != Status.EMPTY) {
            if (table[index].licensePlate != null &&
                    table[index].licensePlate.equals(licensePlate)) {

                long durationMs = System.currentTimeMillis() - table[index].entryTime;
                double durationHours = durationMs / (1000.0 * 60 * 60);
                double fee = durationHours * 5; // $5 per hour

                table[index] = new Spot(); // reset spot
                occupiedSpots--;

                return "Spot #" + index + " freed. Duration: "
                        + String.format("%.2f", durationHours)
                        + " hrs, Fee: $" + String.format("%.2f", fee);
            }

            index = (index + 1) % capacity;
            probes++;
        }

        return "Vehicle not found";
    }

    // Find nearest available spot (linear scan)
    public int findNearestAvailable() {
        for (int i = 0; i < capacity; i++) {
            if (table[i].status == Status.EMPTY) {
                return i;
            }
        }
        return -1; // full
    }

    // Statistics
    public String getStatistics() {
        double occupancy = (occupiedSpots * 100.0) / capacity;
        double avgProbes = totalParked == 0 ? 0 : (double) totalProbes / totalParked;

        return "Occupancy: " + String.format("%.2f", occupancy) + "%" +
                ", Avg Probes: " + String.format("%.2f", avgProbes);
    }

    // Test
    public static void main(String[] args) throws InterruptedException {
        ParkingLot lot = new ParkingLot(10);

        System.out.println(lot.parkVehicle("ABC123"));
        System.out.println(lot.parkVehicle("ABC124"));
        System.out.println(lot.parkVehicle("ABC125"));

        Thread.sleep(2000);

        System.out.println(lot.exitVehicle("ABC123"));

        System.out.println("Nearest free spot: " + lot.findNearestAvailable());

        System.out.println(lot.getStatistics());
    }
}