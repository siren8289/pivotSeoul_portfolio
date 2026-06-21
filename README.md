# Pivot Seoul Portfolio

Pivot Seoul portfolio repository trimmed to the core runtime areas:

- `front/` - Next.js frontend
- `back/` - Spring Boot backend
- `ai/` - FastAPI AI pipeline service

Removed from this slim version:

- mobile Expo sample app
- long-form design/ERD docs
- deployment helper scripts
- IDE and workspace metadata
- duplicate backend Gradle wrapper files

## Run with Docker Compose

```bash
docker compose up --build
```

Services:

- Frontend: `http://localhost:3000`
- Backend: `http://localhost:8080`
- AI service: internal `http://ai:8000`, local standalone port `8000` when run from `ai/`

## Local Development

Frontend:

```bash
cd front
npm install
npm run dev
```

Backend:

```bash
./gradlew :back:bootRun
```

AI:

```bash
cd ai
pip install -r requirements.txt
uvicorn main:app --reload --port 8000
```
