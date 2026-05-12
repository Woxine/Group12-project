import axios from "axios";

/** API 基础地址，生产环境由 Vite 环境变量覆盖 / API base URL, overridden by the Vite environment variable in production. */
const baseURL = import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080";

/** 共享 Axios 实例，统一超时和拦截器 / Shared Axios instance with common timeout and interceptors. */
export const http = axios.create({
  baseURL,
  timeout: 15000
});

/** 为每个请求附加管理员令牌 / Attach the admin token to every outgoing request. */
http.interceptors.request.use((config) => {
  const token = localStorage.getItem("admin_token");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

/** 处理认证失效并跳转登录页 / Handle expired authentication and redirect to the login page. */
http.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error?.response?.status === 401) {
      localStorage.removeItem("admin_token");
      localStorage.removeItem("admin_role");
      localStorage.removeItem("admin_name");
      localStorage.removeItem("admin_user_id");
      if (window.location.pathname !== "/login") {
        window.location.href = "/login";
      }
    }
    return Promise.reject(error);
  }
);
