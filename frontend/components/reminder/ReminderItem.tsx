"use client";

import { useState } from "react";
import { Reminder, Priority } from "@/lib/types";
import { useToggleDone, useUpdateReminder } from "@/hooks/useReminders";
import { useUIStore } from "@/stores/uiStore";

const PRIORITY_BADGE: Record<Priority, { label: string; color: string } | null> = {
  NONE: null,
  LOW: { label: "!", color: "#34C759" },
  MEDIUM: { label: "!!", color: "#FF9500" },
  HIGH: { label: "!!!", color: "#FF3B30" },
};

function DueDateLabel({ dueDate }: { dueDate: string }) {
  const today = new Date().toISOString().split("T")[0];
  const isToday = dueDate === today;
  const isOverdue = dueDate < today;

  const color = isOverdue ? "#FF3B30" : isToday ? "#007AFF" : "#8E8E93";
  const label = isToday ? "오늘" : isOverdue ? dueDate : dueDate;

  return (
    <span style={{ fontSize: 12, color, flexShrink: 0 }}>{label}</span>
  );
}

interface Props {
  reminder: Reminder;
}

export function ReminderItem({ reminder }: Props) {
  const [editing, setEditing] = useState(false);
  const [title, setTitle] = useState(reminder.title);
  const toggleDone = useToggleDone();
  const updateReminder = useUpdateReminder();
  const { selectedReminderId, setSelectedReminderId } = useUIStore();

  const isSelected = selectedReminderId === reminder.id;
  const badge = PRIORITY_BADGE[reminder.priority];

  function handleCheck(e: React.MouseEvent) {
    e.stopPropagation();
    toggleDone.mutate(reminder.id);
  }

  function handleDoubleClick(e: React.MouseEvent) {
    e.stopPropagation();
    setEditing(true);
    setTitle(reminder.title);
  }

  function handleClick() {
    setSelectedReminderId(isSelected ? null : reminder.id);
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
      onClick={handleClick}
      style={{
        display: "flex",
        alignItems: "center",
        gap: 12,
        padding: "8px 16px 8px 52px",
        borderBottom: "1px solid var(--color-separator)",
        background: isSelected ? "rgba(0,122,255,0.06)" : "var(--bg-card)",
        minHeight: 44,
        cursor: "pointer",
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
          border: reminder.isDone ? "none" : `2px solid ${badge ? badge.color : "var(--color-blue)"}`,
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
          onClick={(e) => e.stopPropagation()}
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

      {/* 우측: 마감일 + 우선순위 배지 */}
      <div style={{ display: "flex", alignItems: "center", gap: 6, flexShrink: 0 }}>
        {reminder.dueDate && <DueDateLabel dueDate={reminder.dueDate} />}
        {badge && (
          <span
            style={{
              fontSize: 12,
              fontWeight: 700,
              color: badge.color,
              minWidth: 20,
              textAlign: "right",
            }}
          >
            {badge.label}
          </span>
        )}
      </div>
    </div>
  );
}
