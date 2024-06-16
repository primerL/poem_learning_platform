import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { quasar, transformAssetUrls } from '@quasar/vite-plugin'
import { viteStaticCopy } from 'vite-plugin-static-copy';

export default defineConfig({
  plugins: [
    vue({
      template: { transformAssetUrls }
    }),

    quasar({
    }),

    viteStaticCopy({
      targets: [
        {
          src: 'src/assets/', // 指定源文件目录
          dest: 'src/'      // 指定构建输出目录
        },
        {
          src: 'src/scripts/', // 源文件路径
          dest: 'src/'                      // 目标路径
        },
      ]
    })
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
    assetsDir: 'assets', // 指定构建输出的静态资源目录名
    rollupOptions: {
      output: {
        assetFileNames: '[name].[ext]', // 保持默认输出文件名格式
      },
    },
  },
  
  define: {
    'global': {},
  }
});
