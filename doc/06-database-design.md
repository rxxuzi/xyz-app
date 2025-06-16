# Database Design

## Schema Overview

XYZ uses a MySQL database with a normalized schema designed for social media functionality. The design supports users, posts, social relationships, direct messaging, and hashtag trending.

## Entity Relationship Diagram

```
┌─────────────┐       ┌─────────────┐       ┌─────────────┐
│    users    │       │    posts    │       │  hashtags   │
├─────────────┤       ├─────────────┤       ├─────────────┤
│ id (PK)     │   ┌───│ id (PK)     │       │ id (PK)     │
│ username    │   │   │ user_id (FK)│       │ tag         │
│ password_hash│  │   │ content     │       │ created_at  │
│ email       │   │   │ visibility  │       └─────────────┘
│ status      │   │   │ parent_post_id       │
│ posts_count │   │   │ quoted_post_id   ┌───────────────┐
│ followers_count  │   │ reply_count  │   │ post_hashtags │
│ following_count  │   │ like_count   │   ├───────────────┤
│ created_at  │   │   │ quote_count  │   │ post_id (FK)  │
│ updated_at  │   │   │ created_at   │   │ hashtag_id(FK)│
└─────────────┘   │   │ updated_at   │   └───────────────┘
       │          │   │ deleted_at   │          │
       │          └───│              │          │
       │              └─────────────┘          │
       │                     │                 │
       ▼                     ▼                 ▼
┌─────────────┐       ┌─────────────┐   ┌─────────────┐
│  followers  │       │    likes    │   │  messages   │
├─────────────┤       ├─────────────┤   ├─────────────┤
│follower_id  │       │ user_id(FK) │   │ id (PK)     │
│followee_id  │       │ post_id(FK) │   │sender_id(FK)│
│ created_at  │       │ created_at  │   │receiver_id  │
└─────────────┘       └─────────────┘   │ content     │
                                        │ is_read     │
                                        │ created_at  │
                                        └─────────────┘
```

## Table Definitions

### Core Tables

#### `users` - User Accounts
```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(30) UNIQUE NOT NULL,
    password_hash VARCHAR(60) NOT NULL,
    email VARCHAR(255),
    status ENUM('active', 'inactive') DEFAULT 'active',
    posts_count INT DEFAULT 0,
    followers_count INT DEFAULT 0,
    following_count INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_username (username),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
);
```

**Key Features**:
- **Username Constraint**: 30 characters max, unique
- **Password Security**: 60-character hash storage (SHA-256)
- **Status Management**: Active/inactive user states
- **Denormalized Counts**: Performance optimization for profile display
- **Audit Trail**: Created/updated timestamps

#### `posts` - User Posts
```sql
CREATE TABLE posts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    visibility ENUM('public', 'private') DEFAULT 'public',
    is_edited BOOLEAN DEFAULT FALSE,
    parent_post_id BIGINT,                     -- For replies
    quoted_post_id BIGINT,                     -- For quote posts
    reply_count INT DEFAULT 0,
    like_count INT DEFAULT 0,
    quote_count INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL,                 -- Soft deletion
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (parent_post_id) REFERENCES posts(id) ON DELETE SET NULL,
    FOREIGN KEY (quoted_post_id) REFERENCES posts(id) ON DELETE SET NULL,
    
    INDEX idx_user_id (user_id),
    INDEX idx_created_at (created_at),
    INDEX idx_deleted_at (deleted_at),
    INDEX idx_visibility (visibility),
    INDEX idx_parent_post_id (parent_post_id),
    INDEX idx_quoted_post_id (quoted_post_id)
);
```

**Key Features**:
- **Content Storage**: TEXT field for 280-character limit
- **Threading Support**: Parent-child relationships for replies
- **Quote Posts**: Reference to quoted post
- **Soft Deletion**: Preserves data integrity with `deleted_at`
- **Interaction Counts**: Denormalized for performance
- **Visibility Control**: Public/private post support

### Social Features

