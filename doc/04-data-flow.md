# Data Flow & Processing

## Overview

This document describes how data flows through the XYZ application, from user interactions to database operations and back to the presentation layer.

## Request Processing Flows

### 1. Standard Web Page Request

```mermaid
graph TD
    A[User Browser] -->|HTTP Request| B[DispatcherServlet]
    B --> C[AuthenticationInterceptor]
    C -->|Authenticated| D[Controller Method]
    C -->|Not Authenticated| E[Redirect to Login]
    D --> F[Service Layer]
    F --> G[Mapper Interface]
    G --> H[MyBatis XML]
    H --> I[Database Query]
    I --> J[Result Set]
    J --> K[Entity Mapping]
    K --> L[Business Logic]
    L --> M[Model Preparation]
    M --> N[Thymeleaf Template]
    N --> O[HTML Response]
    O --> A
```

#### Detailed Steps:
1. **Request Reception**: Spring's DispatcherServlet receives HTTP request
2. **Authentication Check**: AuthenticationInterceptor validates session
3. **Route Mapping**: Controller method mapped via @RequestMapping
4. **Service Invocation**: Controller delegates to service layer
5. **Data Access**: Service calls mapper for database operations
6. **Query Execution**: MyBatis executes SQL and maps results
7. **Business Processing**: Service applies business logic
8. **Model Creation**: Controller prepares model for template
9. **Template Rendering**: Thymeleaf processes template with model
10. **Response Delivery**: HTML sent back to browser

### 2. AJAX API Request (Sidebar Data)

```mermaid
graph TD
    A[JavaScript] -->|AJAX Request| B[SidebarController]
    B --> C[PostService & UserService]
    C --> D[Multiple Mappers]
    D --> E[Parallel Database Queries]
    E --> F[Trending Calculation]
    E --> G[User Recommendations]
    F --> H[Result Aggregation]
    G --> H
    H --> I[JSON Response]
    I --> J[DOM Update]
    J --> K[UI Refresh]
```

#### AJAX Flow Characteristics:
- **Asynchronous**: Non-blocking user interface
- **Parallel Processing**: Multiple queries executed simultaneously
- **JSON Response**: Lightweight data format
- **Dynamic Updates**: Real-time sidebar content refresh

### 3. User Authentication Flow

```mermaid
graph TD
    A[Login Form] -->|POST /login| B[UserController]
    B --> C[Input Validation]
    C -->|Valid| D[UserService.authenticate]
    C -->|Invalid| E[Validation Error]
    D --> F[Password Hash Check]
    F --> G[Database Lookup]
    G -->|Match| H[Session Creation]
    G -->|No Match| I[Authentication Failed]
    H --> J[User Object in Session]
    J --> K[Redirect to Home]
    I --> L[Error Message]
    E --> L
    L --> M[Login Page with Error]
```

### 4. Post Creation Flow

```mermaid
graph TD
    A[Post Form] -->|POST /post| B[PostController]
    B --> C[Session Validation]
    C --> D[Input Validation]
    D --> E[XSS Sanitization]
    E --> F[PostService.createPost]
    F --> G[Hashtag Extraction]
    G --> H[Database Transaction]
    H --> I[Insert Post]
    H --> J[Process Hashtags]
    H --> K[Update User Counts]
    I --> L[Transaction Commit]
    J --> L
    K --> L
    L --> M[Redirect to Timeline]
```

#### Transaction Management:
- **@Transactional**: Service layer manages database transactions
- **Rollback**: Automatic rollback on exceptions
- **Consistency**: All related operations succeed or fail together

## Authentication & Authorization Flow

### Session-Based Authentication

```mermaid
graph TD
    A[User Request] --> B[AuthenticationInterceptor]
    B --> C{Session Exists?}
    C -->|Yes| D{Session Valid?}
    C -->|No| E[Redirect to Login]
    D -->|Yes| F[Extract User Context]
    D -->|No| G[Session Expired]
    F --> H[Continue to Controller]
    G --> E
    E --> I[Login Page]
```

#### Session Management:
- **Session Creation**: Upon successful login
- **Session Storage**: Server-side session store
- **Session Timeout**: 30-minute automatic expiration
- **Session Validation**: Per-request authentication check

### Authorization Levels

```mermaid
graph TD
    A[Request] --> B[Route-Level Auth]
    B --> C{Public Route?}
    C -->|Yes| D[Allow Access]
    C -->|No| E[Check Authentication]
    E --> F{User Logged In?}
    F -->|No| G[Redirect to Login]
    F -->|Yes| H[Resource-Level Auth]
    H --> I{User Owns Resource?}
    I -->|Yes| J[Allow Operation]
    I -->|No| K[Forbidden Error]
```

## Data Validation & Security Flow

### Input Processing Pipeline

```mermaid
graph TD
    A[User Input] --> B[Spring Validation]
    B --> C[InputValidator]
    C --> D[XssSanitizer]
    D --> E[Business Logic]
    E --> F[Database Operation]
    
    G[Invalid Input] --> H[Validation Error]
    I[XSS Detected] --> J[Sanitized Content]
    K[Business Rule Violation] --> L[Business Error]
```

