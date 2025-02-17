import { defineConfig } from 'vite';
import { sveltekit } from '@sveltejs/kit/vite';
import UnoCSS from 'unocss/vite';
import unoConfig from './uno.config.js';

export default defineConfig({
  plugins: [sveltekit(), UnoCSS(unoConfig)],
});