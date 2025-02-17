// uno.config.js
import { defineConfig, presetUno, presetAttributify, presetIcons } from 'unocss';

export default defineConfig({
  include: [
    /\.svelte$/,
    /\.vue$/,
    /\.ts$/,
    /\.js$/,
    /\.tsx$/,
    /\.jsx$/,
  ],
  presets: [
    presetUno(),
    presetAttributify(),
    presetIcons(),
  ],
});
