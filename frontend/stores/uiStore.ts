import { create } from "zustand";

export type View = "today" | "scheduled" | "all" | "completed" | { listId: number };

interface UIStore {
  selectedView: View;
  setSelectedView: (view: View) => void;
  searchQuery: string;
  setSearchQuery: (q: string) => void;
}

export const useUIStore = create<UIStore>((set) => ({
  selectedView: "all",
  setSelectedView: (view) => set({ selectedView: view }),
  searchQuery: "",
  setSearchQuery: (q) => set({ searchQuery: q }),
}));
