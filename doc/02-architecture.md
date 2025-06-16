# Architecture Overview

## System Architecture

XYZ follows a traditional **Model-View-Controller (MVC)** architecture pattern implemented with Spring Boot and MyBatis.

### High-Level Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    Client Browser                           │
│  ┌─────────────┐  ┌──────────────┐  ┌─────────────────────┐ │
│  │ Thymeleaf   │  │ CSS/JS       │  │ AJAX API Calls      │ │
│  │ Templates   │  │ Static Files │  │ (Sidebar Data)      │ │
│  └─────────────┘  └──────────────┘  └─────────────────────┘ │
└─────────────────────────┬───────────────────────────────────┘
                          │ HTTP/HTTPS
┌─────────────────────────▼───────────────────────────────────┐
│                  Spring Boot Application                    │
│  ┌─────────────────────────────────────────────────────────┐ │
│  │                   Web Layer                             │ │
│  │  ┌─────────────┐  ┌──────────────┐  ┌─────────────────┐ │ │
│  │  │ Controllers │  │ Interceptors │  │ Exception       │ │ │
│  │  │             │  │              │  │ Handlers        │ │ │
│  │  └─────────────┘  └──────────────┘  └─────────────────┘ │ │
│  └─────────────────────┬───────────────────────────────────┘ │
│  ┌─────────────────────▼───────────────────────────────────┐ │
│  │                Business Layer                           │ │
│  │  ┌─────────────┐  ┌──────────────┐  ┌─────────────────┐ │ │
│  │  │ Services    │  │ Validation   │  │ Security        │ │ │
│  │  │             │  │ Logic        │  │ Components      │ │ │
│  │  └─────────────┘  └──────────────┘  └─────────────────┘ │ │
│  └─────────────────────┬───────────────────────────────────┘ │
│  ┌─────────────────────▼───────────────────────────────────┐ │
│  │                 Data Access Layer                       │ │
│  │  ┌─────────────┐  ┌──────────────┐  ┌─────────────────┐ │ │
│  │  │ MyBatis     │  │ Mappers      │  │ Connection      │ │ │
│  │  │ Mappers     │  │ (XML/Anno)   │  │ Pool (Hikari)   │ │ │
│  │  └─────────────┘  └──────────────┘  └─────────────────┘ │ │
│  └─────────────────────┬───────────────────────────────────┘ │
└─────────────────────────┼───────────────────────────────────┘
                          │ JDBC
┌─────────────────────────▼───────────────────────────────────┐
│                      MySQL Database                         │
│  ┌─────────────┐  ┌──────────────┐  ┌─────────────────────┐ │
│  │ Core Tables │  │ Junction     │  │ System Tables       │ │
│  │ (users,     │  │ Tables       │  │ (sessions, logs)    │ │
│  │ posts, etc) │  │ (followers,  │  │                     │ │
│  │             │  │ hashtags)    │  │                     │ │
│  └─────────────┘  └──────────────┘  └─────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
```

## MVC Pattern Implementation

### Controllers (Presentation Layer)
**Responsibility**: Handle HTTP requests and responses
```java
@Controller
public class HomeController {
    // Route handling
    // Request validation
    // Model preparation
    // View selection
}
```

**Key Controllers**:
- `HomeController` - Timeline and dashboard
- `UserController` - Authentication and profiles
- `PostController` - Content management
- `MessageController` - Direct messaging
- `SidebarController` - API endpoints

### Services (Business Layer)
**Responsibility**: Business logic and transaction management
```java
@Service
@Transactional
public class PostService {
    // Business rules
    // Data validation
    // Transaction coordination
    // External service integration
}
```

**Key Services**:
- `UserService` - User management and authentication
- `PostService` - Content creation and interaction
- `MessageService` - Direct messaging logic

### Mappers (Data Access Layer)
**Responsibility**: Database operations and query execution
```java
@Mapper
public interface PostMapper {
    // CRUD operations
    // Complex queries
    // Result mapping
}
```

**Key Mappers**:
- `UserMapper` - User data operations
- `PostMapper` - Post and hashtag operations
- `MessageMapper` - Message data operations

## Request Processing Flow

### 1. Standard Web Request
```
HTTP Request → DispatcherServlet → Controller → Service → Mapper → Database
                     ↓
