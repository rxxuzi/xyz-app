# API Documentation

## Overview

XYZ provides both traditional web endpoints (HTML responses) and RESTful API endpoints (JSON responses). This document covers all available endpoints, request/response formats, and authentication requirements.

## Authentication

### Session-Based Authentication
All protected endpoints require a valid user session. Sessions are created upon login and expire after 30 minutes of inactivity.

**Session Cookie**: `XYZSESSIONID`
**Timeout**: 30 minutes
**Storage**: Server-side session store

### Public Endpoints
- `GET /login` - Login page
- `POST /login` - User authentication
- `GET /register` - Registration page
- `POST /register` - User registration
- Static resources (`/css/*`, `/js/*`, `/favicon.ico`)

---

## Web Endpoints (HTML Responses)

### Authentication

#### `POST /login`
**Purpose**: Authenticate user and create session

**Request**:
```http
POST /login HTTP/1.1
Content-Type: application/x-www-form-urlencoded

username=johndoe&password=secretpassword
```

**Response** (Success):
```http
HTTP/1.1 302 Found
Location: /home
Set-Cookie: XYZSESSIONID=abc123; Path=/; HttpOnly
```

**Response** (Failure):
```http
HTTP/1.1 200 OK
Content-Type: text/html

<!-- Login page with error message -->
```

#### `POST /register`
**Purpose**: Create new user account

**Request**:
```http
POST /register HTTP/1.1
Content-Type: application/x-www-form-urlencoded

username=newuser&password=newpassword
```

**Validation Rules**:
- Username: 3-30 characters, alphanumeric + underscore
- Password: minimum 6 characters

#### `GET /logout`
**Purpose**: Terminate user session

**Response**:
```http
HTTP/1.1 302 Found
Location: /login
Set-Cookie: XYZSESSIONID=; Path=/; Expires=Thu, 01 Jan 1970 00:00:00 GMT
```

### Content Management

#### `POST /post`
**Purpose**: Create new post

**Request**:
```http
POST /post HTTP/1.1
Content-Type: application/x-www-form-urlencoded
Cookie: XYZSESSIONID=abc123

content=Hello%20world%20%23technology&parentPostId=123
```

**Parameters**:
- `content` (required): Post content, max 280 characters
- `parentPostId` (optional): Parent post ID for replies

**Features**:
- Automatic hashtag extraction
- XSS sanitization
- Reply threading support

#### `GET /post/{id}`
**Purpose**: View individual post with replies

**Response**: HTML page with post details, replies, and interaction options

#### `DELETE /post/{id}`
**Purpose**: Soft delete post (owner only)

**Response**:
```json
{
    "success": true,
    "message": "Post deleted successfully"
}
```

### Social Interactions

#### `POST /post/{id}/like`
**Purpose**: Toggle like status on post

**Response**:
```json
{
    "success": true,
    "liked": true,
    "likeCount": 15
}
```

#### `POST /post/{id}/reply`
**Purpose**: Reply to post

**Request**:
```http
POST /post/123/reply HTTP/1.1
Content-Type: application/x-www-form-urlencoded

content=Great%20post!
```

#### `POST /post/{id}/quote`
**Purpose**: Quote post (retweet with comment)

**Request**:
```http
POST /post/123/quote HTTP/1.1
Content-Type: application/x-www-form-urlencoded

content=Adding%20my%20thoughts...
```

**Response**:
```json
{
    "success": true,
    "redirect": "/home"
}
```

### User Management

#### `GET /u/{username}`
**Purpose**: View user profile

**Response**: HTML page with user info, posts, and follow button

#### `POST /follow/{username}`
**Purpose**: Toggle follow relationship

**Response**:
```json
{
    "success": true,
    "following": true,
    "followersCount": 150
}
```

#### `GET /u/{username}/followers`
**Purpose**: View user's followers list

**Parameters**:
- `page` (optional): Page number for pagination (default: 0)

#### `GET /u/{username}/following`
**Purpose**: View user's following list

**Parameters**:
- `page` (optional): Page number for pagination (default: 0)

### Content Discovery

#### `GET /search`
**Purpose**: Search posts, users, and hashtags

**Parameters**:
- `q` (optional): Search query
- `type` (optional): Search type (`posts`, `users`, `hashtags`)
- `page` (optional): Page number (default: 0)

**Response**: HTML page with search results

#### `GET /search/suggestions`
**Purpose**: Real-time search suggestions

**Parameters**:
- `q` (required): Search query prefix

**Response**:
```json
{
    "users": [
        {
            "username": "johndoe",
            "followersCount": 150
        }
    ],
    "hashtags": [
        {
            "tag": "technology",
            "postCount": 500
        }
    ]
}
```

### Messaging

#### `GET /messages`
**Purpose**: View message inbox

**Response**: HTML page with conversation list

#### `GET /messages/conversation/{username}`
**Purpose**: View conversation with specific user

**Response**: HTML page with message history and send form

#### `POST /messages/conversation/{username}/send`
**Purpose**: Send direct message

**Request**:
```http
POST /messages/conversation/johndoe/send HTTP/1.1
Content-Type: application/x-www-form-urlencoded

content=Hello%20there!
```

---

## REST API Endpoints (JSON Responses)

### Sidebar API

#### `GET /api/sidebar/data`
**Purpose**: Get combined sidebar data (trending + recommendations)

**Authentication**: Optional (affects user recommendations)

