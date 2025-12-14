# CloudEagle Assessment - Calendly Integration Solution

## 🎯 Assignment Complete

This repository contains a **Java 25 Spring Boot 4.0** application that dynamically fetches users from multiple third-party systems (Calendly, Dropbox, etc.) with fully configurable API endpoints and field mappings stored in a database.

## 🌟 Key Features

### ✅ **All Requirements Met**

1. **Multi-Source User Fetching** - Supports fetching from any external system
2. **Dynamic Configuration** - API endpoints and field mappings stored in database, no redeployment needed
3. **Generic API Caller** - Single method to call and parse ANY external REST API
4. **Temporary User Storage** - Normalized user data stored across all sources
5. **Calendly Integration** - Pre-configured and ready to use

### 🚀 **Technical Highlights**

- **Java 25 Modern Features**: Records, pattern matching, sealed types, enhanced switch, virtual threads
- **Spring Boot 4.0**: Latest best practices, WebClient (no deprecated RestTemplate)
- **Layered Architecture**: Controller → Service → Repository pattern
- **JsonPath Dynamic Mapping**: Extract any field from any JSON response using database-configured expressions
- **Global Exception Handling**: Structured error responses with correlation IDs
- **Comprehensive Testing**: Unit tests with Mockito, API mocking with MockWebServer

## 📁 Project Structure

```
assessment/
├── src/main/java/cloud/eagle/assessment/
│   ├── config/
│   │   ├── DataInitializer.java          ← Seeds Calendly configuration
│   │   └── WebClientConfig.java          ← WebClient with timeouts
│   ├── controller/
│   │   ├── GlobalExceptionHandler.java   ← Centralized error handling
│   │   └── UserIntegrationController.java ← REST API endpoints
│   ├── domain/
│   │   ├── dto/                          ← Records for API (Java 25)
│   │   │   ├── FetchedUserDto.java
│   │   │   ├── ApiConfigurationDto.java
│   │   │   └── ...
│   │   └── entity/                       ← JPA Entities
│   │       ├── ApiConfiguration.java     ← Dynamic API config storage
│   │       ├── FieldMapping.java         ← JsonPath field mappings
│   │       └── FetchedUser.java          ← Normalized user storage
│   ├── exception/                        ← Custom exceptions
│   ├── mapper/
│   │   └── EntityMapper.java             ← Entity ↔ DTO conversions
│   ├── repository/                       ← Spring Data JPA
│   └── service/
│       ├── ExternalApiService.java       ← **GENERIC API CALLER** 🔑
│       ├── UserFetchService.java         ← Orchestrator
│       └── ApiConfigService.java         ← Config management
├── src/test/java/                        ← Unit & integration tests
├── demo.sh                               ← Quick demo script
├── SUMMARY.md                            ← Quick overview
├── IMPLEMENTATION.md                     ← Detailed documentation
└── starter.md                            ← Coding standards reference
```

## 🚀 Quick Start

### Prerequisites
- Java 25 installed
- Maven 3.8+

### 1️⃣ Configure Calendly API Token

Edit `src/main/java/cloud/eagle/assessment/config/DataInitializer.java` (line 47):
```java
calendlyConfig.setAuthCredentials("YOUR_CALENDLY_API_TOKEN");
```

**Get your token**: https://calendly.com/integrations/api_webhooks

### 2️⃣ Run the Demo
```bash
cd assessment
./demo.sh
```

This will build the project and show you all features.

### 3️⃣ Start the Application
```bash
./mvnw spring-boot:run
```

Application starts on `http://localhost:8080`

### 4️⃣ Test the API

**Fetch Calendly Users:**
```bash
curl -X POST http://localhost:8080/api/v1/integrations/fetch \
  -H "Content-Type: application/json" \
  -d '{"sourceName": "calendly"}'
```

**View Fetched Users:**
```bash
curl http://localhost:8080/api/v1/integrations/users
```

**List Available Integrations:**
```bash
curl http://localhost:8080/api/v1/integrations/configs
```

## 🔌 REST API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/v1/integrations/fetch` | Trigger user fetch from any source |
| `GET` | `/api/v1/integrations/users` | Get all fetched users (paginated) |
| `GET` | `/api/v1/integrations/users/{source}` | Get users by source (paginated) |
| `GET` | `/api/v1/integrations/configs` | List all API configurations |
| `GET` | `/api/v1/integrations/configs/{source}` | Get specific configuration |

## 💡 How It Works: The Generic API Engine

The core innovation is in `ExternalApiService.java`:

```java
public String callExternalApi(ApiConfiguration config) {
    // 1. Build WebClient with dynamic authentication
    // 2. Execute HTTP request (GET, POST, etc.)
    // 3. Return raw JSON response
}

public List<Map<String, Object>> parseAndMapResponse(String json, ApiConfiguration config) {
    // 1. Use JsonPath to extract data from response
    // 2. Apply field mappings from database
    // 3. Return normalized user data
}
```

### Example: Database Configuration for Calendly

```sql
-- API Configuration
INSERT INTO api_configurations (
    source_name, endpoint_url, http_method, auth_type, 
    auth_credentials, response_root_path
) VALUES (
    'calendly', 
    'https://api.calendly.com/users',
    'GET',
    'BEARER_TOKEN',
    'your-token-here',
    '$.collection'
);

-- Field Mappings (JsonPath expressions)
INSERT INTO field_mappings (api_configuration_id, internal_field_name, json_path, required)
VALUES 
    (1, 'externalId', '$.uri', true),
    (1, 'name', '$.name', false),
    (1, 'email', '$.email', false),
    (1, 'timezone', '$.timezone', false);
```

**Result**: The generic engine can now fetch and parse Calendly users without any code changes!

