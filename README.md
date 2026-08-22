# AI Risk Tracker

**AI-Assisted Software Project Risk Prediction and Mitigation System**

A full-stack web application for tracking software project risk: a Spring Boot 3 / Java 17 REST API
backend with a JPA-backed database, and a static HTML/CSS/JS frontend (Bootstrap 5 + Chart.js).

---

## 1. What this project demonstrates

| Area | What it shows |
|---|---|
| **REST API design** | 7 Spring `@RestController` classes (`Auth`, `Project`, `Risk`, `Resource`, `User`, `Analytics`, `Report`) with a consistent `ApiResponse<T>` envelope and a `@RestControllerAdvice` global exception handler |
| **Persistence** | Spring Data JPA + Hibernate, 4 entities (`Project`, `Risk`, `Resource`, `User`) with relationships, bean validation (`@NotBlank`, `@Min/@Max`, etc.) |
| **Deterministic business logic** | Risk score = `probability x impact` (1-25), classified LOW/MEDIUM/HIGH. Project health score is a weighted formula based on open high risks, budget overrun, and schedule delay. Resource utilization = `assignedHours / availableHours`. All calculated in `@PrePersist`/`@PreUpdate` lifecycle hooks on the entities |
| **A simple, explainable "ML" model** | `RiskPredictionService` scores a project 0-100 from four weighted factors (budget utilization, completion vs. timeline, high-risk count, average risk score) and returns a LOW/MEDIUM/HIGH prediction with a human-readable explanation - a transparent rule-based model, not a black box |
| **Local AI integration (optional)** | `OllamaService` calls a locally-running Ollama instance to generate natural-language mitigation recommendations for each risk. The app is written to degrade gracefully - if Ollama isn't running, risks still get created and deterministic scoring is unaffected |
| **PDF report generation** | `ReportService` uses OpenPDF to generate 5 different downloadable reports (project report, executive summary, risk assessment, resource utilization, financial analysis) |
| **Frontend** | 8 pages (Dashboard, Projects, Risks, Resources, Users, Analytics, Reports, Login) all calling the live REST API with `fetch()`, Chart.js visualizations, and a Bootstrap-based responsive layout |

---

## 2. Quick start (no installation needed except Java + Maven)

The database is **embedded H2** - it creates itself as a file the first time you run the app.
There is nothing to install, no MySQL server to configure, and no passwords to set up.

### Requirements
- **Java 17+** (`java -version` to check)
- **Maven 3.8+** (`mvn -version` to check) - if you don't have Maven, install it or use an IDE
  (IntelliJ / VS Code) that bundles it and just run `RiskTrackerApplication.java` directly.

### Run it
```bash
cd backend
mvn spring-boot:run
```
or double-click **`start-backend.bat`** (Windows) / run **`./start-backend.sh`** (Mac/Linux) from the project root.

The first run downloads dependencies (needs internet, one time only) and takes a couple of minutes.
You'll see:
```
AI Risk Tracker Application Started
Server: http://localhost:8080
```
Sample data (3 users, 3 projects, 6 risks, 5 resources) is inserted automatically on first startup.

### Open the frontend
Open **`frontend/index.html`** directly in your browser (double-click it, or right-click ->
"Open with" your browser). It redirects to the login page automatically. No web server needed -
the pages are static files that call the backend API on `localhost:8080`.

### Log in
| Username | Password | Role |
|---|---|---|
| `admin` | `admin123` | ADMIN |
| `manager` | `manager123` | MANAGER |
| `user` | `user123` | USER |

---

## 3. Feature walkthrough (for demoing to an interviewer)

1. **Login** -> authenticates against `POST /api/auth/login`, backend checks the username/password
   against the H2 database and returns a session token stored in `localStorage`.
2. **Dashboard** -> `GET /api/analytics/dashboard` returns KPIs (total/active projects, high risks,
   average risk score, project health, resource & budget utilization) plus two Chart.js charts
   (risk distribution, risk by category).
3. **Projects** -> full CRUD against `/api/projects`. Creating/editing a project recalculates its
   health score server-side. Click "Predict Risk" on a project to call
   `GET /api/projects/{id}/predict-risk` and see the explainable ML prediction with its reasoning.
4. **Risks** -> full CRUD against `/api/risks`, scoped to a project. Risk score and level are
   computed automatically from probability x impact. Click "Regenerate AI" to call Ollama (if
   running) for a fresh mitigation recommendation.
5. **Resources** -> full CRUD against `/api/resources`. Utilization % and status
   (underutilized/optimal/high/overloaded) are computed automatically from assigned vs. available hours.
6. **User Management** -> full CRUD against `/api/users` (admin/manager only).
7. **Analytics** -> deeper charts and per-project risk predictions in one view.
8. **Reports** -> downloads real PDF files generated server-side with OpenPDF (executive summary,
   risk assessment, resource utilization, financial analysis, per-project report).

### Optional: enable the local AI feature
This is genuinely optional - the app works fully without it, since every AI call is wrapped in a
try/catch that falls back to a plain message.
```bash
# Install Ollama from https://ollama.com, then:
ollama pull llama3
ollama serve
```
With Ollama running, creating or regenerating a risk's AI recommendation will call it for a real,
locally-generated mitigation suggestion.

### Optional: browse the database directly
While the backend is running, open **http://localhost:8080/h2-console** and use JDBC URL
`jdbc:h2:file:./data/risktracker` (username `sa`, no password) to see the live tables - a nice
thing to show an interviewer who asks "how do I know it's really hitting a database?"

---

## 4. Project structure

```
ai-risk-tracker/
|-- backend/                     Spring Boot 3 / Java 17 REST API
|   `-- src/main/java/com/risktracker/
|       |-- controller/          REST endpoints
|       |-- service/             Business logic (scoring, prediction, reports)
|       |-- ai/                  Ollama integration
|       |-- model/               JPA entities
|       |-- repository/          Spring Data repositories
|       |-- dto/                 Request/response objects
|       |-- exception/           Global exception handling
|       `-- config/DataInitializer.java   Seeds sample data on first run
|-- frontend/                    Static HTML/CSS/JS (no build step)
|   |-- index.html, login.html
|   |-- dashboard.html/js, projects.html/js, risks.html/js,
|   |   resources.html/js, users.html/js, analytics.html/js, reports.html/js
|   `-- auth.js                  Shared session/auth helpers
|-- start-backend.bat / .sh
`-- README.md
```

## 5. Switching to MySQL instead of H2
The app defaults to H2 so it runs with zero setup. If you'd rather use MySQL, open
`backend/src/main/resources/application.properties` - the file has a commented MySQL block with
instructions right below the active H2 config.

## 6. Notes for the interview
- Passwords are stored in plain text and login uses a simple UUID token - the code comments this
  explicitly as a simplification appropriate for a student/demo project, and calls out that a real
  system would use BCrypt + JWT. Good to mention proactively if asked about security.
- The "ML" risk prediction is a transparent, rule-based weighted-scoring model - not a trained
  model. This is called out in `RiskPredictionService`'s Javadoc and is worth explaining honestly:
  it's explainable-by-design rather than a black box, which is a reasonable trade-off to describe.
