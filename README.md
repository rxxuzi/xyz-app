# XYZ

A Twitter-like social media application built with Spring Boot and MyBatis.

## Features

- **User Authentication** - Registration, login, and session management
- **Social Networking** - Follow/unfollow users, view timelines
- **Content Creation** - Create posts, replies, and quote posts (280 character limit)
- **Interactions** - Like posts, view threads
- **Direct Messaging** - Private conversations between users
- **Discovery** - Search users/posts, trending hashtags, user recommendations
- **Real-time Updates** - Dynamic sidebar with trending topics

## Requirements

- Java 17+
- MySQL 8.0+
- Maven 3.6+

## Quick Start

```bash
# Clone the repository
git clone https://github.com/rxxuzi/xyz-app xyz
cd xyz

# Setup database
mysql -u root -p < assets/schema.sql

# Configure database connection
# Edit src/main/resources/application.properties
spring.datasource.username=your_username
spring.datasource.password=your_password

# Run the application
./mvnw spring-boot:run
```

The application will be available at `http://localhost:8080`

## Project Structure

```
xyz/
├── src/main/java/          # Java source code
├── src/main/resources/     # Templates, static files, configs
├── assets/                 # Database schema
├── doc/                    # Project documentation
└── pom.xml                # Maven configuration
```

## Documentation

See the [documentation directory](doc/README.md) for detailed information about:
- Architecture and design
- API reference
- Database schema
- Development guides

## License

This project is licensed under the MIT License.