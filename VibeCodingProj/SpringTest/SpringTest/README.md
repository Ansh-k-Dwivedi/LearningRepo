# Book Management System - Production Ready Spring Boot Application

A comprehensive, production-ready REST API for managing books in a library system. Built with Spring Boot 3.5.4, this application demonstrates enterprise-level features including caching, monitoring, security, containerization, and comprehensive testing.

## 🚀 Features

### Core Functionality
- **Complete CRUD Operations**: Create, Read, Update, Delete books
- **Advanced Search**: Multi-field filtering, full-text search, pagination
- **Data Validation**: Input validation with detailed error messages
- **Business Logic**: Duplicate detection, ISBN validation

### Production Features
- **Caching**: Redis-based caching for improved performance
- **Security**: Role-based access control with Spring Security
- **API Documentation**: OpenAPI 3.0 (Swagger UI)
- **Database Migrations**: Flyway for version-controlled schema changes
- **Comprehensive Testing**: Unit, integration, and end-to-end tests
- **Monitoring**: Actuator endpoints, custom health checks, Prometheus metrics
- **Containerization**: Docker and Docker Compose ready
- **Rate Limiting**: API rate limiting with Bucket4j
- **Circuit Breaker**: Resilience4j for fault tolerance

## 🛠️ Technology Stack

- **Framework**: Spring Boot 3.5.4
- **Java**: JDK 21+
- **Database**: H2 (with file persistence)
- **Cache**: Redis
- **Build Tool**: Maven
- **Testing**: JUnit 5, Spring Boot Test, MockMvc
- **API Documentation**: SpringDoc OpenAPI
- **Containerization**: Docker, Docker Compose
- **Monitoring**: Spring Actuator, Micrometer, Prometheus

## 📋 Prerequisites

- Java 21 or higher
- Maven 3.6+
- Docker and Docker Compose (for containerized deployment)
- Redis (if running without Docker)

## 🏃‍♂️ Getting Started

### 1. Clone and Build

```bash
git clone <repository-url>
cd SpringTest
mvn clean install
```

### 2. Run Locally

```bash
# Run with default profile (uses simple cache)
mvn spring-boot:run

# Run with development profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Run with production profile (requires Redis)
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

### 3. Run with Docker Compose

```bash
# Start all services (app + Redis)
docker-compose up -d

# Start with monitoring stack
docker-compose --profile monitoring up -d

# Start with debug tools
docker-compose --profile debug up -d
```

## 📊 API Endpoints

### Books Management
- `GET /api/v1/books` - Get all books
- `POST /api/v1/books` - Create a new book
- `GET /api/v1/books/{id}` - Get book by ID
- `PUT /api/v1/books/{id}` - Update book
- `DELETE /api/v1/books/{id}` - Delete book

### Advanced Search
- `GET /api/v1/books/pageable` - Paginated books list
- `GET /api/v1/books/search` - Advanced filtering
- `GET /api/v1/books/search/text?q={keyword}` - Full-text search
- `GET /api/v1/books/author/{author}` - Books by author
- `GET /api/v1/books/available` - Available books only

### Statistics & Monitoring
- `GET /api/v1/books/stats` - Book collection statistics
- `GET /api/v1/books/exists/{id}` - Check if book exists
- `GET /api/v1/health/status` - Detailed health information
- `GET /api/v1/health/ready` - Readiness probe
- `GET /api/v1/health/live` - Liveness probe

## 📖 API Documentation

Once the application is running, visit:
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

## 🔧 Configuration

### Profiles

- **default**: Uses in-memory cache, suitable for development
- **dev**: Enhanced debugging, all actuator endpoints exposed
- **prod**: Production optimizations, limited endpoint exposure

### Key Configuration Properties

```properties
# Database
spring.datasource.url=jdbc:h2:file:./data/bookdb

# Redis (for caching)
spring.data.redis.host=localhost
spring.data.redis.port=6379

# Security
# Default users: admin/admin123, user/user123

