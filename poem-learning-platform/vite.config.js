import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { quasar, transformAssetUrls } from '@quasar/vite-plugin'

export default defineConfig({
  plugins: [
    vue({
      template: { transformAssetUrls }
    }),

    quasar({
    }),
  ],

  server: {
    // hmr: false,
    host: '0.0.0.0',
    port: 5173,
    proxy: {
      "/api": {
        target: "http://localhost:2347",
        changeOrigin: true,
        ws: true,
        rewrite: (path) => path,
      },
    },
  },
  build: {
    sourcemap: true
  },
  
  define: {
    'global': {},
  }
});
