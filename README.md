# 🏙️ CityFix – Smart Civic Issue Reporting System

CityFix is a **web-based Smart Civic Issue Reporting System** designed to provide a simple and efficient platform for citizens to report civic problems and for administrators to manage, monitor, and resolve reported issues.

The system helps bridge the communication gap between citizens and civic administration by providing a centralized platform for **issue reporting, complaint tracking, status management, and administrative monitoring**.

---

## 📌 Project Overview

In many communities, reporting civic issues such as potholes, garbage accumulation, streetlight failures, drainage problems, and other public infrastructure concerns can be difficult and time-consuming.

**CityFix** provides a digital solution where citizens can:

* Register and securely log in
* Report civic issues
* Provide issue descriptions and relevant details
* Track submitted complaints
* View complaint status
* Manage their profile
* Monitor previously reported issues

Administrators can:

* Securely access the admin dashboard
* View and manage registered users
* View all reported complaints
* Examine individual complaint details
* Update complaint status
* Monitor the overall civic issue management process

---

## Live Demo
  https://cityfix-system.onrender.com

  https://drive.google.com/file/d/1Pnkjyl8BMtsjEwn-k4khVjoO0xNqmu3H/view?usp=sharing

## 🎯 Objectives

The main objectives of CityFix are:

1. To provide citizens with an easy-to-use platform for reporting civic issues.
2. To centralize civic complaint management.
3. To improve transparency in complaint tracking.
4. To enable administrators to efficiently monitor and manage complaints.
5. To maintain complaint and user information in a structured relational database.
6. To provide a responsive and user-friendly web interface.
7. To reduce the dependency on manual complaint reporting processes.

---

## 🚀 Key Features

### 👤 Citizen Module

* User Registration
* User Login
* Secure Authentication
* Citizen Dashboard
* Profile Management
* Report Civic Issue
* View Submitted Complaints
* View Complaint Details
* Track Complaint Status
* Logout

### 📝 Complaint Management

Citizens can report issues by providing relevant information such as:

* Issue title
* Description
* Location
* Category
* Additional complaint details

The system stores the submitted complaint information in the database for further processing.

### 🛠️ Admin Module

Administrators can:

* Access the Admin Dashboard
* View registered users
* View all complaints
* View individual complaint details
* Manage complaint status
* Monitor reported civic issues
* Manage user information

### 📊 Dashboard

The dashboard provides an overview of the application's important information, helping users and administrators quickly access the features relevant to their role.

### 🔐 Authentication & Authorization

CityFix provides separate access for:

* Citizens
* Administrators

Authentication and authorization mechanisms are implemented to ensure that users can access only the features permitted for their role.

---

## 🧑‍💻 Technology Stack

### Backend

* **Java 17**
* **Spring Boot**
* **Spring MVC**
* **Spring Data JPA**
* **Hibernate**
* **REST APIs**
* **Maven**

### Frontend

* **HTML5**
* **CSS3**
* **JavaScript**
* **Thymeleaf**

### Database

* **MySQL**

### Development & Version Control

* **Visual Studio Code**
* **Git**
* **GitHub**
* **Maven Wrapper**

---

## 🏗️ Application Architecture

CityFix follows a layered application architecture based on Spring Boot.

```text
                    ┌──────────────────────┐
                    │       Client         │
                    │  Web Browser / User  │
                    └──────────┬───────────┘
                               │
                               ▼
                    ┌──────────────────────┐
                    │       Thymeleaf      │
                    │   HTML / CSS / JS    │
                    └──────────┬───────────┘
                               │
                               ▼
                    ┌──────────────────────┐
                    │    Controller Layer  │
                    │   Spring MVC / API   │
                    └──────────┬───────────┘
                               │
                               ▼
                    ┌──────────────────────┐
                    │     Service Layer   │
                    │  Business Logic      │
                    └──────────┬───────────┘
                               │
                               ▼
                    ┌──────────────────────┐
                    │   Repository Layer  │
                    │    Spring Data JPA  │
                    └──────────┬───────────┘
                               │
                               ▼
                    ┌──────────────────────┐
                    │       MySQL          │
                    │     Relational DB    │
                    └──────────────────────┘
```

---

## 📂 Project Structure

```text
CityFix/
│
├── database/
│   └── schema.sql
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── cityfix/
│   │   │           │
│   │   │           ├── config/
│   │   │           ├── controller/
│   │   │           ├── dto/
│   │   │           ├── entity/
│   │   │           ├── exception/
│   │   │           ├── repository/
│   │   │           └── service/
│   │   │
│   │   └── resources/
│   │       ├── static/
│   │       │   ├── css/
│   │       │   └── js/
│   │       │
│   │       ├── templates/
│   │       └── application.properties
│   │
│   └── test/
│
├── uploads/
├── pom.xml
├── mvnw
├── mvnw.cmd
├── README.md
└── .gitignore
```

---

## 🧩 Backend Components

The backend is organized into separate layers to maintain clean and maintainable code.

### Controller Layer

Handles incoming HTTP requests and maps them to the appropriate application functionality.

Examples include:

* Authentication Controller
* Complaint Controller
* Profile Controller
* Admin Controller
* View Controller

### Service Layer

Contains the core business logic of the application.

Services include:

* Authentication Service
* User Service
* Complaint Service
* Admin Service
* File Storage Service

