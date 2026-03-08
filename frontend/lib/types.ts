export type Priority = "NONE" | "LOW" | "MEDIUM" | "HIGH";

export interface Reminder {
  id: number;
  title: string;
  note: string | null;
  isDone: boolean;
  listId: number | null;
  dueDate: string | null;
  dueTime: string | null;
  priority: Priority;
  completedAt: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface ReminderRequest {
  title: string;
  note?: string | null;
  listId?: number | null;
  dueDate?: string | null;
  dueTime?: string | null;
  priority?: Priority | null;
}

export interface ReminderList {
  id: number;
  name: string;
  color: string;
  icon: string | null;
  count: number;
}

export interface ReminderListRequest {
  name: string;
  color?: string | null;
  icon?: string | null;
}
