"use client";

import { useState, useEffect } from "react";
import { useUIStore } from "@/stores/uiStore";
import { useReminders } from "@/hooks/useReminders";
import { useLists, useCreateList, useDeleteList } from "@/hooks/useLists";

const SMART_LISTS = [
  { key: "today" as const, label: "오늘", bg: "#007AFF" },
  { key: "scheduled" as const, label: "예정", bg: "#FF3B30" },
  { key: "all" as const, label: "전체", bg: "#8E8E93" },
  { key: "completed" as const, label: "완료됨", bg: "#8E8E93" },
];

export function Sidebar() {
  const { selectedView, setSelectedView, setSearchQuery } = useUIStore();
  const { data: lists = [] } = useLists();
  const createList = useCreateList();
  const deleteList = useDeleteList();

  const [addingList, setAddingList] = useState(false);
  const [newListName, setNewListName] = useState("");
  const [searchInput, setSearchInput] = useState("");

  // 300ms debounce for search
  useEffect(() => {
    const timer = setTimeout(() => setSearchQuery(searchInput), 300);
    return () => clearTimeout(timer);
  }, [searchInput, setSearchQuery]);

  const { data: todayReminders = [] } = useReminders({ view: "today" });
  const { data: scheduledReminders = [] } = useReminders({ view: "scheduled" });
  const { data: allReminders = [] } = useReminders({ view: "all" });
  const { data: completedReminders = [] } = useReminders({ view: "completed" });

  const counts = {
    today: todayReminders.length,
    scheduled: scheduledReminders.length,
    all: allReminders.length,
    completed: completedReminders.length,
  };

  function handleAddList() {
    const name = newListName.trim();
    if (!name || createList.isPending) return;
    setAddingList(false);
    setNewListName("");
    createList.mutate({ name });
  }

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
        <span style={{ fontSize: 13 }}>🔍</span>
        <input
          value={searchInput}
          onChange={(e) => setSearchInput(e.target.value)}
          placeholder="검색"
          style={{
            flex: 1,
            border: "none",
            outline: "none",
            background: "transparent",
            fontSize: 15,
            color: "var(--color-label)",
            fontFamily: "var(--font-system)",
          }}
        />
        {searchInput && (
          <button
            onClick={() => setSearchInput("")}
            style={{
              background: "none",
              border: "none",
              color: "var(--color-gray)",
              cursor: "pointer",
              fontSize: 14,
              padding: 0,
              lineHeight: 1,
            }}
          >
            ×
          </button>
        )}
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
      <div style={{ flex: 1 }}>
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

        <div style={{ display: "flex", flexDirection: "column" }}>
          {lists.map((list) => {
            const isSelected =
              typeof selectedView === "object" && selectedView.listId === list.id;
            return (
              <div
                key={list.id}
                style={{ display: "flex", alignItems: "center" }}
              >
                <button
                  onClick={() => setSelectedView({ listId: list.id })}
                  style={{
                    flex: 1,
                    background: isSelected ? "rgba(0,0,0,0.06)" : "none",
                    border: "none",
                    borderRadius: 8,
                    padding: "8px 8px",
                    cursor: "pointer",
                    textAlign: "left",
                    display: "flex",
                    alignItems: "center",
                    gap: 10,
                  }}
                >
                  <div
                    style={{
                      width: 28,
                      height: 28,
                      borderRadius: "50%",
                      background: list.color,
                      display: "flex",
                      alignItems: "center",
                      justifyContent: "center",
                      fontSize: 14,
                      color: "#fff",
                      flexShrink: 0,
                    }}
                  >
                    ≡
                  </div>
                  <span style={{ fontSize: 15, color: "var(--color-label)", flex: 1 }}>
                    {list.name}
                  </span>
                  <span
                    style={{ fontSize: 15, color: "var(--color-gray)", fontWeight: 600 }}
                  >
                    {list.count}
                  </span>
                </button>
                <button
                  onClick={() => deleteList.mutate(list.id)}
                  style={{
                    background: "none",
                    border: "none",
                    color: "var(--color-gray)",
                    fontSize: 16,
                    cursor: "pointer",
                    padding: "4px 6px",
                    borderRadius: 6,
                    opacity: 0.6,
                  }}
                >
                  ×
                </button>
              </div>
            );
          })}
        </div>

        {/* 새 목록 입력 */}
        {addingList && (
          <div style={{ padding: "4px 8px" }}>
            <input
              autoFocus
              value={newListName}
              onChange={(e) => setNewListName(e.target.value)}
              onKeyDown={(e) => {
                if (e.key === "Enter") handleAddList();
                if (e.key === "Escape") {
                  setAddingList(false);
                  setNewListName("");
                }
              }}
              placeholder="목록 이름"
              style={{
                width: "100%",
                border: "1px solid var(--color-blue)",
                borderRadius: 8,
                padding: "6px 10px",
                fontSize: 15,
                outline: "none",
                background: "var(--bg-card)",
                color: "var(--color-label)",
                boxSizing: "border-box",
              }}
            />
          </div>
        )}
      </div>

      <div style={{ paddingLeft: 8 }}>
        <button
          onClick={() => setAddingList(true)}
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
