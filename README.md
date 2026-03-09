# Campaign Manager

A full-stack application for managing advertising campaigns, built as a recruitment task.

## Tech Stack

**Backend:**
- Java 21
- Spring Boot 4
- Spring Data JPA
- H2 in-memory database
- Lombok
- Springdoc OpenAPI (Swagger)

**Frontend:**
- React 19
- Vite
- Axios
- React Select / Creatable Select
- React Hot Toast

**DevOps:**
- Docker & Docker Compose

## Features

- Full CRUD for advertising campaigns
- Emerald account balance management (deducted when creating/updating campaigns)
- Keyword typeahead with ability to add custom keywords
- Pre-populated town dropdown
- Form validation on both frontend and backend
- Toast notifications
- Live balance preview when entering campaign fund

## How to Run

### Option 1 — Docker (recommended)
```bash
docker compose up --build
```

- Frontend: http://localhost
- Backend: http://localhost:8080
- Swagger: http://localhost:8080/swagger-ui/index.html
- H2 Console: http://localhost:8080/h2-console (local only)

### Option 2 — Local development

**Backend:**
```bash
cd backend
./mvnw spring-boot:run
```

**Frontend:**
```bash
cd frontend
npm install
npm run dev
```

Frontend runs on http://localhost:5173

## Live Demo

- Frontend: https://futurum-tech-interview-1.onrender.com
- Backend API: https://futurum-tech-interview.onrender.com
- Swagger: https://futurum-tech-interview.onrender.com/swagger-ui/index.html

> **Note:** The application uses H2 in-memory database.
> All campaign data is reset on every restart.
> Render free tier sleeps after 15 minutes of inactivity —
> first load may take up to 60 seconds.

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/campaigns | Get all campaigns |
| GET | /api/campaigns/{id} | Get campaign by ID |
| POST | /api/campaigns | Create campaign |
| PUT | /api/campaigns/{id} | Update campaign |
| DELETE | /api/campaigns/{id} | Delete campaign |
| GET | /api/sellers | Get seller with balance |
| GET | /api/keywords?query= | Search keywords |
| GET | /api/towns | Get list of towns |

## Campaign Fields

| Field | Required | Description |
|-------|----------|-------------|
| name | ✅ | Campaign name |
| keywords | ✅ | Min 1 keyword, typeahead supported |
| bidAmount | ✅ | Min 0.01 |
| campaignFund | ✅ | Deducted from Emerald balance |
| status | ✅ | ON / OFF |
| town | ❌ | Selected from dropdown |
| radius | ✅ | In kilometres, min 1, max 500 |
