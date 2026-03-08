"use client";

import { useState } from "react";
import { useCreateReminder } from "@/hooks/useReminders";

export function AddReminder({ listId }: { listId?: number }) {
  const [active, setActive] = useState(false);
  const [title, setTitle] = useState("");
  const createReminder = useCreateReminder();

  function handleSave() {
    if (title.trim()) {
      createReminder.mutate({ title: title.trim(), listId: listId ?? null });
    }
    setTitle("");
    setActive(false);
  }

  function handleKeyDown(e: React.KeyboardEvent) {
    if (e.key === "Enter") handleSave();
    if (e.key === "Escape") { setTitle(""); setActive(false); }
  }

  if (active) {
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
      >
        <div
          style={{
            width: 22,
            height: 22,
            borderRadius: "50%",
            border: "2px solid var(--color-blue)",
            flexShrink: 0,
            marginLeft: -36,
          }}
        />
        <input
          autoFocus
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          onBlur={handleSave}
          onKeyDown={handleKeyDown}
          placeholder="새로운 리마인더"
          style={{
            flex: 1,
            border: "none",
            outline: "none",
            fontSize: 16,
            fontFamily: "var(--font-system)",
            background: "transparent",
          }}
        />
      </div>
    );
  }

  return (
    <button
      onClick={() => setActive(true)}
      style={{
        display: "flex",
        alignItems: "center",
        gap: 8,
        padding: "12px 16px",
        background: "none",
        border: "none",
        color: "var(--color-blue)",
        fontSize: 15,
        cursor: "pointer",
        width: "100%",
        textAlign: "left",
        fontFamily: "var(--font-system)",
      }}
    >
      <span style={{ fontSize: 20, fontWeight: 300 }}>+</span>
      새로운 리마인더
    </button>
  );
}
