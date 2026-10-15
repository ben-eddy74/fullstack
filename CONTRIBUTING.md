# Contributing Guidelines

Thank you for considering contributing to this project! We welcome contributions from everyone—whether you are fixing a bug, improving documentation, or proposing new features.

---

## Table of Contents

- [Code of Conduct](#code-of-conduct)
- [How to Contribute](#how-to-contribute)
  - [Reporting Bugs](#reporting-bugs)
  - [Suggesting Enhancements](#suggesting-enhancements)
  - [Pull Requests](#pull-requests)
- [Development Setup & Prerequisites](#development-setup--prerequisites)
- [Architecture & Coding Standards](#architecture--coding-standards)
  - [Backend Standards (Quarkus & Java 25)](#backend-standards-quarkus--java-25)
  - [Frontend Standards (Vite & Lit)](#frontend-standards-vite--lit)
- [Commit Message Guidelines](#commit-message-guidelines)
- [Pre-Submission Checklist](#pre-submission-checklist)

---

## Code of Conduct

We are committed to providing a friendly, safe, and welcoming environment for all contributors. Please treat fellow contributors with respect, empathy, and professional courtesy at all times.

---

## How to Contribute

### Reporting Bugs

Before creating a bug report, please check existing issues to ensure the problem has not already been reported.

When opening an issue, include:
- A clear, descriptive title.
- Steps to reproduce the issue.
- Expected versus actual behavior.
- Relevant log output or terminal traces.
- Your environment details (OS, Java version, Node version, browser).

### Suggesting Enhancements

Feature requests and architectural proposals are encouraged:
- Explain **why** the enhancement is needed and **what** problem it solves.
- Provide concrete examples, mockups, or API sketches where applicable.
- For architectural changes, discuss your design before writing extensive code.

### Pull Requests

1. **Fork & Branch:** Create a dedicated branch from `main`:
   ```bash
   git checkout -b feature/your-feature-name
   # or
   git checkout -b fix/your-bug-fix
   ```
2. **Keep PRs Focused:** Limit each pull request to a single concern or feature.
3. **Update Documentation:** If you add or modify functionality, update relevant documentation, rulesets, and [CHANGELOG.md](CHANGELOG.md) under `[Unreleased]`.
4. **Pass All Quality Gates:** Ensure all backend and frontend tests pass before requesting review.

---

## Development Setup & Prerequisites

Ensure the following tools are installed on your machine:
- **Java**: Java 25 LTS
- **Maven**: Version 3.9+
- **Node.js**: Version 20+ (LTS) & npm

Refer to [SCAFFOLD.md](SCAFFOLD.md) for detailed step-by-step instructions on setting up and running the backend and frontend dev servers.

---

## Architecture & Coding Standards

This project enforces mechanical architecture boundaries and strict coding standards.

### Backend Standards (Quarkus & Java 25)
- **BCE Pattern:** Code is organized by domain slice first, then into `boundary`, `control`, and `entity` sub-packages (`com.app.<domain>.{boundary, control, entity}`).
- **Mechanical Cage:** All dependencies are verified mechanically by **ArchUnit** in `BceArchitectureTest.java`. Violations will fail the build.
- **Java 25 Idioms:** Use modern Java features: `record` classes, `sealed interface` hierarchies, exhaustive pattern matching expressions, flexible constructor bodies, and scoped values where appropriate.
- Detailed rules: Refer to [backend/AGENTS.md](backend/AGENTS.md).

### Frontend Standards (Vite & Lit)
- **Web Standards First:** Built using native Web Components via Lit. No React-isms or external state bloat.
- **Black-Box Contracts:** Strict Shadow DOM encapsulation. Components expose a clear public contract at the top of the file (typed `@property` inputs and `CustomEvent` outputs).
- **Type-Safe API Client:** Never write manual payload interfaces or make untyped `fetch` calls. Regenerate `src/api/schema.d.ts` via `npm run sync-api`.
- Detailed rules: Refer to [frontend/AGENTS.md](frontend/AGENTS.md).

---

## Commit Message Guidelines

We follow the [Conventional Commits](https://www.conventionalcommits.org/) specification:

```text
<type>(<scope>): <short description>

[optional body]

[optional footer(s)]
```

### Common Types
- `feat`: A new user-facing feature.
- `fix`: A bug fix.
- `docs`: Documentation updates only.
- `refactor`: Code changes that neither fix a bug nor add a feature.
- `test`: Adding or correcting tests.
- `chore`: Build tasks, dependency updates, or tool configurations.

*Examples:*
- `feat(health): add ping latency metrics to PingResponse`
- `fix(frontend): handle network disconnection gracefully in app-root`
- `docs: update setup instructions in SCAFFOLD.md`

---

## Pre-Submission Checklist

Before submitting your pull request, execute the following commands locally:

1. **Verify Backend:**
   ```bash
   cd backend
   mvn test
   ```
   *All unit tests, integration tests, and ArchUnit architecture tests must pass.*

2. **Verify Frontend:**
   ```bash
   cd frontend
   npm run verify
   npm run build
   ```
   *TypeScript checks, lit-analyzer rules, and the Vite production build must complete with zero errors.*

3. **Update Changelog:**
   Add a summary of your changes to the `[Unreleased]` section of [CHANGELOG.md](CHANGELOG.md).
