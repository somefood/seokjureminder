import { Reminder, ReminderRequest, ReminderList, ReminderListRequest } from "./types";

const BASE_URL = process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:8080";

async function request<T>(path: string, init?: RequestInit): Promise<T> {
  const res = await fetch(`${BASE_URL}${path}`, {
    headers: { "Content-Type": "application/json" },
    ...init,
  });
  if (!res.ok) {
    const error = await res.json().catch(() => ({ message: res.statusText }));
    throw new Error(error.message ?? "API Error");
  }
  if (res.status === 204) return undefined as T;
  return res.json();
}

export const api = {
  reminders: {
    list: (listId?: number) =>
      request<Reminder[]>(`/api/reminders${listId != null ? `?listId=${listId}` : ""}`),
    get: (id: number) => request<Reminder>(`/api/reminders/${id}`),
    create: (body: ReminderRequest) =>
      request<Reminder>("/api/reminders", { method: "POST", body: JSON.stringify(body) }),
    update: (id: number, body: ReminderRequest) =>
      request<Reminder>(`/api/reminders/${id}`, { method: "PATCH", body: JSON.stringify(body) }),
    toggleDone: (id: number) =>
      request<Reminder>(`/api/reminders/${id}/done`, { method: "PATCH" }),
    delete: (id: number) =>
      request<void>(`/api/reminders/${id}`, { method: "DELETE" }),
  },
  lists: {
    list: () => request<ReminderList[]>("/api/lists"),
    get: (id: number) => request<ReminderList>(`/api/lists/${id}`),
    create: (body: ReminderListRequest) =>
      request<ReminderList>("/api/lists", { method: "POST", body: JSON.stringify(body) }),
    update: (id: number, body: ReminderListRequest) =>
      request<ReminderList>(`/api/lists/${id}`, { method: "PATCH", body: JSON.stringify(body) }),
    delete: (id: number) =>
      request<void>(`/api/lists/${id}`, { method: "DELETE" }),
  },
};
