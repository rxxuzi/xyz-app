# Project Structure

## Directory Layout

```
xyz/
├── doc/                                    # 📚 Documentation
│   ├── README.md                          # Documentation index
│   ├── 01-project-overview.md             # Project overview
│   ├── 02-architecture.md                 # Architecture guide
│   ├── 03-project-structure.md            # This file
│   ├── 04-data-flow.md                    # Data flow diagrams
│   ├── 05-components.md                   # Component details
│   ├── 06-database-design.md              # Database schema
│   └── 07-api-documentation.md            # API reference
├── assets/                                # 🗂️ Project assets
│   └── schema.sql                         # Database schema
├── src/
│   ├── main/
│   │   ├── java/com/rxxuzi/xyz/          # ☕ Java source code
│   │   │   ├── XyzApplication.java        # Main application class
│   │   │   ├── config/                    # ⚙️ Configuration classes
│   │   │   ├── controller/                # 🎮 Web controllers
│   │   │   ├── entity/                    # 📊 Data entities
│   │   │   ├── mapper/                    # 🗃️ Data access layer
│   │   │   ├── service/                   # 🔧 Business logic
│   │   │   └── security/                  # 🔒 Security components
│   │   └── resources/                     # 📁 Resources
│   │       ├── application.properties     # App configuration
│   │       ├── mapper/                    # 🗺️ MyBatis XML mappers
│   │       ├── static/                    # 🌐 Static web assets
│   │       └── templates/                 # 📄 Thymeleaf templates
│   └── test/                              # 🧪 Test code
├── target/                                # 🏗️ Build output (gitignored)
├── README.md                             # 📖 Project README
├── pom.xml                               # 📦 Maven configuration
├── mvnw                                  # 🔨 Maven wrapper (Unix)
└── mvnw.cmd                              # 🔨 Maven wrapper (Windows)
```

## Source Code Organization

### Java Package Structure

```
com.rxxuzi.xyz/
├── XyzApplication.java                    # 🚀 Spring Boot main class
├── config/                                # ⚙️ Configuration & Setup
│   ├── GlobalExceptionHandler.java        # Global error handling
│   └── WebConfig.java                     # MVC configuration
├── controller/                            # 🎮 HTTP Request Handlers
│   ├── HomeController.java                # Timeline & dashboard
│   ├── PostController.java                # Post management
│   ├── UserController.java                # User & auth management
│   ├── SearchController.java              # Search functionality
│   ├── MessageController.java             # Direct messaging
│   └── SidebarController.java             # Dynamic sidebar API
├── entity/                                # 📊 Data Models
│   ├── User.java                          # User entity
│   ├── Post.java                          # Post entity
│   ├── Message.java                       # Message entity
│   └── Hashtag.java                       # Hashtag entity
├── mapper/                                # 🗃️ Data Access Interfaces
│   ├── UserMapper.java                    # User data operations
│   ├── PostMapper.java                    # Post data operations
│   └── MessageMapper.java                 # Message data operations
├── service/                               # 🔧 Business Logic
│   ├── UserService.java                   # User business logic
│   ├── PostService.java                   # Post business logic
│   └── MessageService.java                # Message business logic
└── security/                              # 🔒 Security Components
    ├── InputValidator.java                # Input validation
    └── XssSanitizer.java                  # XSS prevention
```

## Detailed Component Breakdown

### 🚀 Application Entry Point

#### `XyzApplication.java`
```java
@SpringBootApplication
public class XyzApplication {
    public static void main(String[] args) {
        SpringApplication.run(XyzApplication.class, args);
    }
}
```
- **Purpose**: Main entry point for Spring Boot application
- **Responsibilities**: Application bootstrapping, component scanning

### ⚙️ Configuration Layer

#### `WebConfig.java`
```java
@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {
    // MVC configuration
    // Interceptor registration
    // Static resource handling
}
```
- **Purpose**: Spring MVC configuration
- **Key Features**:
  - Authentication interceptor registration
  - Static resource path mapping
  - CORS configuration
  - View resolver configuration

