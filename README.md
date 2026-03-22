# 🧵 BrainThread

A reactive Q&A backend built with **Spring Boot 3**, **WebFlux**, and **MongoDB**. BrainThread lets users post questions and retrieve them by author, search by keyword, or filter by tag — all served over a fully non-blocking, reactive stack.

---

## 🏗️ High-Level Architecture

```
Client (HTTP)
     │
     ▼
┌─────────────────────┐
│  QuestionController │  ← REST layer (Spring WebFlux)
└────────┬────────────┘
         │  IQuestionService (interface)
         ▼
┌─────────────────────┐
│   QuestionService   │  ← Business logic layer
└────────┬────────────┘
         │  ReactiveMongoRepository
         ▼
┌──────────────────────────┐
│   QuestionRepository     │  ← Reactive MongoDB queries
└────────┬─────────────────┘
         │
         ▼
    MongoDB (BrainThread db)
```

### Request / Response Flow

```
POST /api/questions
  → Controller receives QuestionRequestDTO (validated)
  → Service builds Question model, saves via repository (reactive Mono)
  → QuestionAdapter maps Question → QuestionResponseDTO
  → Response returned as Mono<QuestionResponseDTO>

GET /api/questions/author/{authorId}
  → Controller delegates to service
  → Repository streams results (reactive Flux)
  → QuestionAdapter maps each Question → QuestionResponseDTO
  → Response returned as Flux<QuestionResponseDTO>

GET /api/questions?cursor=...&limit=10
  → Service decodes base64 timestamp cursor
  → Repository fetches top N records older than cursor
  → Streamed as Flux<QuestionResponseDTO>

GET /api/questions/search?query=...&page=0&size=10
  → Service builds PageRequest from page/size params
  → Repository runs MongoDB $or regex query (title or content)
  → Streamed as Flux<QuestionResponseDTO>

GET /api/questions/tag/{tag}
  → Repository queries documents where tags array contains the value
  → Streamed as Flux<QuestionResponseDTO>
```

---

## 🧠 Design Patterns & Concepts Used

| Pattern / Concept | Where | Why |
|---|---|---|
| **Adapter Pattern** | `QuestionAdapter` | Converts `Question` (DB model) ↔ `QuestionResponseDTO` (API shape). Decouples the database model from what the client sees. |
| **Interface Segregation (SOLID)** | `IQuestionService` | Controller depends on an interface, not the concrete `QuestionService`. Makes the service swappable and independently testable. |
| **DTO Pattern** | `QuestionRequestDTO` / `QuestionResponseDTO` | Separates the API contract from the internal model. The response DTO includes `id`; the request DTO never exposes it. |
| **Repository Pattern** | `QuestionRepository` | Abstracts all database access behind a single interface. The service never writes raw MongoDB queries. |
| **Reactive Programming (Project Reactor)** | Entire stack | Uses `Mono<T>` (single result) and `Flux<T>` (stream of results) throughout. Zero threads are blocked waiting for I/O. |
| **Builder Pattern** | `Question`, `QuestionRequestDTO`, `QuestionResponseDTO` | Lombok `@Builder` makes object construction readable and avoids telescoping constructors. |
| **Dependency Injection** | `@RequiredArgsConstructor` (Lombok) | Final fields are injected by Spring at startup. No `@Autowired` field injection needed. |
| **Bean Validation** | `@NotBlank`, `@Size` on DTOs | Jakarta Validation enforces input rules at the request layer, before any business logic runs. |
| **Pagination** | `PageRequest` / `Pageable` | `searchQuestions` and `getQuestionByTag` accept `page` and `size` params, passed as a `Pageable` to the repository. |
| **Audit Timestamps** | `@CreatedDate`, `@LastModifiedDate` on `Question` | Spring Data auto-populates `createdAt` / `updatedAt` for MongoDB documents. |

---

## 🛠️ Tech Stack

