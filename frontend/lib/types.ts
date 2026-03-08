export interface Reminder {
  id: number;
  title: string;
  note: string | null;
  isDone: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface ReminderRequest {
  title: string;
  note?: string | null;
}
