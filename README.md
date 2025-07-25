# 🍳 JamesThew Culinary API (Backend)

A comprehensive REST API backend for a culinary platform built with Java Servlets, providing complete recipe management, cooking contests, user management, and authentication features with role-based access control.

---

## ✨ Key Features

### 🔐 Authentication & Authorization
- User registration and JWT-based authentication
- Multi-role access control (Admin, Staff, Writer, Subscriber, General)
- Permission-based access control for sensitive operations
- Secure password hashing with BCrypt

### 👥 User Management
- User profiles with avatars, location, education details
- Role assignment and permission management
- User activity tracking and subscription validation
- Staff permission management system

### 🍽️ Recipe Management
- CRUD operations for recipes with detailed nutritional information
- Category and area-based recipe organization
- Free and premium recipe access control
- Recipe discovery with advanced search and filtering
- Image upload support with Cloudinary integration

### 🏆 Contest System
- Cooking contest creation and management
- Contest entry submissions with images and instructions
- Contest entry evaluation and scoring system
- Winner announcements and rankings
- Contest entry status tracking

### 💬 Community Features
- Comment system for recipes and contests
- User interaction and engagement tracking
- Community-driven content validation

---

## 🛠️ Tech Stack

### Core Technologies
- **Framework:** Java Servlets (javax.servlet-api 4.0.1)
- **Database:** MySQL 8.0.33 with JDBC
- **Authentication:** JWT (JJWT 0.11.5) + BCrypt
- **Image Storage:** Cloudinary Integration
- **File Upload:** Apache Commons FileUpload 1.4

### Development & Testing
- **Build Tool:** Maven 3.9.6
- **Testing:** JUnit Jupiter 5.10.2 + Mockito 5.12.0
- **Test Database:** H2 Database 2.3.230
- **JSON Processing:** Gson 2.10.1

### Deployment
- **Container:** Docker with Tomcat 9.0.106 + JDK 17
- **Environment:** Java-dotenv 5.2.2 for configuration

---

## 📁 Project Structure Highlights

```
src/main/java/com/ntn/culinary/
├── servlet/           # REST endpoint controllers
│   ├── admin/         # Admin-only endpoints
│   ├── staff/         # Staff-level endpoints  
│   ├── general/       # General user endpoints
│   └── subscriber/    # Subscriber-level endpoints
├── service/           # Business logic layer
├── dao/               # Data Access Objects
├── model/             # Entity models (User, Recipe, Contest, etc.)
├── request/           # Request DTOs
├── response/          # Response DTOs
├── filter/            # JWT authentication filter
├── validator/         # Input validation logic
├── utils/             # Utility classes
└── config/            # Configuration classes
```

---

## 🚀 API Endpoints Overview

### Public Endpoints
- `POST /api/register` - User registration
- `POST /api/login` - User authentication
- `GET /api/recipes` - Browse free recipes
- `GET /api/categories` - Get recipe categories
- `GET /api/areas` - Get recipe areas
- `GET /api/contests` - View active contests
- `GET /api/discover/recipes` - Advanced recipe search

### Protected Endpoints (JWT Required)

#### General User (`/api/protected/general/*`)
- Contest entry management
- User profile updates
- Comment system

#### Subscriber (`/api/protected/subscriber/*`)
- Premium recipe access
- Enhanced contest features

#### Staff (`/api/protected/staff/*`)
- Recipe management (CRUD)
- User moderation
- Contest administration

#### Admin (`/api/protected/admin/*`)
- System-wide configuration
- User role management
- Category/area management
- Permission assignment

---

## 🔑 Role-Based Access Control

| Role | Access Level | Permissions |
|------|-------------|-------------|
| **ADMIN** | Full System Access | All operations, user management, system config |
| **STAFF** | Content Management | Recipe/contest management, user moderation |
| **WRITER** | Content Creation | Recipe creation, contest participation |
| **SUBSCRIBER** | Premium Features | Access to premium recipes, enhanced features |
| **GENERAL** | Basic Access | Free recipes, basic contest participation |

---

## 🗄️ Database Architecture

### Core Entities
- **Users** - User profiles with roles and permissions
- **Recipes** - Recipe details with nutritional information
- **Categories/Areas** - Recipe classification
- **Contests** - Cooking competitions
- **Contest Entries** - User submissions to contests
- **Comments** - Community feedback system

### Key Relationships
- Users have multiple Roles (many-to-many)
- Staff Users have specific Permissions (many-to-many)
- Recipes belong to Categories and Areas
- Contest Entries link Users to Contests
- Comments can be on Recipes or Contest Entries

---

## 🖼️ File Upload & Image Management

- **Cloudinary Integration** for cloud-based image storage
- **Multipart Form Support** for recipe and contest entry images
- **Local File Serving** via ImageServlet for development
- **Automatic Image Processing** with public_id management

---

## 🧪 Testing Strategy

- **Unit Tests** with JUnit Jupiter and Mockito
- **DAO Layer Testing** with H2 in-memory database
- **Service Layer Mocking** for business logic validation
- **Integration Testing** for complete request flows

---

## 🐳 Docker Deployment

```dockerfile
# Multi-stage build with Maven + JDK 17
FROM maven:3.9.6-eclipse-temurin-17 AS build
# Production runtime with Tomcat 9.0.106
FROM tomcat:9.0.106-jdk17-temurin
```

The application is containerized for easy deployment with:
- Optimized build process with dependency caching
- Production-ready Tomcat configuration
- Environment variable support for configuration

---

## 🎯 Key Features in Detail

### Authentication Flow
1. User registers with encrypted password storage
2. JWT tokens issued on successful login
3. Role-based route protection via JwtFilter
4. Permission-based operation authorization

### Recipe Management
1. Multi-tier access control (free vs premium)
2. Rich metadata (nutrition, cooking time, difficulty)
3. Category and geographical area classification
4. Advanced search and filtering capabilities

### Contest System
1. Time-based contest management
2. Multi-step entry submission process
3. Judge assignment and scoring system
4. Automated winner selection and ranking

---

## 💡 Development Highlights

This project demonstrates:
- **Enterprise Java Architecture** with proper separation of concerns
- **Security Best Practices** with JWT and role-based access control
- **Scalable Database Design** with normalized relationships
- **RESTful API Design** following HTTP conventions
- **Image Processing Pipeline** with cloud storage integration
- **Comprehensive Error Handling** with proper HTTP status codes
- **Input Validation** with custom validator classes
- **Test-Driven Development** with extensive unit test coverage