#### `GlobalExceptionHandler.java`
```java
@ControllerAdvice
public class GlobalExceptionHandler {
    // Global error handling
    // Custom error pages
    // API error responses
}
```
- **Purpose**: Centralized exception handling
- **Key Features**:
  - 404 error handling with custom pages
  - Global exception catching
  - Request logging and filtering
  - User-friendly error messages

### 🎮 Controller Layer

#### `HomeController.java`
- **Routes**: `/`, `/home`, `/post/{id}/quote`
- **Purpose**: Main timeline and dashboard
- **Key Features**:
  - Timeline post aggregation
  - Quote post functionality
  - User session management

#### `UserController.java`
- **Routes**: `/login`, `/register`, `/u/{username}/*`, `/follow/*`
- **Purpose**: User management and authentication
- **Key Features**:
  - User authentication (login/logout)
  - User registration with validation
  - Profile management
  - Follow/unfollow functionality

#### `PostController.java`
- **Routes**: `/post`, `/post/{id}/*`
- **Purpose**: Content creation and management
- **Key Features**:
  - Post creation with hashtag extraction
  - Reply and like functionality
  - Post deletion (soft delete)
  - Individual post viewing

#### `MessageController.java`
- **Routes**: `/messages/*`
- **Purpose**: Direct messaging system
- **Key Features**:
  - Message inbox management
  - Conversation threading
  - Message sending and receiving
  - Unread message tracking

#### `SidebarController.java`
- **Routes**: `/api/sidebar/*`
- **Purpose**: Dynamic sidebar data (REST API)
- **Key Features**:
  - Trending hashtag calculation
  - User recommendations
  - JSON API responses

### 📊 Entity Layer

#### `User.java`
```java
public class User {
    private Long id;
    private String username;
    private String passwordHash;
    private Integer postsCount;
    private Integer followersCount;
    // ... other fields
}
```
- **Purpose**: User data representation
- **Key Fields**: ID, username, password hash, counts
- **Features**: Profile data, social counts

#### `Post.java`
```java
public class Post {
    private Long id;
    private Long userId;
    private String content;
    private Long parentPostId;    // For replies
    private Long quotedPostId;    // For quotes
    // ... interaction counts
}
```
- **Purpose**: Post data representation
- **Key Fields**: Content, user reference, parent/quote relationships
- **Features**: Interaction counts, timestamps, soft deletion

### 🗃️ Mapper Layer (Data Access)

#### MyBatis Interface Pattern
```java
@Mapper
public interface UserMapper {
    User selectUserById(Long id);
    void insertUser(User user);
    List<User> selectRandomUsers(@Param("currentUserId") Long currentUserId, 
                                @Param("limit") int limit);
}
```

#### XML Mapper Configuration
```xml
<mapper namespace="com.rxxuzi.xyz.mapper.UserMapper">
    <resultMap id="userResultMap" type="com.rxxuzi.xyz.entity.User">
        <id property="id" column="id"/>
        <result property="username" column="username"/>
        <!-- ... field mappings -->
    </resultMap>
    
    <select id="selectUserById" resultMap="userResultMap">
        SELECT * FROM users WHERE id = #{id}
    </select>
</mapper>
```

### 🔧 Service Layer

#### Service Pattern Implementation
```java
@Service
@Transactional
public class UserService {
    @Autowired
    private UserMapper userMapper;
    
    public User authenticate(String username, String password) {
        // Business logic for authentication
    }
}
```
- **Purpose**: Business logic encapsulation
- **Key Features**:
  - Transaction management
  - Data validation
  - Business rule enforcement
  - Service composition

### 🔒 Security Layer

#### `InputValidator.java`
```java
public class InputValidator {
    public static boolean isValidUsername(String username) {
        // 3-30 characters, alphanumeric + underscore
    }
    
    public static boolean isValidPassword(String password) {
        // Minimum 6 characters
    }
}
```

#### `XssSanitizer.java`
```java
public class XssSanitizer {
    public static String sanitize(String content) {
        // Remove dangerous HTML/JS content
    }
}
```

