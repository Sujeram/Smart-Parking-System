package com.parking;

import com.parking.model.*;

import java.util.Scanner;
import java.util.List;
import java.util.Map;

/**
 * Text-based console interface for Parking Management System
 * Works with StandaloneParkingService (file-based, no database)
 * 
 * IMPORTANT: Do NOT run this while the web application is running!
 * 
 * @version 2.0 - Updated for standalone file storage
 */
public class ParkingSystemConsole {
    
    private static StandaloneParkingService parkingService;
    private static Scanner scanner;
    
    /**
     * Main method - Entry point for console application
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        // Initialize services
        scanner = new Scanner(System.in);
        
        // Initialize the parking service (loads from files automatically)
        System.out.println("\nInitializing system...");
        parkingService = new StandaloneParkingService();
        
        // Display welcome message
        displayWelcome();
        
        // Main menu loop
        boolean running = true;
        while (running) {
            displayMainMenu();
            int choice = getIntInput("Enter your choice: ");
            
            switch (choice) {
                case 1:
                    registerCustomer();
                    break;
                case 2:
                    viewAllCustomers();
                    break;
                case 3:
                    parkVehicle();
                    break;
                case 4:
                    viewActiveTickets();
                    break;
                case 5:
                    exitVehicle();
                    break;
                case 6:
                    createReservation();
                    break;
                case 7:
                    viewActiveReservations();
                    break;
                case 8:
                    viewReports();
                    break;
                case 9:
                    visualizeParkingSpaces();
                    break;
                case 10:
                    saveDataToFiles();
                    break;
                case 0:
                    running = false;
                    exitSystem();
                    break;
                default:
                    System.out.println("\n[ERROR] Invalid choice! Please try again.");
            }
            
            if (running) {
                System.out.println("\nPress Enter to continue...");
                scanner.nextLine();
            }
        }
    }
    
    /**
     * Displays welcome banner
     */
    private static void displayWelcome() {
        System.out.println("\n+===========================================+");
        System.out.println("|   PARKING MANAGEMENT SYSTEM - v2.0       |");
        System.out.println("|   Smart Parking for Smart Cities          |");
        System.out.println("|   Console Interface                        |");
        System.out.println("+===========================================+\n");
        System.out.println("[WARNING] Ensure web application is NOT running!");
        System.out.println("[INFO] Data will be automatically saved to files\n");
    }
    
    /**
     * Displays main menu options
     */
    private static void displayMainMenu() {
        System.out.println("\n" + "=".repeat(45));
        System.out.println("              MAIN MENU");
        System.out.println("=".repeat(45));
        System.out.println("1. Register New Customer");
        System.out.println("2. View All Customers");
        System.out.println("3. Park Vehicle");
        System.out.println("4. View Active Tickets");
        System.out.println("5. Exit Vehicle");
        System.out.println("6. Create Reservation");
        System.out.println("7. View Active Reservations");
        System.out.println("8. View Reports & Analytics");
        System.out.println("9. Visualize Parking Spaces");
        System.out.println("10. Save Data to Files");
        System.out.println("0. Exit System");
        System.out.println("=".repeat(45));
    }
    
    /**
     * Registers a new customer
     */
    private static void registerCustomer() {
        System.out.println("\n" + "=".repeat(45));
        System.out.println("          CUSTOMER REGISTRATION");
        System.out.println("=".repeat(45));
        
        System.out.print("Enter customer name: ");
        String name = scanner.nextLine();
        
        System.out.print("Enter phone number: ");
        String phone = scanner.nextLine();
        
        System.out.print("Enter email address: ");
        String email = scanner.nextLine();
        
        System.out.print("Is customer handicapped? (yes/no): ");
        boolean handicapped = scanner.nextLine().equalsIgnoreCase("yes");
        
        Customer customer = parkingService.registerCustomer(name, phone, email, handicapped);
        
        System.out.println("\n[SUCCESS] Customer registered successfully!");
        System.out.println("+===========================================+");
        System.out.println("|          CUSTOMER DETAILS                 |");
        System.out.println("+===========================================+");
        System.out.println("| Customer ID: " + String.format("%-29s", customer.getId()) + "|");
        System.out.println("| Name: " + String.format("%-36s", customer.getName()) + "|");
        System.out.println("| Email: " + String.format("%-35s", customer.getEmail()) + "|");
        System.out.println("| Phone: " + String.format("%-35s", customer.getContact()) + "|");
        System.out.println("| Loyalty Points: " + String.format("%-26s", customer.getLoyaltyPoints()) + "|");
        if (handicapped) {
            System.out.println("| [HANDICAPPED] Priority Enabled           |");
        }
        System.out.println("+===========================================+");
    }
    
