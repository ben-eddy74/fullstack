# Frontend Agent Rules (`frontend/`)

## 1. Directory & Scope Isolation

- **Strict Boundary:** Operate ONLY inside `frontend/`. Never create or modify files in `../backend/` during a frontend task.
- **Build Target:** Production builds (`npm run build`) output directly to the backend's static web root as configured in `vite.config.js`. Never alter `outDir` without explicit instruction.
- **API Source of Truth:** Backend OpenAPI schemas are strictly read-only inputs. Run `npm run sync-api` to regenerate `src/api/schema.d.ts` before wiring new endpoints.

## 2. Lit Web Component Architecture ("Black-Box Contract")

Every component must enforce strict Shadow DOM encapsulation so a human reviewer only needs to read the top 30 lines of the file:

1. **Top-of-File Contract Header (Lines 1–30):**
   Every Lit component file MUST place its public contract at the very top of the class before any internal state, styles, or `render()` logic:
   - `@customElement('kebab-case-name')`
   - All public `@property()` inputs with explicit TypeScript types (referencing generated OpenAPI types, never `any`).
   - Explicit TypeScript interfaces for all emitted `CustomEvent<T>` payloads.
   - Global tag registration at the bottom or top of the file:
     ```ts
     declare global {
       interface HTMLElementTagNameMap {
         'kebab-case-name': ComponentClassName;
       }
     }
     ```
2. **Properties Down, Events Up:**
   - Pass data down strictly via typed properties (`.data=${...}`) or primitive attributes.
   - Communicate upward strictly via `this.dispatchEvent(new CustomEvent('event-name', { detail, bubbles: true, composed: true }))`.
3. **Smart vs. Presentational Split (Max 250 Lines):**
   - **Container/View Components (`src/views/` or `*-view.ts`):** Talk to the typed API client, manage top-level state, and orchestrate child components.
   - **Presentational Components (`src/components/`):** Pure UI black boxes. Zero API client imports; driven entirely by `@property()` inputs and `CustomEvent` outputs.
   - **Hard File Size Limit:** Maximum **250 lines** per `.ts` file. If a component exceeds 250 lines, extract child web components or CSS modules immediately.

## 3. Strictly Banned Anti-Patterns (Zero Tolerance)

- **NO React-isms:** Never write functional hooks, JSX, `className`, `onChange`, or React state patterns. Use Lit's `@state()` for private reactive state, `?disabled=${...}` for boolean attributes, `.value=${...}` for properties, and `@click=${...}` for events.
- **NO Shadow DOM Piercing:** Never use `document.querySelector()`, `window` globals, or `.shadowRoot.querySelector()` to reach outside a component or mutate a child component's internals.
- **NO Hand-Written API Types or Raw `fetch()`:** Never manually write interfaces that mirror backend payloads, and never call untyped `fetch('/api/...')` directly inside components. Always use the generated OpenAPI client/types in `src/api/`.
- **NO Type Escapes:** Never use `any`, `@ts-ignore`, or `@ts-expect-error`.
- **NO Imperative DOM Mutation:** Never manually append/remove DOM nodes when Lit declarative templates (`html`, `repeat`, `when`, `classMap`, `styleMap`) can express the state.

## 4. Styling & CSS Encapsulation

- Define component styles inside `static styles = css\`...\`;` (or imported `*.styles.ts` CSSResult modules).
- Use **CSS Custom Properties (`--app-*`)** inherited from `:host` for theming, colors, spacing, and typography—never hardcode magic hex colors if design tokens exist.
- Always style `:host { display: block; }` (or `inline-block`/`flex`/`grid`) explicitly on every custom element.

## 5. Mandatory Verification Loop (Run Before Finishing)

Never report a frontend task as complete until you have executed the verification suite inside `frontend/` and resolved all errors:

```bash
npm run verify
```


