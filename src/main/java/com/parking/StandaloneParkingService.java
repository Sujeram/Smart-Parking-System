package com.parking;

import com.parking.model.*;
import com.parking.service.FileStorageService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Standalone Parking Service for Console Application
 * Works without Spring Boot and Database - Uses only files and in-memory storage
 * 
 * @version 1.0 - Console Only
 */
public class StandaloneParkingService {

    // In-memory storage (no database)
    private List<Customer> customers = new ArrayList<>();
    private List<ParkingSpace> parkingSpaces = new ArrayList<>();
    private List<ParkingTicket> tickets = new ArrayList<>();
    private List<Reservation> reservations = new ArrayList<>();
    private List<Vehicle> vehicles = new ArrayList<>();
    
    private FileStorageService fileStorage;
    
    private int ticketCounter = 1;
    private int reservationCounter = 1;
    private final boolean weekendPricingEnabled = true;
    private final double weekendMultiplier = 1.2;

    /**
     * Constructor - initializes file storage and loads data
     */
    public StandaloneParkingService() {
        fileStorage = new FileStorageService();
        initialize();
    }

    /**
     * Initializes parking system - loads from files or creates new spaces
     */
    public void initialize() {
        System.out.println("[INFO] Initializing Standalone Parking System...");
        
        try {
            // Try to load existing data from files
            parkingSpaces = fileStorage.loadParkingSpaces();
            customers = fileStorage.loadCustomers();
            tickets = fileStorage.loadTickets();
            reservations = fileStorage.loadReservations();
            
            if (parkingSpaces.isEmpty()) {
                System.out.println("[INFO] No parking spaces found, creating default layout...");
                createDefaultParkingSpaces();
                saveAllDataToFiles();
            } else {
                System.out.println("[SUCCESS] Loaded " + parkingSpaces.size() + " parking spaces from file");
                System.out.println("[SUCCESS] Loaded " + customers.size() + " customers from file");
                System.out.println("[SUCCESS] Loaded " + tickets.size() + " tickets from file");
                System.out.println("[SUCCESS] Loaded " + reservations.size() + " reservations from file");
            }
            
            // Set counters based on existing data
            if (!tickets.isEmpty()) {
                ticketCounter = tickets.stream()
                    .map(t -> Integer.parseInt(t.getTicketId().substring(1)))
                    .max(Integer::compareTo)
                    .orElse(0) + 1;
            }
            if (!reservations.isEmpty()) {
                reservationCounter = reservations.stream()
                    .map(r -> Integer.parseInt(r.getReservationId().substring(1)))
                    .max(Integer::compareTo)
                    .orElse(0) + 1;
            }
            
        } catch (Exception e) {
            System.out.println("[INFO] No backup files found, creating fresh parking spaces...");
            createDefaultParkingSpaces();
            saveAllDataToFiles();
        }
    }

    /**
     * Creates default parking space layout
     */
    private void createDefaultParkingSpaces() {
        parkingSpaces.clear();
        
        for (int floor = 1; floor <= 3; floor++) {
            // Regular car spaces (8 per floor)
            for (int i = 1; i <= 8; i++) {
                String spaceId = "F" + floor + "-A-" + i;
                parkingSpaces.add(new ParkingSpace(spaceId, floor, "A", VehicleType.CAR, false));
            }

            // Handicapped car spaces (2 per floor)
            for (int i = 9; i <= 10; i++) {
                String spaceId = "F" + floor + "-A-" + i;
                parkingSpaces.add(new ParkingSpace(spaceId, floor, "A", VehicleType.CAR, true));
            }

            // Motorcycles (15 per floor)
            for (int i = 1; i <= 15; i++) {
                String spaceId = "F" + floor + "-B-" + i;
                parkingSpaces.add(new ParkingSpace(spaceId, floor, "B", VehicleType.MOTORCYCLE, false));
            }

            // Trucks (5 per floor)
            for (int i = 1; i <= 5; i++) {
                String spaceId = "F" + floor + "-C-" + i;
                parkingSpaces.add(new ParkingSpace(spaceId, floor, "C", VehicleType.TRUCK, false));
            }
        }
        
        System.out.println("[SUCCESS] Created " + parkingSpaces.size() + " parking spaces");
    }

