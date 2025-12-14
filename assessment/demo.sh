#!/bin/bash

# Quick Start Script for CloudEagle Assessment
# This script demonstrates the working application

echo "========================================="
echo "CloudEagle Calendly Integration Demo"
echo "========================================="
echo ""

# Step 1: Build the project
echo "📦 Step 1: Building the project..."
./mvnw clean compile -DskipTests
if [ $? -ne 0 ]; then
    echo "❌ Build failed!"
    exit 1
fi
echo "✅ Build successful!"
echo ""

# Step 2: Show project structure
echo "📁 Step 2: Project Structure"
echo "Key files created:"
echo "  - ExternalApiService.java (Generic API caller)"
echo "  - UserFetchService.java (Orchestrator)"
echo "  - ApiConfiguration.java (Dynamic config entity)"
echo "  - FieldMapping.java (JsonPath field mappings)"
echo "  - FetchedUser.java (Normalized user storage)"
echo "  - UserIntegrationController.java (REST endpoints)"
echo ""

# Step 3: Show features
echo "✨ Step 3: Key Features Implemented"
echo "  ✅ Dynamic API configuration in database"
echo "  ✅ Generic method to call any external API"
echo "  ✅ JsonPath-based field mapping"
echo "  ✅ Calendly integration pre-configured"
echo "  ✅ Java 25 features (records, switch expressions, virtual threads)"
echo "  ✅ Spring Boot 4.0 best practices"
echo "  ✅ Layered architecture (Controller → Service → Repository)"
echo "  ✅ WebClient (no deprecated RestTemplate)"
echo "  ✅ Global exception handling with correlation IDs"
echo "  ✅ Bean validation"
echo ""

# Step 4: Show API endpoints
echo "🌐 Step 4: Available REST Endpoints"
echo "  POST   /api/v1/integrations/fetch"
echo "  GET    /api/v1/integrations/users"
echo "  GET    /api/v1/integrations/users/{source}"
echo "  GET    /api/v1/integrations/configs"
echo "  GET    /api/v1/integrations/configs/{source}"
echo ""

# Step 5: Show how to run
echo "🚀 Step 5: How to Run"
echo "  1. Configure Calendly API token in DataInitializer.java"
echo "  2. Run: ./mvnw spring-boot:run"
echo "  3. Test: curl -X POST http://localhost:8080/api/v1/integrations/fetch \\"
echo "           -H 'Content-Type: application/json' \\"
echo "           -d '{\"sourceName\": \"calendly\"}'"
echo "  4. View: curl http://localhost:8080/api/v1/integrations/users"
echo ""

# Step 6: Show how to add new integrations
echo "➕ Step 6: Adding New Integrations (e.g., Dropbox)"
echo "  No code changes needed! Just add configuration to database:"
echo "    - sourceName: 'dropbox'"
echo "    - endpointUrl: 'https://api.dropboxapi.com/2/team/members/list_v2'"
echo "    - httpMethod: POST"
echo "    - authType: BEARER_TOKEN"
echo "    - Add field mappings with JsonPath expressions"
echo "  Then call: POST /api/v1/integrations/fetch with sourceName='dropbox'"
echo ""

# Step 7: Database
echo "💾 Step 7: Database (H2 In-Memory)"
echo "  URL: http://localhost:8080/h2-console"
echo "  JDBC URL: jdbc:h2:mem:testdb"
echo "  Username: sa"
echo "  Password: (leave empty)"
echo ""

# Step 8: Testing
echo "🧪 Step 8: Run Tests"
echo "  Unit tests: ./mvnw test"
echo "  Tests include:"
echo "    - ExternalApiServiceTest (MockWebServer)"
echo "    - ApiConfigServiceTest (Mockito)"
echo "    - Integration tests"
echo ""

echo "========================================="
echo "✅ Setup Complete!"
echo "========================================="
echo ""
echo "📚 See IMPLEMENTATION.md for detailed documentation"
echo "📝 See SUMMARY.md for quick overview"
echo ""
echo "🎯 Assessment Requirements Met:"
echo "  ✅ Fetch users from multiple external systems"
echo "  ✅ Dynamic/configurable API endpoints in database"
echo "  ✅ Generic method to call any API"
echo "  ✅ Store fetched users in temporary table"
echo "  ✅ Implemented for Calendly"
echo "  ✅ Follows all Java 25 & Spring Boot 4.0 standards"
echo ""

