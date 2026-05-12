import { http } from "./client";
import type { ApiEnvelope, LoginResponse } from "@/types/api";

/** 认证相关接口 / Authentication APIs. */
/** 管理员登录并返回认证信息 / Log in an admin user and return authentication details. */
export async function login(email: string, password: string): Promise<LoginResponse> {
  const response = await http.post<ApiEnvelope<LoginResponse>>("/api/v1/auth/login", {
    email,
    password
  });
  return response.data.data;
}