    /**
     * Saves all data to files
     */
    public void saveAllDataToFiles() {
        try {
            fileStorage.saveCustomers(customers);
            fileStorage.saveTickets(tickets);
            fileStorage.saveReservations(reservations);
            fileStorage.saveParkingSpaces(parkingSpaces);
            System.out.println("[SUCCESS] Data saved to files");
        } catch (Exception e) {
            System.err.println("[ERROR] Could not save data: " + e.getMessage());
        }
    }

    // ============================================================
    // CUSTOMER MANAGEMENT
    // ============================================================
    
    public Customer registerCustomer(String name, String contact, String email, boolean handicapped) {
        // Generate new ID
        Long newId = customers.stream()
            .map(Customer::getId)
            .max(Long::compareTo)
            .orElse(0L) + 1;
        
        Customer customer = new Customer(name, contact, email, handicapped);
        customer.setId(newId);
        customers.add(customer);
        
        saveAllDataToFiles();
        return customer;
    }

    public Optional<Customer> findCustomerByContact(String contact) {
        return customers.stream()
            .filter(c -> c.getContact().equals(contact))
            .findFirst();
    }

    public List<Customer> getAllCustomers() {
        return new ArrayList<>(customers);
    }

    // ============================================================
    // RESERVATION SYSTEM
    // ============================================================
    
    public Reservation createReservation(Long customerId, VehicleType type, int validityHours) {
        Optional<Customer> optCustomer = customers.stream()
            .filter(c -> c.getId().equals(customerId))
            .findFirst();
        
        if (optCustomer.isEmpty()) return null;

        Customer customer = optCustomer.get();
        List<ParkingSpace> availableSpaces = findOptimalSpace(customer, type);

        if (availableSpaces.isEmpty()) return null;

        ParkingSpace space = availableSpaces.get(0);
        space.reserve();

        String resId = "R" + String.format("%03d", reservationCounter++);
        Reservation reservation = new Reservation(resId, customer, space, type, validityHours);
        reservations.add(reservation);
        
        saveAllDataToFiles();
        return reservation;
    }

    public List<Reservation> getActiveReservations() {
        return reservations.stream()
                .filter(r -> !r.isUsed() && r.isValid())
                .collect(Collectors.toList());
    }

    public void expireOldReservations() {
        boolean hasChanges = false;
        
        for (Reservation res : reservations) {
            if (!res.isUsed() && res.isExpired()) {
                ParkingSpace space = res.getReservedSpace();
                space.releaseSpace();
                res.setUsed(true);
                hasChanges = true;
            }
        }
        
        if (hasChanges) {
            saveAllDataToFiles();
        }
    }

    // ============================================================
    // PARKING OPERATIONS
    // ============================================================
    
    public ParkingTicket parkVehicleWithCustomer(String licensePlate, VehicleType type,
                                                 String contact, Long customerId, String reservationId) {
        expireOldReservations();

        Customer customer = null;
        if (customerId != null) {
            customer = customers.stream()
                .filter(c -> c.getId().equals(customerId))
                .findFirst()
                .orElse(null);
        }

        Vehicle vehicle = new Vehicle(licensePlate, type, contact);
        vehicles.add(vehicle);

        ParkingSpace space = null;

        // Check for reservation first
        if (reservationId != null && !reservationId.isEmpty()) {
            Optional<Reservation> optRes = reservations.stream()
                .filter(r -> r.getReservationId().equals(reservationId))
                .findFirst();
            
            if (optRes.isPresent() && optRes.get().isValid()) {
                Reservation res = optRes.get();
                space = res.getReservedSpace();
                res.setUsed(true);
            }
        }

        // Find optimal space if no reservation
        if (space == null) {
            List<ParkingSpace> availableSpaces = findOptimalSpace(customer, type);
            if (availableSpaces.isEmpty()) return null;
            space = availableSpaces.get(0);
        }

        space.occupySpace(vehicle);

        String ticketId = "T" + String.format("%03d", ticketCounter++);
        ParkingTicket ticket = new ParkingTicket(ticketId, vehicle, space, customer);
        tickets.add(ticket);
        
        saveAllDataToFiles();
        return ticket;
    }