    /**
     * Displays all registered customers
     */
    private static void viewAllCustomers() {
        System.out.println("\n" + "=".repeat(45));
        System.out.println("          ALL CUSTOMERS");
        System.out.println("=".repeat(45));
        
        List<Customer> customers = parkingService.getAllCustomers();
        
        if (customers.isEmpty()) {
            System.out.println("\n[INFO] No customers registered yet.");
            return;
        }
        
        System.out.println("\nTotal Customers: " + customers.size());
        
        for (Customer c : customers) {
            System.out.println("\n[CUSTOMER] ID: " + c.getId());
            System.out.println("   Name: " + c.getName());
            System.out.println("   Email: " + c.getEmail());
            System.out.println("   Phone: " + c.getContact());
            System.out.println("   Loyalty Points: " + c.getLoyaltyPoints());
            if (c.isHandicapped()) {
                System.out.println("   [HANDICAPPED] Priority Customer");
            }
            System.out.println("   " + "-".repeat(40));
        }
    }
    
    /**
     * Parks a new vehicle
     */
    private static void parkVehicle() {
        System.out.println("\n" + "=".repeat(45));
        System.out.println("          PARK VEHICLE");
        System.out.println("=".repeat(45));
        
        System.out.print("Enter license plate: ");
        String licensePlate = scanner.nextLine().trim();
        
        System.out.println("\nSelect vehicle type:");
        System.out.println("1. Car (Rs.20/hr)");
        System.out.println("2. Motorcycle (Rs.10/hr)");
        System.out.println("3. Truck (Rs.30/hr)");
        int typeChoice = getIntInput("Enter choice (1-3): ");
        
        VehicleType vehicleType;
        switch (typeChoice) {
            case 1: vehicleType = VehicleType.CAR; break;
            case 2: vehicleType = VehicleType.MOTORCYCLE; break;
            case 3: vehicleType = VehicleType.TRUCK; break;
            default:
                System.out.println("[ERROR] Invalid choice!");
                return;
        }
        
        System.out.print("Enter customer ID (or 0 for guest): ");
        Long customerId = getLongInput("");
        if (customerId == 0) customerId = null;
        
        System.out.print("Enter contact number: ");
        String contact = scanner.nextLine().trim();
        
        System.out.print("Enter reservation ID (or press Enter to skip): ");
        String reservationId = scanner.nextLine().trim();
        if (reservationId.isEmpty()) reservationId = null;
        
        try {
            ParkingTicket ticket = parkingService.parkVehicleWithCustomer(
                licensePlate, vehicleType, contact, customerId, reservationId
            );
            
            if (ticket == null) {
                System.out.println("\n[ERROR] No available parking space for " + vehicleType);
                return;
            }
            
            System.out.println("\n[SUCCESS] Vehicle parked successfully!");
            System.out.println("+===========================================+");
            System.out.println("|          PARKING TICKET                   |");
            System.out.println("+===========================================+");
            System.out.println("| Ticket ID: " + String.format("%-30s", ticket.getTicketId()) + "|");
            System.out.println("| License Plate: " + String.format("%-26s", licensePlate) + "|");
            System.out.println("| Space: " + String.format("%-34s", ticket.getAllocatedSpace().getSpaceId()) + "|");
            System.out.println("| Entry Time: " + String.format("%-28s", ticket.getEntryTime()) + "|");
            System.out.println("| Vehicle Type: " + String.format("%-27s", vehicleType) + "|");
            if (ticket.getCustomer() != null) {
                System.out.println("| Customer: " + String.format("%-31s", ticket.getCustomer().getName()) + "|");
            }
            System.out.println("+===========================================+");
            System.out.println("\n[IMPORTANT] Save your Ticket ID for exit!");
        } catch (Exception e) {
            System.out.println("\n[ERROR] Error parking vehicle: " + e.getMessage());
        }
    }
    
