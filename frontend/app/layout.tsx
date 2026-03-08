import type { Metadata } from "next";
import "./globals.css";
import { Providers } from "./providers";
import { Sidebar } from "@/components/sidebar/Sidebar";

export const metadata: Metadata = {
  title: "SeokjuReminder",
  description: "Apple Reminder 웹 버전",
};

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="ko">
      <body>
        <Providers>
          <div style={{ display: "flex", height: "100vh", overflow: "hidden" }}>
            <Sidebar />
            <main style={{ flex: 1, overflow: "auto", background: "var(--bg-main)" }}>
              {children}
            </main>
          </div>
        </Providers>
      </body>
    </html>
  );
}
