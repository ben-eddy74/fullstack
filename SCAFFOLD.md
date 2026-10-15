# Project Initialization Guide (Quarkus Java 25 + Vite/Lit BCE)

Follow these steps when copying this template skeleton into a brand-new project folder.

---

## 1. Directory Structure Check

Ensure your new project folder matches this layout before running any commands:

```text
my-new-project/
├── .gitignore
├── CHANGELOG.md
├── CONTRIBUTING.md
├── README.md
├── backend/
│   ├── AGENTS.md
│   ├── pom.xml
│   └── src/
│       ├── main/
│       │   ├── java/com/app/health/
│       │   │   ├── boundary/PingResource.java
│       │   │   └── entity/PingResponse.java
│       │   └── resources/application.properties
│       └── test/java/com/app/
│           ├── BceArchitectureTest.java
│           └── health/boundary/PingResourceTest.java
└── frontend/
    ├── AGENTS.md
    ├── index.html
    ├── package.json
    ├── tsconfig.json
    ├── vite.config.js
    └── src/
        ├── api/schema.d.ts
        └── app-root.ts
```


## 2. Initialize the Frontend

Open a terminal in the frontend/ directory and install dependencies:

```bash
cd frontend
npm install
```

Useful Frontend Commands:

```npm run dev```: Starts the Vite dev server with hot reload and API proxying to Quarkus on port 8080.
```npm run sync-api```: Regenerates src/api/schema.d.ts from the backend's OpenAPI export.
```npm run verify```: Runs TypeScript type checking (tsc --noEmit) and Lit template analysis (lit-analyzer).
```npm run build```: Runs verification and compiles the production bundle directly into backend/src/main/resources/META-INF/resources/.

## 3. Initialize the Backend

Open a terminal in the backend/ directory. Ensure you have Java 25 LTS and Maven installed.

```bash
cd backend
```

Useful Backend Commands:

```mvn quarkus:dev```: Starts Quarkus in Live Coding mode with continuous testing enabled. Triggers SmallRye OpenAPI augmentation and updates `backend/target/generated/openapi/openapi.yaml`.
```mvn test```: Runs all JUnit tests and the ArchUnit mechanical architecture checks (BceArchitectureTest.java).
```mvn package```: Compiles, runs tests, and runs Quarkus augmentation to generate/update `backend/target/generated/openapi/openapi.yaml`.

## 4. First-Time End-to-End Workflow Test

Start the backend in terminal 1 (starting `quarkus:dev` or running `mvn package` generates the `openapi.yaml` required by the frontend):

```bash
cd backend && mvn quarkus:dev
```

In a second terminal (once Quarkus has initialized and generated the OpenAPI spec), sync the API and start the frontend dev server:

```bash
cd frontend
npm run sync-api
npm run dev
```

Open http://localhost:5173 to see your Lit web component communicating with Quarkus.
