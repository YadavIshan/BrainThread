# 🧵 BrainThread

A reactive Q&A backend built with **Spring Boot 3**, **WebFlux**, and **MongoDB**. BrainThread lets users post questions and retrieve them by author — all served over a fully non-blocking, reactive stack.

---

## 🏗️ High-Level Architecture

```
Client (HTTP)
     │
     ▼
┌─────────────────────┐
│  QuestionController │  ← REST layer (Spring WebFlux)
└────────┬────────────┘
         │  calls
         ▼
┌─────────────────────┐
│   QuestionService   │  ← Business logic layer
└────────┬────────────┘
         │  reads/writes
         ▼
┌──────────────────────────┐
│   QuestionRepository     │  ← ReactiveMongoRepository
└────────┬─────────────────┘
         │
         ▼
    MongoDB (local)
```

### Request / Response Flow

```
POST /api/questions
  → Controller receives QuestionRequestDTO
  → Service builds Question model, saves via repository (reactive Mono)
  → QuestionAdapter maps Question → QuestionRequestDTO
  → Response returned as Mono<QuestionRequestDTO>

GET /api/questions/author/{authorId}
  → Controller delegates to service
  → Repository streams results (reactive Flux)
  → QuestionAdapter maps each Question → QuestionRequestDTO
  → Response returned as Flux<QuestionRequestDTO>
```

### Key Design Decisions

| Layer | Pattern | Reason |
|---|---|---|
| Controller → Service | Interface (`IQuestionService`) | Decouples HTTP layer from implementation |
| Service → Repository | `ReactiveMongoRepository` | Non-blocking I/O with Project Reactor |
| Model → DTO | `QuestionAdapter` (Adapter pattern) | Keeps the DB model private; controls response shape |
| Boilerplate | Lombok (`@Data`, `@Builder`, etc.) | Reduces noise in POJOs |
| Validation | Jakarta Bean Validation (`@NotBlank`, `@Size`) | Enforced at the DTO layer before hitting the service |

---

## 🛠️ Tech Stack

| Technology | Version |
|---|---|
| Java | 17 |
| Spring Boot | 3.4.2 |
| Spring WebFlux | (via Boot starter) |
| Spring Data MongoDB Reactive | (via Boot starter) |
| Lombok | Latest |
| Gradle | 9.x |
| MongoDB | 6+ recommended |

---

## ⚙️ Prerequisites

Make sure you have the following installed:

- **Java 17+** — [Download](https://adoptium.net/)
- **MongoDB** (running locally on port `27017`) — [Download](https://www.mongodb.com/try/download/community)
- **Git** — [Download](https://git-scm.com/)

> MongoDB must be **running** before you start the app. You do not need to create the database manually — Spring will create it on first write.

---

## 🚀 Running Locally

### 1. Clone the repository

```bash
git clone https://github.com/<your-username>/BrainThread.git
cd BrainThread
```

### 2. Start MongoDB

**macOS/Linux:**
```bash
mongod --dbpath /data/db
```

**Windows:**
```powershell
mongod --dbpath "C:\data\db"
```

Or if installed as a service, it may already be running.

### 3. Build and run the app

**macOS/Linux:**
```bash
./gradlew bootRun
```

**Windows:**
```powershell
.\gradlew.bat bootRun
```

The app will start on **`http://localhost:8080`**.

---

## 🔧 Configuration

All config lives in `src/main/resources/application.properties`. The defaults work out of the box for a local MongoDB setup:

```properties
spring.application.name=BrainThread

spring.data.mongodb.host=localhost
spring.data.mongodb.port=27017
spring.data.mongodb.database=BrainThread
spring.data.mongodb.auto-index-creation=true
```

If your MongoDB runs on a different host/port, update these values accordingly.

---

## 📡 API Reference

Base URL: `http://localhost:8080/api/questions`

---

### ➕ Create a Question

**`POST /api/questions`**

**Request Body:**
```json
{
  "title": "What is reactive programming?",
  "content": "I want to understand the core concepts of reactive programming and how it differs from traditional threading models.",
  "userId": "user_abc123"
}
```

**Validation rules:**
- `title` — required, 10–100 characters
- `content` — required, 10–1000 characters
- `userId` — required, non-blank

**Response `200 OK`:**
```json
{
  "title": "What is reactive programming?",
  "content": "I want to understand the core concepts of reactive programming...",
  "userId": "user_abc123",
  "createdAt": "2026-02-23T05:12:00.000+00:00",
  "updatedAt": "2026-02-23T05:12:00.000+00:00"
}
```

**cURL:**
```bash
curl -X POST http://localhost:8080/api/questions \
  -H "Content-Type: application/json" \
  -d '{
    "title": "What is reactive programming?",
    "content": "I want to understand the core concepts of reactive programming and how it differs from traditional threading models.",
    "userId": "user_abc123"
  }'
```

---

### 📋 Get Questions by Author

**`GET /api/questions/author/{authorId}`**

Returns all questions posted by the given user as a streaming JSON array.

**Response `200 OK`:**
```json
[
  {
    "title": "What is reactive programming?",
    "content": "...",
    "userId": "user_abc123",
    "createdAt": "...",
    "updatedAt": "..."
  }
]
```

**cURL:**
```bash
curl http://localhost:8080/api/questions/author/user_abc123
```

---

## 🧪 Running Tests

```bash
# macOS/Linux
./gradlew test

# Windows
.\gradlew.bat test
```

Test reports are generated at `build/reports/tests/test/index.html`.

---

## 📁 Project Structure

```
src/main/java/com/ishan/BrainThread/
├── BrainThreadApplication.java   # Entry point
├── controllers/
│   └── QuestionController.java   # REST endpoints
├── servicce/
│   ├── IQuestionService.java     # Service interface
│   └── QuestionService.java      # Business logic
├── repositories/
│   └── QuestionRepository.java   # Reactive MongoDB queries
├── models/
│   └── Question.java             # MongoDB document model
├── dto/
│   └── QuestionRequestDTO.java   # Request/Response DTO
└── adapter/
    └── QuestionAdapter.java      # Model ↔ DTO mapping
```

---

## 🤝 Contributing

1. Fork the repo
2. Create your feature branch: `git checkout -b feature/my-feature`
3. Commit your changes: `git commit -m 'Add my feature'`
4. Push to the branch: `git push origin feature/my-feature`
5. Open a Pull Request
