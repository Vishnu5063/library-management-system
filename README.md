# Library Management System

A layered Spring Boot application for managing library books, members, book issue/return transactions, fines, authentication, and administrative reports.

## Project Overview

The Library Management System provides a centralized application for:

- Book catalogue management
- Member management
- Email OTP-based authentication
- Role-based access control for Administrators and Members
- Book issue and return management
- Overdue identification
- Fine calculation and payment-status tracking
- Book search by title, author, ISBN, and category
- Administrative dashboard and reports
- Most-borrowed book reporting for a selected date range

The current implementation models a single library branch. Online payment gateways, inter-library loans, and barcode/RFID hardware integration are outside the current scope.

## Technology Stack

| Technology | Version / Use |
|---|---|
| Java | 25.0.2 |
| Spring Boot | 4.1.1 |
| Build Tool | Maven |
| Spring Web | REST APIs |
| Spring Data JPA | Persistence / DAO layer |
| Hibernate | ORM |
| MySQL | 8.0.x |
| Spring Validation | Request validation |
| Spring Security | Authentication and authorization |
| JJWT | JWT generation and validation |
| Java Mail Sender | Email OTP delivery |
| Postman | API testing |
| Git / GitHub | Version control |

## Architecture

The application follows a layered / N-tier architecture with MVC and DAO patterns.

```text
Client / Postman
       |
       v
Presentation / REST Controllers
       |
       v
Service Layer
       |
       v
Repository / DAO Layer
       |
       v
MySQL Database
```

### Main Layers

- **Controller Layer** – exposes REST endpoints and handles HTTP requests/responses.
- **Service Layer** – contains business rules and application logic.
- **Repository Layer** – communicates with the database using Spring Data JPA.
- **Entity Layer** – represents persistent domain objects.
- **DTO Layer** – represents data structures used for selected API responses.
- **Configuration / Security** – JWT authentication, role-based authorization, and application configuration.
- **Exception Layer** – centralized REST exception handling and validation responses.

## Main Entities

- `Administrator`
- `Member`
- `Book`
- `Transaction`
- `Fine`
- `OtpVerification`

`TransactionStatus` is an enum used to represent transaction state such as `ISSUED` and `RETURNED`.

## Authentication and Security

The system uses email OTP authentication instead of storing user passwords for the login flow.

### OTP flow

```text
User enters email
       |
       v
System checks Administrator / Member
       |
       v
6-digit OTP generated
       |
       v
OTP hash stored in database
       |
       v
OTP sent by email
       |
       v
User submits OTP
       |
       v
OTP verified
       |
       v
JWT generated with role
       |
       v
Protected API access
```

OTP properties:

- 6 digits
- Valid for 5 minutes
- Maximum 5 verification attempts
- Single-use after successful verification
- Previous OTP is superseded by the latest OTP for the email
- OTP is stored as a BCrypt hash rather than plaintext

JWT tokens contain the authenticated email and role and expire after 1 hour.

### Roles

**ADMIN** can access administrative operations such as:

- Book management
- Member management
- Issue/return operations
- Fine management
- Reports

**MEMBER** can access member-level operations such as:

- Browse/search books
- View personal transactions
- View current loans
- View personal fines

## Core Business Rules

- Maximum simultaneous books per member: **3**
- Standard borrowing period: **14 days**
- Fine rate: **₹2 per overdue day**
- A book cannot be issued when no copies are available.
- An inactive member cannot borrow books.
- An inactive book cannot be issued.
- ISBN values are unique.
- Available copies cannot exceed total copies.
- Returning a book increases its available-copy count.

## Search

Books can be searched using a keyword across:

- Title
- Author
- ISBN
- Category

Search uses case-insensitive partial matching.

## Reports

Administrative reporting includes:

- Dashboard totals
- Currently issued books
- Overdue transactions
- All transactions
- Most-borrowed books for a selected date range

## REST API Endpoints

### Authentication

| Method | Endpoint | Purpose |
|---|---|---|
| POST | `/api/auth/send-otp` | Send OTP to registered email |
| POST | `/api/auth/verify-otp` | Verify OTP and receive JWT |

### Books

| Method | Endpoint | Purpose |
|---|---|---|
| GET | `/api/books` | List books |
| GET | `/api/books/{id}` | Get a book |
| GET | `/api/books/search?keyword=...` | Search books |
| POST | `/api/books` | Add a book (ADMIN) |
| PUT | `/api/books/{id}` | Update a book (ADMIN) |
| DELETE | `/api/books/{id}` | Deactivate a book (ADMIN) |

### Members

| Method | Endpoint | Purpose |
|---|---|---|
| GET | `/api/members` | List members |
| GET | `/api/members/{id}` | Get a member |
| POST | `/api/members` | Add a member |
| PUT | `/api/members/{id}` | Update a member |
| DELETE | `/api/members/{id}` | Deactivate a member |

Member-management endpoints require ADMIN authorization.

### Transactions

| Method | Endpoint | Purpose |
|---|---|---|
| POST | `/api/transactions/issue?memberId={id}&bookId={id}` | Issue a book |
| PUT | `/api/transactions/return/{id}` | Return a book |
| GET | `/api/transactions` | List all transactions |
| GET | `/api/transactions/{id}` | Get a transaction |
| GET | `/api/transactions/member/{memberId}` | Member transaction history |
| GET | `/api/transactions/book/{bookId}` | Book transaction history |
| GET | `/api/transactions/my` | Logged-in member's history |
| GET | `/api/transactions/my/current` | Logged-in member's current loans |

