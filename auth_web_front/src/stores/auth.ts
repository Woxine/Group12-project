import { defineStore } from "pinia";
import { computed, ref } from "vue";

import { login } from "@/api/auth";

const TOKEN_KEY = "admin_token";
const ROLE_KEY = "admin_role";
const NAME_KEY = "admin_name";
const USER_ID_KEY = "admin_user_id";

export const useAuthStore = defineStore("auth", () => {
  const token = ref(localStorage.getItem(TOKEN_KEY) ?? "");
  const role = ref(localStorage.getItem(ROLE_KEY) ?? "");
  const name = ref(localStorage.getItem(NAME_KEY) ?? "");
  const userId = ref(localStorage.getItem(USER_ID_KEY) ?? "");

  const isAuthenticated = computed(() => token.value.length > 0);
  const isAdmin = computed(() => role.value.toUpperCase() === "ADMIN");

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
