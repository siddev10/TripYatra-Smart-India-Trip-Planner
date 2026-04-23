# 🚂 TripYatra — Smart India Trip Planner
**Full Stack Web Project** | Java Spring Boot + JPA/Hibernate + HTML/CSS/JS + OpenAI/Gemini AI

---

## 📁 Project Structure

```
tripyatra/
├── backend/                             ← Java Spring Boot REST API
│   ├── pom.xml                          ← Maven build (dependencies)
│   ├── src/main/java/com/tripyatra/
│   │   ├── TripYatraApplication.java    ← Spring Boot entry point
│   │   ├── config/
│   │   │   └── SecurityConfig.java      ← Spring Security + CORS + BCrypt
│   │   ├── security/
│   │   │   ├── JwtUtil.java             ← JWT token generation/validation
│   │   │   └── JwtAuthFilter.java       ← JWT authentication filter
│   │   ├── model/                       ← JPA Entities (OOP)
│   │   │   ├── User.java               ← @Entity → 'users' table
│   │   │   └── Trip.java               ← @Entity → 'trips' table
│   │   ├── dto/                         ← Data Transfer Objects
│   │   ├── repository/                  ← Spring Data JPA Repositories
│   │   ├── service/                     ← Business Logic Layer
│   │   ├── controller/                  ← REST API Controllers
│   │   └── exception/                   ← Global Error Handling
│   ├── src/main/resources/
│   │   ├── application.properties       ← H2 config (default)
│   │   ├── application-mysql.properties ← MySQL config (production)
│   │   └── all-stations.json            ← 8400+ Indian railway stations
│   └── src/test/java/com/tripyatra/    ← JUnit 5 + Mockito Tests
│
└── frontend/
    └── public/
        ├── index.html                   ← Landing page
        ├── login.html                   ← Login page
        ├── register.html                ← Register page
        ├── dashboard.html               ← Main dashboard (protected)
        ├── plan-trip.html               ← Trip planner + AI train search
        ├── train-search.html            ← Standalone train search
        ├── my-trips.html                ← View/delete saved trips
        ├── explore.html                 ← Explore Indian destinations
        ├── styles.css                   ← Global CSS
        └── app.js                       ← Shared JS (auth, API client)
```

---

## ⚙️ Setup & Run

### Prerequisites
- **Java 17+** (download from https://adoptium.net)
- **Maven 3.9+** (download from https://maven.apache.org or `choco install maven`)
- **Node.js** (for the frontend server)

### Step 1 — Add API Keys
Create `backend/src/main/resources/application-local.properties`:
```properties
openai.api.key=your_openai_key_here
gemini.api.key=your_gemini_key_here
rail.api.key=your_rail_api_key_here
```

### Step 2 — Build & Run the Backend
```bash
cd backend
mvn clean install           # Build + run tests
mvn spring-boot:run -Dspring-boot.run.profiles=local   # Start with API keys
```
Server starts at **http://localhost:5000**

### Step 3 — Run the Frontend
```bash
cd frontend
node server.js
```
Open **http://localhost:3000** in your browser.

---

## 🔌 API Endpoints

| Method | Endpoint                | Auth | Description           |
|--------|-------------------------|------|-----------------------|
| POST   | `/api/auth/register`    | No   | Register new user     |
| POST   | `/api/auth/login`       | No   | Login, get JWT token  |
| GET    | `/api/auth/me`          | Yes  | Get current user      |
| GET    | `/api/trips`            | Yes  | Get all user trips    |
| POST   | `/api/trips`            | Yes  | Create new trip       |
| DELETE | `/api/trips/:id`        | Yes  | Delete a trip         |
| POST   | `/api/trains/search`    | Yes  | AI train search       |
| GET    | `/api/packages`         | No   | List travel packages  |
| POST   | `/api/itinerary/generate` | Yes | AI itinerary        |
| POST   | `/api/flights/search`   | Yes  | Flight search         |
| GET    | `/api/health`           | No   | Server status         |

---

## 🧪 Running Tests
```bash
cd backend
mvn test
```
```
Tests run: 15, Failures: 0, Errors: 0, Skipped: 0 — BUILD SUCCESS
```

---

## 🎯 Technologies Used

### Backend
- **Java 17** — Core language
- **Spring Boot 3.2** — Application framework
- **Spring MVC** — REST controllers
- **Spring Security** — Authentication (JWT + BCrypt)
- **Spring Data JPA** — Repository abstraction
- **Hibernate ORM 6.4** — Object-relational mapping
- **H2 / MySQL** — Relational databases
- **Maven** — Build tool & dependency management
- **JUnit 5 + Mockito** — Unit & integration testing
- **Apache Tomcat 10** — Embedded application server

### Frontend
- **HTML5** — Semantic structure
- **CSS3** — Custom properties, animations, responsive design
- **Vanilla JavaScript** — Fetch API, DOM manipulation

### External APIs
- **OpenAI API** (GPT-4o-mini) — AI-powered train schedules & itineraries
- **Google Gemini API** — Fallback AI provider
- **Indian Rail API** — Live train data (8400+ stations)

---

## 🎯 Features

- ✅ User Registration & Login with BCrypt password hashing
- ✅ JWT-based authentication & session management
- ✅ Protected routes — redirect to login if not authenticated
- ✅ Dashboard with stats (trips, searches, upcoming, cities)
- ✅ Plan Trip — form → AI fetches train details
- ✅ IRCTC-style train cards (name, number, departure, arrival, duration, classes, fares)
- ✅ Save trips to user account (persisted in database)
- ✅ My Trips — view, filter (upcoming/past), delete
- ✅ AI-generated travel itineraries with day-by-day plans
- ✅ Explore India — destinations with travel info
- ✅ Toast notifications for all actions
- ✅ Responsive design (mobile-friendly)

---

## 📊 MySQL Setup (Optional)

```sql
CREATE DATABASE tripyatra;
```
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=mysql,local
```
Hibernate auto-creates `users` and `trips` tables.