| Technology | Version |
|---|---|
| Java | 17 |
| Spring Boot | 3.4.2 |
| Spring WebFlux | via Boot starter |
| Spring Data MongoDB Reactive | via Boot starter |
| Project Reactor | via WebFlux |
| Lombok | Latest |
| Jakarta Bean Validation | via Boot starter |
| Gradle | 9.x |
| MongoDB | 6+ recommended |

---

## ⚙️ Prerequisites

- **Java 17+** — [Download](https://adoptium.net/)
- **MongoDB** running on port `27017` — local install or Docker (see below)
- **Git** — [Download](https://git-scm.com/)

> Spring will create the `BrainThread` database automatically on the first write. You do not need to create it manually.

---

## 🚀 Running Locally

### 1. Clone the repository

```bash
git clone https://github.com/<your-username>/BrainThread.git
cd BrainThread
```

### 2. Start MongoDB

**Option A — Docker (recommended if you don't have Mongo installed):**
```bash
docker run -d -p 27017:27017 --name mongo mongo:latest
```

**Option B — Local install (macOS/Linux):**
```bash
mongod --dbpath /data/db
```

**Option B — Local install (Windows):**
```powershell
mongod --dbpath "C:\data\db"
```

### 3. Run the application

```bash
# macOS / Linux
./gradlew bootRun

# Windows
.\gradlew.bat bootRun
```

The app starts on **`http://localhost:8080`**.

---

## 🔧 Configuration

`src/main/resources/application.properties`:

```properties
spring.application.name=BrainThread

spring.data.mongodb.host=localhost
spring.data.mongodb.port=27017
spring.data.mongodb.database=BrainThread
spring.data.mongodb.auto-index-creation=true
```

---

## 📡 API Reference

**Base URL:** `http://localhost:8080/api/questions`

---

### ➕ POST `/api/questions` — Create a Question

**Request Body:**
```json
{
  "title": "What is reactive programming?",
  "content": "I want to understand the core concepts of reactive programming and how it differs from traditional threading models.",
  "userId": "user_abc123"
}
```

**Validation:**
| Field | Rules |
|---|---|
| `title` | Required, 10–100 characters |
| `content` | Required, 10–1000 characters |
| `userId` | Required, non-blank |

**Response `200 OK`:**
```json
{
  "id": "65f1a2b3c4d5e6f7a8b9c0d1",
  "title": "What is reactive programming?",
  "content": "I want to understand the core concepts of reactive programming and how it differs from traditional threading models.",
  "userId": "user_abc123",
  "createdAt": "2026-03-10T16:58:00.000+00:00",
  "updatedAt": "2026-03-10T16:58:00.000+00:00"
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

**PowerShell:**
```powershell
Invoke-RestMethod -Method POST -Uri "http://localhost:8080/api/questions" `
  -ContentType "application/json" `
  -Body '{
    "title": "What is reactive programming?",
    "content": "I want to understand the core concepts of reactive programming and how it differs from traditional threading models.",
    "userId": "user_abc123"
  }'
```

---

### 📋 GET `/api/questions/author/{authorId}` — Get Questions by Author

Returns all questions posted by a user as a streaming JSON array.

**Path Variable:** `authorId` — the userId string used when creating the question.

**Response `200 OK`:**
```json
[
  {
    "id": "65f1a2b3c4d5e6f7a8b9c0d1",
    "title": "What is reactive programming?",
    "content": "...",
    "userId": "user_abc123",
    "createdAt": "2026-03-10T16:58:00.000+00:00",
    "updatedAt": "2026-03-10T16:58:00.000+00:00"
  }
]
```

**cURL:**
```bash
curl http://localhost:8080/api/questions/author/user_abc123
```

**PowerShell:**
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/questions/author/user_abc123"
```

---

### 📜 GET `/api/questions` — Get All Questions (Cursor Paginated)

Returns questions globally, paginated using a `cursor` (timestamp) for maximum database performance.

**Query Parameters:**
| Param | Type | Default | Description |
|---|---|---|---|
| `cursor` | `String` | `null` | The `createdAt` timestamp of the last item you saw. If omitted, returns the absolute newest questions. |
| `limit` | `int` | `10` | Maximum results to return |

**Response `200 OK`:**
```json
[
  {
    "id": "65f1a2b3c4d5e6f7a8b9c0d1",
    "title": "What is reactive programming?",
    "content": "...",
    "userId": "user_abc123",
    "createdAt": "2026-03-10T16:58:00.000",
    "updatedAt": "2026-03-10T16:58:00.000"
  }
]
```

**cURL:**
```bash
# Initial load (no cursor)
curl "http://localhost:8080/api/questions?limit=5"

# Next page (using the timestamp of the last item in the previous request)
curl "http://localhost:8080/api/questions?cursor=2026-03-10T16:58:00.000&limit=5"
```

**PowerShell:**
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/questions?cursor=2026-03-10T16:58:00.000&limit=5"
```

---

### 🔍 GET `/api/questions/search` — Search Questions

Searches across **title and content** using a case-insensitive regex. Supports pagination.

**Query Parameters:**
| Param | Type | Default | Description |
|---|---|---|---|
| `query` | `String` | required | Search term |
| `page` | `int` | `0` | Page number (0-indexed) |
| `size` | `int` | `10` | Results per page |

**Response `200 OK`:**
```json
[
  {
    "id": "65f1a2b3c4d5e6f7a8b9c0d1",
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
# Basic search
curl "http://localhost:8080/api/questions/search?query=reactive"

# With pagination
curl "http://localhost:8080/api/questions/search?query=reactive&page=0&size=5"
```

**PowerShell:**
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/questions/search?query=reactive&page=0&size=5"
```

---

### 🏷️ GET `/api/questions/tag/{tag}` — Get Questions by Tag

Returns all questions that have the given tag. Supports pagination.

**Path Variable:** `tag` — the tag value to filter by (must be stored in the `tags` array on the document).

**Query Parameters:**
| Param | Type | Default | Description |
|---|---|---|---|
| `page` | `int` | `0` | Page number (0-indexed) |
| `size` | `int` | `10` | Results per page |

**Response `200 OK`:**
```json
[
  {
    "id": "65f1a2b3c4d5e6f7a8b9c0d1",
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
# Basic tag filter
curl "http://localhost:8080/api/questions/tag/java"

# With pagination
curl "http://localhost:8080/api/questions/tag/java?page=0&size=5"
```

**PowerShell:**
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/questions/tag/java?page=0&size=5"
```

---

## 🧪 Running Tests

```bash
# macOS / Linux
./gradlew test

# Windows
.\gradlew.bat test
```

Test reports are generated at `build/reports/tests/test/index.html`.

---

## 📁 Project Structure

```
src/main/java/com/ishan/BrainThread/
├── BrainThreadApplication.java      # Entry point (@SpringBootApplication)
├── controllers/
│   └── QuestionController.java      # REST endpoints (Spring WebFlux)
├── service/
│   ├── IQuestionService.java        # Service interface
│   └── QuestionService.java         # Business logic implementation
├── repositories/
│   └── QuestionRepository.java      # ReactiveMongoRepository queries
├── models/
│   └── Question.java                # MongoDB document model (@Document)
├── dto/
│   ├── QuestionRequestDTO.java      # Inbound request shape (validated)
│   └── QuestionResponseDTO.java     # Outbound response shape (includes id)
└── adapter/
    └── QuestionAdapter.java         # Question → QuestionResponseDTO mapping
```

---

## 🤝 Contributing

1. Fork the repo
2. Create your feature branch: `git checkout -b feature/my-feature`
3. Commit your changes: `git commit -m 'Add my feature'`
4. Push to the branch: `git push origin feature/my-feature`
5. Open a Pull Request
