"use client";

import { useUIStore } from "@/stores/uiStore";
import { useReminders } from "@/hooks/useReminders";

const SMART_LISTS = [
  { key: "today" as const, label: "오늘", color: "#007AFF", bg: "#007AFF" },
  { key: "scheduled" as const, label: "예정", color: "#FF3B30", bg: "#FF3B30" },
  { key: "all" as const, label: "전체", color: "#8E8E93", bg: "#8E8E93" },
  { key: "completed" as const, label: "완료됨", color: "#8E8E93", bg: "#8E8E93" },
];

export function Sidebar() {
  const { selectedView, setSelectedView } = useUIStore();
  const { data: reminders = [] } = useReminders();

  const counts = {
    today: reminders.filter((r) => !r.isDone).length,
    scheduled: reminders.filter((r) => !r.isDone).length,
    all: reminders.filter((r) => !r.isDone).length,
    completed: reminders.filter((r) => r.isDone).length,
  };

  return (
    <aside
      style={{
        width: 260,
        minWidth: 260,
        background: "var(--bg-sidebar)",
        backdropFilter: "blur(20px)",
        borderRight: "1px solid var(--color-separator)",
        padding: "16px 12px",
        display: "flex",
        flexDirection: "column",
        gap: 20,
        overflowY: "auto",
      }}
    >
      {/* 검색바 */}
      <div
        style={{
          background: "rgba(142,142,147,0.12)",
          borderRadius: 10,
          padding: "6px 12px",
          display: "flex",
          alignItems: "center",
          gap: 8,
          color: "var(--color-gray)",
          fontSize: 15,
        }}
      >
        <span>🔍</span>
        <span>검색</span>
      </div>

      {/* 스마트 리스트 2×2 그리드 */}
      <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 10 }}>
        {SMART_LISTS.map((item) => {
          const isSelected =
            typeof selectedView === "string" && selectedView === item.key;
          return (
            <button
              key={item.key}
              onClick={() => setSelectedView(item.key)}
              style={{
                background: isSelected ? item.bg + "22" : "var(--bg-card)",
                border: isSelected ? `2px solid ${item.bg}` : "2px solid transparent",
                borderRadius: 14,
                padding: "12px 14px",
                cursor: "pointer",
                textAlign: "left",
                boxShadow: "0 1px 4px rgba(0,0,0,0.08)",
                display: "flex",
                flexDirection: "column",
                gap: 4,
              }}
            >
              <div
                style={{
                  width: 32,
                  height: 32,
                  borderRadius: "50%",
                  background: item.bg,
                  display: "flex",
                  alignItems: "center",
                  justifyContent: "center",
                  fontSize: 16,
                  color: "#fff",
                }}
              >
                {item.key === "today" && "★"}
                {item.key === "scheduled" && "📅"}
                {item.key === "all" && "≡"}
                {item.key === "completed" && "✓"}
              </div>
              <div style={{ fontSize: 13, fontWeight: 600, color: "#000", marginTop: 4 }}>
                {item.label}
              </div>
              <div style={{ fontSize: 28, fontWeight: 700, color: item.bg, lineHeight: 1 }}>
                {counts[item.key]}
              </div>
            </button>
          );
        })}
      </div>

      {/* 나의 목록 */}
      <div>
        <div
          style={{
            fontSize: 11,
            fontWeight: 600,
            color: "var(--color-gray)",
            textTransform: "uppercase",
            letterSpacing: "0.04em",
            marginBottom: 4,
            paddingLeft: 8,
          }}
        >
          나의 목록
        </div>
      </div>

      <div style={{ marginTop: "auto", paddingLeft: 8 }}>
        <button
          style={{
            background: "none",
            border: "none",
            color: "var(--color-blue)",
            fontSize: 15,
            cursor: "pointer",
            padding: 0,
          }}
        >
          + 목록 추가
        </button>
      </div>
    </aside>
  );
}
