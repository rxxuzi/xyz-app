# Component Documentation

## Controllers

### 🏠 HomeController

**Purpose**: Main timeline and dashboard functionality

**Class Declaration**:
```java
@Controller
public class HomeController {
    @Autowired private PostService postService;
}
```

#### Key Endpoints

##### `GET /` - Root Redirect
```java
@GetMapping("/")
public String index(HttpSession session) {
    User user = (User) session.getAttribute("user");
    return user != null ? "redirect:/home" : "redirect:/login";
}
```
- **Purpose**: Route users based on authentication status
- **Logic**: Authenticated users → `/home`, guests → `/login`

##### `GET /home` - Timeline Display
```java
@GetMapping("/home")
public String home(Model model, HttpSession session, 
                   @RequestParam(defaultValue = "0") int page) {
    // Load timeline posts for authenticated user
}
```
- **Features**:
  - Timeline aggregation from followed users
  - Pagination support (20 posts per page)
  - User's own posts inclusion
  - Like status for current user

##### `POST /post/{id}/quote` - Quote Post Creation
```java
@PostMapping("/post/{id}/quote")
@ResponseBody
public String createQuotePost(@PathVariable Long id,
                             @RequestParam String content,
                             HttpSession session) {
    // Create quote post (retweet with comment)
}
```
- **Features**:
  - JSON response for AJAX calls
  - Quote relationship tracking
  - Content validation (280 characters)
  - Authentication requirement

---

### 👤 UserController

**Purpose**: User authentication, profiles, and social relationships

#### Authentication Endpoints

##### `GET /login` - Login Page
```java
@GetMapping("/login")
public String loginPage(HttpSession session) {
    // Redirect if already logged in
}
```

##### `POST /login` - User Authentication
```java
@PostMapping("/login")
public String login(@RequestParam String username,
                   @RequestParam String password,
                   HttpSession session, Model model) {
    // Authenticate user and create session
}
```
- **Process**:
  1. Input validation
  2. Password hash verification
  3. Session creation
  4. Redirect to home or show errors

##### `GET /register` - Registration Page
```java
@GetMapping("/register")
public String registerPage() {
    return "register";
}
```

##### `POST /register` - User Registration
```java
@PostMapping("/register")
public String register(@RequestParam String username,
                      @RequestParam String password,
                      Model model) {
    // Create new user account
}
```
- **Validation**:
  - Username: 3-30 chars, alphanumeric + underscore
  - Password: minimum 6 characters
  - Uniqueness check

#### Profile Management

##### `GET /u/{username}` - User Profile
```java
@GetMapping("/u/{username}")
public String userProfile(@PathVariable String username, 
                         Model model, HttpSession session) {
    // Display user profile with posts
}
```
- **Features**:
  - User information display
  - Post history (paginated)
  - Follow/unfollow button
  - Social statistics

##### `GET /u/{username}/followers` - Followers List
```java
@GetMapping("/u/{username}/followers")
public String followers(@PathVariable String username,
                       @RequestParam(defaultValue = "0") int page,
                       Model model, HttpSession session) {
    // Display followers with pagination
}
```

##### `GET /u/{username}/following` - Following List
```java
@GetMapping("/u/{username}/following")
public String following(@PathVariable String username,
                       @RequestParam(defaultValue = "0") int page,
                       Model model, HttpSession session) {
    // Display following with pagination
}
```

#### Social Actions

##### `POST /follow/{username}` - Toggle Follow
```java
@PostMapping("/follow/{username}")
@ResponseBody
public Map<String, Object> toggleFollow(@PathVariable String username,
                                       HttpSession session) {
    // Follow or unfollow user
}
```
- **Response**: JSON with success status and new relationship state

---

### 📝 PostController

**Purpose**: Content creation, interaction, and management

#### Content Management

##### `POST /post` - Create Post
```java
@PostMapping("/post")
public String createPost(@RequestParam String content,
                        @RequestParam(required = false) Long parentPostId,
                        HttpSession session, Model model) {
    // Create new post with hashtag extraction
}
```
- **Features**:
  - Content validation (280 characters)
  - XSS sanitization
  - Hashtag extraction and storage
  - Reply threading support

##### `GET /post/{id}` - Individual Post View
```java
@GetMapping("/post/{id}")
public String viewPost(@PathVariable Long id, 
                      Model model, HttpSession session) {
    // Display post with replies
}
```
- **Features**:
  - Post details with user information
  - Reply thread display
  - Interaction buttons (like, reply, quote)
  - Quote post display if applicable