Model Data ← Thymeleaf Template ← Controller ← Service ← Mapper ← Database
                     ↓
HTTP Response (HTML)
```

### 2. AJAX API Request
```
AJAX Request → @RestController → Service → Mapper → Database
                     ↓
JSON Response ← Controller ← Service ← Mapper ← Database
```

### 3. Authentication Flow
```
Login Request → UserController → AuthenticationInterceptor
                     ↓                     ↓
              Session Created ← UserService → Password Validation
                     ↓                     ↓
              Redirect Response ← Success ← Database Lookup
```

## Component Interactions

### Authentication & Authorization
```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│ Web Request     │───▶│ Authentication  │───▶│ Session         │
│                 │    │ Interceptor     │    │ Management      │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                ↓
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│ User Context    │◀───│ Security Check  │───▶│ Route Access    │
│                 │    │                 │    │ Control         │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

### Data Validation & Security
```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│ User Input      │───▶│ Input Validator │───▶│ XSS Sanitizer   │
│                 │    │                 │    │                 │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                ↓
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│ Clean Data      │◀───│ Business Logic  │───▶│ Database        │
│                 │    │ Processing      │    │ Operations      │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

## Design Patterns

### 1. **Repository Pattern** (via MyBatis Mappers)
- Encapsulates data access logic
- Provides clean separation between business and data layers
- Enables easy testing with mock implementations

### 2. **Service Layer Pattern**
- Encapsulates business logic
- Provides transaction boundaries
- Enables code reuse across controllers

### 3. **DTO Pattern** (via Entities)
- Clean data transfer between layers
- Type-safe data operations
- Clear data contracts

### 4. **Template Method Pattern** (via Thymeleaf)
- Consistent page layout and structure
- Reusable UI components
- Dynamic content rendering

### 5. **Interceptor Pattern**
- Cross-cutting concerns (authentication, logging)
- Request/response processing
- Clean separation of concerns

## Scalability Considerations

### Current Architecture Benefits
- **Stateless Services**: Easy horizontal scaling
- **Database Connection Pooling**: Efficient resource usage
- **Session Management**: Distributed session support ready
- **Modular Design**: Independent component scaling

### Future Enhancements
- **Caching Layer**: Redis for session and data caching
- **Message Queue**: Asynchronous processing with RabbitMQ
- **Microservices**: Service decomposition for larger scale
- **Load Balancing**: Multiple application instances

## Security Architecture

### Authentication Flow
```
User Credentials → Password Hash Verification → Session Creation → JWT Token (Future)
       ↓                      ↓                      ↓                     ↓
Input Validation → Database Lookup → Session Storage → Token Management
```

### Authorization Layers
1. **Interceptor Level**: Route-based access control
2. **Service Level**: Business logic authorization
3. **Data Level**: Row-level security (user ownership)

### Security Components
- **InputValidator**: Prevents malicious input
- **XssSanitizer**: Prevents XSS attacks  
- **AuthenticationInterceptor**: Session validation
- **GlobalExceptionHandler**: Secure error responses

## Performance Optimizations

### Database Layer
- **Connection Pooling**: HikariCP for efficient connections
- **Query Optimization**: Indexed columns and optimized queries
- **Lazy Loading**: On-demand data fetching

### Application Layer
- **Session Management**: Efficient session storage
- **Static Resources**: Optimized CSS/JS delivery
- **Response Caching**: Browser-friendly caching headers

### Frontend Layer
- **Progressive Enhancement**: Core functionality without JavaScript
- **Efficient DOM Updates**: Minimal re-rendering
- **Asset Optimization**: Minified CSS/JS (production ready)

This architecture provides a solid foundation for a scalable, maintainable social media application while following Spring Boot best practices and enterprise-grade design patterns.