import { defineConfig } from "vite";

export default defineConfig({
  build: {
    // Outputs directly into Quarkus static web root (change for Python if needed)
    outDir: "../backend/src/main/resources/META-INF/resources",
    emptyOutDir: true,
    target: "esnext",
  },
  server: {
    port: 5173,
    strictPort: true,
    // Vite 8+: Forwards browser console errors to terminal for AI agents
    forwardConsole: true,
    proxy: {
      "/api": {
        target: "http://localhost:8080",
        changeOrigin: true,
      },
      "/q": {
        target: "http://localhost:8080",
        changeOrigin: true,
      },
    },
  },
});
