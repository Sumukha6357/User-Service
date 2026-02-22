export type ApiResponse<T> = {
  statusCode: number;
  message: string;
  data: T;
};

export type ErrorResponse = {
  statusCode: number;
  errorCode: string;
  message: string;
  path: string;
  timestamp: string;
  validationErrors?: Record<string, string>;
};

export type LoginRequest = {
  username: string;
  password: string;
};

export type LoginData = {
  accessToken: string;
  tokenType: string;
  expiresInSeconds: number;
  role: "ADMIN" | "VIEWER";
};

export type User = {
  userId: number;
  userName: string;
  age: number;
  gender: string;
  createdAt: string;
  updatedAt: string;
};

export type UserPayload = {
  userName: string;
  age: number;
  gender: string;
};

export type UserPage = {
  items: User[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  hasNext: boolean;
};

const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL?.trim() || "http://localhost:8080";

async function parseBody(response: Response) {
  const text = await response.text();
  if (!text) return undefined;
  try {
    return JSON.parse(text);
  } catch {
    return undefined;
  }
}

async function request<T>(
  path: string,
  options?: RequestInit,
  token?: string
): Promise<{ ok: true; data: ApiResponse<T> } | { ok: false; error: ErrorResponse | { message: string } }> {
  const headers = new Headers(options?.headers);
  headers.set("Content-Type", "application/json");

  if (token) {
    headers.set("Authorization", `Bearer ${token}`);
  }

  const response = await fetch(`${API_BASE_URL}${path}`, {
    ...options,
    headers,
    cache: "no-store",
  });

  const body = await parseBody(response);

  if (response.ok) {
    return { ok: true, data: body as ApiResponse<T> };
  }

  return {
    ok: false,
    error: (body as ErrorResponse) || { message: "Request failed" },
  };
}

export function login(payload: LoginRequest) {
  return request<LoginData>("/api/v1/auth/login", {
    method: "POST",
    body: JSON.stringify(payload),
  });
}

export function listUsers(
  token: string,
  params: { page: number; size: number; search?: string; sortBy: string; direction: "ASC" | "DESC" }
) {
  const query = new URLSearchParams({
    page: String(params.page),
    size: String(params.size),
    sortBy: params.sortBy,
    direction: params.direction,
  });
  if (params.search) {
    query.set("search", params.search);
  }
  return request<UserPage>(`/api/v1/users?${query.toString()}`, { method: "GET" }, token);
}

export function getUserById(token: string, userId: number) {
  return request<User>(`/api/v1/users/${userId}`, { method: "GET" }, token);
}

export function createUser(token: string, payload: UserPayload) {
  return request<User>("/api/v1/users", {
    method: "POST",
    body: JSON.stringify(payload),
  }, token);
}

export function updateUser(token: string, userId: number, payload: UserPayload) {
  return request<User>(`/api/v1/users/${userId}`, {
    method: "PUT",
    body: JSON.stringify(payload),
  }, token);
}

export function deleteUser(token: string, userId: number) {
  return request<User>(`/api/v1/users/${userId}`, { method: "DELETE" }, token);
}