## Resource Organization

### 📁 Static Resources (`src/main/resources/static/`)

```
static/
├── css/                                   # 🎨 Stylesheets
│   ├── style.css                         # CSS variables & utilities
│   ├── design.css                        # Layout & component styles
│   ├── common.css                        # Common UI components
│   ├── post.css                          # Post-specific styles
│   ├── auth.css                          # Authentication pages
│   ├── msg.css                           # Messaging interface
│   └── follow-list.css                   # Follow lists
├── js/                                   # 📜 JavaScript
│   ├── app.js                           # Main application logic
│   ├── ui.js                            # UI interactions & modals
│   └── rp.js                            # Real-time features
└── favicon.ico                          # 🌐 Site icon
```

### 📄 Templates (`src/main/resources/templates/`)

```
templates/
├── layout.html                           # 🏗️ Main layout template
├── home.html                            # 🏠 Timeline page
├── profile.html                         # 👤 User profile
├── post-detail.html                     # 📝 Individual post view
├── search.html                          # 🔍 Search results
├── messages.html                        # 💬 Message inbox
├── conversation.html                    # 💬 Message conversation
├── follow-list.html                     # 👥 Followers/following
├── login.html                           # 🔐 Login form
├── register.html                        # 📝 Registration form
├── error.html                           # ❌ General error page
├── 404.html                             # 🚫 Not found page
└── not-found.html                       # 🔍 Resource not found
```

### 🗺️ MyBatis Mappers (`src/main/resources/mapper/`)

```
mapper/
├── UserMapper.xml                        # User data operations
├── PostMapper.xml                        # Post & hashtag operations
└── MessageMapper.xml                     # Message operations
```

## File Naming Conventions

### Java Classes
- **Controllers**: `[Entity]Controller.java` (e.g., `UserController.java`)
- **Services**: `[Entity]Service.java` (e.g., `PostService.java`)
- **Mappers**: `[Entity]Mapper.java` (e.g., `MessageMapper.java`)
- **Entities**: `[EntityName].java` (e.g., `User.java`, `Post.java`)

### Templates
- **Pages**: `[page-name].html` (kebab-case)
- **Fragments**: `[fragment-name].html`
- **Layouts**: `layout.html`, `[layout-type].html`

### Static Assets
- **CSS**: `[component-name].css` (kebab-case)
- **JavaScript**: `[module-name].js` (kebab-case)
- **Images**: `[descriptive-name].[ext]`

### Configuration Files
- **Properties**: `application[-profile].properties`
- **XML Mappers**: `[MapperInterface].xml`

## Build Structure

### Maven Configuration (`pom.xml`)
```xml
<groupId>com.rxxuzi</groupId>
<artifactId>xyz</artifactId>
<version>0.0.1-SNAPSHOT</version>
<packaging>jar</packaging>
```

### Build Output (`target/`)
```
target/
├── classes/                              # Compiled Java classes
├── generated-sources/                    # Generated code
├── maven-archiver/                       # Maven metadata
├── maven-status/                         # Build status
└── xyz-0.0.1-SNAPSHOT.jar              # Executable JAR
```

## Development Workflow

### 1. **Adding New Features**
```
1. Create Entity (if needed)              → entity/
2. Create Mapper Interface               → mapper/
3. Create MyBatis XML                    → resources/mapper/
4. Create Service                        → service/
5. Create Controller                     → controller/
6. Create Templates                      → resources/templates/
7. Add CSS/JS (if needed)               → resources/static/
```

### 2. **File Dependencies**
```
Controller → Service → Mapper → XML → Database
    ↓          ↓         ↓       ↓
Template ← Model ← Business ← Data ← Schema
```

### 3. **Testing Structure**
```
src/test/java/com/rxxuzi/xyz/
├── XyzApplicationTests.java              # Context loading test
├── controller/                           # Controller tests (future)
├── service/                             # Service tests (future)
└── mapper/                              # Mapper tests (future)
```

This structure provides clear separation of concerns, follows Spring Boot conventions, and enables scalable development practices.