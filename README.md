# CityFix – Smart Civic Issue Reporting System

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.4-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.x-blue.svg)](https://www.mysql.com/)
[![License](https://img.shields.io/badge/License-MIT-lightgrey.svg)]()

**CityFix** is a modern, full-stack civic issue reporting and municipal grievance redressal web application. It empowers citizens to report public infrastructure defects (potholes, overflowing garbage, broken streetlights, water leakages, drainage issues, etc.) with location coordinates and photos, and enables municipal administrators to track, assign work orders, update status, and communicate official resolution remarks.

---

## Table of Contents

1. [Key Features](#key-features)
2. [Civic Categories & Status Workflow](#civic-categories--status-workflow)
3. [Technologies Used](#technologies-used)
4. [System Architecture & Folder Structure](#system-architecture--folder-structure)
5. [Prerequisites & System Requirements](#prerequisites--system-requirements)
6. [Database Setup](#database-setup)
7. [Application Configuration](#application-configuration)
8. [How to Run the Application](#how-to-run-the-application)
9. [Default User & Admin Credentials](#default-user--admin-credentials)
10. [Citizen Module Walkthrough](#citizen-module-walkthrough)
11. [Admin Module Walkthrough](#admin-module-walkthrough)
12. [REST API Documentation](#rest-api-documentation)
13. [Troubleshooting & Common Questions](#troubleshooting--common-questions)
14. [Future Enhancements](#future-enhancements)

---

## Key Features

### Citizen Portal
* **User Authentication**: Secure registration and session-based login with BCrypt password hashing.
* **Civic Issue Reporting**: Intuitive form with category selection, issue description, address, auto-GPS detection, estimated priority, and photo upload.
* **Auto-Generated Unique IDs**: Every complaint receives an official tracking ID (e.g. `CF-2026-0001`).
* **Visual Status Tracker**: Interactive step-by-step progress timeline (`Submitted` &rarr; `Assigned` &rarr; `In Progress` &rarr; `Resolved`).
* **My Complaints & Filters**: Search complaints by title/ID and filter by category or status.
* **Citizen Profile**: View total complaints filed, update contact details, and change password securely.

### Admin & Municipal Portal
* **Operations Dashboard**: Real-time summary cards (Total, Pending, Assigned, In Progress, Resolved, Rejected, Citizens) and category distribution analytics.
* **Complaint Redressal Management**: Filterable complaint table with photo thumbnails, citizen contact info, and status badges.
* **Status Updates & Remarks**: Modal workflow to update status (`Pending`, `Assigned`, `In Progress`, `Resolved`, `Rejected`) and enter official municipal remarks.
* **Complaint Deletion**: Ability to permanently remove spam or invalid complaints.
* **Citizen Directory**: View registered citizens and their complaint submission volume.

---

## Civic Categories & Status Workflow

### Civic Categories
1. **Pothole**
2. **Garbage**
3. **Streetlight**
4. **Water Leakage**
5. **Drainage**
6. **Road Damage**
7. **Traffic Signal**
8. **Public Toilet**
9. **Illegal Dumping**
10. **Other**

### Status Workflow
* <span style="color:#d97706; font-weight:bold;">Pending</span> &ndash; Initial submission awaiting municipal review.
* <span style="color:#0284c7; font-weight:bold;">Assigned</span> &ndash; Work order dispatched to field maintenance crew.
* <span style="color:#7c3aed; font-weight:bold;">In Progress</span> &ndash; Physical repair work is actively underway.
* <span style="color:#059669; font-weight:bold;">Resolved</span> &ndash; Defect fixed, verified on site, and closed with remarks.
* <span style="color:#dc2626; font-weight:bold;">Rejected</span> &ndash; Invalid, duplicate, or out-of-jurisdiction report.

---

## Technologies Used

* **Backend**: Java 17, Spring Boot 3.3.4, Spring MVC, Spring Data JPA, Hibernate
* **Database**: MySQL 8.x, MySQL Connector/J
* **Security & Auth**: Spring Security Crypto (`BCryptPasswordEncoder`), Spring `HttpSession`, Role-based Interceptors
* **Template Engine**: Thymeleaf (HTML5 with reusable fragments)
* **Frontend**: HTML5, CSS3, Vanilla JavaScript (ES6+ `fetch`), Bootstrap 5, Bootstrap Icons
* **Build Tool**: Apache Maven (with included Maven Wrapper `mvnw` / `mvnw.cmd`)

---

## System Architecture & Folder Structure

```text
CityFix/
│
├── pom.xml                               # Maven project dependencies & plugins
├── README.md                             # Comprehensive project documentation
├── mvnw / mvnw.cmd                       # Cross-platform Maven wrapper scripts
├── database/
│   └── schema.sql                        # MySQL schema creation script (optional manual setup)
├── uploads/                              # Directory for uploaded defect images
│
└── src/
    ├── main/
    │   ├── java/com/cityfix/
    │   │   ├── CityFixApplication.java   # Spring Boot entry point
    │   │   │
    │   │   ├── config/                   # Configuration & Security
    │   │   │   ├── AppConfig.java        # BCrypt password encoder bean
    │   │   │   ├── AuthInterceptor.java  # Session & role-based route guard
    │   │   │   ├── DataInitializer.java  # Auto-seeds default admin & demo complaints
    │   │   │   └── WebMvcConfig.java     # Interceptor & static upload resource mapping
    │   │   │
    │   │   ├── controller/               # MVC & REST Controllers
    │   │   │   ├── AdminController.java  # Admin REST endpoints
    │   │   │   ├── AuthController.java   # Authentication REST endpoints
    │   │   │   ├── ComplaintController.java # Complaint REST endpoints & image serving
    │   │   │   ├── ProfileController.java   # Citizen profile REST endpoints
    │   │   │   └── ViewController.java   # Spring MVC view routing (Thymeleaf)
    │   │   │
    │   │   ├── dto/                      # Data Transfer Objects (Requests & Responses)
    │   │   │   ├── ApiResponse.java
    │   │   │   ├── ComplaintRequest.java
    │   │   │   ├── ComplaintResponse.java
    │   │   │   ├── DashboardStatsResponse.java
    │   │   │   ├── LoginRequest.java
    │   │   │   ├── ProfileUpdateRequest.java
    │   │   │   ├── RegisterRequest.java
    │   │   │   ├── StatusUpdateRequest.java
    │   │   │   └── UserResponse.java
    │   │   │
    │   │   ├── entity/                   # JPA / Hibernate Entities
    │   │   │   ├── Admin.java
    │   │   │   ├── Complaint.java
    │   │   │   └── User.java
    │   │   │
    │   │   ├── exception/                # Exception Handling
    │   │   │   ├── BadRequestException.java
    │   │   │   ├── GlobalExceptionHandler.java
    │   │   │   ├── ResourceNotFoundException.java
    │   │   │   └── UnauthorizedException.java
    │   │   │
    │   │   ├── repository/               # Spring Data JPA Repositories
    │   │   │   ├── AdminRepository.java
    │   │   │   ├── ComplaintRepository.java
    │   │   │   └── UserRepository.java
    │   │   │
    │   │   └── service/                  # Business Logic Layer
    │   │       ├── AdminService.java
    │   │       ├── AuthService.java
    │   │       ├── ComplaintService.java
    │   │       ├── FileStorageService.java
    │   │       └── UserService.java
    │   │
    │   └── resources/
    │       ├── application.properties    # Database, server, and upload settings
    │       │
    │       ├── static/                   # Static Frontend Assets
    │       │   ├── css/
    │       │   │   └── style.css         # Modern civic-service styling
    │       │   ├── js/
    │       │   │   ├── admin.js          # Admin dashboard & management JS
    │       │   │   ├── auth.js           # Login & Registration JS
    │       │   │   ├── citizen.js        # Citizen dashboard, reporting, tracking JS
    │       │   │   └── main.js           # Common badges, toasts, and helpers
    │       │   └── images/
    │       │       └── placeholder.png   # Default fallback image
    │       │
    │       └── templates/                # Thymeleaf HTML Templates
    │           ├── fragments/
    │           │   ├── alerts.html       # Toast notification fragment
    │           │   ├── footer.html       # Global footer fragment
    │           │   └── navbar.html       # Dynamic header navigation fragment
    │           ├── index.html            # Landing / Homepage
    │           ├── login.html            # Citizen Sign In
    │           ├── register.html         # Citizen Registration
    │           ├── citizen_dashboard.html# Citizen Dashboard
    │           ├── report_issue.html     # Report Civic Issue Form
    │           ├── my_complaints.html    # Complaint List & Search
    │           ├── complaint_details.html# Complaint Timeline & Details
    │           ├── profile.html          # Citizen Profile & Password
    │           ├── admin_login.html      # Municipal Staff Sign In
    │           ├── admin_dashboard.html  # Municipal Admin Operations Console
    │           ├── admin_complaints.html # Admin Complaint Management
    │           ├── admin_complaint_details.html # Admin Complaint Review & Status Update
    │           └── admin_users.html      # Registered Citizens Directory
    │
    └── test/
        └── java/com/cityfix/             # Unit and integration tests
```

---

## Prerequisites & System Requirements

1. **Java Development Kit (JDK) 17** or higher
   * Check your version in terminal:
     ```powershell
     java -version
     ```
2. **MySQL Server 8.x** (or MariaDB / XAMPP / WAMP) running on port `3306`.
3. Internet connection for initial Maven dependencies download and Bootstrap CDN.

---

## Database Setup

By default, Spring Boot and Hibernate will **automatically create and initialize the database tables** (`users`, `complaints`, `admins`) when the application starts up!

### Option A: Automatic Setup (Recommended)
Spring Boot will auto-create the database `cityfix` if it doesn't already exist via JDBC connection parameters.

### Option B: Manual SQL Setup (Optional)
If you prefer creating the database manually before running:
1. Open MySQL Command Line or MySQL Workbench.
2. Run:
   ```sql
   CREATE DATABASE cityfix CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```
*(You can also run the full `database/schema.sql` script).*

---

## Application Configuration

Open `src/main/resources/application.properties` to configure your MySQL connection details:

```properties
spring.application.name=CityFix

# MySQL Configuration (Port 3306)
spring.datasource.url=jdbc:mysql://127.0.0.1:3306/cityfix?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true&createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD_HERE
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA / Hibernate Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect

# Server Port
server.port=8080

# Upload Limits
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
cityfix.upload.dir=./uploads
```

> **Note**: If your local MySQL `root` user has a password (e.g. `root`, `123456`, or `admin`), enter it in `spring.datasource.password=...`. If there is no password, leave it empty.

---

## How to Run the Application

### 1. Open Terminal in the Project Directory
```powershell
cd C:\Users\nithe\.gemini\antigravity\scratch\CityFix
```

### 2. Compile and Start with Maven Wrapper
On Windows PowerShell or Command Prompt:
```powershell
.\mvnw.cmd spring-boot:run
```
*(On Linux/macOS: `./mvnw spring-boot:run`)*

### 3. Open in Browser
Once you see `Started CityFixApplication in ... seconds`:
* Open [http://localhost:8080](http://localhost:8080) in your web browser.

---

## Default User & Admin Credentials

During the very first startup, `DataInitializer.java` automatically seeds the default administrator and sample citizen accounts (hashed securely with BCrypt):

### 1. Municipal Administrator
* **URL**: [http://localhost:8080/admin/login](http://localhost:8080/admin/login)
* **Username**: `admin`
* **Password**: `admin123`

> ⚠️ *Important*: In a real production deployment, change the default admin password immediately.

### 2. Demo Citizen Accounts
* **URL**: [http://localhost:8080/login](http://localhost:8080/login)
* **Email**: `citizen@cityfix.com`
* **Password**: `citizen123`
* *(Or click **Register** on the navigation bar to create your own account)*

---

## Citizen Module Walkthrough

1. **Sign Up / Sign In**:
   * Navigate to `/register` or `/login`.
2. **Citizen Dashboard (`/citizen/dashboard`)**:
   * Displays instant counts of your Pending, Under Action, Resolved, and Rejected complaints.
   * Lists your 5 most recent complaints with direct links.
3. **Report an Issue (`/citizen/report`)**:
   * Select a civic category (e.g. *Pothole*, *Streetlight*, *Garbage*).
   * Enter the title and detailed description.
   * Click **Auto-Detect GPS** or type the location address.
   * Choose an estimated priority (*Low*, *Medium*, *High*).
   * Attach a photo of the defect (JPG, JPEG, PNG, WEBP).
   * Click **Submit Complaint**. A unique ID (e.g., `CF-2026-0005`) is automatically assigned.
4. **My Complaints (`/citizen/complaints`)**:
   * Search through your complaints in real time.
   * Filter by status or category.
5. **Complaint Details & Progress Tracker (`/citizen/complaints/{id}`)**:
   * Visual 4-stage timeline: `Submitted` &rarr; `Assigned` &rarr; `In Progress` &rarr; `Resolved`.
   * View attached photos and official remarks from the municipal admin.
6. **Profile (`/citizen/profile`)**:
   * View total issues submitted and update your phone number or password.

---

## Admin Module Walkthrough

1. **Admin Login (`/admin/login`)**:
   * Sign in using username `admin` and password `admin123`.
2. **Operations Dashboard (`/admin/dashboard`)**:
   * Real-time metrics on civic complaints across all city zones.
   * Visual progress bars for issue category distribution.
   * Recent incoming complaints feed.
3. **Complaints Redressal (`/admin/complaints`)**:
   * Filter complaints by Category, Status, or Priority.
   * Search by Complaint ID, citizen name, or location.
   * Click the **Pencil Icon** for quick status updates and remarks.
   * Click the **Trash Icon** to delete invalid/spam reports.
4. **Review & Redressal Details (`/admin/complaints/{id}`)**:
   * Full view of citizen contact information (name, email, phone).
   * High-resolution photo inspection.
   * Update workflow status to `Assigned`, `In Progress`, `Resolved`, or `Rejected`.
   * Add official department remarks explaining the action taken.
5. **Citizen Directory (`/admin/users`)**:
   * View all registered citizens and their complaint submission statistics.

---

## REST API Documentation

CityFix exposes clean RESTful JSON endpoints:

| HTTP Method | Endpoint | Description | Role Required |
|---|---|---|---|
| `POST` | `/api/auth/register` | Register new citizen | Public |
| `POST` | `/api/auth/login` | Citizen authentication | Public |
| `POST` | `/api/auth/admin-login` | Admin authentication | Public |
| `POST` | `/api/auth/logout` | Invalidate active session | Logged In |
| `GET` | `/api/auth/me` | Current session user details | Logged In |
| `GET` | `/api/complaints/public/stats` | Homepage summary metrics | Public |
| `GET` | `/api/complaints/images/{filename}` | Serve uploaded defect photo | Public |
| `POST` | `/api/complaints` | Submit new civic complaint (multipart) | Citizen |
| `GET` | `/api/complaints/my` | Get current citizen's complaints | Citizen |
| `GET` | `/api/complaints/{id}` | Get complaint details by ID / code | Citizen / Admin |
| `GET` | `/api/complaints/citizen/dashboard-stats` | Citizen dashboard metrics | Citizen |
| `GET` | `/api/profile` | Get citizen profile | Citizen |
| `PUT` | `/api/profile` | Update citizen profile & password | Citizen |
| `GET` | `/api/admin/dashboard-stats` | Admin dashboard metrics | Admin |
| `GET` | `/api/admin/complaints` | Search & filter all complaints | Admin |
| `PUT` | `/api/admin/complaints/{id}/status` | Update status & admin remarks | Admin |
| `DELETE` | `/api/admin/complaints/{id}` | Delete complaint | Admin |
| `GET` | `/api/admin/users` | List registered citizens | Admin |

---

## Troubleshooting & Common Questions

### 1. `Access denied for user 'root'@'localhost'`
* **Cause**: MySQL root user requires a password on your computer.
* **Fix**: Open `src/main/resources/application.properties` and update `spring.datasource.password=YOUR_PASSWORD`.

### 2. `Communications link failure / Connection refused`
* **Cause**: MySQL server service is not running.
* **Fix**: Start the MySQL service using Windows Services (`services.msc`) or run `net start mysql`.

### 3. Port 8080 is already in use
* **Fix**: In `src/main/resources/application.properties`, change `server.port=8080` to `server.port=8081` (or another free port).

### 4. Uploaded photos are not displaying
* **Fix**: Ensure the `uploads/` directory exists in the project root directory (it is created automatically on startup).

---

## Future Enhancements

* SMS and Email notifications upon status transitions.
* Interactive Leaflet / Google Maps cluster view for municipal ward heatmaps.
* Citizen upvoting / "Me Too" endorsement for duplicate civic issues.
* AI-based image classification to auto-categorize potholes and garbage dumps.
* Ward-level escalation SLAs for delayed complaints.

---

## License

This project is open-source and built for educational and academic project presentation purposes.
