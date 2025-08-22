
# 🎓 Academy REST API

A Spring Boot REST API for managing **Courses, Students, and Teachers**.  
The app demonstrates entity relationships, validation, and full CRUD operations and ships with **OpenAPI/Swagger** docs and **integration tests**.

---

## ✨ Features

- Manage **Courses** with types (`MAIN`, `SECONDARY`)
- Manage **Teachers** and assign them to courses (at least one teacher required)
- Manage **Students** with attributes (name, age, group) and course participation
- **Entity relationships** (many-to-many): a course can have multiple teachers and students; teachers and students can participate in multiple courses
- **Filtering & queries**:
  - Students by course, group, or age
  - Teachers by group and course
  - Count teachers/students/courses by criteria
- **Validation** with RFC7807 Problem Details (problem+json) responses
- **OpenAPI 3** docs + **Swagger UI**
- **RestAssured** integration tests for behavior verification

---

## 🛠 Tech Stack

- **Java 21+**
- **Spring Boot** (Web, Data JPA, Validation)
- **Springdoc OpenAPI** (Swagger UI)
- **H2** (in-memory, dev/test)
- **JUnit 5 & RestAssured**
- **Maven/Gradle**

---

## 🚀 Getting Started

```bash
# build & run
./mvnw spring-boot:run
```

API base URL (default): `http://localhost:8080`


## 📖 API Documentation (OpenAPI & Swagger)

Once the app is running, the interactive docs are available at:

- **Swagger UI**:  
  - `http://localhost:8080/swagger-ui/index.html` 

- **OpenAPI JSON**: `http://localhost:8080/api-docs`  
- **OpenAPI YAML**: `http://localhost:8080/api-docs.yaml`

Export the spec to a file:
```bash
curl -s http://localhost:8080/v3/api-docs.yaml -o openapi.yaml
```
---

## 📚 API Endpoints

### Courses
- `POST /courses` — create course (**requires at least one teacher**)
- `GET /courses/{id}` — get by id
- `PUT /courses/{id}` — full update (**must include teachers**)
- `PATCH /courses/{id}` — partial update
- `DELETE /courses/{id}` — delete
- `GET /courses/count?type=MAIN` — count courses by type

### Students
- `POST /students` — create
- `GET /students/{id}` — get by id
- `PUT /students/{id}` — update
- `DELETE /students/{id}` — delete
- `GET /students/count` — count
- Queries:
  - `GET /students/course-participation?courseId=...`
  - `GET /students/group-participation?name=A`
  - `GET /students/age-course-participation?age=27&courseId=...`
  - `GET /students/group-course-participation?group=A&courseId=...`

### Teachers
- `POST /teachers` — create
- `GET /teachers/{id}` — get by id
- `PUT /teachers/{id}` — update
- `DELETE /teachers/{id}` — delete
- `GET /teachers/count` — count
- Queries:
  - `GET /teachers/group-course-participation?group=A&courseId=...`

---

## ✅ Request/Response Examples

### Create Student
```http
POST /students
Content-Type: application/json

{
  "name": "Ivo",
  "age": 28,
  "group": "A",
  "courses": ["<course-uuid>"]
}
```

Response `201 Created`:
```json
{
  "id": "<student-uuid>",
  "name": "Ivo",
  "age": 28,
  "group": "A",
  "courses": ["<course-uuid>"]
}
```

### Validation Error (Problem+JSON)
```http
POST /students
Content-Type: application/json

{
  "name": null,
  "age": null,
  "group": null,
  "courses": []
}
```

Response `400 Bad Request`:
```json
{
  "title": "Validation failed",
  "type": "https://example.com/problems/validation-error",
  "errors": {
    "name": "Student name must not be blank",
    "age": "Student age is required",
    "group": "Student group must not be blank"
  }
}
```

---

## 🔍 Tests

Integration tests (JUnit 5 + RestAssured) cover:
- CRUD for Courses, Students, Teachers
- Relationship updates (add/remove students & teachers)
- Query endpoints and counters
- Validation/Problem Details

Run tests:
```bash
./mvnw test
```

---

## 🧩 Project Structure (high level)

```
src/
 ├─ main/
 │   ├─ java/com/sub/academy/...
 │   └─ resources/
 └─ test/
     └─ java/com/sub/academy/AcademyApplicationTests.java   # RestAssured integration tests
```

---