### Repository Layer

Uses **Spring Data JPA** to communicate with the MySQL database.

Repositories include:

* User Repository
* Complaint Repository
* Admin Repository

### Entity Layer

Represents the application's database entities.

Major entities include:

* User
* Admin
* Complaint

### DTO Layer

Data Transfer Objects are used to transfer structured data between application layers while avoiding unnecessary exposure of entity objects.

---

## 🗄️ Database

CityFix uses **MySQL** as its relational database.

The database stores information related to:

* Users
* Administrators
* Civic complaints
* Complaint status
* Complaint details
* User profile information

The database schema is available in:

```text
database/schema.sql
```

---

## 🔄 Application Workflow

### Citizen Workflow

```text
Register
   ↓
Login
   ↓
Citizen Dashboard
   ↓
Report Civic Issue
   ↓
Complaint Stored in MySQL
   ↓
Track Complaint
   ↓
View Updated Status
```

### Administrator Workflow

```text
Admin Login
   ↓
Admin Dashboard
   ↓
View Complaints
   ↓
View Complaint Details
   ↓
Update Complaint Status
   ↓
Monitor Complaint Management
```

---

## 📋 Complaint Status Management

The complaint management workflow allows administrators to update the progress of reported issues.

A typical workflow is:

```text
Submitted
    ↓
Under Review
    ↓
In Progress
    ↓
Resolved
```

This provides citizens with better visibility into the progress of their reported issues.

---

## ⚙️ Prerequisites

Before running CityFix, make sure the following are installed:

* Java 17 or later
* MySQL
* Maven (optional because Maven Wrapper is included)
* Git
* A modern web browser
* Visual Studio Code or another Java-compatible IDE

---

## 🔧 Configuration

Update the database configuration in:

```text
src/main/resources/application.properties
```

Configure your MySQL database connection according to your local environment.

Example:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/cityfix
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD
```

> Do not commit real database passwords, API keys, or other credentials to GitHub.

---

## ▶️ How to Run the Project

### 1. Clone the Repository

```bash
git clone https://github.com/YOUR-GITHUB-USERNAME/CityFix.git
```

### 2. Open the Project

```bash
cd CityFix
```

### 3. Create the MySQL Database

Create a database named:

```sql
CREATE DATABASE cityfix;
```

If required, execute:

```text
database/schema.sql
```

against the CityFix database.

### 4. Configure MySQL

Update:

```text
src/main/resources/application.properties
```

with your MySQL username, password, and database configuration.

### 5. Run the Application

#### Windows

```powershell
.\mvnw.cmd spring-boot:run
```

#### Linux / macOS

```bash
./mvnw spring-boot:run
```

### 6. Open in Browser

Once the application starts successfully, open:

```text
http://localhost:8080
```

---

## 🧪 Testing

The project includes a test structure under:

```text
src/test/
```

Run the tests using:

### Windows

```powershell
.\mvnw.cmd test
```

### Linux / macOS

```bash
./mvnw test
```

---

## 🔒 Security Considerations

The application includes role-based access concepts for separating citizen and administrator functionality.

For production deployment, the following should also be implemented or strengthened:

* Password hashing
* CSRF protection
* Secure session management
* Input validation
* File upload validation
* Environment-based secrets
* HTTPS
* Production database credentials management
* Rate limiting
* Audit logging

---

## 📱 User Interface

CityFix provides a clean and responsive interface designed to make civic issue reporting simple for citizens.

The interface includes:

* Responsive navigation
* Dashboard cards
* Complaint forms
* Complaint status indicators
* Profile management
* Administrative management screens
* Structured complaint details

---

## 🌟 Highlights

* Full-stack Java web application
* MVC-based architecture
* MySQL-backed persistent data
* Role-based citizen and admin workflows
* Complaint reporting and tracking
* RESTful backend architecture
* Layered Spring Boot design
* DTO-based data transfer
* Exception handling
* File storage support
* Responsive frontend
* Maven-based project management

---

## 🔮 Future Enhancements

Possible future improvements include:

* 📍 GPS-based complaint location
* 🗺️ Interactive map integration
* 📸 Image-based issue reporting
* 🤖 AI-powered civic issue classification
* 🔔 Email and SMS notifications
* 📱 Mobile application
* 📊 Advanced analytics dashboard
* 🏛️ Department-based complaint assignment
* ⏱️ SLA and resolution-time monitoring
* 🔎 Advanced complaint search and filtering
* ⭐ Citizen feedback and rating system
* ☁️ Cloud deployment
* 🔐 Enhanced Spring Security implementation

---

## 💡 Real-World Impact

CityFix is designed to improve the way civic issues are reported and managed by creating a centralized digital communication channel between citizens and administrators.

The platform can help:

* Improve complaint visibility
* Reduce manual complaint handling
* Increase transparency
* Improve issue tracking
* Support faster administrative response
* Maintain structured civic issue records

---

## 👩‍💻 Developer

**Swetha R**

**Project:** CityFix – Smart Civic Issue Reporting System

---

## 📄 License

This project is developed for educational and portfolio purposes.

If you intend to use or distribute this project commercially, add an appropriate open-source or proprietary license.

---

## ⭐ Support

If you find this project useful, consider giving the repository a ⭐ on GitHub.
