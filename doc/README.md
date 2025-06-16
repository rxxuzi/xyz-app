# XYZ Project Documentation

> **A comprehensive Twitter-like social media application built with Spring Boot 3.4.6 and MyBatis 3.0.4**

Welcome to the XYZ project documentation hub. This directory contains detailed documentation for understanding, developing, and maintaining the XYZ social media platform.

## 📚 Documentation Index

### Getting Started
| Document                                            | Description                                            | For Who                |
|-----------------------------------------------------|--------------------------------------------------------|------------------------|
| **[📖 Project Overview](01-project-overview.md)**   | Project goals, features, and technology stack          | Everyone               |
| **[🏗️ Architecture](02-architecture.md)**          | System design, MVC pattern, and component interactions | Developers, Architects |
| **[📁 Project Structure](03-project-structure.md)** | File organization, naming conventions, and code layout | Developers             |

### Development Resources
| Document                                         | Description                                            | For Who                     |
|--------------------------------------------------|--------------------------------------------------------|-----------------------------|
| **[🔄 Data Flow](04-data-flow.md)**              | Request processing, authentication, and data pipelines | Developers, System Analysts |
| **[🛠️ Components](05-components.md)**           | Detailed controller, service, and mapper documentation | Developers                  |
| **[🗃️ Database Design](06-database-design.md)** | Schema, relationships, queries, and optimization       | Database Developers, DBAs   |

### API & Configuration
| Document                                            | Description                                              | For Who                          |
|-----------------------------------------------------|----------------------------------------------------------|----------------------------------|
| **[🌐 API Documentation](07-api-documentation.md)** | REST endpoints, request/response formats, authentication | Frontend Developers, Integrators |

---

## 🚀 Quick Start

### For Developers
1. **Start Here**: [📖 Project Overview](01-project-overview.md) - Understand what XYZ is and its goals
2. **Setup Database**: Run `mysql -u root -p < assets/schema.sql` to create and setup database
3. **Explore Architecture**: [🏗️ Architecture](02-architecture.md) - Learn the system design patterns
4. **Study Components**: [🛠️ Components](05-components.md) - Understand individual components

### For Frontend/API Developers
1. **API Reference**: [🌐 API Documentation](07-api-documentation.md) - Complete endpoint documentation
2. **Data Models**: [🗃️ Database Design](06-database-design.md) - Entity relationships and data structure
3. **Data Flow**: [🔄 Data Flow](04-data-flow.md) - How data moves through the system

---

## 🏗️ Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Controllers   │───▶│    Services     │───▶│    Mappers      │
│                 │    │                 │    │                 │
│ HTTP Endpoints  │    │ Business Logic  │    │ Data Access     │
│ Request/Response│    │ Transactions    │    │ SQL Queries     │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                                        │
                                                        ▼
                                                ┌─────────────────┐
                                                │    Database     │
                                                │     (MySQL)     │
                                                └─────────────────┘
```

## 📊 Technology Stack

| Layer        | Technology            | Purpose               |
|--------------|-----------------------|-----------------------|
| **Backend**  | Spring Boot 3.4.6     | Application framework |
| **ORM**      | MyBatis 3.0.4         | Database mapping      |
| **Database** | MySQL 8.0+            | Data persistence      |
| **Frontend** | Thymeleaf + CSS3 + JS | User interface        |
| **Build**    | Maven                 | Dependency management |
| **Security** | Custom Session Auth   | User authentication   |

---

## 📋 About This Documentation

This documentation provides comprehensive technical information about the XYZ social media platform. Each document is designed for specific audiences and purposes:

Start with the [Project Overview](01-project-overview.md) and navigate through the documentation based on your needs.