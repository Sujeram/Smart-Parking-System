# Parking Management System

A comprehensive parking lot management system with both **Web Application** (Spring Boot + Thymeleaf) and **Console-based** versions. Features include customer management, vehicle parking, reservations, payment processing, and real-time availability tracking with persistent data storage.

## ✨ Features

### Core Features
- ✅ Multi-floor parking lot with configurable capacity
- ✅ Support for multiple vehicle types (Car, Bike, Truck)
- ✅ Intelligent parking spot allocation
- ✅ Automated ticket generation with unique IDs
- ✅ Time-based parking fee calculation
- ✅ Real-time parking availability tracking

### Advanced Features
- 🎫 **Reservation System** - Pre-book parking spots
- 👥 **Customer Management** - Register and manage customer accounts
- 💳 **Payment Processing** - Multiple payment methods support
- 📊 **Reports & Analytics** - View parking statistics and reports
- 🌐 **Web Interface** - User-friendly HTML templates with Thymeleaf
- 💻 **Console Application** - Standalone command-line version
- 💾 **Dual Storage** - JSON files + Database support

## 🛠 Technologies Used

- **Java**: 17
- **Spring Boot**: 3.x (Web Application)
- **Maven**: 3.6+ (Build & Dependency Management)
- **Thymeleaf**: Template Engine (Web UI)
- **Gson**: 2.8.9 (JSON Processing)
- **Database**: SQLite/H2 (for web application)
- **JUnit 5**: Testing Framework
- **HTML/CSS**: Frontend Templates

## 📦 Prerequisites

### Required (Manual Installation)