    /**
     * Displays all active parking tickets
     */
    private static void viewActiveTickets() {
        System.out.println("\n" + "=".repeat(45));
        System.out.println("          ACTIVE TICKETS");
        System.out.println("=".repeat(45));
        
        List<ParkingTicket> tickets = parkingService.getActiveTickets();
        
        if (tickets.isEmpty()) {
            System.out.println("\n[INFO] No active tickets.");
            return;
        }
        
        System.out.println("\nTotal Active Tickets: " + tickets.size());
        
        for (ParkingTicket t : tickets) {
            System.out.println("\n[TICKET] ID: " + t.getTicketId());
            System.out.println("   License Plate: " + t.getVehicle().getLicensePlate());
            System.out.println("   Vehicle Type: " + t.getVehicle().getType());
            System.out.println("   Space: " + t.getAllocatedSpace().getSpaceId());
            System.out.println("   Entry Time: " + t.getEntryTime());
            if (t.getCustomer() != null) {
                System.out.println("   Customer: " + t.getCustomer().getName());
                System.out.println("   Loyalty Points: " + t.getCustomer().getLoyaltyPoints());
            }
            System.out.println("   " + "-".repeat(40));
        }
    }
    
    /**
     * Processes vehicle exit and payment
     */
    private static void exitVehicle() {
        System.out.println("\n" + "=".repeat(45));
        System.out.println("          EXIT VEHICLE");
        System.out.println("=".repeat(45));
        
        System.out.print("Enter ticket ID: ");
        String ticketId = scanner.nextLine().trim();
        
        // Calculate fee preview
        Map<String, Object> preview = parkingService.calculateExitPreview(ticketId);
        
        if (!(boolean) preview.getOrDefault("success", false)) {
            System.out.println("\n[ERROR] " + preview.get("message"));
            return;
        }
        
        System.out.println("\n" + "=".repeat(45));
        System.out.println("          PAYMENT SUMMARY");
        System.out.println("=".repeat(45));
        System.out.println("Parking Duration: " + preview.get("hours") + " hours");
        System.out.println("Amount to Pay: Rs." + String.format("%.2f", preview.get("amount")));
        if ((boolean) preview.get("isWeekend")) {
            System.out.println("[INFO] Weekend Surcharge Applied (1.2x)");
        }
        if ((boolean) preview.get("hasDiscount")) {
            System.out.println("[DISCOUNT] 10% Loyalty Discount Applied");
        }
        System.out.println("=".repeat(45));
        
        // Payment method selection
        System.out.println("\nSelect payment method:");
        System.out.println("1. Cash");
        System.out.println("2. Card");
        System.out.println("3. UPI");
        int paymentChoice = getIntInput("Enter choice (1-3): ");
        
        PaymentMethod paymentMethod;
        String paymentDetails = null;
        double cashReceived = 0.0;
        
        switch (paymentChoice) {
            case 1:
                paymentMethod = PaymentMethod.CASH;
                cashReceived = getDoubleInput("Enter cash received: Rs.");
                break;
            case 2:
                paymentMethod = PaymentMethod.CARD;
                System.out.print("Enter last 4 digits of card: ");
                paymentDetails = scanner.nextLine().trim();
                break;
            case 3:
                paymentMethod = PaymentMethod.UPI;
                System.out.print("Enter UPI ID: ");
                paymentDetails = scanner.nextLine().trim();
                break;
            default:
                System.out.println("[ERROR] Invalid choice!");
                return;
        }
        
        // Process payment
        try {
            Map<String, Object> result = parkingService.exitVehicleWithPayment(
                ticketId, paymentMethod, paymentDetails, cashReceived
            );
            
            if (!(boolean) result.getOrDefault("success", false)) {
                System.out.println("\n[ERROR] " + result.get("message"));
                return;
            }
            
            System.out.println("\n[SUCCESS] Payment successful!");
            System.out.println("+===========================================+");
            System.out.println("|          PAYMENT RECEIPT                  |");
            System.out.println("+===========================================+");
            System.out.println("| Ticket ID: " + String.format("%-30s", ticketId) + "|");
            System.out.println("| Amount Paid: Rs." + String.format("%-24.2f", result.get("amount")) + "|");
            System.out.println("| Payment Method: " + String.format("%-24s", paymentMethod) + "|");
            if (paymentMethod == PaymentMethod.CASH && result.containsKey("change")) {
                System.out.println("| Change: Rs." + String.format("%-29.2f", result.get("change")) + "|");
            }
            if (result.containsKey("discountApplied") && (boolean) result.get("discountApplied")) {
                System.out.println("| [DISCOUNT] Loyalty Discount Applied      |");
            }
            System.out.println("+===========================================+");
            System.out.println("\n[INFO] Data automatically saved to files");
            System.out.println("Thank you for using our parking system!");
        } catch (Exception e) {
            System.out.println("\n[ERROR] Error processing payment: " + e.getMessage());
        }
    }
    
