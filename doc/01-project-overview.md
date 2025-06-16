# Project Overview

## About XYZ

XYZ is a Twitter-like social networking application built with **Spring Boot 3.4.6** and **MyBatis 3.0.4**. It features real-time messaging, post interactions, hashtag trending, and user recommendations.

## Key Features

### 🔐 **User Management**
- **Session-based Authentication**: 30-minute timeout with automatic logout
- **User Profiles**: Customizable profiles with follower/following counts
- **Social Relationships**: Follow/unfollow functionality

### 📝 **Content Creation**
- **Post Management**: Create, reply, quote posts with 280-character limit
- **Rich Interactions**: Like, reply, quote, and delete posts
- **Hashtag Support**: Automatic hashtag extraction and trending

### 💬 **Messaging System**
- **Direct Messages**: Private messaging between users
- **Conversation Threading**: Organized message history
- **Real-time Updates**: Live message status and notifications

### 🔍 **Discovery Features**
- **Content Search**: Search across posts, users, and hashtags
- **Trending Topics**: Dynamic hashtag trending with recency weighting
- **User Recommendations**: Smart user suggestions based on activity
- **Real-time Suggestions**: Dynamic search suggestions

### 🎨 **User Experience**
- **Responsive Design**: Twitter-like modern UI
- **Dynamic Sidebar**: Real-time trending and recommendations
- **Modal Interactions**: Post creation, replies, and quote posts
- **Progressive Enhancement**: JavaScript-enhanced interactions

## Technology Stack

### Backend
- **Framework**: Spring Boot 3.4.6
- **Architecture**: Spring MVC pattern
- **ORM**: MyBatis 3.0.4 with XML mappers
- **Database**: MySQL with HikariCP connection pooling
- **Build Tool**: Maven

### Frontend
- **Template Engine**: Thymeleaf
- **Styling**: CSS3 with custom properties (CSS variables)
- **JavaScript**: Vanilla ES6+ for dynamic interactions
- **Icons**: Font Awesome 6.4.0
- **Fonts**: Geist Sans

### Security
- **Authentication**: Custom session-based system
- **Validation**: Custom input validators
- **XSS Protection**: Content sanitization utilities
- **Password Security**: SHA-256 hashing (60-character storage)

### Development Tools
- **IDE Support**: IntelliJ IDEA project structure
- **Version Control**: Git with conventional commits
- **Documentation**: Markdown with GitHub-flavored syntax

## Project Goals

### Primary Objectives
1. **Simplicity**: Clean, maintainable codebase following MVC patterns
2. **Performance**: Efficient database queries and caching strategies
3. **Security**: Robust input validation and XSS prevention
4. **Scalability**: Modular architecture for future enhancements

### Educational Purpose
This project serves as a comprehensive example of:
- Modern Spring Boot development practices
- MyBatis ORM integration and optimization
- Session-based authentication implementation
- Real-time web application features
- Twitter-like social media functionality

## Getting Started

### Prerequisites
- **Java 17+**: Required for Spring Boot 3.4.6
- **MySQL 8.0+**: Database server
- **Maven 3.6+**: Build tool
- **Git**: Version control

### Quick Start
```bash
# Clone the repository
git clone https://github.com/rxxuzi/xyz-app xyz
cd xyz

# Configure database (see database-setup.md)
mysql -u root -p < assets/schema.sql

# Run the application
./mvnw spring-boot:run
```

### Next Steps
- 📖 [Architecture Overview](02-architecture.md)
- 🏗️ [Project Structure](03-project-structure.md)
- 🔄 [Data Flow](04-data-flow.md)
- 🗃️ [Database Design](06-database-design.md)

## Contributing

### Development Workflow
1. Setup database with `mysql -u root -p < assets/schema.sql`
2. Configure application properties
3. Check the [API Documentation](07-api-documentation.md)
4. Submit pull requests with proper testing

### Support
- 📚 [Documentation Index](README.md)
- 🏗️ [Architecture](02-architecture.md)
- 🌐 [API Reference](07-api-documentation.md)