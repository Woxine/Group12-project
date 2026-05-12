import { createPinia } from "pinia";
import ElementPlus from "element-plus";
import "element-plus/dist/index.css";
import { createApp } from "vue";

import App from "./App.vue";
import router from "./router";
import "./styles.css";

/** 应用入口：注册 Pinia、路由和 Element Plus 后挂载到页面 / App entry: register Pinia, router, and Element Plus before mounting. */
const app = createApp(App);
app.use(createPinia());
app.use(router);
app.use(ElementPlus);
app.mount("#app");
