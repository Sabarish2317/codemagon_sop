import java.io.*;
import java.util.*;


class InvalidInputException extends Exception {
    public InvalidInputException(String message) {
        super(message);
    }
}

class House {
    private int id;
    private String location;
    private double price;
    private int bedrooms;
    private String owner;
    private boolean isBooked;

    public House(int id, String location, double price, int bedrooms, String owner) throws InvalidInputException {
        if (id < 0) {
            throw new InvalidInputException("House ID cannot be negative.");
        }
        if (price < 0) {
            throw new InvalidInputException("House price cannot be negative.");
        }
        if (bedrooms < 0) {
            throw new InvalidInputException("Number of bedrooms cannot be negative.");
        }
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
        return id + "," + location + "," + price + "," + bedrooms + "," + owner + "," + isBooked;
    }
}

class Tenant {
    private String name;
    private String contact;
    private String preferredLocation;

    public Tenant(String name, String contact, String preferredLocation) throws InvalidInputException {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidInputException("Tenant name cannot be empty.");
        }
        if (contact == null || contact.trim().isEmpty()) {
            throw new InvalidInputException("Contact information cannot be empty.");
        }
        this.name = name;
        this.contact = contact;
        this.preferredLocation = preferredLocation;
    }

    public String getName() { return name; }
    public String getContact() { return contact; }
    public String getPreferredLocation() { return preferredLocation; }

    @Override
    public String toString() {
        return name + "," + contact + "," + preferredLocation;
    }
}

class HouseRentalSystem {
    private List<House> houses = new ArrayList<>();
    private List<Tenant> tenants = new ArrayList<>();
    private Scanner scanner = new Scanner(System.in);

    public void addHouse() {
        try {
            System.out.print("Enter House ID: ");
            int id = Integer.parseInt(scanner.nextLine());
            System.out.print("Enter Location: ");
            String location = scanner.nextLine();
            System.out.print("Enter Price: ");
            double price = Double.parseDouble(scanner.nextLine());
            System.out.print("Enter Bedrooms: ");
            int bedrooms = Integer.parseInt(scanner.nextLine());
            System.out.print("Enter Owner Name: ");
            String owner = scanner.nextLine();
            
            House newHouse = new House(id, location, price, bedrooms, owner);
            houses.add(newHouse);
            System.out.println("House added successfully.");
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter numeric values for ID, Price, and Bedrooms.");
        } catch (InvalidInputException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void registerTenant() {
        try {
            System.out.print("Enter Tenant Name: ");
            String name = scanner.nextLine();
            System.out.print("Enter Contact: ");
            String contact = scanner.nextLine();
            System.out.print("Enter Preferred Location: ");
            String preferredLocation = scanner.nextLine();
            
            Tenant newTenant = new Tenant(name, contact, preferredLocation);
            tenants.add(newTenant);
            System.out.println("Tenant registered successfully.");
        } catch (InvalidInputException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void bookHouse() {
        try {
            System.out.print("Enter House ID to book: ");
            int houseId = Integer.parseInt(scanner.nextLine());
            
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
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid House ID.");
        }
    }

    public void saveData() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("houses.txt"))) {
            for (House house : houses) {
                writer.write(house.toString() + "\n");
            }
            System.out.println("Houses saved successfully.");
        } catch (IOException e) {
            System.out.println("Error saving houses: " + e.getMessage());
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("tenants.txt"))) {
            for (Tenant tenant : tenants) {
                writer.write(tenant.toString() + "\n");
            }
            System.out.println("Tenants saved successfully.");
        } catch (IOException e) {
            System.out.println("Error saving tenants: " + e.getMessage());
        }
    }

    public void run() {
        while (true) {
            System.out.println("\nHouse Rental Management System");
            System.out.println("1. Add House");
            System.out.println("2. Register Tenant");
            System.out.println("3. Book House");
            System.out.println("4. Save Data");
            System.out.println("5. Exit");
            System.out.print("Choose an option: ");
            
            try {
                int choice = Integer.parseInt(scanner.nextLine());
                switch (choice) {
                    case 1 -> addHouse();
                    case 2 -> registerTenant();
                    case 3 -> bookHouse();
                    case 4 -> saveData();
                    case 5 -> { 
                        System.out.println("Exiting..."); 
                        return; 
                    }
                    default -> System.out.println("Invalid choice!");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
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