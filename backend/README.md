# Loan Approval Hub - Backend

AI-powered Loan Decision Engine Backend built with Spring Boot 3.2, PostgreSQL, JWT authentication, and Gemini API integration.

## 🚀 Features

- **JWT Authentication**: Secure token-based authentication with role-based access control (RBAC)
- **Loan Application Management**: Full CRUD operations with status workflow
- **Risk Scoring Engine**: Sophisticated algorithm analyzing 5 weighted factors
- **Document Management**: File upload, verification, and OCR extraction
- **Decision Management**: Approval, rejection, and manual review workflows
- **Dashboard Analytics**: Real-time statistics and metrics
- **Gemini AI Integration**: Decision explanations and improvement suggestions (upcoming)
- **Audit Logging**: Complete audit trail for compliance
- **API Documentation**: Swagger/OpenAPI integration

## 📋 Technology Stack

- **Framework**: Spring Boot 3.2
- **Database**: PostgreSQL
- **Authentication**: JWT (JSON Web Tokens)
- **API Documentation**: Springdoc OpenAPI (Swagger)
- **Build Tool**: Maven
- **Java Version**: 17+

## 🔧 Setup & Installation

### Prerequisites

- Java 17 or higher
- PostgreSQL 12+
- Maven 3.8+

### Step 1: Database Setup

```bash
# Create database
createdb loan_approval_db

# Create user (optional)
createuser -P loan_user
```

### Step 2: Clone and Build

```bash
# Clone repository
git clone <repository-url>
cd loan-approval-hub/backend

# Build with Maven
mvn clean install
```

### Step 3: Configuration

Update `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/loan_approval_db
spring.datasource.username=postgres
spring.datasource.password=your_password

jwt.secret=your-super-secret-key-change-in-production-with-min-256-bits-entropy

# Gemini API (when ready)
gemini.api.key=YOUR_GEMINI_API_KEY_HERE

# CORS configuration
cors.allowed-origins=http://localhost:5173,http://localhost:3000
```

### Step 4: Run Application

```bash
# Using Maven
mvn spring-boot:run

# Or build JAR and run
mvn clean package
java -jar target/loan-approval-hub-backend-1.0.0.jar
```

Server starts at: `http://localhost:8080`

## 📚 API Documentation

Once running, access Swagger UI: `http://localhost:8080/swagger-ui.html`

## 🔐 Default Users

The application initializes with test users:

| Email | Password | Role |
|-------|----------|------|
| customer@example.com | password123 | CUSTOMER |
| officer@example.com | password123 | OFFICER |
| admin@example.com | password123 | ADMIN |

## 🌐 API Endpoints

### Authentication
```
POST /api/auth/login          - Login user
GET  /api/auth/me             - Get current user
POST /api/auth/logout         - Logout user
```

### Loan Applications
```
POST   /api/applications            - Submit new application
GET    /api/applications            - List applications (paginated)
GET    /api/applications/{id}       - Get application details
PUT    /api/applications/{id}       - Update application
DELETE /api/applications/{id}       - Delete application
```

### Application Decisions
```
POST /api/applications/{id}/approve         - Approve application
POST /api/applications/{id}/reject          - Reject application
POST /api/applications/{id}/review-request  - Request manual review
POST /api/applications/{id}/notes           - Add officer notes
```

### Documents
```
POST   /api/documents/upload                - Upload document
GET    /api/documents/{id}/download         - Download document
POST   /api/documents/{id}/verify           - Verify document
DELETE /api/documents/{id}                  - Delete document
GET    /api/documents/application/{appId}   - Get application documents
```

### Dashboard
```
GET /api/dashboard/stats - Get dashboard statistics
```

## 🎯 Risk Scoring Algorithm

### Factors (Weighted)

1. **Credit Score (30%)**: Normalized 300-850 score
2. **Debt-to-Income Ratio (25%)**: Monthly obligations vs income
3. **Employment Stability (20%)**: Years at current job
4. **Loan-to-Income Ratio (15%)**: Loan amount vs annual income
5. **Document Verification (10%)**: Percentage of verified documents

### Decision Thresholds

- **Risk Score ≤ 30**: ✅ Auto-Approved
- **Risk Score 31-60**: ⚠️ Manual Review Required
- **Risk Score ≥ 61**: ❌ Auto-Rejected

## 📦 Project Structure

```
src/main/java/com/loanapproval/
├── entity/              # JPA entities
├── repository/          # Data access layer
├── service/             # Business logic
├── controller/          # REST endpoints
├── dto/                 # Data transfer objects
├── security/            # Authentication & authorization
├── config/              # Spring configurations
├── exception/           # Custom exceptions
├── common/enums/        # Enumeration types
└── LoanApprovalHubApplication.java  # Main class
```

## 🔐 Security

- JWT tokens expire in 24 hours
- Refresh tokens valid for 7 days
- Passwords encrypted with BCrypt
- Role-based access control on all endpoints
- CORS configured for frontend

## 📝 Database Schema

### Key Tables
- `users` - User accounts and roles
- `loan_applications` - Loan application records
- `loan_documents` - Uploaded documents
- `risk_factors` - Risk calculation breakdown
- `audit_logs` - Change tracking

All tables include audit fields: `created_at`, `updated_at`

## 🧪 Testing

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=ApplicationControllerTest

# Generate coverage report
mvn jacoco:report
```

## 🚀 Deployment

### Docker Deployment
```bash
# Build Docker image
docker build -t loan-approval-backend .

# Run container
docker run -p 8080:8080 --env-file .env loan-approval-backend
```

### Production Checklist
- [ ] Change JWT secret to strong random value
- [ ] Enable HTTPS/TLS
- [ ] Configure production database
- [ ] Set up environment variables for sensitive data
- [ ] Enable rate limiting
- [ ] Configure logging and monitoring
- [ ] Set up automated backups
- [ ] Enable database encryption

## 🔄 Gemini API Integration (Upcoming)

The backend is prepared for Gemini AI integration:

1. **Document OCR**: Extract data from salary slips and bank statements
2. **Decision Explanations**: Generate AI-powered decision rationale
3. **Improvement Suggestions**: Provide actionable recommendations

## 📋 Environment Variables

```
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/loan_approval_db
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=password
JWT_SECRET=your-secret-key
GEMINI_API_KEY=your-gemini-key
CORS_ALLOWED_ORIGINS=http://localhost:5173
```

## 🐛 Troubleshooting

### Database Connection Error
- Verify PostgreSQL is running
- Check credentials in application.properties
- Ensure database exists: `createdb loan_approval_db`

### CORS Error
- Verify `cors.allowed-origins` includes your frontend URL
- Check that CORS filter is enabled in SecurityConfig

### JWT Token Issues
- Ensure `Authorization: Bearer <token>` header format
- Check token expiration
- Verify JWT secret matches across requests

## 📞 Support

For issues or questions:
1. Check Swagger API documentation at `/swagger-ui.html`
2. Review application logs for error details
3. Verify database connectivity

## 📄 License

MIT License - See LICENSE file for details

## 🤝 Contributing

1. Create feature branch
2. Make changes
3. Submit pull request
4. Ensure tests pass

---

**Backend Ready!** Now integrate with the React frontend at `http://localhost:5173`