    public List<ParkingTicket> getActiveTickets() {
        return tickets.stream()
            .filter(t -> t.getStatus() == TicketStatus.ACTIVE)
            .collect(Collectors.toList());
    }

    // ============================================================
    // EXIT AND PAYMENT
    // ============================================================
    
    public Map<String, Object> calculateExitPreview(String ticketId) {
        Map<String, Object> preview = new HashMap<>();
        
        Optional<ParkingTicket> optTicket = tickets.stream()
            .filter(t -> t.getTicketId().equals(ticketId))
            .findFirst();
        
        if (optTicket.isEmpty()) {
            preview.put("success", false);
            preview.put("message", "Ticket not found");
            return preview;
        }

        ParkingTicket ticket = optTicket.get();
        long hours = ChronoUnit.HOURS.between(ticket.getEntryTime(), LocalDateTime.now());
        if (hours == 0) hours = 1;

        double rate = getHourlyRate(ticket.getVehicle().getType());
        double amount = hours * rate;

        boolean isWeekend = isWeekend(LocalDateTime.now());
        if (weekendPricingEnabled && isWeekend) {
            amount *= weekendMultiplier;
        }

        boolean hasDiscount = ticket.getCustomer() != null && ticket.getCustomer().hasLoyaltyDiscount();
        if (hasDiscount) {
            amount *= 0.9;
        }

        preview.put("success", true);
        preview.put("amount", amount);
        preview.put("hours", hours);
        preview.put("isWeekend", isWeekend);
        preview.put("hasDiscount", hasDiscount);

        return preview;
    }

    public Map<String, Object> exitVehicleWithPayment(String ticketId, PaymentMethod paymentMethod,
                                                      String paymentDetails, double cashReceived) {
        Map<String, Object> result = new HashMap<>();

        Optional<ParkingTicket> optTicket = tickets.stream()
            .filter(t -> t.getTicketId().equals(ticketId))
            .findFirst();
        
        if (optTicket.isEmpty()) {
            result.put("success", false);
            result.put("message", "Ticket not found");
            return result;
        }

        ParkingTicket ticket = optTicket.get();
        ticket.setExitTime(LocalDateTime.now());

        long hours = ChronoUnit.HOURS.between(ticket.getEntryTime(), LocalDateTime.now());
        if (hours == 0) hours = 1;

        double rate = getHourlyRate(ticket.getVehicle().getType());
        double amount = hours * rate;

        // Weekend pricing
        if (weekendPricingEnabled && isWeekend(ticket.getExitTime())) {
            amount *= weekendMultiplier;
        }

        // Loyalty discount
        boolean discountApplied = false;
        if (ticket.getCustomer() != null && ticket.getCustomer().hasLoyaltyDiscount()) {
            amount *= 0.9;
            discountApplied = true;
            ticket.setLoyaltyDiscountApplied(true);
        }

        ticket.setAmount(amount);
        ticket.setPaymentMethod(paymentMethod);
        ticket.setPaymentDetails(paymentDetails);

        // Process cash payment
        if (paymentMethod == PaymentMethod.CASH) {
            if (cashReceived < amount) {
                result.put("success", false);
                result.put("message", "Insufficient cash. Required: Rs." + amount);
                return result;
            }
            ticket.setCashReceived(cashReceived);
            ticket.setChangeReturned(cashReceived - amount);
            result.put("change", cashReceived - amount);
        }

        // Award loyalty points
        if (ticket.getCustomer() != null) {
            int pointsEarned = (int) (amount / 10);
            ticket.getCustomer().addLoyaltyPoints(pointsEarned);
            ticket.setLoyaltyPointsEarned(pointsEarned);
        }

        ticket.setStatus(TicketStatus.PAID);

        // Release space
        ParkingSpace space = ticket.getAllocatedSpace();
        space.releaseSpace();

        saveAllDataToFiles();

        result.put("success", true);
        result.put("ticket", ticket);
        result.put("amount", amount);
        result.put("discountApplied", discountApplied);

        return result;
    }

