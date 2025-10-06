# Admin Application Master

A comprehensive loan application management system built with React frontend and Spring Boot microservices backend.

## Architecture

This application follows a microservices architecture with three main services:

- **AdminApplicationMaster** (Port 8080) - Main application handling user management and loan applications
- **LocationServiceMaster** (Port 8081) - Address validation and eligibility checking
- **DecisionServiceMaster** (Port 8082) - Loan decision processing

## Tech Stack

### Frontend
- React 18
- React Router v6
- Axios for API calls
- CSS3 for styling

### Backend
- Spring Boot 3.x
- PostgreSQL (separate databases for each service)
- Redis (for caching address lookups)
- Spring Security with JWT authentication
- Spring Data JPA
- Flyway for database migrations

## Prerequisites

- Node.js 18+ and npm
- Java 21
- Spring Boot 3.5
- Maven 3.6+
- Docker and Docker Compose
- PostgreSQL 15+
- Redis 7+

## Getting Started

### 1. Database Setup

Start all three PostgreSQL databases:
```bash
cd AdminApplicationMaster
docker-compose -f docker-compose-databases.yml up -d
```
This starts:
- admin-app-postgres on port 5432
- location-service-postgres on port 5433
- decision-service-postgres on port 5434

### 2. Start Backend Services
**Terminal 1** - LocationServiceMaster:
```bash
cd LocationServiceMaster
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=local,--server.port=8081"
```
**Terminal 2** - DecisionServiceMaster:
```bash
cd DecisionServiceMaster
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=local,--server.port=8082"
```
**Terminal 3** - AdminApplicationMaster:
```bash
cd AdminApplicationMaster
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=local"
```
Wait for all services to start. Look for logs indicating:

Tomcat started on port(s): XXXX
Started [ServiceName]Application in X.XXX seconds

### 3. Start Redis (for LocationServiceMaster)
If not already running via Docker Compose:
```bash
cd LocationServiceMaster
docker-compose up redis -d
```
Verify Redis is running:
```bash
bashdocker ps | grep redis
```
4. Start Frontend
```bash
cd AdminApplicationMaster/frontend
npm install
npm start
```
The application will be available at http://localhost:3000
Default Credentials
#### Test Account
Email: admin@example.com
Password: password123

## Features
### User Management

Create and manage users (Admin/Contractor roles)
User authentication with JWT tokens
Role-based access control

### Loan Application Processing

**Address Eligibility Check** - Validates property address against eligible regions
**Applicant Information** - Collects borrower details (name, SSN, income, etc.)
**Decision Processing** - Evaluates loan eligibility based on:

Address location (California and Florida eligible)
Income verification
Requested loan amount

### Address Validation
Pre-loaded test addresses from addresses.yml
Redis caching for fast lookups
Geographic eligibility rules for California and Florida counties/cities
Ineligible regions: New York, Pennsylvania, and other states

## Project Structure
```
AdminApplicationMaster/
├── src/main/java/com/adminapplicationmaster/
│   ├── controller/          # REST API endpoints
│   ├── service/            # Business logic
│   ├── domain/entity/      # JPA entities
│   ├── repository/         # Data access layer
│   ├── security/           # JWT authentication
│   └── config/             # Spring configuration
├── src/main/resources/
│   ├── db/migration/       # Flyway SQL migrations
│   └── application.yml     # Configuration
└── frontend/
    ├── src/
    │   ├── components/     # React components
    │   ├── pages/         # Page components
    │   ├── hooks/         # Custom React hooks
    │   ├── api/           # API clients
    │   └── styles/        # CSS files
    └── public/
```

## API Endpoints
AdminApplicationMaster (Port 8080)
```
POST /api/auth/login - User authentication
GET /api/v1/users - List all users
POST /api/v1/users - Create new user
GET /api/v1/users/{id} - Get user by ID
PUT /api/v1/users/{id} - Update user
DELETE /api/v1/users/{id} - Delete user
GET /api/v1/loan_applications - List loan applications
POST /api/v1/loan_applications - Create loan application
POST /api/v1/location_services - Check address eligibility
```

## Configuration
```
yamlspring:
  datasource:
    url: jdbc:postgresql://localhost:5432/admin_app_db
    username: postgres
    password: postgres

location:
  service:
    host: http://localhost:8081

decision:
  service:
    host: http://localhost:8082

encryption:
  key: local-dev-encryption-key-change-me

server:
  port: 8082
```

## Loan Application Workflow

Login with test credentials
Navigate to Loan Applications from sidebar
Click "New Loan Application"
### Step 1: Address Eligibility

Enter a valid address from the list above
System checks if address is in eligible region
If eligible, proceed to next step

### Step 2: Applicant Information

Fill in borrower details
Enter income and requested loan amount
Submit Application

System processes decision via DecisionServiceMaster
View application status and decision

## Troubleshooting

### View container logs
docker logs admin-app-postgres
docker logs location-service-postgres
docker logs decision-service-postgres

### Restart containers
docker-compose -f docker-compose-databases.yml restart
Redis Connection Issues
```bash
# Check Redis status
docker ps | grep redis
```

## Start Redis
```bash
cd LocationServiceMaster
docker-compose up redis -d
```

Redis available on port 6379
```bash
redis-cli ping
```

Addresses must match exactly as they appear in addresses.yml (case-insensitive)
Check LocationServiceMaster logs for: Successfully loaded X addresses into Redis cache
Verify Redis is running and accessible

### Service Communication Errors

Ensure all three services are running on correct ports (8080, 8081, 8082)
Check application-local.yml for correct service URLs
Verify API security is disabled in local profile

### Frontend Not Connecting to Backend

Check that AdminApplicationMaster is running on port 8080
Verify CORS is configured correctly
Check browser console for specific error messages

## Development
### Running Tests
```bash
# Backend tests
mvn test
```

## Frontend tests
```bash
cd ui
npm test
```
Building for Production
```bash# Backend
mvn clean package
```

## Frontend
```bash
cd ui
npm run build 
npm run dev
```
## AdminApplicationMaster
DATABASE_URL=jdbc:postgresql://prod-host:5432/admin_db
SECRET_KEY_BASE=your-secret-key
ENCRYPTION_KEY=your-encryption-key
LOCATION_SERVICE_HOST=http://location-service:8081
DECISION_SERVICE_HOST=http://decision-service:8082

## LocationServiceMaster
REDIS_HOST=redis-host
REDIS_PORT=6379
DATABASE_URL=jdbc:postgresql://prod-host:5433/location_db

## DecisionServiceMaster
DATABASE_URL=jdbc:postgresql://prod-host:5434/decision_db

## DecisionServiceMaster
DATABASE_URL=jdbc:postgresql://prod-host:5434/decision_db
