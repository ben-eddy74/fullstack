# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added

### Changed

### Deprecated

### Removed

### Fixed

### Security

## [0.1.0] - 2026-10-15

### Added
- Backend skeleton powered by **Quarkus 3.21** and **Java 25 LTS**.
- Strict BCE (Boundary-Control-Entity) mechanical layering enforced by **ArchUnit** (`BceArchitectureTest`).
- SmallRye OpenAPI auto-exporting `target/generated/openapi/openapi.yaml` during packaging and live reload.
- Minimal `/api/ping` health endpoint (`PingResource` boundary, `PingResponse` entity record) and integration test (`PingResourceTest`).
- Frontend skeleton using **Lit 3** web components, TypeScript strict mode, and **Vite 8**.
- End-to-end API type synchronization from backend OpenAPI specs via `openapi-typescript`.
- Automated production build pipeline routing frontend bundles directly into Quarkus `META-INF/resources`.
- Static analysis verification suites (`tsc --noEmit` and `lit-analyzer --strict`).
- Autonomous agent rulesets (`backend/AGENTS.md` and `frontend/AGENTS.md`).
- Project initialization and scaffold guide (`SCAFFOLD.md`).
- Root `.gitignore` for Quarkus, Maven, Node, and IDE artifacts.
