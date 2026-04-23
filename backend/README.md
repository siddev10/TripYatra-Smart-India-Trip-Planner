# 🚂 TripYatra — Spring Boot Backend

**Java 17 + Spring Boot 3.2 + JPA/Hibernate + MySQL + JWT + JUnit 5**

---

## 📁 Project Structure (Enterprise Java)

```
backend-spring/
├── pom.xml                              ← Maven build (dependencies)
├── src/main/java/com/tripyatra/
│   ├── TripYatraApplication.java        ← Spring Boot entry point
│   ├── config/
│   │   └── SecurityConfig.java          ← Spring Security + CORS + BCrypt
│   ├── security/
│   │   ├── JwtUtil.java                 ← JWT token generation/validation
│   │   └── JwtAuthFilter.java           ← JWT authentication filter
│   ├── model/                           ← JPA Entities (OOP)
│   │   ├── User.java                    ← @Entity → 'users' table
│   │   └── Trip.java                    ← @Entity → 'trips' table
│   ├── dto/                             ← Data Transfer Objects
│   │   ├── RegisterRequest.java
│   │   ├── LoginRequest.java
│   │   ├── AuthResponse.java
│   │   ├── TrainSearchRequest.java
│   │   ├── TripRequest.java
│   │   └── ItineraryRequest.java
│   ├── repository/                      ← Spring Data JPA Repositories
│   │   ├── UserRepository.java
│   │   └── TripRepository.java
│   ├── service/                         ← Business Logic Layer
│   │   ├── AuthService.java
│   │   ├── TripService.java
│   │   ├── TrainService.java
│   │   └── StationDataService.java
│   ├── controller/                      ← REST API Controllers
│   │   ├── AuthController.java
│   │   ├── TripController.java
│   │   ├── TrainController.java
│   │   ├── PackageController.java
│   │   ├── FlightController.java
│   │   └── HealthController.java
│   └── exception/
│       └── GlobalExceptionHandler.java  ← Centralized error handling
├── src/main/resources/
│   ├── application.properties           ← H2 config (default)
│   ├── application-mysql.properties     ← MySQL config (production)
│   └── all-stations.json                ← 8400+ Indian stations
└── src/test/java/com/tripyatra/
    ├── TripYatraApplicationTests.java   ← Context smoke test
    ├── controller/
    │   └── AuthControllerTest.java      ← Integration tests (MockMvc)
    └── service/
        └── AuthServiceTest.java         ← Unit tests (Mockito)
```

---

## ⚙️ Setup & Run

### Prerequisites
- Java 17+ (you have Java 22 ✓)
- Maven (install via `choco install maven` or download from https://maven.apache.org)

### Step 1 — Install Maven
```powershell
# Option A: Using Chocolatey (recommended)
choco install maven

# Option B: Download manually from https://maven.apache.org/download.cgi
# Unzip and add the bin/ folder to your PATH
```

### Step 2 — Build & Run
```powershell
cd backend-spring

# Build the project
mvn clean install

# Run the server (H2 in-memory database — works out of the box)
mvn spring-boot:run

# OR run with MySQL (requires MySQL on localhost:3306)
mvn spring-boot:run -Dspring-boot.run.profiles=mysql
```

### Step 3 — The server starts at http://localhost:5000
The same port as the Node.js version, so the **frontend works without any changes**.

---

## 🧪 Running Tests
```powershell
# Run all tests
mvn test

# Run with verbose output
mvn test -Dsurefire.useFile=false
```

---

## 🔌 API Endpoints (Same as Node.js version)

| Method | Endpoint               | Auth | Description           |
|--------|------------------------|------|-----------------------|
| POST   | /api/auth/register     | No   | Register new user     |
| POST   | /api/auth/login        | No   | Login, get JWT token  |
| GET    | /api/auth/me           | Yes  | Get current user      |
| GET    | /api/trips             | Yes  | Get all user trips    |
| POST   | /api/trips             | Yes  | Create new trip       |
| DELETE | /api/trips/:id         | Yes  | Delete a trip         |
| POST   | /api/trains/search     | Yes  | AI train search       |
| GET    | /api/packages          | No   | List travel packages  |
| POST   | /api/itinerary/generate| Yes  | AI itinerary          |
| GET    | /api/health            | No   | Server status         |

---

## 🎯 Technologies Used (matching Job Description)

| Requirement          | Implementation                              |
|----------------------|---------------------------------------------|
| **Java**             | Java 17 (compatible with Java 8+ patterns) |
| **Spring**           | Spring Boot 3.2, Spring MVC, Spring Security|
| **JPA / Hibernate**  | Spring Data JPA with Hibernate ORM          |
| **MySQL**            | MySQL profile + H2 for development         |
| **REST / Web Services** | RESTful API with 10 endpoints           |
| **OOP**              | Entities, Services, Controllers, DTOs       |
| **Maven**            | Maven build with pom.xml                    |
| **JUnit / TDD**      | JUnit 5 + Mockito + MockMvc                |
| **Git**              | Version controlled                          |
| **Tomcat**           | Embedded Tomcat (Spring Boot default)       |
| **BCrypt**           | Spring Security password encoder            |

---

## 📊 MySQL Setup (Optional)

```sql
CREATE DATABASE tripyatra;
```

Then run with:
```powershell
mvn spring-boot:run -Dspring-boot.run.profiles=mysql
```

Hibernate will auto-create the `users` and `trips` tables.
