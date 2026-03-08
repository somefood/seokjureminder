import { create } from "zustand";

type View = "today" | "scheduled" | "all" | "completed" | { listId: number };

interface UIStore {
  selectedView: View;
  setSelectedView: (view: View) => void;
}

export const useUIStore = create<UIStore>((set) => ({
  selectedView: "all",
  setSelectedView: (view) => set({ selectedView: view }),
}));
