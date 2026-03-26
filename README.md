# StockFlow — Inventory Management Backend

Spring Boot 3 + PostgreSQL REST API backend for the StockFlow Inventory Management System.

---

## Tech Stack

| Layer        | Technology                        |
|--------------|-----------------------------------|
| Framework    | Spring Boot 3.2                   |
| Language     | Java 17                           |
| Security     | Spring Security 6 + JWT (jjwt)   |
| Database     | PostgreSQL 15+                    |
| ORM          | Spring Data JPA / Hibernate 6     |
| Validation   | Jakarta Bean Validation           |
| Build Tool   | Maven 3.9+                        |
| Utilities    | Lombok                            |

---

## Project Structure

```
src/main/java/com/stockflow/inventory/
├── InventoryApplication.java          ← Entry point
│
├── config/
│   ├── SecurityConfig.java            ← Spring Security 6, CORS, JWT filter chain
│   └── DataInitializer.java           ← Seeds admin + products on first boot
│
├── security/
│   ├── JwtService.java                ← Token generation & validation
│   ├── JwtAuthFilter.java             ← Bearer token interceptor (OncePerRequestFilter)
│   └── UserDetailsServiceImpl.java    ← Loads user from DB for Spring Security
│
├── entity/
│   ├── User.java
│   ├── Product.java
│   ├── Request.java
│   ├── NewProductRequest.java
│   ├── Purchase.java
│   └── Notification.java
│
├── enums/
│   ├── Role.java                      ← ADMIN | STAFF
│   ├── RequestStatus.java             ← PENDING | APPROVED | REJECTED
│   └── NotificationType.java          ← REQUEST | NEW_PRODUCT | LOW_STOCK | SYSTEM
│
├── repository/                        ← Spring Data JPA repositories (custom JPQL)
│   ├── UserRepository.java
│   ├── ProductRepository.java
│   ├── RequestRepository.java
│   ├── NewProductRequestRepository.java
│   ├── PurchaseRepository.java
│   └── NotificationRepository.java
│
├── dto/
│   ├── request/                       ← Inbound validated payloads
│   │   ├── LoginRequest.java
│   │   ├── ProductRequest.java
│   │   ├── StockUpdateRequest.java
│   │   ├── CartRequest.java
│   │   ├── RequestStatusUpdate.java
│   │   ├── NewProductRequestDTO.java
│   │   └── PurchaseRequest.java
│   └── response/                      ← Outbound response shapes
│       ├── ApiResponse.java           ← Generic wrapper { success, message, data }
│       ├── LoginResponse.java
│       ├── UserResponse.java
│       ├── ProductResponse.java
│       ├── RequestResponse.java
│       ├── NewProductRequestResponse.java
│       ├── PurchaseResponse.java
│       ├── NotificationResponse.java
│       └── AdminDashboardStats.java
│
├── service/                           ← Interfaces
│   ├── AuthService.java
│   ├── ProductService.java
│   ├── RequestService.java
│   ├── NewProductRequestService.java
│   ├── PurchaseService.java
│   ├── NotificationService.java
│   └── DashboardService.java
│   └── impl/                          ← Implementations
│       ├── AuthServiceImpl.java
│       ├── ProductServiceImpl.java
│       ├── RequestServiceImpl.java
│       ├── NewProductRequestServiceImpl.java
│       ├── PurchaseServiceImpl.java
│       ├── NotificationServiceImpl.java
│       └── DashboardServiceImpl.java
│
├── controller/
│   ├── AuthController.java
│   ├── ProductController.java
│   ├── RequestController.java
│   ├── NewProductRequestController.java
│   ├── PurchaseController.java
│   ├── NotificationController.java
│   └── DashboardController.java
│
└── exception/
    ├── ResourceNotFoundException.java
    ├── BusinessException.java
    └── GlobalExceptionHandler.java    ← Handles all errors uniformly

src/main/resources/
├── application.properties             ← DB URL, JWT secret, server port
└── init.sql                           ← Reference schema (Hibernate auto-creates tables)
```

---

## Prerequisites

| Tool        | Version    | Install                            |
|-------------|------------|------------------------------------|
| Java JDK    | 17 or 21   | https://adoptium.net               |
| Maven       | 3.9+       | https://maven.apache.org/download  |
| PostgreSQL  | 15+        | https://www.postgresql.org/download|

---

## Step-by-Step Setup

### Step 1 — Install PostgreSQL

**macOS (Homebrew)**
```bash
brew install postgresql@15
brew services start postgresql@15
```

**Ubuntu / Debian**
```bash
sudo apt update
sudo apt install postgresql postgresql-contrib
sudo systemctl start postgresql
sudo systemctl enable postgresql
```

**Windows**
Download and run the installer from https://www.postgresql.org/download/windows/

---

### Step 2 — Create the Database

Connect to PostgreSQL as the superuser:

