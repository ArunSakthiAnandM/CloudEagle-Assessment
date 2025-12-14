# CloudEagle Assessment - Multi-Source User Integration

A Spring Boot 4.0 application built with Java 25 that dynamically fetches users from multiple third-party systems (Calendly, Dropbox, etc.) with configurable API endpoints and field mappings stored in a database.

## Features

- ✅ **Dynamic API Configuration**: API endpoints, authentication, and field mappings stored in H2 database
- ✅ **Generic API Integration Engine**: Single method to call any external API and parse responses
- ✅ **Flexible Field Mapping**: JsonPath-based field mapping with support for required/optional fields
- ✅ **Calendly Integration**: Pre-configured integration for Calendly Users API
- ✅ **RESTful API**: Clean REST endpoints following layered architecture
- ✅ **Java 25 Features**: Records, pattern matching, sealed types, enhanced switch expressions
- ✅ **Virtual Threads**: Optimized for blocking I/O operations
- ✅ **Comprehensive Testing**: Unit and integration tests with MockWebServer and Testcontainers
- ✅ **Error Handling**: Structured error responses with correlation IDs

## Technology Stack

- **Java 25** (with modern language features)
- **Spring Boot 4.0.0**
- **Spring Data JPA** (with H2 in-memory database)
- **Spring WebFlux** (WebClient for HTTP calls)
- **JsonPath** (for dynamic field extraction)
- **Lombok** (to reduce boilerplate)
- **JUnit 5** + **MockWebServer** (for testing)

## Architecture

Following the coding standards from `starter.md`, the application uses a layered architecture:

```
Controller Layer (REST endpoints)
    ↓
Service Layer (business logic)
    ↓
Repository Layer (data access)
    ↓
Database (H2)
```

### Key Components

1. **ApiConfiguration Entity**: Stores API endpoint details, authentication, and HTTP method
2. **FieldMapping Entity**: Defines how to map external API fields to internal user fields using JsonPath
3. **FetchedUser Entity**: Temporary storage for normalized user data across all sources
4. **ExternalApiService**: Generic service that calls any external API and parses responses
5. **UserFetchService**: Orchestrates the fetch process and stores results
6. **UserIntegrationController**: Thin REST controller exposing integration endpoints

## API Endpoints

### 1. Fetch Users from External Source
```bash
POST /api/v1/integrations/fetch
Content-Type: application/json

{
  "sourceName": "calendly"
}
```

**Response:**
```json
{
  "sourceName": "calendly",
  "usersFetched": 5,
  "message": "Successfully fetched 5 users from calendly"
}
```

### 2. Get All Fetched Users (Paginated)
```bash
GET /api/v1/integrations/users?page=0&size=20
```

### 3. Get Users by Source (Paginated)
```bash
GET /api/v1/integrations/users/calendly?page=0&size=20
```

### 4. List All API Configurations
```bash
GET /api/v1/integrations/configs
```

### 5. Get Specific API Configuration
```bash
GET /api/v1/integrations/configs/calendly
```

## Setup Instructions

### Prerequisites
- Java 25 installed
- Maven 3.8+

### 1. Clone and Navigate to Project
```bash
cd assessment
```

### 2. Configure Calendly API Token
Edit `src/main/java/cloud/eagle/assessment/config/DataInitializer.java` and replace `YOUR_CALENDLY_API_TOKEN` with your actual Calendly API token.

**To get a Calendly API token:**
1. Log in to your Calendly account
2. Go to Integrations > API & Webhooks
3. Generate a Personal Access Token
4. Copy the token and paste it in the DataInitializer

### 3. Build the Application
```bash
./mvnw clean install
```

### 4. Run the Application
```bash
./mvnw spring-boot:run
```

The application will start on `http://localhost:8080`

### 5. Access H2 Console (Optional)
- URL: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: (leave empty)

## Testing

### Run All Tests
```bash
./mvnw test
```

### Run Specific Test Class
```bash
./mvnw test -Dtest=ExternalApiServiceTest
```

## Usage Example: Fetching Calendly Users

### 1. Start the application
```bash
./mvnw spring-boot:run
```

### 2. Verify Calendly configuration exists
```bash
curl http://localhost:8080/api/v1/integrations/configs/calendly
```

### 3. Fetch users from Calendly
```bash
curl -X POST http://localhost:8080/api/v1/integrations/fetch \
  -H "Content-Type: application/json" \
  -d '{"sourceName": "calendly"}'
```

### 4. Retrieve fetched users
```bash
curl http://localhost:8080/api/v1/integrations/users
```