    /**
     * Creates a new reservation
     */
    private static void createReservation() {
        System.out.println("\n" + "=".repeat(45));
        System.out.println("          CREATE RESERVATION");
        System.out.println("=".repeat(45));
        
        Long customerId = getLongInput("Enter customer ID: ");
        
        System.out.println("\nSelect vehicle type:");
        System.out.println("1. Car");
        System.out.println("2. Motorcycle");
        System.out.println("3. Truck");
        int typeChoice = getIntInput("Enter choice (1-3): ");
        
        VehicleType vehicleType;
        switch (typeChoice) {
            case 1: vehicleType = VehicleType.CAR; break;
            case 2: vehicleType = VehicleType.MOTORCYCLE; break;
            case 3: vehicleType = VehicleType.TRUCK; break;
            default:
                System.out.println("[ERROR] Invalid choice!");
                return;
        }
        
        int validityHours = getIntInput("Enter validity hours (1-24): ");
        
        if (validityHours < 1 || validityHours > 24) {
            System.out.println("[ERROR] Invalid hours! Must be between 1-24");
            return;
        }
        
        try {
            Reservation reservation = parkingService.createReservation(
                customerId, vehicleType, validityHours
            );
            
            if (reservation == null) {
                System.out.println("\n[ERROR] No available space for reservation");
                return;
            }
            
            System.out.println("\n[SUCCESS] Reservation created successfully!");
            System.out.println("+===========================================+");
            System.out.println("|          RESERVATION DETAILS              |");
            System.out.println("+===========================================+");
            System.out.println("| Reservation ID: " + String.format("%-26s", reservation.getReservationId()) + "|");
            System.out.println("| Space: " + String.format("%-34s", reservation.getReservedSpace().getSpaceId()) + "|");
            System.out.println("| Valid Until: " + String.format("%-28s", reservation.getExpiresAt()) + "|");
            System.out.println("+===========================================+");
            System.out.println("\n[IMPORTANT] Use this Reservation ID when parking!");
            System.out.println("[INFO] Reservation saved to file");
        } catch (Exception e) {
            System.out.println("\n[ERROR] Error creating reservation: " + e.getMessage());
        }
    }
    
    /**
     * Displays all active reservations
     */
    private static void viewActiveReservations() {
        System.out.println("\n" + "=".repeat(45));
        System.out.println("          ACTIVE RESERVATIONS");
        System.out.println("=".repeat(45));
        
        parkingService.expireOldReservations();
        List<Reservation> reservations = parkingService.getActiveReservations();
        
        if (reservations.isEmpty()) {
            System.out.println("\n[INFO] No active reservations.");
            return;
        }
        
        System.out.println("\nTotal Active Reservations: " + reservations.size());
        
        for (Reservation r : reservations) {
            System.out.println("\n[RESERVATION] ID: " + r.getReservationId());
            System.out.println("   Customer: " + r.getCustomer().getName() + " (ID: " + r.getCustomer().getId() + ")");
            System.out.println("   Space: " + r.getReservedSpace().getSpaceId());
            System.out.println("   Vehicle Type: " + r.getVehicleType());
            System.out.println("   Created: " + r.getCreatedAt());
            System.out.println("   Expires At: " + r.getExpiresAt());
            System.out.println("   " + "-".repeat(40));
        }
    }
    
