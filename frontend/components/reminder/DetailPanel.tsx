"use client";

import { useEffect, useState } from "react";
import { Reminder, Priority } from "@/lib/types";
import { useUpdateReminder, useDeleteReminder } from "@/hooks/useReminders";
import { useLists } from "@/hooks/useLists";
import { useUIStore } from "@/stores/uiStore";

const PRIORITY_OPTIONS: { value: Priority; label: string; color: string }[] = [
  { value: "NONE", label: "없음", color: "#8E8E93" },
  { value: "LOW", label: "낮음", color: "#34C759" },
  { value: "MEDIUM", label: "중간", color: "#FF9500" },
  { value: "HIGH", label: "높음", color: "#FF3B30" },
];

interface Props {
  reminder: Reminder;
  onClose: () => void;
}

export function DetailPanel({ reminder, onClose }: Props) {
  const { data: lists = [] } = useLists();
  const updateReminder = useUpdateReminder();
  const deleteReminder = useDeleteReminder();
  const { setSelectedReminderId } = useUIStore();

  const [title, setTitle] = useState(reminder.title);
  const [note, setNote] = useState(reminder.note ?? "");
  const [dueDate, setDueDate] = useState(reminder.dueDate ?? "");
  const [dueTime, setDueTime] = useState(reminder.dueTime ?? "");
  const [priority, setPriority] = useState<Priority>(reminder.priority);
  const [listId, setListId] = useState<number | null>(reminder.listId);

  // sync when reminder changes
  useEffect(() => {
    setTitle(reminder.title);
    setNote(reminder.note ?? "");
    setDueDate(reminder.dueDate ?? "");
    setDueTime(reminder.dueTime ?? "");
    setPriority(reminder.priority);
    setListId(reminder.listId);
  }, [reminder.id]);

  function handleSave() {
    updateReminder.mutate({
      id: reminder.id,
      body: {
        title: title.trim() || reminder.title,
        note: note || null,
        dueDate: dueDate || null,
        dueTime: dueTime || null,
        priority,
        listId,
      },
    });
  }

  function handleDelete() {
    deleteReminder.mutate(reminder.id, {
      onSuccess: () => setSelectedReminderId(null),
    });
  }

  return (
    <div
      style={{
        width: 270,
        minWidth: 270,
        background: "var(--bg-card)",
        borderLeft: "1px solid var(--color-separator)",
        display: "flex",
        flexDirection: "column",
        overflowY: "auto",
        animation: "slideIn 0.25s ease",
      }}
    >
      <style>{`@keyframes slideIn { from { transform: translateX(270px); opacity: 0; } to { transform: translateX(0); opacity: 1; } }`}</style>

      {/* 헤더 */}
      <div
        style={{
          display: "flex",
          alignItems: "center",
          justifyContent: "space-between",
          padding: "16px 16px 8px",
          borderBottom: "1px solid var(--color-separator)",
        }}
      >
        <span style={{ fontSize: 13, fontWeight: 600, color: "var(--color-gray)" }}>
          세부 정보
        </span>
        <button
          onClick={onClose}
          style={{
            background: "none",
            border: "none",
            color: "var(--color-blue)",
            fontSize: 15,
            cursor: "pointer",
          }}
        >
          완료
        </button>
      </div>

      <div style={{ padding: "16px", display: "flex", flexDirection: "column", gap: 16, flex: 1 }}>
        {/* 제목 */}
        <div>
          <div style={{ fontSize: 11, color: "var(--color-gray)", marginBottom: 4, textTransform: "uppercase", letterSpacing: "0.04em" }}>제목</div>
          <input
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            onBlur={handleSave}
            style={{
              width: "100%",
              border: "none",
              borderBottom: "1px solid var(--color-separator)",
              outline: "none",
              fontSize: 16,
              fontFamily: "var(--font-system)",
              padding: "4px 0",
              background: "transparent",
              color: "var(--color-label)",
              boxSizing: "border-box",
            }}
          />
        </div>

        {/* 메모 */}
        <div>
          <div style={{ fontSize: 11, color: "var(--color-gray)", marginBottom: 4, textTransform: "uppercase", letterSpacing: "0.04em" }}>메모</div>
          <textarea
            value={note}
            onChange={(e) => setNote(e.target.value)}
            onBlur={handleSave}
            rows={3}
            placeholder="메모 추가..."
            style={{
              width: "100%",
              border: "1px solid var(--color-separator)",
              borderRadius: 8,
              outline: "none",
              fontSize: 14,
              fontFamily: "var(--font-system)",
              padding: "8px",
              background: "transparent",
              color: "var(--color-label)",
              resize: "none",
              boxSizing: "border-box",
            }}
          />
        </div>

        {/* 마감일 */}
        <div>
          <div style={{ fontSize: 11, color: "var(--color-gray)", marginBottom: 4, textTransform: "uppercase", letterSpacing: "0.04em" }}>마감일</div>
          <input
            type="date"
            value={dueDate}
            onChange={(e) => setDueDate(e.target.value)}
            onBlur={handleSave}
            style={{
              width: "100%",
              border: "1px solid var(--color-separator)",
              borderRadius: 8,
              outline: "none",
              fontSize: 14,
              fontFamily: "var(--font-system)",
              padding: "6px 8px",
              background: "transparent",
              color: "var(--color-label)",
              boxSizing: "border-box",
            }}
          />
        </div>

        {/* 마감 시간 */}
        <div>
          <div style={{ fontSize: 11, color: "var(--color-gray)", marginBottom: 4, textTransform: "uppercase", letterSpacing: "0.04em" }}>마감 시간</div>
          <input
            type="time"
            value={dueTime}
            onChange={(e) => setDueTime(e.target.value)}
            onBlur={handleSave}
            style={{
              width: "100%",
              border: "1px solid var(--color-separator)",
              borderRadius: 8,
              outline: "none",
              fontSize: 14,
              fontFamily: "var(--font-system)",
              padding: "6px 8px",
              background: "transparent",
              color: "var(--color-label)",
              boxSizing: "border-box",
            }}
          />
        </div>

        {/* 우선순위 */}
        <div>
          <div style={{ fontSize: 11, color: "var(--color-gray)", marginBottom: 8, textTransform: "uppercase", letterSpacing: "0.04em" }}>우선순위</div>
          <div style={{ display: "flex", gap: 6 }}>
            {PRIORITY_OPTIONS.map((opt) => (
              <button
                key={opt.value}
                onClick={() => { setPriority(opt.value); updateReminder.mutate({ id: reminder.id, body: { title: reminder.title, priority: opt.value } }); }}
                style={{
                  flex: 1,
                  padding: "6px 4px",
                  borderRadius: 8,
                  border: priority === opt.value ? `2px solid ${opt.color}` : "2px solid var(--color-separator)",
                  background: priority === opt.value ? opt.color + "22" : "transparent",
                  color: priority === opt.value ? opt.color : "var(--color-gray)",
                  fontSize: 12,
                  fontWeight: 600,
                  cursor: "pointer",
                  fontFamily: "var(--font-system)",
                }}
              >
                {opt.label}
              </button>
            ))}
          </div>
        </div>

        {/* 목록 */}
        <div>
          <div style={{ fontSize: 11, color: "var(--color-gray)", marginBottom: 4, textTransform: "uppercase", letterSpacing: "0.04em" }}>목록</div>
          <select
            value={listId ?? ""}
            onChange={(e) => {
              const val = e.target.value ? Number(e.target.value) : null;
              setListId(val);
              updateReminder.mutate({ id: reminder.id, body: { title: reminder.title, listId: val } });
            }}
            style={{
              width: "100%",
              border: "1px solid var(--color-separator)",
              borderRadius: 8,
              outline: "none",
              fontSize: 14,
              fontFamily: "var(--font-system)",
              padding: "6px 8px",
              background: "transparent",
              color: "var(--color-label)",
              boxSizing: "border-box",
            }}
          >
            <option value="">없음</option>
            {lists.map((list) => (
              <option key={list.id} value={list.id}>{list.name}</option>
            ))}
          </select>
        </div>
      </div>

      {/* 삭제 버튼 */}
      <div style={{ padding: "16px", borderTop: "1px solid var(--color-separator)" }}>
        <button
          onClick={handleDelete}
          style={{
            width: "100%",
            padding: "10px",
            borderRadius: 10,
            border: "none",
            background: "rgba(255,59,48,0.1)",
            color: "var(--color-red)",
            fontSize: 15,
            fontWeight: 600,
            cursor: "pointer",
            fontFamily: "var(--font-system)",
          }}
        >
          리마인더 삭제
        </button>
      </div>
    </div>
  );
}