    // ============================================================
    // REPORTS AND ANALYTICS
    // ============================================================
    
    public Map<String, Object> getRevenueReport() {
        List<ParkingTicket> paidTickets = tickets.stream()
            .filter(t -> t.getStatus() == TicketStatus.PAID)
            .collect(Collectors.toList());

        double totalRevenue = paidTickets.stream()
                .mapToDouble(ParkingTicket::getAmount)
                .sum();

        Map<PaymentMethod, Double> paymentBreakdown = new HashMap<>();
        for (PaymentMethod method : PaymentMethod.values()) {
            double methodTotal = paidTickets.stream()
                    .filter(t -> t.getPaymentMethod() == method)
                    .mapToDouble(ParkingTicket::getAmount)
                    .sum();
            paymentBreakdown.put(method, methodTotal);
        }

        Map<String, Object> report = new HashMap<>();
        report.put("totalRevenue", totalRevenue);
        report.put("totalTickets", paidTickets.size());
        report.put("paymentBreakdown", paymentBreakdown);
        report.put("averageAmount", totalRevenue / Math.max(1, paidTickets.size()));

        return report;
    }

    public Map<VehicleType, Map<String, Integer>> getAvailability() {
        Map<VehicleType, Map<String, Integer>> availability = new HashMap<>();

        for (VehicleType type : VehicleType.values()) {
            Map<String, Integer> stats = new HashMap<>();

            List<ParkingSpace> allSpaces = parkingSpaces.stream()
                .filter(s -> s.getCompatibleVehicleType() == type)
                .collect(Collectors.toList());
            
            List<ParkingSpace> availableSpaces = allSpaces.stream()
                .filter(s -> s.getStatus() == SpaceStatus.AVAILABLE)
                .collect(Collectors.toList());
            
            List<ParkingSpace> occupiedSpaces = allSpaces.stream()
                .filter(s -> s.getStatus() == SpaceStatus.OCCUPIED)
                .collect(Collectors.toList());

            stats.put("total", allSpaces.size());
            stats.put("available", availableSpaces.size());
            stats.put("occupied", occupiedSpaces.size());

            availability.put(type, stats);
        }

        return availability;
    }

    public Map<Integer, List<ParkingSpace>> getSpacesByFloor() {
        Map<Integer, List<ParkingSpace>> floorMap = new HashMap<>();

        for (ParkingSpace space : parkingSpaces) {
            floorMap.computeIfAbsent(space.getFloorId(), k -> new ArrayList<>()).add(space);
        }

        return floorMap;
    }

    // ============================================================
    // HELPER METHODS
    // ============================================================
    
    private List<ParkingSpace> findOptimalSpace(Customer customer, VehicleType type) {
        List<ParkingSpace> availableSpaces;

        if (customer != null && customer.isHandicapped()) {
            availableSpaces = parkingSpaces.stream()
                .filter(s -> s.getStatus() == SpaceStatus.AVAILABLE)
                .filter(s -> s.getCompatibleVehicleType() == type)
                .filter(ParkingSpace::isHandicappedSpace)
                .collect(Collectors.toList());
            
            if (availableSpaces.isEmpty()) {
                availableSpaces = parkingSpaces.stream()
                    .filter(s -> s.getStatus() == SpaceStatus.AVAILABLE)
                    .filter(s -> s.getCompatibleVehicleType() == type)
                    .collect(Collectors.toList());
            }
        } else {
            availableSpaces = parkingSpaces.stream()
                .filter(s -> s.getStatus() == SpaceStatus.AVAILABLE)
                .filter(s -> s.getCompatibleVehicleType() == type)
                .collect(Collectors.toList());
        }

        return availableSpaces;
    }

    private double getHourlyRate(VehicleType type) {
        return switch (type) {
            case CAR -> 20.0;
            case MOTORCYCLE -> 10.0;
            case TRUCK -> 30.0;
        };
    }

    private boolean isWeekend(LocalDateTime dateTime) {
        int dayOfWeek = dateTime.getDayOfWeek().getValue();
        return dayOfWeek == 6 || dayOfWeek == 7;
    }
}