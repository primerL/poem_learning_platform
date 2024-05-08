// FILE: vite.config.js

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { quasar, transformAssetUrls } from '@quasar/vite-plugin'

// https://vitejs.dev/config/
export default defineConfig({
<<<<<<< HEAD
  plugins: [
    vue({
      template: { transformAssetUrls }
    }),

    quasar({
    })
  ]
})
=======
    plugins: [vue()],
    server: {
        hmr: false
    },
    build: {
        sourcemap: true
    }
});
>>>>>>> 98fc620b86018128c8abd0215b208bdf39f732b0