    /**
     * Displays reports and analytics
     */
    private static void viewReports() {
        System.out.println("\n" + "=".repeat(45));
        System.out.println("          REPORTS & ANALYTICS");
        System.out.println("=".repeat(45));
        
        Map<String, Object> revenue = parkingService.getRevenueReport();
        Map<VehicleType, Map<String, Integer>> availability = parkingService.getAvailability();
        
        System.out.println("\n[REVENUE REPORT]");
        System.out.println("   Total Revenue: Rs." + String.format("%.2f", revenue.get("totalRevenue")));
        System.out.println("   Total Tickets: " + revenue.get("totalTickets"));
        System.out.println("   Average Amount: Rs." + String.format("%.2f", revenue.get("averageAmount")));
        
        System.out.println("\n[PAYMENT BREAKDOWN]");
        @SuppressWarnings("unchecked")
        Map<PaymentMethod, Double> breakdown = (Map<PaymentMethod, Double>) revenue.get("paymentBreakdown");
        breakdown.forEach((method, amount) -> 
            System.out.println("   " + method + ": Rs." + String.format("%.2f", amount))
        );
        
        System.out.println("\n[PARKING AVAILABILITY]");
        availability.forEach((type, stats) -> {
            int total = stats.get("total");
            int occupied = stats.get("occupied");
            int available = stats.get("available");
            double occupancyRate = total > 0 ? (occupied * 100.0 / total) : 0.0;
            
            System.out.println("\n   " + type + ":");
            System.out.println("      Total: " + total);
            System.out.println("      Occupied: " + occupied);
            System.out.println("      Available: " + available);
            System.out.println("      Occupancy Rate: " + String.format("%.1f%%", occupancyRate));
        });
    }
    
    /**
     * Visualizes parking space layout
     */
    private static void visualizeParkingSpaces() {
        System.out.println("\n" + "=".repeat(45));
        System.out.println("          PARKING VISUALIZATION");
        System.out.println("=".repeat(45));
        System.out.println("\n[A] = Available  [O] = Occupied  [R] = Reserved  [M] = Maintenance");
        
        Map<Integer, List<ParkingSpace>> floorMap = parkingService.getSpacesByFloor();
        
        floorMap.forEach((floor, spaces) -> {
            System.out.println("\n[FLOOR " + floor + "]");
            System.out.print("   ");
            
            int count = 0;
            for (ParkingSpace space : spaces) {
                String icon;
                switch (space.getStatus()) {
                    case AVAILABLE: icon = "[A]"; break;
                    case OCCUPIED: icon = "[O]"; break;
                    case RESERVED: icon = "[R]"; break;
                    case MAINTENANCE: icon = "[M]"; break;
                    default: icon = "[?]";
                }
                System.out.print(icon + " ");
                
                count++;
                if (count % 10 == 0) {
                    System.out.print("\n   ");
                }
            }
            System.out.println("\n   Total spaces on floor: " + spaces.size());
        });
    }
    
    /**
     * Manually saves all data to files
     */
    private static void saveDataToFiles() {
        System.out.println("\n[INFO] Saving all data to files...");
        try {
            parkingService.saveAllDataToFiles();
            System.out.println("[SUCCESS] All data saved successfully!");
            System.out.println("[INFO] Files location: data/ directory");
        } catch (Exception e) {
            System.out.println("[ERROR] Error saving data: " + e.getMessage());
        }
    }
    
    /**
     * Exits the system
     */
    private static void exitSystem() {
        System.out.println("\n" + "=".repeat(45));
        System.out.print("Save data before exiting? (yes/no): ");
        String choice = scanner.nextLine().trim();
        
        if (choice.equalsIgnoreCase("yes") || choice.equalsIgnoreCase("y")) {
            saveDataToFiles();
        }
        
        System.out.println("\n+===========================================+");
        System.out.println("| Thank you for using Parking System!      |");
        System.out.println("| You can now safely start the web app     |");
        System.out.println("+===========================================+\n");
        scanner.close();
    }
    
    // ===== HELPER METHODS =====
    
    private static int getIntInput(String prompt) {
        System.out.print(prompt);
        while (!scanner.hasNextInt()) {
            scanner.next();
            System.out.print("[ERROR] Invalid input! " + prompt);
        }
        int value = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        return value;
    }
    
    private static long getLongInput(String prompt) {
        System.out.print(prompt);
        while (!scanner.hasNextLong()) {
            scanner.next();
            System.out.print("[ERROR] Invalid input! " + prompt);
        }
        long value = scanner.nextLong();
        scanner.nextLine(); // Consume newline
        return value;
    }
    
    private static double getDoubleInput(String prompt) {
        System.out.print(prompt);
        while (!scanner.hasNextDouble()) {
            scanner.next();
            System.out.print("[ERROR] Invalid input! " + prompt);
        }
        double value = scanner.nextDouble();
        scanner.nextLine(); // Consume newline
        return value;
    }
}