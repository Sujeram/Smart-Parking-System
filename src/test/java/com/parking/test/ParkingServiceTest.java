package com.parking.test;

import com.parking.model.*;
import com.parking.service.ParkingService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit test cases for Parking Management System
 * Tests all major functionalities including parking, exit, reservations
 * 
 * @author Subhiksha
 * @version 1.0
 */
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ParkingServiceTest {
    
    @Autowired
    private ParkingService parkingService;
    
    private static Customer testCustomer;
    
    /**
     * Setup method executed once before all tests
     */
    @BeforeAll
    public static void setUpClass() {
        System.out.println("========================================");
        System.out.println("Starting Parking System Tests");
        System.out.println("========================================\n");
    }
    
    /**
     * Setup method executed before each test
     */
    @BeforeEach
    public void setUp() {
        if (testCustomer == null) {
            testCustomer = parkingService.registerCustomer(
                "John Doe", 
                "9876543210", 
                "john@example.com", 
                false
            );
        }
    }
    
    // ===== CUSTOMER REGISTRATION TESTS =====
    
    /**
     * Test Case 1: Successful customer registration
     */
    @Test
    @Order(1)
    @DisplayName("Test: Customer Registration - Valid Data")
    public void testCustomerRegistration() {
        Customer customer = parkingService.registerCustomer(
            "Jane Smith", 
            "9123456780", 
            "jane@example.com", 
            false
        );
        
        assertNotNull(customer, "Customer should not be null");
        assertEquals("Jane Smith", customer.getName(), "Customer name should match");
        assertEquals("jane@example.com", customer.getEmail(), "Email should match");
        assertEquals(0, customer.getLoyaltyPoints(), "Initial loyalty points should be 0");
        
        System.out.println("✓ Test 1: Customer registration passed");
    }
    
    /**
     * Test Case 2: Handicapped customer registration
     */
    @Test
    @Order(2)
    @DisplayName("Test: Handicapped Customer Registration")
    public void testHandicappedCustomerRegistration() {
        Customer customer = parkingService.registerCustomer(
            "Bob Williams", 
            "9988776655", 
            "bob@example.com", 
            true
        );
        
        assertTrue(customer.isHandicapped(), "Customer should be marked as handicapped");
        System.out.println("✓ Test 2: Handicapped customer registration passed");
    }
    
    // ===== PARKING OPERATIONS TESTS =====
    
    /**
     * Test Case 3: Successful vehicle parking
     */
    @Test
    @Order(3)
    @DisplayName("Test: Park Vehicle - Valid Input")
    public void testParkVehicle() {
        ParkingTicket ticket = parkingService.parkVehicleWithCustomer(
            "TN01AB1234", 
            VehicleType.CAR, 
            "9876543210", 
            testCustomer.getId(), 
            null
        );
        
        assertNotNull(ticket, "Ticket should not be null");
        assertNotNull(ticket.getTicketId(), "Ticket ID should be generated");
        assertEquals("TN01AB1234", ticket.getVehicle().getLicensePlate(), 
                     "License plate should match");
        assertNotNull(ticket.getAllocatedSpace(), "Parking space should be allocated");
        
        System.out.println("✓ Test 3: Vehicle parking passed");
    }
    
    /**
     * Test Case 4: Park motorcycle
     */
    @Test
    @Order(4)
    @DisplayName("Test: Park Motorcycle")
    public void testParkMotorcycle() {
        ParkingTicket ticket = parkingService.parkVehicleWithCustomer(
            "TN02CD5678", 
            VehicleType.MOTORCYCLE, 
            "9123456789", 
            null, 
            null
        );
        
        assertNotNull(ticket, "Motorcycle ticket should not be null");
        
        System.out.println("✓ Test 4: Motorcycle parking passed");
    }
    
    /**
     * Test Case 5: Park truck
     */
    @Test
    @Order(5)
    @DisplayName("Test: Park Truck")
    public void testParkTruck() {
        ParkingTicket ticket = parkingService.parkVehicleWithCustomer(
            "TN03EF9012", 
            VehicleType.TRUCK, 
            "9988776655", 
            null, 
            null
        );
        
        assertNotNull(ticket, "Truck ticket should not be null");
        
        System.out.println("✓ Test 5: Truck parking passed");
    }
    
    // ===== RESERVATION TESTS =====
    
    /**
     * Test Case 6: Create valid reservation
     */
    @Test
    @Order(6)
    @DisplayName("Test: Create Reservation - Valid")
    public void testCreateReservation() {
        Reservation reservation = parkingService.createReservation(
            testCustomer.getId(), 
            VehicleType.CAR, 
            2
        );
        
        assertNotNull(reservation, "Reservation should not be null");
        assertNotNull(reservation.getReservationId(), "Reservation ID should be generated");
        
        System.out.println("✓ Test 6: Reservation creation passed");
    }
    
    /**
     * Test Case 7: Park with valid reservation
     */
    @Test
    @Order(7)
    @DisplayName("Test: Park with Reservation")
    public void testParkWithReservation() {
        Reservation reservation = parkingService.createReservation(
            testCustomer.getId(), 
            VehicleType.CAR, 
            1
        );
        
        ParkingTicket ticket = parkingService.parkVehicleWithCustomer(
            "TN04GH3456", 
            VehicleType.CAR, 
            "9876543210", 
            testCustomer.getId(), 
            reservation.getReservationId()
        );
        
        assertNotNull(ticket, "Ticket should be generated with reservation");
        System.out.println("✓ Test 7: Park with reservation passed");
    }
    
    // ===== EXIT AND PAYMENT TESTS =====
    
    /**
     * Test Case 8: Calculate exit fee
     */
    @Test
    @Order(8)
    @DisplayName("Test: Calculate Exit Fee")
    public void testCalculateExitFee() {
        ParkingTicket ticket = parkingService.parkVehicleWithCustomer(
            "TN05IJ7890", 
            VehicleType.CAR, 
            "9123456789", 
            testCustomer.getId(), 
            null
        );
        
        Map<String, Object> preview = parkingService.calculateExitPreview(ticket.getTicketId());
        
        assertTrue((boolean) preview.get("success"), "Preview calculation should succeed");
        assertNotNull(preview.get("amount"), "Amount should be calculated");
        
        System.out.println("✓ Test 8: Exit fee calculation passed");
    }
    
    /**
     * Test Case 9: Process payment - Cash
     */
    @Test
    @Order(9)
    @DisplayName("Test: Process Payment - Cash")
    public void testProcessPaymentCash() {
        ParkingTicket ticket = parkingService.parkVehicleWithCustomer(
            "TN06KL1234", 
            VehicleType.CAR, 
            "9988776655", 
            testCustomer.getId(), 
            null
        );
        
        Map<String, Object> result = parkingService.exitVehicleWithPayment(
            ticket.getTicketId(), 
            PaymentMethod.CASH, 
            null, 
            100.0
        );
        
        assertTrue((boolean) result.get("success"), "Payment should succeed");
        
        System.out.println("✓ Test 9: Cash payment passed");
    }
    
    /**
     * Test Case 10: Process payment - Card
     */
    @Test
    @Order(10)
    @DisplayName("Test: Process Payment - Card")
    public void testProcessPaymentCard() {
        ParkingTicket ticket = parkingService.parkVehicleWithCustomer(
            "TN07MN5678", 
            VehicleType.CAR, 
            "9123456789", 
            testCustomer.getId(), 
            null
        );
        
        Map<String, Object> result = parkingService.exitVehicleWithPayment(
            ticket.getTicketId(), 
            PaymentMethod.CARD, 
            "1234", 
            0.0
        );
        
        assertTrue((boolean) result.get("success"), "Card payment should succeed");
        System.out.println("✓ Test 10: Card payment passed");
    }
    
    /**
     * Test Case 11: Process payment - UPI
     */
    @Test
    @Order(11)
    @DisplayName("Test: Process Payment - UPI")
    public void testProcessPaymentUPI() {
        ParkingTicket ticket = parkingService.parkVehicleWithCustomer(
            "TN08OP9012", 
            VehicleType.CAR, 
            "9988776655", 
            testCustomer.getId(), 
            null
        );
        
        Map<String, Object> result = parkingService.exitVehicleWithPayment(
            ticket.getTicketId(), 
            PaymentMethod.UPI, 
            "john@upi", 
            0.0
        );
        
        assertTrue((boolean) result.get("success"), "UPI payment should succeed");
        System.out.println("✓ Test 11: UPI payment passed");
    }
    
    // ===== AVAILABILITY TESTS =====
    
    /**
     * Test Case 12: Check parking availability
     */
    @Test
    @Order(12)
    @DisplayName("Test: Check Parking Availability")
    public void testParkingAvailability() {
        Map<VehicleType, Map<String, Integer>> availability = parkingService.getAvailability();
        
        assertNotNull(availability, "Availability map should not be null");
        assertTrue(availability.containsKey(VehicleType.CAR), "Should have car availability");
        assertTrue(availability.containsKey(VehicleType.MOTORCYCLE), 
                   "Should have motorcycle availability");
        assertTrue(availability.containsKey(VehicleType.TRUCK), 
                   "Should have truck availability");
        
        System.out.println("✓ Test 12: Availability check passed");
    }
    
    // ===== NEGATIVE TEST CASES =====
    
    /**
     * Test Case 13: Invalid ticket ID for exit
     */
    @Test
    @Order(13)
    @DisplayName("Test: Exit with Invalid Ticket ID")
    public void testExitWithInvalidTicket() {
        Map<String, Object> result = parkingService.calculateExitPreview("INVALID_TICKET");
        
        assertFalse((boolean) result.getOrDefault("success", false), 
                    "Should fail with invalid ticket");
        
        System.out.println("✓ Test 13: Invalid ticket test passed");
    }
    
    /**
     * Test Case 14: Get all customers
     */
    @Test
    @Order(14)
    @DisplayName("Test: Get All Customers")
    public void testGetAllCustomers() {
        List<Customer> customers = parkingService.getAllCustomers();
        
        assertNotNull(customers, "Customer list should not be null");
        assertTrue(customers.size() > 0, "Should have at least one customer");
        
        System.out.println("✓ Test 14: Get all customers passed");
    }
    
    /**
     * Test Case 15: Get active tickets
     */
    @Test
    @Order(15)
    @DisplayName("Test: Get Active Tickets")
    public void testGetActiveTickets() {
        // Park a vehicle first
        parkingService.parkVehicleWithCustomer(
            "TN99ZZ9999", 
            VehicleType.CAR, 
            "9999999999", 
            null, 
            null
        );
        
        List<ParkingTicket> tickets = parkingService.getActiveTickets();
        
        assertNotNull(tickets, "Tickets list should not be null");
        
        System.out.println("✓ Test 15: Get active tickets passed");
    }
    
    /**
     * Test summary method
     */
    @AfterAll
    public static void testSummary() {
        System.out.println("\n========================================");
        System.out.println("ALL TESTS COMPLETED SUCCESSFULLY!");
        System.out.println("Total Test Cases: 15");
        System.out.println("========================================");
    }
}