# Backend Agent Rules (`backend/`)

## 1. Directory & Scope Isolation

- **Strict Boundary:** Operate ONLY inside `backend/`. Never create or modify files in `../frontend/` during a backend task.
- **Static Web Assets:** Do not manually edit files inside `src/main/resources/META-INF/resources/`—that directory is managed and overwritten by the frontend Vite build.
- **OpenAPI Contract Output:** Packaging the backend (`mvn package`) or running dev mode (`mvn quarkus:dev`) triggers SmallRye OpenAPI augmentation to generate `target/generated/openapi/openapi.yaml`. Note that `mvn compile` or unit tests alone do not generate this file. Keep all REST endpoints and payload records strictly typed so this contract remains accurate.

## 2. Architecture: Strict BCE (Boundary-Control-Entity)

Organize all code by **business domain slice first**, then strictly into three sub-packages: `com.app.<domain>.{boundary, control, entity}`.

- **`boundary`**: External facade (JAX-RS `@Path` REST resources, top-level `@Transactional` boundaries, `@RolesAllowed`).
  - *Rules:* Keep thin. Delegate all business logic and orchestration to `control`. Never inject another domain's `control`—only communicate across domains via their `boundary` or shared events.
- **`control`**: Procedural business logic, domain orchestration, and integration logic.
  - *Rules:* Annotate with `@ApplicationScoped` (or `@Dependent`). Accessible ONLY by `boundary` and `control` classes within the **same** `<domain>` slice (enforced by ArchUnit).
- **`entity`**: Domain state, JPA/Panache entities, immutable Java `record` DTOs, and `sealed interface` hierarchies.
  - *Rules:* Zero dependencies on `control` or `boundary`. Annotate API payload records with Jakarta Bean Validation (`@NotNull`, `@NotBlank`, `@Valid`) and MicroProfile OpenAPI `@Schema` where helpful.
- **Strictly Banned Structural Anti-Patterns:**
  - Never create top-level technical packages (`controllers/`, `services/`, `repositories/`, `dtos/`, `mappers/`).
  - Never create `*Impl` interfaces for single-implementation `control` classes.
  - Never create `PanacheRepository` classes—either use Active Record methods on `PanacheEntity` inside `entity` or inject `EntityManager` directly inside `control`.

## 3. Quarkus & Jakarta EE Standards

- **Namespaces:** STRICTLY use `jakarta.*` and `org.eclipse.microprofile.*`. Never import `javax.*` or `org.springframework.*`.
- **REST Stack:** Use Quarkus REST (`quarkus-rest` / `quarkus-rest-jackson`). Prefer constructor injection (no `@Inject` needed on a single constructor) over field injection.
- **Concurrency:** Prefer imperative blocking code annotated with `@RunOnVirtualThread` on the `boundary` method over complex Mutiny `Uni<T>` / `Multi<T>` reactive chains unless streaming (`Server-Sent Events` / WebSockets) is explicitly required.

## 4. Java 25 LTS Idioms (Mandatory)

Target **Java 25 LTS** (`<maven.compiler.release>25</maven.compiler.release>`). Never write legacy Java 8/11/17 boilerplate:
1. **Exhaustive Pattern Matching:** Model domain states/outcomes with `sealed interface` and `record` implementations in `entity`. Process them in `control` using exhaustive `switch` expressions with record pattern deconstruction (`case PaidInvoice(var id, var amount) when amount > 0 -> ...`). **Never include a `default` branch** on a `sealed interface` switch so the compiler catches unhandled states.
2. **Flexible Constructor Bodies (JEP 513):** Validate arguments and normalize fields directly inside constructors *before* `super(...)` or `this(...)` calls instead of writing static factory workarounds.
3. **Unnamed Variables & Patterns (`_`):** Always use `_` for unused lambda parameters, ignored catch blocks, or unused record components in deconstruction patterns.
4. **Scoped Values (JEP 506):** Never use `ThreadLocal`; use `ScopedValue<T>` for thread-scoped context sharing across virtual threads.
5. **Sequenced Collections & Stream Gatherers (JEP 485):** Use `.getFirst()`, `.getLast()`, `.reversed()`, and `Stream.gather(Gatherers.*)` instead of manual index math or custom chunking loops.

## 5. 2-Phase Execution Gate & Mandatory Verification

1. **Phase 1 (Contract & Spine):** When implementing a new feature, create/update `entity` records/classes, `boundary` method signatures, and the `@QuarkusTest` test stubs first. Pause for human review when requested before writing heavy `control` logic.
2. **Phase 2 (Implementation & Gate):** Never report a backend task as complete until you have executed:
   ```bash
   mvn test
   ```
   - All JUnit `@QuarkusTest` suites and `BceArchitectureTest` (ArchUnit) MUST pass with zero errors.
   - Verify that `target/generated/openapi/openapi.yaml` was cleanly generated so the frontend can sync against it.