#### `followers` - Follow Relationships
```sql
CREATE TABLE followers (
    follower_id BIGINT,
    followee_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    PRIMARY KEY (follower_id, followee_id),
    FOREIGN KEY (follower_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (followee_id) REFERENCES users(id) ON DELETE CASCADE,
    
    INDEX idx_follower_id (follower_id),
    INDEX idx_followee_id (followee_id),
    INDEX idx_created_at (created_at)
);
```

**Key Features**:
- **Composite Primary Key**: Prevents duplicate relationships
- **Bidirectional Indexing**: Fast follower/following queries
- **Cascade Deletion**: Automatic cleanup on user deletion

#### `likes` - Post Likes
```sql
CREATE TABLE likes (
    user_id BIGINT,
    post_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    PRIMARY KEY (user_id, post_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
    
    INDEX idx_user_id (user_id),
    INDEX idx_post_id (post_id),
    INDEX idx_created_at (created_at)
);
```

### Content Discovery

#### `hashtags` - Hashtag Registry
```sql
CREATE TABLE hashtags (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    tag VARCHAR(100) UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_tag (tag),
    INDEX idx_created_at (created_at)
);
```

#### `post_hashtags` - Post-Hashtag Relationships
```sql
CREATE TABLE post_hashtags (
    post_id BIGINT,
    hashtag_id BIGINT,
    
    PRIMARY KEY (post_id, hashtag_id),
    FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
    FOREIGN KEY (hashtag_id) REFERENCES hashtags(id) ON DELETE CASCADE,
    
    INDEX idx_post_id (post_id),
    INDEX idx_hashtag_id (hashtag_id)
);
```

**Design Benefits**:
- **Many-to-Many Relationship**: Posts can have multiple hashtags
- **Hashtag Reuse**: Efficient storage and trending calculation
- **Cascade Deletion**: Automatic cleanup

### Messaging System

#### `messages` - Direct Messages
```sql
CREATE TABLE messages (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    sender_id BIGINT NOT NULL,
    receiver_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (receiver_id) REFERENCES users(id) ON DELETE CASCADE,
    
    INDEX idx_sender_id (sender_id),
    INDEX idx_receiver_id (receiver_id),
    INDEX idx_conversation (sender_id, receiver_id, created_at),
    INDEX idx_created_at (created_at),
    INDEX idx_is_read (is_read)
);
```

**Key Features**:
- **Conversation Support**: Bi-directional messaging
- **Read Status**: Message read tracking
- **Composite Index**: Optimized conversation queries

## Query Patterns & Optimization

### Timeline Generation
```sql
-- Get timeline posts for user
SELECT p.*, u.username, u.status,
       (SELECT COUNT(*) > 0 FROM likes WHERE post_id = p.id AND user_id = ?) as is_liked
FROM posts p
INNER JOIN users u ON p.user_id = u.id
WHERE p.user_id IN (
    SELECT followee_id FROM followers WHERE follower_id = ?
    UNION
    SELECT ?  -- Include user's own posts
)
AND p.deleted_at IS NULL
AND p.visibility = 'public'
ORDER BY p.created_at DESC
LIMIT ? OFFSET ?;
```

**Optimization Strategy**:
- **Index Usage**: `user_id`, `created_at`, `deleted_at` indexes
- **Subquery Optimization**: Union for own posts inclusion
- **Pagination**: LIMIT/OFFSET for memory efficiency

### Trending Hashtags Algorithm
```sql
-- Trending hashtags with recency weighting
SELECT h.id, h.tag, h.created_at, COUNT(DISTINCT ph.post_id) as postCount
FROM hashtags h
INNER JOIN post_hashtags ph ON h.id = ph.hashtag_id
INNER JOIN posts p ON ph.post_id = p.id
WHERE p.deleted_at IS NULL
  AND p.visibility = 'public'
  AND p.created_at >= DATE_SUB(NOW(), INTERVAL 7 DAY)
GROUP BY h.id, h.tag, h.created_at
ORDER BY 
    (COUNT(DISTINCT ph.post_id) * 
     EXP(-TIMESTAMPDIFF(HOUR, MAX(p.created_at), NOW()) / 24.0)) DESC,
    COUNT(DISTINCT ph.post_id) DESC
LIMIT ?;
```

**Algorithm Features**:
- **Time Window**: Last 7 days only
- **Recency Weighting**: Exponential decay function
- **Public Posts Only**: Excludes private content
- **Compound Sorting**: Trending score + raw count

