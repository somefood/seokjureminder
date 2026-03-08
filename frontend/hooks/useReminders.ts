"use client";

import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { api } from "@/lib/api";
import { ReminderRequest } from "@/lib/types";

const QUERY_KEY = ["reminders"];

export function useReminders(listId?: number) {
  return useQuery({
    queryKey: listId != null ? [...QUERY_KEY, listId] : QUERY_KEY,
    queryFn: () => api.reminders.list(listId),
  });
}

export function useCreateReminder() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (body: ReminderRequest) => api.reminders.create(body),
    onSuccess: () => qc.invalidateQueries({ queryKey: QUERY_KEY }),
  });
}

export function useUpdateReminder() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: ({ id, body }: { id: number; body: ReminderRequest }) =>
      api.reminders.update(id, body),
    onSuccess: () => qc.invalidateQueries({ queryKey: QUERY_KEY }),
  });
}

export function useToggleDone() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (id: number) => api.reminders.toggleDone(id),
    onSuccess: () => qc.invalidateQueries({ queryKey: QUERY_KEY }),
  });
}

export function useDeleteReminder() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (id: number) => api.reminders.delete(id),
    onSuccess: () => qc.invalidateQueries({ queryKey: QUERY_KEY }),
  });
}