```bash
# macOS / Linux
psql -U postgres

# Windows (run in psql shell from Start Menu)
```

Inside the `psql` shell, run:

```sql
CREATE DATABASE stockflow_db;
CREATE USER postgres WITH PASSWORD 'postgres';
GRANT ALL PRIVILEGES ON DATABASE stockflow_db TO postgres;
\q
```

> **Note:** If your PostgreSQL `postgres` user already has a password, just make sure
> `spring.datasource.password` in `application.properties` matches it.

---

### Step 3 — Configure application.properties

Open `src/main/resources/application.properties` and update if needed:

```properties
# Change these to match your PostgreSQL setup
spring.datasource.url=jdbc:postgresql://localhost:5432/stockflow_db
spring.datasource.username=postgres
spring.datasource.password=postgres
```

The JWT secret and expiration can also be changed:

```properties
app.jwt.secret=StockFlow2024SecretKeyForJWTTokenGenerationMustBe256BitsLong!!
app.jwt.expiration=86400000   # 24 hours in milliseconds
```

---

### Step 4 — Build the Project

```bash
cd inventory-backend
mvn clean install -DskipTests
```

You should see `BUILD SUCCESS`.

---

### Step 5 — Run the Application

```bash
mvn spring-boot:run
```

Or run the JAR directly:

```bash
java -jar target/inventory-1.0.0.jar
```

The server starts at **http://localhost:8080**

---

### Step 6 — Verify It's Running

```bash
curl http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

Expected response:
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "user": {
      "id": 1,
      "username": "admin",
      "name": "Admin User",
      "role": "ADMIN"
    }
  }
}
```

---

## Default Credentials (Auto-seeded on First Boot)

| Role  | Username | Password  |
|-------|----------|-----------|
| Admin | `admin`  | `admin123`|
| Staff | `staff1` | `staff123`|
| Staff | `staff2` | `staff123`|
| Staff | `staff3` | `staff123`|

---

## Complete API Reference

All protected endpoints require the `Authorization: Bearer <token>` header.

### Authentication

| Method | Endpoint           | Auth | Body                                   | Description  |
|--------|--------------------|------|----------------------------------------|--------------|
| POST   | `/api/auth/login`  | None | `{ "username": "", "password": "" }`   | Login        |

---

### Products

| Method | Endpoint                      | Role        | Description               |
|--------|-------------------------------|-------------|---------------------------|
| GET    | `/api/products`               | Both        | List all (paginated)      |
| GET    | `/api/products/{id}`          | Both        | Get single product        |
| GET    | `/api/products/low-stock`     | Admin       | Products below threshold  |
| POST   | `/api/products`               | Admin       | Create new product        |
| PUT    | `/api/products/{id}`          | Admin       | Update product            |
| PATCH  | `/api/products/{id}/stock`    | Admin       | Update stock quantity     |
| DELETE | `/api/products/{id}`          | Admin       | Delete product            |

**GET /api/products query params:**
```
?search=fan          → filter by name (partial, case-insensitive)
?category=FAN        → filter by category
?page=0&size=10      → pagination (0-based page index)
?sortBy=name&sortDir=asc
```

**POST /api/products body:**
```json
{
  "name": "Ceiling Fan 48\"",
  "category": "FAN",
  "stockQuantity": 20,
  "lowStockThreshold": 5,
  "description": "Optional description"
}
```

**PATCH /api/products/{id}/stock body:**
```json
{ "quantity": 50 }
```

---

### Requests (Staff Cart Submissions)

| Method | Endpoint                        | Role  | Description                  |
|--------|---------------------------------|-------|------------------------------|
| GET    | `/api/requests`                 | Admin | All requests (with filters)  |
| GET    | `/api/requests/my`              | Staff | My own requests              |
| POST   | `/api/requests`                 | Staff | Submit cart as request       |
| PATCH  | `/api/requests/{id}/status`     | Admin | Approve or reject            |

**GET /api/requests query params:**
```
?status=PENDING    → filter by status (PENDING | APPROVED | REJECTED)
?search=fan        → search by product name or staff name
?page=0&size=10
```

**POST /api/requests body:**
```json
{
  "items": [
    { "productId": 1, "quantity": 3 },
    { "productId": 5, "quantity": 2 }
  ],
  "note": "Needed for conference room"
}
```

**PATCH /api/requests/{id}/status body:**
```json
{
  "status": "APPROVED",
  "note": "Approved — will purchase next week"
}
```

---

### New Product Requests

| Method | Endpoint                                  | Role  | Description              |
|--------|-------------------------------------------|-------|--------------------------|
| GET    | `/api/new-product-requests`               | Both  | My requests (staff)      |
| GET    | `/api/new-product-requests/all`           | Admin | All requests             |
| POST   | `/api/new-product-requests`               | Staff | Submit new product req   |
| PATCH  | `/api/new-product-requests/{id}/status`   | Admin | Approve or reject        |

