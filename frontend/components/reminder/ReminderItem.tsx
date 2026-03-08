"use client";

import { useState } from "react";
import { Reminder } from "@/lib/types";
import { useToggleDone, useUpdateReminder } from "@/hooks/useReminders";

interface Props {
  reminder: Reminder;
}

export function ReminderItem({ reminder }: Props) {
  const [editing, setEditing] = useState(false);
  const [title, setTitle] = useState(reminder.title);
  const toggleDone = useToggleDone();
  const updateReminder = useUpdateReminder();

  function handleCheck() {
    toggleDone.mutate(reminder.id);
  }

  function handleDoubleClick() {
    setEditing(true);
    setTitle(reminder.title);
  }

  function handleSave() {
    if (title.trim() && title !== reminder.title) {
      updateReminder.mutate({ id: reminder.id, body: { title: title.trim() } });
    }
    setEditing(false);
  }

  function handleKeyDown(e: React.KeyboardEvent) {
    if (e.key === "Enter") handleSave();
    if (e.key === "Escape") { setTitle(reminder.title); setEditing(false); }
  }

  return (
    <div
      style={{
        display: "flex",
        alignItems: "center",
        gap: 12,
        padding: "8px 16px 8px 52px",
        borderBottom: "1px solid var(--color-separator)",
        background: "var(--bg-card)",
        minHeight: 44,
      }}
      onDoubleClick={handleDoubleClick}
    >
      {/* 원형 체크박스 */}
      <div
        onClick={handleCheck}
        style={{
          width: 22,
          height: 22,
          borderRadius: "50%",
          border: reminder.isDone ? "none" : "2px solid var(--color-blue)",
          background: reminder.isDone ? "var(--color-blue)" : "transparent",
          cursor: "pointer",
          flexShrink: 0,
          marginLeft: -36,
          display: "flex",
          alignItems: "center",
          justifyContent: "center",
          color: "#fff",
          fontSize: 13,
          fontWeight: 700,
          transition: "all 0.15s ease",
        }}
      >
        {reminder.isDone && "✓"}
      </div>

      {/* 제목 */}
      {editing ? (
        <input
          autoFocus
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          onBlur={handleSave}
          onKeyDown={handleKeyDown}
          style={{
            flex: 1,
            border: "none",
            outline: "none",
            fontSize: 16,
            fontFamily: "var(--font-system)",
            background: "transparent",
          }}
        />
      ) : (
        <span
          style={{
            flex: 1,
            fontSize: 16,
            color: reminder.isDone ? "var(--color-gray)" : "#000",
            textDecoration: reminder.isDone ? "line-through" : "none",
            opacity: reminder.isDone ? 0.6 : 1,
            transition: "opacity 0.3s ease",
          }}
        >
          {reminder.title}
        </span>
      )}
    </div>
  );
}
