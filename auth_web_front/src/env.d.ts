/// <reference types="vite/client" />

/** 允许 TypeScript 识别单文件组件导入 / Let TypeScript understand single-file component imports. */
declare module "*.vue" {
  import type { DefineComponent } from "vue";
  const component: DefineComponent<Record<string, unknown>, Record<string, unknown>, any>;
  export default component;
}