### Fines

| Method | Endpoint | Purpose |
|---|---|---|
| POST | `/api/fines/calculate/{transactionId}` | Calculate/create a fine |
| GET | `/api/fines/transaction/{transactionId}` | Get fine for a transaction |
| GET | `/api/fines` | List all fines (ADMIN) |
| GET | `/api/fines/my` | Logged-in member's fines |
| PUT | `/api/fines/pay/{fineId}` | Mark a fine as paid (ADMIN) |

### Reports

| Method | Endpoint | Purpose |
|---|---|---|
| GET | `/api/reports/dashboard` | Dashboard statistics |
| GET | `/api/reports/overdue` | Overdue transactions |
| GET | `/api/reports/transactions` | All transactions |
| GET | `/api/reports/most-borrowed?startDate=YYYY-MM-DD&endDate=YYYY-MM-DD` | Most-borrowed books |

## Database Setup

Create the database in MySQL:

```sql
CREATE DATABASE library_management_system;
```

Create the application database user:

```sql
CREATE USER 'library_app'@'localhost' IDENTIFIED BY 'YOUR_PASSWORD';
GRANT ALL PRIVILEGES ON library_management_system.* TO 'library_app'@'localhost';
FLUSH PRIVILEGES;
```

Replace `YOUR_PASSWORD` with a password chosen locally. Do not commit database credentials to Git.

Hibernate is configured with `ddl-auto=update`, so the required tables are created/updated from the JPA entities when the application starts.

## Environment Variables

The application intentionally reads sensitive configuration from environment variables.

PowerShell example:

```powershell
$env:DB_USERNAME="library_app"
$env:DB_PASSWORD="YOUR_MYSQL_APP_PASSWORD"
$env:MAIL_USERNAME="YOUR_GMAIL_ADDRESS"
$env:MAIL_PASSWORD="YOUR_GMAIL_APP_PASSWORD"
$env:JWT_SECRET="REPLACE_WITH_A_SECURE_SECRET_AT_LEAST_32_CHARACTERS_LONG"
```

Do not commit real passwords, Gmail app passwords, OTPs, or JWT secrets to GitHub.

`application.properties` does not automatically load a `.env` file; the variables must be available in the process environment or supplied through an appropriate external configuration mechanism.

## Gmail OTP Configuration

For Gmail SMTP, use a Gmail App Password rather than a normal Gmail account password when two-step verification is enabled.

Required values:

```text
MAIL_USERNAME = Gmail address
MAIL_PASSWORD = Gmail App Password
```

## Running the Application

From the project directory:

```powershell
mvn clean package
mvn spring-boot:run
```

The configured application port is:

```text
http://localhost:8081
```

## Testing with Postman

1. Start MySQL.
2. Set the required environment variables in the same terminal used to start Spring Boot.
3. Start the application.
4. Use `POST /api/auth/send-otp` with a registered administrator or member email.
5. Retrieve the OTP from the configured email account.
6. Use `POST /api/auth/verify-otp`.
7. Copy the returned JWT.
8. In Postman, send the token using:

```text
Authorization: Bearer <JWT>
```

9. Test protected endpoints according to the user's role.

## Validation and Error Handling

The application uses Jakarta Bean Validation for request validation and a centralized `GlobalExceptionHandler` for REST error responses.

Examples of validation/business errors include:

- Required book fields missing
- Negative copy counts
- Duplicate ISBN
- Book not found
- Member not found
- Inactive member/book
- No available copies
- Maximum borrowing limit reached
- Invalid or expired OTP
- Attempting to return an already returned transaction

## Project Structure

```text
src/
└── main/
    ├── java/com/library/
    │   ├── config/
    │   │   ├── JwtAuthenticationFilter.java
    │   │   └── SecurityConfig.java
    │   ├── controller/
    │   ├── dto/
    │   ├── entity/
    │   ├── exception/
    │   ├── repository/
    │   ├── service/
    │   └── LibraryManagementSystemApplication.java
    │
    └── resources/
        └── application.properties
```

## Team Members

| Name | Register Number |
|---|---|
| Vishnu Yamsani | 23MIC7113 |
| S. Mohammad Abbas | 23MIC7086 |
| P. Vinay Kumar | 23MIC7055 |
| S. Mohammad Ali | 23MIC7228 |
| T. Pawan Kumar | 23MIC7227 |

## Academic Architecture Patterns

The implementation demonstrates the following software architecture concepts:

- **Layered / N-tier architecture** for separation of responsibilities
- **MVC** through REST controllers, service logic, and domain models
- **DAO pattern** through Spring Data JPA repositories
- **Dependency Injection** through Spring-managed components
- **ORM** through JPA/Hibernate
- **RESTful API design** for client-server communication
- **JWT-based stateless authentication**
- **Role-Based Access Control (RBAC)**
- **Transactional consistency** for issue/return operations
- **Centralized exception handling**

## Project Status

Core backend functionality has been implemented and tested, including:

- OTP authentication
- JWT generation and validation
- ADMIN/MEMBER RBAC
- Book CRUD and search
- Member management
- Issue/return workflow
- Fine calculation and payment-status tracking
- Dashboard and reports
- Validation and duplicate-ISBN handling
- MySQL persistence

## Version Control

The project is maintained using Git and GitHub.

Repository:

`https://github.com/Vishnu5063/library-management-system`