##### `DELETE /post/{id}` - Delete Post
```java
@DeleteMapping("/post/{id}")
@ResponseBody
public Map<String, Object> deletePost(@PathVariable Long id,
                                     HttpSession session) {
    // Soft delete post (ownership check)
}
```
- **Security**: Only post owner can delete
- **Method**: Soft deletion with `deleted_at` timestamp

#### Interactions

##### `POST /post/{id}/reply` - Reply to Post
```java
@PostMapping("/post/{id}/reply")
public String replyToPost(@PathVariable Long id,
                         @RequestParam String content,
                         HttpSession session) {
    // Create reply post
}
```

##### `POST /post/{id}/like` - Toggle Like
```java
@PostMapping("/post/{id}/like")
@ResponseBody
public Map<String, Object> toggleLike(@PathVariable Long id,
                                     HttpSession session) {
    // Like or unlike post
}
```
- **Response**: JSON with new like count and user's like status

---

### 🔍 SearchController

**Purpose**: Content discovery and search functionality

##### `GET /search` - Search Interface
```java
@GetMapping("/search")
public String search(@RequestParam(required = false) String q,
                    @RequestParam(defaultValue = "posts") String type,
                    @RequestParam(defaultValue = "0") int page,
                    Model model, HttpSession session) {
    // Perform search across posts, users, hashtags
}
```
- **Search Types**:
  - Posts: Content-based search
  - Users: Username and profile search
  - Hashtags: Tag-based post discovery

##### `GET /search/suggestions` - Real-time Suggestions
```java
@GetMapping("/search/suggestions")
@ResponseBody
public Map<String, Object> getSearchSuggestions(@RequestParam String q,
                                               HttpSession session) {
    // Return JSON suggestions for autocomplete
}
```

---

### 💬 MessageController

**Purpose**: Direct messaging system

##### `GET /messages` - Message Inbox
```java
@GetMapping("/messages")
public String messages(Model model, HttpSession session) {
    // Display conversation list
}
```

##### `GET /messages/conversation/{username}` - Conversation View
```java
@GetMapping("/messages/conversation/{username}")
public String conversation(@PathVariable String username,
                          Model model, HttpSession session) {
    // Display message conversation
}
```

##### `POST /messages/conversation/{username}/send` - Send Message
```java
@PostMapping("/messages/conversation/{username}/send")
public String sendMessage(@PathVariable String username,
                         @RequestParam String content,
                         HttpSession session) {
    // Send direct message
}
```

---

### 📊 SidebarController (API)

**Purpose**: Dynamic sidebar data provision

##### `GET /api/sidebar/data` - Combined Sidebar Data
```java
@GetMapping("/data")
public Map<String, Object> getSidebarData(HttpSession session) {
    // Return trending hashtags + user recommendations
}
```

##### `GET /api/sidebar/trending` - Trending Hashtags
```java
@GetMapping("/trending")
public Map<String, Object> getTrendingHashtags() {
    // Return top 10 trending hashtags
}
```

##### `GET /api/sidebar/recommendations` - User Recommendations
```java
@GetMapping("/recommendations")
public Map<String, Object> getUserRecommendations(HttpSession session) {
    // Return 3 recommended users
}
```

---

## Services

### 👤 UserService

**Purpose**: User management and authentication business logic

#### Core Methods

##### `authenticate(String username, String password)`
```java
public User authenticate(String username, String password) {
    // 1. Validate input format
    // 2. Retrieve user by username
    // 3. Verify password hash
    // 4. Return user or null
}
```

##### `createUser(User user)`
```java
@Transactional
public void createUser(User user) {
    // 1. Validate user data
    // 2. Hash password
    // 3. Check username uniqueness
    // 4. Insert user record
}
```

##### `toggleFollow(Long followerId, Long followeeId)`
```java
@Transactional
public boolean toggleFollow(Long followerId, Long followeeId) {
    // 1. Check current relationship
    // 2. Create or remove follow relationship
    // 3. Update follower/following counts
    // 4. Return new relationship state
}
```

---

### 📝 PostService

**Purpose**: Content management and interaction business logic

#### Core Methods

##### `createPost(Long userId, String content, Long parentId, Long quotedId)`
```java
@Transactional
public void createPost(Long userId, String content, Long parentId, Long quotedId) {
    // 1. Validate content
    // 2. Sanitize input
    // 3. Extract hashtags
    // 4. Insert post record
    // 5. Process hashtag relationships
    // 6. Update user post count
}
```

##### `getTimelinePosts(Long userId, int page, int size)`
```java
public List<Post> getTimelinePosts(Long userId, int page, int size) {
    // 1. Get user's following list
    // 2. Query posts from followed users + own posts
    // 3. Sort by creation date
    // 4. Apply pagination
    // 5. Load user data and like status
}
```

