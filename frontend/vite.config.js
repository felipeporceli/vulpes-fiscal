import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/oauth2': { target: 'http://localhost:8080', changeOrigin: true },
      '/api':    { target: 'http://localhost:8080', changeOrigin: true },
      '^/(empresas|estabelecimentos|produtos|consumidores|vendas|pagamentos|usuarios|nfce|clientes|empresa|produto-tributacao)': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
});
