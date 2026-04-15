import { http } from "./client";
import type { ApiEnvelope, LoginResponse } from "@/types/api";

export async function login(email: string, password: string): Promise<LoginResponse> {
  const response = await http.post<ApiEnvelope<LoginResponse>>("/api/v1/auth/login", {
    email,
    password
  });
  return response.data.data;
}
