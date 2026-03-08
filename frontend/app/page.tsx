import { ReminderList } from "@/components/reminder/ReminderList";

export default function Home() {
  return (
    <div style={{ padding: "32px 24px" }}>
      <h1
        style={{
          fontSize: 28,
          fontWeight: 700,
          color: "#000",
          marginBottom: 20,
        }}
      >
        전체
      </h1>
      <ReminderList />
    </div>
  );
}
