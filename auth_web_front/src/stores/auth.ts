import { defineStore } from "pinia";
import { computed, ref } from "vue";

import { login } from "@/api/auth";

/** 本地存储键名，保持刷新后管理员会话可恢复 / Local storage keys used to restore the admin session after refresh. */
const TOKEN_KEY = "admin_token";
const ROLE_KEY = "admin_role";
const NAME_KEY = "admin_name";
const USER_ID_KEY = "admin_user_id";

/** 管理端认证状态 Store / Admin authentication state store. */
export const useAuthStore = defineStore("auth", () => {
  /** 从 localStorage 初始化会话状态 / Initialize session state from localStorage. */
  const token = ref(localStorage.getItem(TOKEN_KEY) ?? "");
  const role = ref(localStorage.getItem(ROLE_KEY) ?? "");
  const name = ref(localStorage.getItem(NAME_KEY) ?? "");
  const userId = ref(localStorage.getItem(USER_ID_KEY) ?? "");

  /** 路由守卫使用的认证和管理员角色判断 / Authenticated and admin-role checks used by route guards. */
  const isAuthenticated = computed(() => token.value.length > 0);
  const isAdmin = computed(() => role.value.toUpperCase() === "ADMIN");

  /** 登录成功后同步内存状态与本地存储 / Sync in-memory state and localStorage after successful login. */
  async function signIn(email: string, password: string) {
    const result = await login(email, password);
    token.value = result.token;
    role.value = result.role;
    name.value = result.name;
    userId.value = result.userId;

    localStorage.setItem(TOKEN_KEY, token.value);
    localStorage.setItem(ROLE_KEY, role.value);
    localStorage.setItem(NAME_KEY, name.value);
    localStorage.setItem(USER_ID_KEY, userId.value);
  }

  /** 登出时清空所有管理员会话信息 / Clear all admin session data on sign-out. */
  function signOut() {
    token.value = "";
    role.value = "";
    name.value = "";
    userId.value = "";
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(ROLE_KEY);
    localStorage.removeItem(NAME_KEY);
    localStorage.removeItem(USER_ID_KEY);
  }

  return {
    token,
    role,
    name,
    userId,
    isAuthenticated,
    isAdmin,
    signIn,
    signOut
  };
});
