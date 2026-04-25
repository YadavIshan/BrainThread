# 🧵 BrainThread

A reactive Q&A backend built with **Spring Boot 3**, **WebFlux**, **MongoDB**, **Apache Kafka**, and **Elasticsearch**. BrainThread lets users post questions, write answers, leave likes, and perform blazing-fast full-text search. The core database runs on MongoDB, full-text search is powered by Elasticsearch, and view counts are tracked asynchronously via a Kafka event pipeline.

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


── View Count Event Flow (Async / Kafka) ──────────────────────

GET /api/questions/{id}
     │
     ▼
┌─────────────────────┐
│   QuestionService   │  → fetches question, then fires event
└────────┬────────────┘
         │  publishes ViewCountEvent
         ▼
┌──────────────────────────┐
│   KafkaEventProducer     │  → sends to "view-count-topic" (keyed by targetId)
└────────┬─────────────────┘
         │
         ▼
    Apache Kafka (view-count-topic)
         │
         ▼
┌──────────────────────────┐
│   KafkaEventConsumer     │  → @KafkaListener (3 concurrent threads)
└────────┬─────────────────┘
         │  increments viewCount via repository
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

GET /api/questions/{id}
  → Repository fetches question by ID (Mono)
  → QuestionAdapter maps Question → QuestionResponseDTO
  → doOnSuccess fires a ViewCountEvent → KafkaEventProducer sends to Kafka
  → KafkaEventConsumer receives event, increments viewCount in MongoDB (async)
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
| **Event-Driven Architecture** | `ViewCountEvent`, `KafkaEventProducer`, `KafkaEventConsumer` | View count increments are decoupled from the read path — the HTTP response is returned immediately and the counter update happens asynchronously via Kafka. |
| **Observer / Pub-Sub Pattern** | Kafka topic `view-count-topic` | Producer publishes events; consumer subscribes independently. Neither knows about the other, keeping them fully decoupled. |

---

## 🛠️ Tech Stack

| Technology | Version |
|---|---|
| Java | 17 |
| Spring Boot | 3.4.2 |
| Spring WebFlux | via Boot starter |
| Spring Data MongoDB Reactive | via Boot starter |
| Spring Data Elasticsearch | via Boot starter |
| Project Reactor | via WebFlux |
| Apache Kafka | 3.x |
| Spring Kafka | via Boot starter |
| Lombok | Latest |
| Jakarta Bean Validation | via Boot starter |
| Gradle | 9.x |
| MongoDB | 6+ recommended |
| Elasticsearch | 8.x recommended |

---

## ⚙️ Prerequisites

