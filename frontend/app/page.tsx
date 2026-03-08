"use client";

import { useUIStore, View } from "@/stores/uiStore";
import { ReminderList } from "@/components/reminder/ReminderList";
import { DetailPanel } from "@/components/reminder/DetailPanel";
import { useLists } from "@/hooks/useLists";
import { useReminders } from "@/hooks/useReminders";

const VIEW_LABELS: Record<string, string> = {
  today: "오늘",
  scheduled: "예정",
  all: "전체",
  completed: "완료됨",
};

function getViewTitle(view: View, listName?: string): string {
  if (typeof view === "object") return listName ?? "목록";
  return VIEW_LABELS[view] ?? "전체";
}

function getViewColor(view: View, listColor?: string): string {
  if (typeof view === "object") return listColor ?? "#007AFF";
  const colors: Record<string, string> = {
    today: "#007AFF",
    scheduled: "#FF3B30",
    all: "#8E8E93",
    completed: "#8E8E93",
  };
  return colors[view] ?? "#007AFF";
}

export default function Home() {
  const { selectedView, searchQuery, selectedReminderId, setSelectedReminderId } = useUIStore();
  const { data: lists = [] } = useLists();

  const currentList =
    typeof selectedView === "object"
      ? lists.find((l) => l.id === selectedView.listId)
      : undefined;

  const title = getViewTitle(selectedView, currentList?.name);
  const color = getViewColor(selectedView, currentList?.color);

  const params =
    typeof selectedView === "object"
      ? { listId: selectedView.listId, q: searchQuery || undefined }
      : { view: selectedView, q: searchQuery || undefined };

  // fetch all reminders for the current view to find the selected one
  const { data: reminders = [] } = useReminders(params);
  const selectedReminder = reminders.find((r) => r.id === selectedReminderId);

  return (
    <div style={{ display: "flex", height: "100%", overflow: "hidden" }}>
      <div style={{ flex: 1, overflow: "auto", padding: "32px 24px" }}>
        <h1 style={{ fontSize: 28, fontWeight: 700, color, marginBottom: 20 }}>
          {title}
        </h1>
        <div style={{ maxWidth: 640 }}>
          <ReminderList params={params} />
        </div>
      </div>

      {selectedReminder && (
        <DetailPanel
          reminder={selectedReminder}
          onClose={() => setSelectedReminderId(null)}
        />
      )}
    </div>
  );
}