# Rate Limiting
bucket4j.filters[0].rate-limits[0].bandwidths[0].capacity=100
```

## 🧪 Testing

```bash
# Run all tests
mvn test

# Run with coverage
mvn test jacoco:report

# Run integration tests only
mvn test -Dtest=*IntegrationTest
```

## 📈 Monitoring & Health Checks

### Actuator Endpoints
- `/actuator/health` - Application health
- `/actuator/metrics` - Application metrics
- `/actuator/prometheus` - Prometheus metrics
- `/actuator/info` - Application information

### Custom Health Endpoints
- `/api/v1/health/status` - Detailed system information
- `/api/v1/health/ready` - Kubernetes readiness probe
- `/api/v1/health/live` - Kubernetes liveness probe

## 🐳 Docker Deployment

### Build Image
```bash
docker build -t book-management:latest .
```

### Run Container
```bash
docker run -p 8080:8080 book-management:latest
```

### Docker Compose Services
- **app**: Main Spring Boot application
- **redis**: Cache storage
- **redis-commander**: Redis management UI (debug profile)
- **prometheus**: Metrics collection (monitoring profile)
- **grafana**: Dashboards (monitoring profile)

## 🔒 Security

### Authentication
- HTTP Basic Authentication
- Default users: `admin/admin123`, `user/user123`

### Authorization
- Public endpoints: `/api/v1/books/**`, health checks, API docs
- Admin only: `/actuator/**` (except health, info, metrics)

### Rate Limiting
- 100 requests per minute per IP
- Configurable via Bucket4j

## 🏗️ Database

### Schema Management
- Flyway migrations in `src/main/resources/db/migration/`
- Automatic schema updates on startup
- Sample data included in V1 migration

### Sample Data
- 10 classic books with complete metadata
- Various genres: Fiction, Science Fiction, Fantasy, Romance
- Realistic pricing and stock quantities

## 📝 Sample Usage

### Create a Book
```bash
curl -X POST http://localhost:8080/api/v1/books \
  -H "Content-Type: application/json" \
  -d '{
    "title": "The Pragmatic Programmer",
    "author": "David Thomas",
    "isbn": "978-0-201-61622-4",
    "description": "Your journey to mastery",
    "genre": "Programming",
    "price": 34.99,
    "publicationYear": 1999,
    "stockQuantity": 25
  }'
```

### Search Books
```bash
# Search by genre and price range
curl "http://localhost:8080/api/v1/books/search?genre=Fiction&minPrice=10&maxPrice=20"

# Full-text search
curl "http://localhost:8080/api/v1/books/search/text?q=programming"

# Paginated results
curl "http://localhost:8080/api/v1/books/pageable?page=0&size=5&sortBy=title&sortDir=asc"
```

## 🚨 Error Handling

The application provides comprehensive error handling:

- **Validation Errors**: Field-level validation messages
- **Not Found**: Detailed error for missing resources
- **Business Logic**: Custom exceptions for duplicate books, etc.
- **System Errors**: Generic error responses for unexpected issues

## 🔄 Caching Strategy

- **Books Cache**: 15-minute TTL for individual books and lists
- **Statistics Cache**: 30-minute TTL for analytics data
- **Cache Eviction**: Automatic invalidation on data changes

## 📊 Performance Features

- **Connection Pooling**: HikariCP with optimized settings
- **Query Optimization**: Strategic database indexes
- **Lazy Loading**: JPA optimization to prevent N+1 queries
- **Compression**: Response compression enabled
- **Caching**: Multi-level caching strategy

## 🛡️ Production Considerations

### Security Hardening
- Non-root Docker user
- Input validation and sanitization
- SQL injection prevention via JPA
- XSS protection headers

### Scalability
- Stateless design for horizontal scaling
- Redis clustering support
- Database connection pooling
- Efficient pagination

### Observability
- Structured logging with correlation IDs
- Metrics collection (JVM, custom business metrics)
- Health checks for Kubernetes deployment
- Distributed tracing ready

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Add tests for new functionality
4. Ensure all tests pass
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details. 