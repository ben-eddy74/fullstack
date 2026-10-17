# Fullstack Application Template

> A modern, type-safe fullstack starter pairing **Quarkus (Java 25 LTS)** and **Vite + Lit Web Components**, architected around Adam Bien's **BCE (Boundary-Control-Entity)** pattern.

---

## Architecture & Features

- **Modern Java 25 LTS Backend**: Powered by Quarkus 3.21, utilizing modern Java features such as record classes, sealed interface hierarchies, exhaustive pattern matching, and compact object headers (JEP 519).
- **Strict BCE Architecture Cage**: Enforces clean business domain slices and layered architecture (`boundary`, `control`, `entity`) mechanically via **ArchUnit** tests.
- **Contract-First API Typing**: SmallRye OpenAPI automatically generates `openapi.yaml`, which syncs directly to frontend TypeScript types via `openapi-typescript`.
- **Lit Web Components Frontend**: Native Web Components leveraging Shadow DOM encapsulation, reactive properties, and a "black-box" contract approach without external framework lock-in.
- **Fast Developer Experience**: Vite 8 dev server with hot module reloading (HMR) and transparent proxying to Quarkus REST endpoints (`/api` and `/q`).
- **Unified Single-Binary Deployment**: Production builds (`npm run build`) compile directly into the Quarkus static web root (`META-INF/resources`), allowing Quarkus to serve both the API and the SPA from a single deployable artifact.
- **Agentic Pairing Ready**: Pre-configured with strict agent rules in [`backend/AGENTS.md`](backend/AGENTS.md) and [`frontend/AGENTS.md`](frontend/AGENTS.md) for human-AI pair programming.

---

## Directory Structure

```text
.
├── .gitignore
├── CHANGELOG.md             # Version history following Keep a Changelog
├── CONTRIBUTING.md          # Development setup, commit standards, and PR flow
├── README.md                # Project overview and quick start guide
├── SCAFFOLD.md              # Template initialization instructions
├── backend/                 # Quarkus Java 25 backend
│   ├── AGENTS.md            # Backend AI agent rules & BCE architecture constraints
│   ├── pom.xml              # Maven project configuration
│   └── src/
│       ├── main/
│       │   ├── java/com/app/health/
│       │   │   ├── boundary/PingResource.java
│       │   │   └── entity/PingResponse.java
│       │   └── resources/application.properties
│       └── test/java/com/app/
│           ├── BceArchitectureTest.java
│           └── health/boundary/PingResourceTest.java
└── frontend/                # Vite + Lit web component frontend
    ├── AGENTS.md            # Frontend AI agent rules & component standards
    ├── index.html           # SPA entry point
    ├── package.json         # NPM scripts and dependencies
    ├── tsconfig.json        # TypeScript configuration (strict mode)
    ├── vite.config.js       # Vite configuration with API proxying
    └── src/
        ├── api/schema.d.ts  # Auto-generated OpenAPI TypeScript schema
        └── app-root.ts      # Root Lit application component
```

---

## Prerequisites

Before running the application, ensure the following are installed:

- **Java**: Java 25 LTS (`java -version`)
- **Maven**: Version 3.9+ (`mvn -version`)
- **Node.js**: Version 20+ LTS (`node -v`) & npm (`npm -v`)

---

## Quick Start

### 1. Start the Backend (Terminal 1)

```bash
cd backend
mvn quarkus:dev
```

Quarkus will start in live coding mode on `http://localhost:8080`. Continuous testing and Dev UI are available at `http://localhost:8080/q/dev`.

### 2. Start the Frontend (Terminal 2)

```bash
cd frontend
npm install
npm run sync-api
npm run dev
```

Open `http://localhost:5173` to view the Lit application communicating with the Quarkus backend.

---

## Common Commands

### Backend (`backend/`)
- `mvn quarkus:dev`: Start live coding with hot reload and continuous testing.
- `mvn test`: Execute JUnit tests and ArchUnit architectural checks (`BceArchitectureTest`).
- `mvn package`: Compile, test, and run Quarkus augmentation to export `target/generated/openapi/openapi.yaml`.
- `mvn versions:display-property-updates`: Check updates for properties.

### Frontend (`frontend/`)
- `npm run dev`: Start Vite dev server on port 5173 with proxying to backend.
- `npm run sync-api`: Regenerate TypeScript interfaces from `openapi.yaml`.
- `npm run verify`: Run strict type checking (`tsc --noEmit`) and Lit template analysis (`lit-analyzer`).
- `npm run build`: Verify and compile production assets into `backend/src/main/resources/META-INF/resources/`.

---

## Quality Gates

Before committing changes, ensure all verification suites pass:

```bash
# Backend checks
cd backend && mvn test

# Frontend checks
cd ../frontend && npm run verify && npm run build
```

---

## Documentation & Community

- [Contributing Guidelines](CONTRIBUTING.md) — Information on reporting issues, coding conventions, and pull request workflows.
- [Changelog](CHANGELOG.md) — Detailed record of notable changes for each release.
- [Scaffolding Guide](SCAFFOLD.md) — Step-by-step instructions for initializing new projects from this template.
- [Backend Agent Guidelines](backend/AGENTS.md) — Architectural rules and Java 25 idioms for backend development.
- [Frontend Agent Guidelines](frontend/AGENTS.md) — Component contracts, styling, and banned anti-patterns for frontend development.

---

## License

This project is licensed under the [MIT License](LICENSE) (or your preferred license).