- **Java 17+** — [Download](https://adoptium.net/)
- **MongoDB** running on port `27017` — local install or Docker (see below)
- **Apache Kafka** running on port `9092` — local install or Docker (see below)
- **Elasticsearch** running on port `9200` — local install or Docker (see below)
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

### 3. Start Kafka

**Docker (easiest — runs both Zookeeper and Kafka):**
```bash
docker run -d --name zookeeper -p 2181:2181 zookeeper:latest
docker run -d --name kafka -p 9092:9092 \
  -e KAFKA_ZOOKEEPER_CONNECT=zookeeper:2181 \
  -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092 \
  --link zookeeper confluentinc/cp-kafka:latest
```

> The topic `view-count-topic` is created automatically by the application on first publish.

### 4. Start Elasticsearch

**Docker (easiest):**
```bash
docker run -d --name elasticsearch -p 9200:9200 -e "discovery.type=single-node" -e "xpack.security.enabled=false" docker.elastic.co/elasticsearch/elasticsearch:8.12.0
```

### 5. Run the application

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

# Elasticsearch
elasticsearch.uris=http://localhost:9200

# Kafka
spring.kafka.bootstrap-servers=localhost:9092
spring.kafka.consumer.group-id=view-count-consumer
```

---

## 📡 API Reference

**Base URLs:** 
- Questions: `http://localhost:8080/api/questions`
- Answers: `http://localhost:8080/api/answers`
- Likes: `http://localhost:8080/api/likes`

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

### 🔎 GET `/api/questions/{id}` — Get Question by ID

Fetches a single question by its MongoDB ID. Asynchronously fires a view count event to Kafka.

**Path Variable:** `id` — the MongoDB document ID.

**Response `200 OK`:**
```json
{
  "id": "65f1a2b3c4d5e6f7a8b9c0d1",
  "title": "What is reactive programming?",
  "content": "...",
  "userId": "user_abc123",
  "viewCount": 42,
  "createdAt": "2026-03-10T16:58:00.000+00:00",
  "updatedAt": "2026-03-10T16:58:00.000+00:00"
}
```

**cURL:**
```bash
curl http://localhost:8080/api/questions/65f1a2b3c4d5e6f7a8b9c0d1
```

**PowerShell:**
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/questions/65f1a2b3c4d5e6f7a8b9c0d1"
```

> ℹ️ The `viewCount` field is incremented **asynchronously** after the response is returned — the user never waits for it.

---

### 🚀 GET `/api/questions/elasticsearch` — Super-Fast Full-Text Search

Powered by Elasticsearch, querying both the Title and Content fields for highly optimized search results.

**Query Parameters:**
- `query` (String): Search term

**cURL:**
```bash
curl "http://localhost:8080/api/questions/elasticsearch?query=reactive"
```

**PowerShell:**
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/questions/elasticsearch?query=reactive"
```

---

### 📝 POST `/api/answers` — Create an Answer

Submit an answer replying to a specific question.

**Request Body:**
```json
{
  "content": "Reactive programming handles concurrency with an event loop, drastically reducing memory overhead.",
  "questionId": "65f1a2b3c4d5e6f7a8b9c0d1",
  "userId": "user_expert89"
}
```

**cURL:**
```bash
curl -X POST http://localhost:8080/api/answers \
  -H "Content-Type: application/json" \
  -d '{
    "content": "Reactive programming handles concurrency with an event loop, drastically reducing memory overhead.",
    "questionId": "65f1a2b3c4d5e6f7a8b9c0d1",
    "userId": "user_expert89"
  }'
```

---

### 📚 GET `/api/answers/question/{questionId}` — Get Answers for a Question

Streams all the answers that belong to a single question.

**cURL:**
```bash
curl http://localhost:8080/api/answers/question/65f1a2b3c4d5e6f7a8b9c0d1
```

---

### ❤️ POST `/api/likes/toggle` — Toggle a Like or Dislike

Dynamically adds, flips, or removes a like depending on the user's current like state for the target entity (such as a Question or an Answer).

**Query Parameters:**
| Param | Type | Description |
|---|---|---|
| `targetId` | `String` | MongoDB ID of the liked entity |
| `targetType` | `String` | e.g. `Question` or `Answer` |
| `userId` | `String` | ID of the user performing the like |

**cURL:**
```bash
curl -X POST "http://localhost:8080/api/likes/toggle?targetId=65f1a2b3c4d5e6f7a8b9c0d1&targetType=Question&userId=user_abc123"
```

---

### 📊 GET `/api/likes/count/likes` — Get Total Likes

Quickly calculate the absolute number of affirmative likes applied to a target.

**cURL:**
```bash
curl "http://localhost:8080/api/likes/count/likes?targetId=65f1a2b3c4d5e6f7a8b9c0d1&targetType=Question"
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
│   └── Question.java                # MongoDB document model (@Document, includes viewCount)
├── dto/
│   ├── QuestionRequestDTO.java      # Inbound request shape (validated)
│   └── QuestionResponseDTO.java     # Outbound response shape (includes id)
├── adapter/
│   └── QuestionAdapter.java         # Question → QuestionResponseDTO mapping
├── events/
│   └── ViewCountEvent.java          # Kafka event payload (targetId, targetType, timestamp)
├── producer/
│   └── KafkaEventProducer.java      # Publishes ViewCountEvent to Kafka topic
├── consumers/
│   └── KafkaEventConsumer.java      # @KafkaListener — increments viewCount in MongoDB
├── config/
│   └── KafkaConfig.java             # ProducerFactory, ConsumerFactory, KafkaTemplate, ListenerContainerFactory
└── utils/
    └── CursorUtils.java             # Base64 encode/decode for cursor-based pagination
```

---

## 🤝 Contributing

1. Fork the repo
2. Create your feature branch: `git checkout -b feature/my-feature`
3. Commit your changes: `git commit -m 'Add my feature'`
4. Push to the branch: `git push origin feature/my-feature`
5. Open a Pull Request