### User Recommendations
```sql
-- Random active users not followed by current user
SELECT id, username, status, created_at, posts_count, followers_count, following_count
FROM users
WHERE status = 'active'
  AND id != ?  -- Exclude current user
  AND id NOT IN (
      SELECT followee_id FROM followers WHERE follower_id = ?
  )
  AND posts_count > 0  -- Only active content creators
ORDER BY RAND()
LIMIT ?;
```

**Selection Criteria**:
- **Active Users**: Status = 'active'
- **Content Creators**: posts_count > 0
- **Not Following**: Exclude existing relationships
- **Random Selection**: RAND() for variety

## Performance Considerations

### Indexing Strategy

#### Primary Indexes
- **Primary Keys**: Auto-indexed for fast lookups
- **Foreign Keys**: Essential for join performance
- **Unique Constraints**: Username uniqueness enforcement

#### Secondary Indexes
- **Timeline Queries**: `(user_id, created_at)` composite
- **Hashtag Trending**: `(hashtag_id, post_id)` for aggregation
- **Message Conversations**: `(sender_id, receiver_id, created_at)`

#### Query-Specific Indexes
```sql
-- Timeline optimization
CREATE INDEX idx_posts_timeline ON posts(user_id, created_at, deleted_at, visibility);

-- Hashtag trending optimization  
CREATE INDEX idx_hashtag_trending ON post_hashtags(hashtag_id, post_id);

-- User search optimization
CREATE INDEX idx_users_search ON users(username, status);
```

### Denormalization Strategy

#### Count Fields
**Purpose**: Avoid expensive COUNT queries
**Tables**: users.posts_count, users.followers_count, posts.like_count

**Maintenance Strategy**:
```sql
-- Trigger-based count updates (future implementation)
DELIMITER //
CREATE TRIGGER update_user_posts_count 
AFTER INSERT ON posts
FOR EACH ROW
BEGIN
    UPDATE users SET posts_count = posts_count + 1 WHERE id = NEW.user_id;
END//
DELIMITER ;
```

#### Cached Relationships
**Purpose**: Fast relationship checks
**Implementation**: Service-layer caching (future)

### Connection Pooling

#### HikariCP Configuration
```properties
# Connection pool settings
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.idle-timeout=300000
spring.datasource.hikari.connection-timeout=30000
spring.datasource.hikari.leak-detection-threshold=60000
```

**Benefits**:
- **Connection Reuse**: Reduced connection overhead
- **Pool Management**: Automatic scaling
- **Leak Detection**: Development debugging

## Data Integrity & Constraints

### Referential Integrity
- **Cascade Deletions**: User deletion removes associated data
- **Set NULL**: Post deletion preserves reply structure
- **Constraint Enforcement**: Database-level data validation

### Business Rules Enforcement

#### Database Level
```sql
-- Ensure users cannot follow themselves
ALTER TABLE followers 
ADD CONSTRAINT chk_no_self_follow 
CHECK (follower_id != followee_id);

-- Ensure hashtag tags are properly formatted
ALTER TABLE hashtags 
ADD CONSTRAINT chk_hashtag_format 
CHECK (tag REGEXP '^[a-zA-Z0-9_]+$');
```

#### Application Level
- **Input Validation**: Service layer validation
- **Business Logic**: Complex rule enforcement
- **Transaction Management**: ACID compliance

## Migration Strategy

### Schema Versioning
```sql
-- Migration tracking table
CREATE TABLE schema_migrations (
    version VARCHAR(50) PRIMARY KEY,
    applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Future Enhancements

#### Planned Additions
1. **User Verification**: Verified user badge support
2. **Media Storage**: Image/video attachment support  
3. **Push Notifications**: Real-time notification system
4. **Content Moderation**: Automated content filtering

#### Scaling Considerations
1. **Read Replicas**: Separate read/write databases
2. **Sharding**: User-based data partitioning
3. **Caching Layer**: Redis for frequently accessed data
4. **Archive Strategy**: Old data archival system

This database design provides a solid foundation for a Twitter-like social media platform with room for future enhancements and optimizations.