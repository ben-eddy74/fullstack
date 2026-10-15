import { LitElement, html, css } from "lit";
import { customElement, state } from "lit/decorators.js";
import type { components } from "./api/schema";

type PingResponse = components["schemas"]["PingResponse"];

@customElement("app-root")
export class AppRoot extends LitElement {
  // Public Contract / Header
  // (Container component orchestrating initial backend connectivity)

  // Internal reactive state
  @state() private pingData: PingResponse | null = null;
  @state() private error: string | null = null;
  @state() private loading: boolean = false;

  static override styles = css`
    :host {
      display: block;
      font-family:
        system-ui,
        -apple-system,
        sans-serif;
      margin: 0;
      padding: 1.5rem;
      color: #1a1a1a;
    }
    .status-badge {
      display: inline-block;
      padding: 0.25rem 0.6rem;
      border-radius: 4px;
      font-weight: 600;
      font-size: 0.875rem;
      background: #e6f4ea;
      color: #137333;
    }
    .status-badge.error {
      background: #fce8e6;
      color: #c5221f;
    }
    .meta {
      margin-top: 0.5rem;
      color: #5f6368;
      font-size: 0.875rem;
    }
  `;

  override connectedCallback(): void {
    super.connectedCallback();
    void this.fetchPing();
  }

  async fetchPing(): Promise<void> {
    this.loading = true;
    this.error = null;
    try {
      const response = await fetch("/api/ping");
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }
      this.pingData = (await response.json()) as PingResponse;
    } catch (err) {
      this.error = err instanceof Error ? err.message : String(err);
    } finally {
      this.loading = false;
    }
  }

  override render() {
    return html`
      <main>
        <h1>App Ready</h1>
        ${this.loading ? html`<p>Connecting to backend...</p>` : ""}
        ${this.error
          ? html`
              <p>
                <span class="status-badge error">Backend Error</span>
              </p>
              <div class="meta">${this.error}</div>
            `
          : ""}
        ${this.pingData
          ? html`
              <p>
                <span class="status-badge">Backend Connected: ${this.pingData.status}</span>
              </p>
              <div class="meta">Server Time: ${this.pingData.timestamp}</div>
            `
          : ""}
      </main>
    `;
  }
}

declare global {
  interface HTMLElementTagNameMap {
    "app-root": AppRoot;
  }
}