## Adding New Integrations (e.g., Dropbox)

To add a new integration, insert a new `ApiConfiguration` record in the database:

```java
// In DataInitializer.java or via REST endpoint

ApiConfiguration dropboxConfig = new ApiConfiguration();
dropboxConfig.setSourceName("dropbox");
dropboxConfig.setEndpointUrl("https://api.dropboxapi.com/2/team/members/list_v2");
dropboxConfig.setHttpMethod(HttpMethod.POST);
dropboxConfig.setAuthType(AuthType.BEARER_TOKEN);
dropboxConfig.setAuthCredentials("YOUR_DROPBOX_TOKEN");
dropboxConfig.setResponseRootPath("$.members");
dropboxConfig.setActive(true);

// Add field mappings
FieldMapping emailMapping = new FieldMapping("email", "$.profile.email", true);
FieldMapping nameMapping = new FieldMapping("name", "$.profile.name.display_name", false);
// ... add more mappings

dropboxConfig.addFieldMapping(emailMapping);
dropboxConfig.addFieldMapping(nameMapping);

repository.save(dropboxConfig);
```

Then fetch users:
```bash
curl -X POST http://localhost:8080/api/v1/integrations/fetch \
  -H "Content-Type: application/json" \
  -d '{"sourceName": "dropbox"}'
```

## Design Decisions

### Java 25 Features Used
- **Records**: All DTOs (FetchedUserDto, ApiConfigurationDto, etc.) use records for immutability
- **Enhanced Switch Expressions**: HTTP method handling in ExternalApiService
- **Pattern Matching**: Error handling in GlobalExceptionHandler
- **Virtual Threads**: Enabled via Spring Boot configuration for blocking I/O operations

### Why WebClient over RestTemplate?
Per Spring Boot 4.0 best practices (starter.md), `RestTemplate` is deprecated. WebClient provides:
- Non-blocking reactive capabilities
- Better timeout handling
- Support for virtual threads

### Why JsonPath?
JsonPath allows dynamic field extraction from any JSON structure without creating Java classes for each external API response format. This makes the solution truly generic and configurable.

### Security Considerations
- API credentials stored in database (in production, use Spring Vault or environment variables)
- No secrets logged
- Jackson configured with safe defaults
- Input validation on all endpoints

## Project Structure

```
assessment/
├── src/main/java/cloud/eagle/assessment/
│   ├── AssessmentApplication.java
│   ├── config/
│   │   ├── DataInitializer.java           # Seeds Calendly config
│   │   └── WebClientConfig.java           # WebClient beans
│   ├── controller/
│   │   ├── GlobalExceptionHandler.java    # Centralized error handling
│   │   └── UserIntegrationController.java # REST endpoints
│   ├── domain/
│   │   ├── dto/                           # Records for API responses
│   │   └── entity/                        # JPA entities
│   ├── exception/                         # Custom exceptions
│   ├── mapper/
│   │   └── EntityMapper.java              # Entity ↔ DTO mapping
│   ├── repository/                        # Spring Data repositories
│   └── service/
│       ├── ApiConfigService.java          # Config management
│       ├── ExternalApiService.java        # Generic API caller
│       └── UserFetchService.java          # Orchestrator
├── src/main/resources/
│   └── application.properties
└── src/test/java/                         # Unit & integration tests
```

## Compliance with Starter.md Standards

✅ **Java 25-First**: Uses records, pattern matching, enhanced switch, virtual threads  
✅ **Layered Architecture**: Controller → Service → Repository  
✅ **Immutability**: Records for DTOs, final fields, no exposed setters  
✅ **Fail Fast**: Early validation, descriptive exceptions  
✅ **Explicit**: No magic defaults, transparent dependency wiring  
✅ **Testing**: Unit tests with Mockito, integration tests with MockWebServer  
✅ **Error Handling**: Structured ErrorResponse with correlation IDs  
✅ **Logging**: SLF4J with @Slf4j, no System.out  
✅ **DTOs**: Never expose entities through API  

## Future Enhancements

- [ ] Add pagination support for external APIs (cursor-based)
- [ ] Implement retry logic with exponential backoff (Resilience4j)
- [ ] Add async processing with virtual threads for batch operations
- [ ] Support for field transformations (date formatting, concatenation)
- [ ] Admin UI for managing API configurations
- [ ] Metrics and observability with Micrometer
- [ ] Rate limiting for external API calls
- [ ] Data retention policy with TTL

## License

This project is part of the CloudEagle Assessment.

## Author

Built following enterprise Java 25 and Spring Boot 4.0 best practices.

