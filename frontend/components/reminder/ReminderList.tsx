"use client";

import { useReminders } from "@/hooks/useReminders";
import { ReminderItem } from "./ReminderItem";
import { AddReminder } from "./AddReminder";

export function ReminderList() {
  const { data: reminders = [], isLoading, isError } = useReminders();

  if (isLoading) {
    return (
      <div style={{ padding: 32, color: "var(--color-gray)", fontSize: 15 }}>
        불러오는 중...
      </div>
    );
  }

  if (isError) {
    return (
      <div style={{ padding: 32, color: "var(--color-red)", fontSize: 15 }}>
        데이터를 불러올 수 없습니다.
      </div>
    );
  }

  return (
    <div style={{ background: "var(--bg-card)", borderRadius: 12, overflow: "hidden" }}>
      {reminders.map((reminder) => (
        <ReminderItem key={reminder.id} reminder={reminder} />
      ))}
      <AddReminder />
    </div>
  );
}