1. **Java Development Kit (JDK) 17 or higher**
   - Download: [Oracle JDK](https://www.oracle.com/java/technologies/downloads/) or [OpenJDK](https://adoptium.net/)
   - Verify: `java -version`
   - Expected: `java version "17.x.x"` or higher

2. **Apache Maven 3.6 or higher**
   - Download: [Maven Official Site](https://maven.apache.org/download.cgi)
   - Verify: `mvn -version`
   - Expected: `Apache Maven 3.x.x`

### Auto-Installed (by Maven)

✅ Spring Boot Framework  
✅ Thymeleaf Template Engine  
✅ Gson Library  
✅ JUnit 5  
✅ Database Drivers  
✅ All other dependencies from `pom.xml`

**Note**: Internet connection required for first-time build to download dependencies (~150 MB).

## 🚀 Installation & Setup

### Step 1: Extract the Project

```bash
# Extract the zip file
unzip PARKING-WEB.zip

# Navigate to project directory
cd PARKING-WEB
```

### Step 2: Build the Project

```bash
# This command will:
# - Download all dependencies (Spring Boot, Thymeleaf, Gson, etc.)
# - Compile the source code
# - Run all unit tests
# - Package the application

mvn clean install
```

**Expected Output:**
```
[INFO] Downloading from central: https://repo.maven.apache.org/...
[INFO] Building jar: target/parking-web-1.0-SNAPSHOT.jar
[INFO] Tests run: 15, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
[INFO] Total time: 45.123 s
```

## ▶️ Running the Application

### Web Application 

```bash
# Run Spring Boot web application
mvn spring-boot:run
```

**Access the application:**
- 🌐 Open browser: `http://localhost:8080`
- 📱 Web Interface with all features
- 💾 Data stored in database + JSON

**Available Web Pages:**
- `/` - Home/Index page
- `/customer-register.html` - Register new customer
- `/reserve.html` - Reserve parking spot
- `/park.html` - Park vehicle
- `/exit.html` - Exit and payment
- `/tickets.html` - View all tickets
- `/customers-list.html` - View customers
- `/reservations-list.html` - View reservations
- `/reports.html` - Analytics and reports
- `/visualize.html` - Visual parking layout


**Features:**
- 💻 Command-line interface
- 📝 Text-based menu system
- 💾 Data stored in JSON only


## 📁 Project Structure

PARKING-WEB/
├── src/
│   ├── main/
│   │   ├── java/com/parking/
│   │   │   ├── ParkingWebApplication.java     # Spring Boot main class
│   │   │   ├── ParkingSystemConsole.java      # Console application
│   │   │   ├── StandaloneParkingService.java  # Service helper
│   │   │   ├── controller/
│   │   │   │   └── ParkingWebController.java  # Web endpoints
│   │   │   ├── model/
│   │   │   │   ├── AvailabilityStats.java     # Parking stats
│   │   │   │   ├── Customer.java              # Customer entity
│   │   │   │   ├── ParkingSpace.java          # Parking spot
│   │   │   │   ├── ParkingTicket.java         # Ticket entity
│   │   │   │   ├── PaymentMethod.java         # Payment types
│   │   │   │   ├── Reservation.java           # Reservation entity
│   │   │   │   ├── SpaceStatus.java           # Spot status enum
│   │   │   │   ├── TicketStatus.java          # Ticket status enum
│   │   │   │   ├── Vehicle.java               # Vehicle entity
│   │   │   │   └── VehicleType.java           # Vehicle types
│   │   │   ├── repository/
│   │   │   │   ├── CustomerRepository.java    # Customer data access
│   │   │   │   ├── ParkingSpaceRepository.java # Space data access
│   │   │   │   ├── ParkingTicketRepository.java # Ticket data access
│   │   │   │   ├── ReservationRepository.java # Reservation data
│   │   │   │   └── VehicleRepository.java     # Vehicle data access
│   │   │   └── service/
│   │   │       ├── FileStorageService.java    # File storage operations
│   │   │       └── ParkingService.java        # Core parking logic
│   │   └── resources/
│   │       ├── templates/                         # Thymeleaf HTML pages
│   │       │   ├── customer-register.html         # Customer registration
│   │       │   ├── customer-success.html          # Success confirmation
│   │       │   ├── customers-list.html            # All customers
│   │       │   ├── error.html                     # Error page
│   │       │   ├── exit-payment.html              # Payment page
│   │       │   ├── exit-result.html               # Payment result
│   │       │   ├── exit.html                      # Exit vehicle
│   │       │   ├── index.html                     # Home page
│   │       │   ├── park-result.html               # Park confirmation
│   │       │   ├── park.html                      # Park vehicle
│   │       │   ├── payment-result.html            # Payment success
│   │       │   ├── reports.html                   # Analytics
│   │       │   ├── reservations-list.html         # All reservations
│   │       │   ├── reserve-result.html            # Reserve confirmation
│   │       │   ├── reserve.html                   # Make reservation
│   │       │   ├── tickets.html                   # All tickets
│   │       │   └── visualize.html                 # Parking visualization
│   │       └── application.properties             # Configuration
│   └── test/
│       └── java/com/parking/test/
│           └── ParkingServiceTest.java            # Unit tests (15 cases)
├── target/                                        # Build output (excluded)
├── data/                                          # Runtime data (excluded)
├── pom.xml                                        # Maven dependencies
└── README.md                                      # This file

## 💾 Data Persistence

### Web Application
- **Database**: SQLite/H2 database for structured data
  - Customers, vehicles, tickets, reservations
- **JSON Files**: Backup and export functionality
- **Location**: `data/` directory (auto-created)


**Note**: The `data/` folder is created automatically on first run and should be excluded from version control.

## ⚙️ Configuration

Edit `src/main/resources/application.properties`:

```properties
# Server Configuration
server.port=8080

# Database Configuration
spring.datasource.url=jdbc:h2:file:./data/parkingdb
spring.datasource.driver-class-name=org.h2.Driver
spring.jpa.hibernate.ddl-auto=update

# Thymeleaf Configuration
spring.thymeleaf.cache=false
spring.thymeleaf.enabled=true

# Parking Configuration
parking.floors=3
parking.spots.per.floor=10

# Fee Structure (per hour)
parking.fee.car=20
parking.fee.bike=10
parking.fee.truck=50

# Logging
logging.level.com.parking=INFO
```

## 🔍 Troubleshooting

### Port Already in Use
```bash
# Change port in application.properties
server.port=8081
```

### Database Issues
```bash
# Delete database and restart (data will be lost)
rm -rf data/parkingdb*
mvn spring-boot:run
```

### Maven Build Fails
```bash
# Clean and rebuild
mvn clean
mvn clean install -U
```

### Can't Access Web Interface
- Verify application is running: Check console for "Started ParkingWebApplication"
- Check URL: Must be `http://localhost:8080` (include http://)
- Check firewall: Ensure port 8080 is not blocked

## 📝 Usage Guide

### Web Application Workflow

1. **Start Application**
   ```bash
   mvn spring-boot:run
   ```

2. **Register Customer** (Optional)
   - Navigate to `/customer-register.html`
   - Fill in details and submit

3. **Park Vehicle**
   - Go to `/park.html`
   - Enter vehicle number and type
   - System assigns spot and generates ticket

4. **Exit & Payment**
   - Go to `/exit.html`
   - Enter ticket ID
   - System calculates fee
   - Process payment

5. **View Reports**
   - Access `/reports.html` for analytics
   - View `/tickets.html` for all tickets
   - Check `/visualize.html` for parking layout


## 👨‍💻 Author

Subhiksha 

## 📄 License

This project is developed for educational purposes.