**POST /api/new-product-requests body:**
```json
{
  "name": "Standing Desk",
  "category": "FURNITURE",
  "description": "Height-adjustable standing desk for office use"
}
```

---

### Purchases

| Method | Endpoint                 | Role  | Description                          |
|--------|--------------------------|-------|--------------------------------------|
| GET    | `/api/purchases`         | Admin | All purchases (paginated)            |
| GET    | `/api/purchases/report`  | Admin | Filtered report for Reports page     |
| POST   | `/api/purchases`         | Admin | Record purchase (auto-increments stock) |

**GET /api/purchases/report query params:**
```
?range=monthly          → this calendar month (default)
?range=weekly           → last 7 days
?range=custom&from=2024-01-01&to=2024-03-31
```

**POST /api/purchases body:**
```json
{
  "productId": 2,
  "quantity": 5,
  "pricePerUnit": 35000.00,
  "purchaseDate": "2024-03-25"
}
```

> Stock is automatically incremented after a purchase is saved.

---

### Notifications

| Method | Endpoint                             | Role  | Description              |
|--------|--------------------------------------|-------|--------------------------|
| GET    | `/api/notifications`                 | Admin | All notifications        |
| GET    | `/api/notifications/unread-count`    | Admin | `{ "count": 3 }`         |
| PATCH  | `/api/notifications/{id}/read`       | Admin | Mark one as read         |
| PATCH  | `/api/notifications/read-all`        | Admin | Mark all as read         |

---

### Dashboard

| Method | Endpoint                | Role  | Description                     |
|--------|-------------------------|-------|---------------------------------|
| GET    | `/api/dashboard/admin`  | Admin | Stats: products, low stock, etc.|
| GET    | `/api/dashboard/staff`  | Staff | Staff dashboard stub            |

**GET /api/dashboard/admin response:**
```json
{
  "success": true,
  "data": {
    "totalProducts": 10,
    "lowStockProducts": 4,
    "pendingRequests": 2,
    "totalPurchasesThisMonth": 334000.00,
    "totalUsers": 4
  }
}
```

---

## Standard API Response Format

Every endpoint returns the same wrapper:

```json
{
  "success": true,
  "message": "Optional message",
  "data": { ... },
  "timestamp": "2024-03-25T10:30:00"
}
```

On error:
```json
{
  "success": false,
  "message": "Product not found with id: 99",
  "timestamp": "2024-03-25T10:30:00"
}
```

---

## HTTP Status Codes Used

| Code | Meaning                          |
|------|----------------------------------|
| 200  | OK — successful GET / PATCH      |
| 201  | Created — successful POST        |
| 400  | Bad Request — validation failed  |
| 401  | Unauthorized — invalid/missing token |
| 403  | Forbidden — wrong role           |
| 404  | Not Found — resource missing     |
| 500  | Internal Server Error            |

---

## Available Product Categories

```
FAN | AC | COOLER | ELECTRICAL | FURNITURE | IT_EQUIPMENT | OTHER
```

---

## Running Both Frontend and Backend Together

### Terminal 1 — Backend
```bash
cd inventory-backend
mvn spring-boot:run
# Runs on http://localhost:8080
```

### Terminal 2 — Frontend
```bash
cd inventory-frontend
npm install
npm run dev
# Runs on http://localhost:5173
# Proxies /api → http://localhost:8080
```

Open **http://localhost:5173** in your browser.

---

## Troubleshooting

### Port 8080 already in use
```bash
# Find process using port 8080
lsof -i :8080          # macOS/Linux
netstat -ano | findstr :8080   # Windows

# Kill it, or change the port in application.properties:
server.port=9090
```

### PostgreSQL connection refused
```bash
# Check PostgreSQL is running
sudo systemctl status postgresql   # Linux
brew services list | grep postgres  # macOS

# Check you can connect manually
psql -U postgres -d stockflow_db -h localhost
```

### Tables not created / schema issues
Set this in `application.properties` temporarily to recreate all tables:
```properties
spring.jpa.hibernate.ddl-auto=create-drop
```
Then switch back to `update` after the first run.

### BCrypt password mismatch
The `DataInitializer` runs on every boot but only inserts data when the table is empty.
If you changed passwords or the DB was manually edited, you can reset by:
```sql
TRUNCATE users RESTART IDENTITY CASCADE;
```
Then restart the app — it will re-seed.

---

## Environment-Specific Configuration

For production, override properties via environment variables:

```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://prod-host:5432/stockflow_db
export SPRING_DATASOURCE_PASSWORD=secure_password
export APP_JWT_SECRET=your-very-long-production-secret-key-256-bits
java -jar target/inventory-1.0.0.jar
```

Or create `application-prod.properties` and run with:
```bash
java -jar target/inventory-1.0.0.jar --spring.profiles.active=prod
```
#   w b i n v e n t a r y m a n a e m e n t _ b a c k e n d  
 