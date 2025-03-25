import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

class House {
    private int id;
    private String location;
    private double price;
    private int bedrooms;
    private String owner;
    private boolean isBooked;

    public House(int id, String location, double price, int bedrooms, String owner) {
        this.id = id;
        this.location = location;
        this.price = price;
        this.bedrooms = bedrooms;
        this.owner = owner;
        this.isBooked = false;
    }

    public int getId() { return id; }
    public String getLocation() { return location; }
    public double getPrice() { return price; }
    public int getBedrooms() { return bedrooms; }
    public String getOwner() { return owner; }
    public boolean isBooked() { return isBooked; }
    public synchronized void bookHouse() { this.isBooked = true; }

    @Override
    public String toString() {
        return id + ", " + location + ", " + price + ", " + bedrooms + " beds, Owner: " + owner + ", Booked: " + isBooked;
    }
}

class Tenant {
    private String name;
    private String contact;
    private String preferredLocation;

    public Tenant(String name, String contact, String preferredLocation) {
        this.name = name;
        this.contact = contact;
        this.preferredLocation = preferredLocation;
    }

    public String getName() { return name; }
    public String getContact() { return contact; }
    public String getPreferredLocation() { return preferredLocation; }

    @Override
    public String toString() {
        return name + " (Contact: " + contact + ", Prefers: " + preferredLocation + ")";
    }
}

class HouseRentalSystem {
    private List<House> houses = new ArrayList<>();
    private List<Tenant> tenants = new ArrayList<>();
    private Scanner scanner = new Scanner(System.in);

    public void addHouse() {
        System.out.print("Enter House ID: ");
        int id = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Enter Location: ");
        String location = scanner.nextLine();
        System.out.print("Enter Price: ");
        double price = scanner.nextDouble();
        System.out.print("Enter Bedrooms: ");
        int bedrooms = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Enter Owner Name: ");
        String owner = scanner.nextLine();
        houses.add(new House(id, location, price, bedrooms, owner));
        System.out.println("House added successfully.");
    }

    public void registerTenant() {
        System.out.print("Enter Tenant Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter Contact: ");
        String contact = scanner.nextLine();
        System.out.print("Enter Preferred Location: ");
        String preferredLocation = scanner.nextLine();
        tenants.add(new Tenant(name, contact, preferredLocation));
        System.out.println("Tenant registered successfully.");
    }

   public void searchHouses() {
        System.out.print("Enter location to search: ");
        String location = scanner.nextLine();
        System.out.print("Enter maximum price: ");
        double maxPrice = scanner.nextDouble();
        List<House> availableHouses = houses.stream()
            .filter(house -> house.getLocation().equalsIgnoreCase(location) && house.getPrice() <= maxPrice && !house.isBooked())
            .collect(Collectors.toList());
        
        if (availableHouses.isEmpty()) {
            System.out.println("No available houses in " + location + " under price " + maxPrice);
        } else {
            availableHouses.forEach(System.out::println);
        }
    }

    public void bookHouse() {
        System.out.print("Enter House ID to book: ");
        int houseId = scanner.nextInt();
        for (House house : houses) {
            if (house.getId() == houseId && !house.isBooked()) {
                synchronized (house) {
                    house.bookHouse();
                    System.out.println("House booked successfully.");
                }
                return;
            }
        }
        System.out.println("House not found or already booked.");
    }

    public void saveData() {
        try (BufferedWriter houseWriter = new BufferedWriter(new FileWriter("houses.txt"));
             BufferedWriter tenantWriter = new BufferedWriter(new FileWriter("tenants.txt"))) {
            for (House house : houses) {
                houseWriter.write(house.getId() + "," + house.getLocation() + "," + house.getPrice() + "," + house.getBedrooms() + "," + house.getOwner() + "," + house.isBooked() + "\n");
            }
            for (Tenant tenant : tenants) {
                tenantWriter.write(tenant.getName() + "," + tenant.getContact() + "," + tenant.getPreferredLocation() + "\n");
            }
            System.out.println("Data saved successfully.");
        } catch (IOException e) {
            System.out.println("Error saving data: " + e.getMessage());
        }
    }

    public void run() {
        while (true) {
            System.out.println("\nHouse Rental Management System");
            System.out.println("1. Add House");
            System.out.println("2. Register Tenant");
            System.out.println("3. Search Houses");
            System.out.println("4. Book House");
            System.out.println("5. Save Data");
            System.out.println("6. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine();
            switch (choice) {
                case 1 -> addHouse();
                case 2 -> registerTenant();
                case 3 -> searchHouses();
                case 4 -> bookHouse();
                case 5 -> saveData();
                case 6 -> { System.out.println("Exiting..."); return; }
                default -> System.out.println("Invalid choice!");
            }
        }
    }
}

public class Main {
    public static void main(String[] args) {
        HouseRentalSystem system = new HouseRentalSystem();
        system.run();
    }
}