#### Validation Layers:
1. **Client-Side**: JavaScript validation for UX
2. **Controller**: Spring validation annotations
3. **Custom Validation**: InputValidator class
4. **XSS Prevention**: XssSanitizer processing
5. **Business Rules**: Service layer validation

### Security Processing

```mermaid
graph TD
    A[Raw Input] --> B[Input Validation]
    B -->|Valid| C[XSS Sanitization]
    B -->|Invalid| D[Validation Error Response]
    C --> E[Content Processing]
    E --> F[Database Storage]
    F --> G[Safe Content Retrieval]
    G --> H[Template Rendering]
    H --> I[Safe HTML Output]
```

## Database Operation Flows

### Read Operations

```mermaid
graph TD
    A[Service Request] --> B[Mapper Method]
    B --> C[MyBatis SQL]
    C --> D[Database Query]
    D --> E[Result Set]
    E --> F[Entity Mapping]
    F --> G[Collection Assembly]
    G --> H[Return to Service]
```

### Write Operations

```mermaid
graph TD
    A[Service Call] --> B[Transaction Start]
    B --> C[Validation]
    C --> D[Primary Insert]
    D --> E[Related Operations]
    E --> F[Count Updates]
    F --> G[Transaction Commit]
    G --> H[Success Response]
    
    I[Validation Error] --> J[Transaction Rollback]
    K[Database Error] --> J
    J --> L[Error Response]
```

## Complex Operation Flows

### Timeline Generation

```mermaid
graph TD
    A[Home Page Request] --> B[HomeController]
    B --> C[Get Current User]
    C --> D[PostService.getTimelinePosts]
    D --> E[Get Following List]
    E --> F[Query Posts from Followed Users]
    F --> G[Include User's Own Posts]
    G --> H[Sort by Created Date]
    H --> I[Paginate Results]
    I --> J[Load User Data]
    J --> K[Check Like Status]
    K --> L[Assemble Timeline]
    L --> M[Return to Controller]
```

### Trending Hashtag Calculation

```mermaid
graph TD
    A[Sidebar API Request] --> B[SidebarController]
    B --> C[PostService.getTrendingHashtags]
    C --> D[Query Last 7 Days]
    D --> E[Group by Hashtag]
    E --> F[Count Posts per Tag]
    F --> G[Apply Recency Weight]
    G --> H[Sort by Trending Score]
    H --> I[Limit Results]
    I --> J[Return JSON]
```

#### Trending Algorithm:
```sql
trending_score = post_count * EXP(-hours_since_last_post / 24.0)
```

### Message Conversation Flow

```mermaid
graph TD
    A[Message Page] --> B[MessageController]
    B --> C[Get Conversation Users]
    C --> D[Load Message History]
    D --> E[Mark Messages as Read]
    E --> F[Update Unread Counts]
    F --> G[Render Conversation]
    G --> H[Real-time Updates]
```

## Error Handling Flows

### Exception Processing

```mermaid
graph TD
    A[Exception Thrown] --> B[GlobalExceptionHandler]
    B --> C{Exception Type?}
    C -->|404 NotFound| D[Custom 404 Page]
    C -->|Validation Error| E[Error with Form]
    C -->|Authentication| F[Redirect to Login]
    C -->|Business Logic| G[Business Error Page]
    C -->|Unexpected| H[Generic Error Page]
    
    I[Error Logging] --> J[Request Context]
    D --> I
    E --> I
    F --> I
    G --> I
    H --> I
```

### Error Recovery Strategies

```mermaid
graph TD
    A[Error Detected] --> B{Error Type?}
    B -->|Transient| C[Retry Operation]
    B -->|Validation| D[Return with Errors]
    B -->|Authentication| E[Redirect to Login]
    B -->|Authorization| F[Access Denied]
    B -->|System| G[Graceful Degradation]
    
    C --> H[Success or Max Retries]
    D --> I[User Correction]
    E --> J[Re-authentication]
    F --> K[Permission Request]
    G --> L[Minimal Functionality]
```

## Performance Considerations

### Database Connection Flow

```mermaid
graph TD
    A[Request] --> B[HikariCP Pool]
    B --> C{Connection Available?}
    C -->|Yes| D[Acquire Connection]
    C -->|No| E[Wait or Create New]
    D --> F[Execute Query]
    E --> F
    F --> G[Process Results]
    G --> H[Return Connection to Pool]
```

### Caching Strategy (Future)

```mermaid
graph TD
    A[Data Request] --> B{Cache Hit?}
    B -->|Yes| C[Return Cached Data]
    B -->|No| D[Query Database]
    D --> E[Store in Cache]
    E --> F[Return Fresh Data]
    
    G[Cache Invalidation] --> H[Remove/Update Cache]
    I[Time-based Expiry] --> H
```

## Real-time Feature Flow

### Dynamic Sidebar Updates

```mermaid
graph TD
    A[Page Load] --> B[Initial Data Load]
    B --> C[DOM Rendering]
    C --> D[Set Update Timer]
    D --> E[Periodic API Calls]
    E --> F[Data Comparison]
    F --> G{Changes Detected?}
    G -->|Yes| H[Update DOM]
    G -->|No| I[Next Cycle]
    H --> I
    I --> E
```

This comprehensive data flow documentation helps developers understand how information moves through the system and how different components interact to deliver the application's functionality.