**Response**:
```json
{
    "trendingHashtags": [
        {
            "id": 1,
            "tag": "technology",
            "postCount": 150,
            "createdAt": "2025-01-15T10:30:00Z"
        },
        {
            "id": 2,
            "tag": "programming",
            "postCount": 89,
            "createdAt": "2025-01-14T15:45:00Z"
        }
    ],
    "userRecommendations": [
        {
            "id": 123,
            "username": "techguru",
            "postsCount": 245,
            "followersCount": 1850,
            "followingCount": 340,
            "isFollowing": false
        },
        {
            "id": 456,
            "username": "developer",
            "postsCount": 189,
            "followersCount": 920,
            "followingCount": 180,
            "isFollowing": false
        }
    ],
    "isLoggedIn": true
}
```

#### `GET /api/sidebar/trending`
**Purpose**: Get trending hashtags only

**Response**:
```json
{
    "trendingHashtags": [
        {
            "id": 1,
            "tag": "technology",
            "postCount": 150
        }
        // ... up to 10 hashtags
    ]
}
```

#### `GET /api/sidebar/recommendations`
**Purpose**: Get user recommendations only

**Authentication**: Required

**Response**:
```json
{
    "userRecommendations": [
        {
            "id": 123,
            "username": "techguru",
            "postsCount": 245,
            "followersCount": 1850,
            "isFollowing": false
        }
        // ... up to 3 users
    ],
    "isLoggedIn": true
}
```

---

## Response Formats

### Success Response
```json
{
    "success": true,
    "message": "Operation completed successfully",
    "data": {
        // Response data
    },
    "timestamp": "2025-01-16T10:30:00Z"
}
```

### Error Response
```json
{
    "success": false,
    "message": "Error description",
    "error": "ERROR_CODE",
    "timestamp": "2025-01-16T10:30:00Z"
}
```

### Validation Error Response
```json
{
    "success": false,
    "message": "Validation failed",
    "errors": [
        {
            "field": "username",
            "message": "Username must be 3-30 characters"
        },
        {
            "field": "password",
            "message": "Password must be at least 6 characters"
        }
    ],
    "timestamp": "2025-01-16T10:30:00Z"
}
```

---

## Data Models

### User Object
```json
{
    "id": 123,
    "username": "johndoe",
    "status": "active",
    "postsCount": 50,
    "followersCount": 200,
    "followingCount": 150,
    "createdAt": "2025-01-01T00:00:00Z",
    "isFollowing": false  // Only when applicable
}
```

### Post Object
```json
{
    "id": 456,
    "userId": 123,
    "content": "Hello world! #technology",
    "visibility": "public",
    "isEdited": false,
    "parentPostId": null,
    "quotedPostId": null,
    "replyCount": 5,
    "likeCount": 25,
    "quoteCount": 3,
    "createdAt": "2025-01-16T10:30:00Z",
    "updatedAt": "2025-01-16T10:30:00Z",
    "deletedAt": null,
    "isLikedByCurrentUser": true,
    "user": {
        "id": 123,
        "username": "johndoe",
        "status": "active"
    }
}
```

### Message Object
```json
{
    "id": 789,
    "senderId": 123,
    "receiverId": 456,
    "content": "Hello there!",
    "isRead": false,
    "createdAt": "2025-01-16T10:30:00Z"
}
```

### Hashtag Object
```json
{
    "id": 1,
    "tag": "technology",
    "createdAt": "2025-01-15T10:30:00Z",
    "postCount": 150  // Only in trending context
}
```

---

## Error Codes

### Authentication Errors
- `AUTH_REQUIRED`: Authentication required for this endpoint
- `INVALID_CREDENTIALS`: Username or password incorrect
- `SESSION_EXPIRED`: User session has expired
- `ACCOUNT_INACTIVE`: User account is inactive

### Validation Errors
- `INVALID_INPUT`: General input validation failure
- `USERNAME_TAKEN`: Username already exists
- `INVALID_USERNAME`: Username format invalid
- `INVALID_PASSWORD`: Password too short
- `CONTENT_TOO_LONG`: Post content exceeds 280 characters
- `CONTENT_EMPTY`: Post content cannot be empty

### Authorization Errors
- `ACCESS_DENIED`: User lacks permission for this operation
- `RESOURCE_NOT_FOUND`: Requested resource does not exist
- `CANNOT_FOLLOW_SELF`: User cannot follow themselves
- `ALREADY_FOLLOWING`: User already following target
- `NOT_FOLLOWING`: User not following target

### Business Logic Errors
- `POST_NOT_FOUND`: Post does not exist or is deleted
- `USER_NOT_FOUND`: User does not exist
- `CONVERSATION_NOT_FOUND`: Message conversation does not exist
- `CANNOT_DELETE_POST`: User cannot delete this post

---

## Rate Limiting (Future Implementation)

### Planned Limits
- **Post Creation**: 100 posts per hour
- **Follow Actions**: 50 follows per hour
- **API Requests**: 1000 requests per hour
- **Search Requests**: 100 searches per hour

### Rate Limit Headers
```http
X-RateLimit-Limit: 100
X-RateLimit-Remaining: 95
X-RateLimit-Reset: 1642521600
```

---

## API Versioning (Future)

### Version Strategy
- **URL Versioning**: `/api/v1/`, `/api/v2/`
- **Header Versioning**: `Accept: application/vnd.xyz.v1+json`
- **Backward Compatibility**: Minimum 6 months support

### Current Version
All endpoints are currently unversioned and considered v1.0.

---

## Development & Testing

### API Testing Tools
- **Postman Collection**: Available in `/doc/postman/`
- **cURL Examples**: Included in endpoint descriptions
- **Swagger/OpenAPI**: Planned for future releases

### Local Development
```bash
# Start development server
./mvnw spring-boot:run

# Base URL
http://localhost:8080

# API Base URL  
http://localhost:8080/api
```

This API documentation provides comprehensive coverage of all available endpoints and serves as a reference for both frontend development and third-party integrations.