##### `getTrendingHashtags(int limit)`
```java
public List<Hashtag> getTrendingHashtags(int limit) {
    // 1. Query hashtags from last 7 days
    // 2. Count posts per hashtag
    // 3. Apply recency weighting
    // 4. Sort by trending score
    // 5. Return top results
}
```

---

### 💬 MessageService

**Purpose**: Direct messaging business logic

#### Core Methods

##### `sendMessage(Long senderId, Long receiverId, String content)`
```java
@Transactional
public void sendMessage(Long senderId, Long receiverId, String content) {
    // 1. Validate participants
    // 2. Sanitize message content
    // 3. Insert message record
    // 4. Update conversation metadata
}
```

---

## Mappers (Data Access Layer)

### 👤 UserMapper

**Interface Methods**:
```java
@Mapper
public interface UserMapper {
    User selectUserById(Long id);
    User selectUserByUsername(String username);
    void insertUser(User user);
    void updateUser(User user);
    List<User> selectRandomUsers(@Param("currentUserId") Long currentUserId, 
                                @Param("limit") int limit);
    boolean selectIsFollowing(@Param("followerId") Long followerId, 
                             @Param("followeeId") Long followeeId);
    void insertFollowRelationship(@Param("followerId") Long followerId, 
                                 @Param("followeeId") Long followeeId);
    void deleteFollowRelationship(@Param("followerId") Long followerId, 
                                 @Param("followeeId") Long followeeId);
}
```

### 📝 PostMapper

**Interface Methods**:
```java
@Mapper
public interface PostMapper {
    void insertPost(Post post);
    Post selectPostById(@Param("id") Long id, @Param("currentUserId") Long currentUserId);
    List<Post> selectTimelinePosts(@Param("userId") Long userId, 
                                  @Param("limit") int limit, 
                                  @Param("offset") int offset);
    List<Post> selectPostsByUser(@Param("userId") Long userId, 
                                @Param("currentUserId") Long currentUserId,
                                @Param("limit") int limit, 
                                @Param("offset") int offset);
    void updatePost(Post post);
    void deletePost(@Param("id") Long id, @Param("userId") Long userId);
    
    // Hashtag operations
    Long selectHashtagIdByTag(String tag);
    void insertHashtag(@Param("tag") String tag);
    void insertPostHashtag(@Param("postId") Long postId, @Param("hashtagId") Long hashtagId);
    List<Hashtag> selectTrendingHashtags(@Param("limit") int limit);
    
    // Interaction operations
    boolean selectIsLiked(@Param("postId") Long postId, @Param("userId") Long userId);
    void insertLike(@Param("postId") Long postId, @Param("userId") Long userId);
    void deleteLike(@Param("postId") Long postId, @Param("userId") Long userId);
    void updateLikeCount(@Param("postId") Long postId);
}
```

---

## Security Components

### 🔒 InputValidator

**Purpose**: Input validation and security

**Key Methods**:
```java
public class InputValidator {
    public static boolean isValidUsername(String username) {
        return username != null && 
               username.matches("^[a-zA-Z0-9_]{3,30}$");
    }
    
    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }
    
    public static boolean isValidPostContent(String content) {
        return content != null && 
               content.trim().length() > 0 && 
               content.length() <= 280;
    }
}
```

### 🛡️ XssSanitizer

**Purpose**: XSS attack prevention

**Key Methods**:
```java
public class XssSanitizer {
    public static String sanitize(String content) {
        if (content == null) return null;
        
        return content
            .replaceAll("<script[^>]*>.*?</script>", "")
            .replaceAll("<[^>]+>", "")
            .replaceAll("javascript:", "")
            .replaceAll("on\\w+\\s*=", "");
    }
}
```

---

## Configuration Components

### ⚙️ WebConfig

**Purpose**: Spring MVC configuration

**Key Features**:
```java
@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthenticationInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/login", "/register", "/css/**", "/js/**");
    }
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/css/**")
                .addResourceLocations("classpath:/static/css/");
        registry.addResourceHandler("/js/**")
                .addResourceLocations("classpath:/static/js/");
    }
}
```

### ❌ GlobalExceptionHandler

**Purpose**: Centralized error handling

**Key Methods**:
```java
@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(NoHandlerFoundException.class)
    public String handleNotFound(HttpServletRequest request, Model model) {
        logger.warn("404 error for URL: {}", request.getRequestURL());
        model.addAttribute("message", "Page not found");
        return "404";
    }
    
    @ExceptionHandler(Exception.class)
    public String handleGlobalException(Exception ex, HttpServletRequest request, Model model) {
        // Filter browser auto-requests
        // Log error with context
        // Return appropriate error page
    }
}
```

This component documentation provides detailed insight into each layer of the application, helping developers understand the responsibilities and interactions of different components.