## ➕ Adding New Integrations (e.g., Dropbox)

**No code changes required!** Just add configuration to the database:

1. Create `ApiConfiguration` for Dropbox:
   - Source: "dropbox"
   - Endpoint: `https://api.dropboxapi.com/2/team/members/list_v2`
   - Method: POST
   - Auth: BEARER_TOKEN

2. Add field mappings:
   - `externalId` → `$.profile.team_member_id`
   - `email` → `$.profile.email`
   - `name` → `$.profile.name.display_name`

3. Fetch users:
```bash
curl -X POST http://localhost:8080/api/v1/integrations/fetch \
  -d '{"sourceName": "dropbox"}'
```

✨ **That's it!** The same generic engine handles Dropbox automatically.

## 🧪 Testing

### Run All Tests
```bash
./mvnw test
```

### Tests Included
- **ExternalApiServiceTest**: Tests generic API caller with MockWebServer
- **ApiConfigServiceTest**: Unit tests with Mockito
- **UserIntegrationControllerIntegrationTest**: Full Spring context tests

### Test Coverage
- Generic API integration logic
- JsonPath field mapping
- Error handling scenarios
- Repository layer
- DTO conversions

## 💾 Database

**H2 In-Memory Database** (for development)

- Console: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: _(empty)_

**Tables:**
- `api_configurations` - Stores API endpoint details
- `field_mappings` - JsonPath field extraction rules
- `fetched_users` - Normalized user data

## 📋 Java 25 & Spring Boot 4.0 Compliance

### ✅ Java 25 Features Used
- **Records** for all DTOs (immutable data carriers)
- **Enhanced Switch Expressions** for HTTP method handling
- **Pattern Matching** in exception handling
- **Virtual Threads** enabled for blocking I/O
- **Final fields** and immutability by default

### ✅ Spring Boot 4.0 Best Practices
- **WebClient** (no deprecated RestTemplate)
- **Layered Architecture** (Controller → Service → Repository)
- **Bean Validation** with `@Valid`
- **Global Exception Handling** with `@RestControllerAdvice`
- **DTOs only in API** (never expose entities)
- **JPA with proper transaction boundaries**

### ✅ Coding Standards (starter.md)
- Readability over cleverness ✓
- Explicit dependency wiring ✓
- Fail fast with descriptive exceptions ✓
- Proper logging with SLF4J ✓
- Comprehensive testing ✓

## 📚 Documentation

- **SUMMARY.md** - Quick overview of implementation
- **IMPLEMENTATION.md** - Detailed technical documentation
- **starter.md** - Coding standards reference
- **demo.sh** - Interactive demonstration script

## 🎯 Assessment Requirements Checklist

✅ **Support fetching users from multiple external systems** (Calendly, Dropbox, etc.)  
✅ **API endpoints and field mappings are dynamic/configurable** (stored in database)  
✅ **Changes without redeploying** (update database, no code changes needed)  
✅ **One generic method to call any external API** (`ExternalApiService`)  
✅ **Store all fetched users in temporary collection/table** (`FetchedUser` entity)  
✅ **Implemented for Calendly app** (pre-configured and ready to use)  
✅ **Follow all instructions in starter.md** (Java 25, Spring Boot 4.0 standards)  

## 🏗️ Architecture Highlights

### Generic Integration Engine
```
┌─────────────────────────────────────────────┐
│         UserIntegrationController           │
│    (Thin REST layer, input validation)      │
└──────────────────┬──────────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────────┐
│          UserFetchService                   │
│    (Orchestrates fetch process)             │
└──────────────────┬──────────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────────┐
│       ExternalApiService                    │
│  ┌────────────────────────────────────────┐ │
│  │ 1. callExternalApi()                   │ │
│  │    - Dynamic authentication            │ │
│  │    - HTTP method dispatch              │ │
│  │    - Error handling                    │ │
│  │                                        │ │
│  │ 2. parseAndMapResponse()               │ │
│  │    - JsonPath extraction               │ │
│  │    - Field mapping from database       │ │
│  │    - Data normalization                │ │
│  └────────────────────────────────────────┘ │
└─────────────────────────────────────────────┘
                   │
                   ▼
         ┌──────────────────┐
         │  External APIs   │
         │  - Calendly      │
         │  - Dropbox       │
         │  - Any JSON API  │
         └──────────────────┘
```

### Database-Driven Configuration
```
ApiConfiguration ──┬── HttpMethod (GET/POST/PUT/DELETE)
                   ├── AuthType (BEARER/API_KEY/BASIC/NONE)
                   ├── Endpoint URL
                   ├── Auth Credentials
                   ├── Response Root Path (JsonPath)
                   └── FieldMappings[]
                       ├── Internal Field Name
                       ├── JsonPath Expression
                       ├── Required Flag
                       └── Default Value
```

## 🔒 Security Notes

- API credentials stored in database (production: use Spring Vault)
- No secrets logged
- Input validation on all endpoints
- Jackson configured with safe defaults
- CORS can be configured in WebClientConfig

## 📞 Support

For questions about implementation:
- See `IMPLEMENTATION.md` for detailed documentation
- Check `starter.md` for coding standards
- Run `./demo.sh` for quick demonstration

## 🎓 Learning Highlights

This implementation demonstrates:
1. **Generic, data-driven architecture** - No hardcoding for specific APIs
2. **Java 25 modern features** - Records, pattern matching, virtual threads
3. **Spring Boot 4.0 best practices** - WebClient, layered architecture
4. **Extensibility** - Add new integrations without code changes
5. **Enterprise patterns** - Global exception handling, structured logging, testing

---

**Built for CloudEagle Assessment** | Java 25 + Spring Boot 4.0 | December 2025

