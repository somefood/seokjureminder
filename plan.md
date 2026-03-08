# 개발 계획: SeokjuReminder

> 참고 스펙: `spec.md`
> 개발 방향: 단순한 것부터 점진적으로 기능 추가 (Incremental Delivery)

---

## 기술 스택 요약

### Backend
| 항목 | 내용 |
|------|------|
| 프레임워크 | Spring Boot 4.0.3 |
| 언어 | Java 25 |
| ORM | Spring Data JPA |
| DB | H2 (인메모리, 개발용) |
| 유틸 | Lombok |
| 빌드 | Gradle (Kotlin DSL) |
| 포트 | 8080 |

**추가 필요 의존성**
```kotlin
// build.gradle.kts
implementation("org.springframework.boot:spring-boot-starter-web")   // REST API
implementation("org.springframework.boot:spring-boot-starter-validation") // 입력 검증
```

**application.properties 설정**
```properties
spring.datasource.url=jdbc:h2:mem:reminders;DB_CLOSE_DELAY=-1
spring.datasource.driver-class-name=org.h2.Driver
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
server.port=8080
```

### Frontend
| 항목 | 내용 |
|------|------|
| 프레임워크 | Next.js 15 (App Router) |
| 언어 | TypeScript |
| 스타일 | Tailwind CSS v4 |
| 서버 상태 | TanStack Query v5 |
| UI 상태 | Zustand v5 |
| HTTP | fetch (Next.js 내장) |
| 포트 | 3000 |
| 위치 | `/frontend` 디렉토리 |

**초기화 명령**
```bash
cd frontend
npx create-next-app@latest . --typescript --tailwind --app --src-dir=false
npm install @tanstack/react-query zustand
```

### 프로젝트 구조 (최종)
```
seokjureminder/
├── src/main/java/seokju/ai/seokjureminder/
│   ├── domain/          # JPA Entity
│   ├── repository/      # Spring Data Repository
│   ├── service/         # 비즈니스 로직
│   ├── controller/      # REST Controller
│   └── dto/             # Request / Response DTO
├── src/main/resources/
│   ├── application.properties
│   └── data.sql         # 초기 시드 데이터
├── frontend/
│   ├── app/             # Next.js App Router
│   │   ├── layout.tsx
│   │   ├── page.tsx
│   │   └── providers.tsx
│   ├── components/
│   │   ├── sidebar/
│   │   ├── reminder/
│   │   └── ui/          # 공통 컴포넌트
│   ├── hooks/           # TanStack Query hooks
│   ├── stores/          # Zustand store
│   └── lib/
│       └── api.ts       # API client
├── spec.md
└── plan.md
```

---

## Phase 1: 백엔드 기초 — 리마인더 단순 CRUD

**목표**: 리스트 없이 리마인더만 생성·조회·수정·삭제할 수 있는 REST API

### 작업 목록

**1-1. 의존성 추가**
- `build.gradle.kts`에 `spring-boot-starter-web`, `spring-boot-starter-validation` 추가

**1-2. application.properties 설정**
- H2 인메모리 DB, JPA DDL auto, H2 콘솔 활성화, CORS 설정

**1-3. Reminder Entity**
```
Reminder
├── id          Long (PK)
├── title       String (not null)
├── note        String (nullable)
├── isDone      Boolean (default false)
├── createdAt   LocalDateTime
└── updatedAt   LocalDateTime
```

**1-4. ReminderRepository** (JpaRepository 상속)

**1-5. ReminderService**
- `findAll()`, `findById()`, `create()`, `update()`, `delete()`, `toggleDone()`

**1-6. ReminderController** (`/api/reminders`)
| Method | Path | 동작 |
|--------|------|------|
| GET | `/api/reminders` | 전체 조회 |
| POST | `/api/reminders` | 생성 |
| PATCH | `/api/reminders/{id}` | 수정 |
| PATCH | `/api/reminders/{id}/done` | 완료 토글 |
| DELETE | `/api/reminders/{id}` | 삭제 |

**1-7. CORS 설정** (`WebMvcConfigurer`)
- `http://localhost:3000` 허용

**1-8. 초기 시드 데이터** (`data.sql`)

### 완료 기준
- `curl`로 모든 엔드포인트 정상 동작 확인
- H2 콘솔(`/h2-console`)에서 데이터 확인

---

## Phase 2: 프론트엔드 기초 — 리마인더 목록 + 완료 체크

**목표**: 리마인더 목록을 보고 완료 체크할 수 있는 최소 UI

### 작업 목록

**2-1. Next.js 프로젝트 생성** (`/frontend`)
- Tailwind CSS v4, TypeScript, App Router

**2-2. API 클라이언트** (`lib/api.ts`)
- `fetch` 기반 타입 안전 API 함수

**2-3. TanStack Query 설정** (`app/providers.tsx`)
- `QueryClientProvider` 래핑

**2-4. 기본 레이아웃** (`app/layout.tsx`)
- 사이드바 + 메인 영역 2단 레이아웃 (고정 너비)

**2-5. 리마인더 목록 컴포넌트**
- `useReminders()` 훅 (TanStack Query)
- 원형 체크박스 (Apple 스타일, `#007AFF`)
- 완료 시 취소선 + 페이드아웃

**2-6. 새 리마인더 추가**
- 목록 하단 "+ 새로운 리마인더" 클릭 → 인라인 입력 필드
- `Enter` 저장, `Esc` 취소

### 완료 기준
- 브라우저에서 리마인더 목록 조회
- 체크박스 클릭으로 완료 토글
- 새 리마인더 인라인 추가

---

## Phase 3: 리스트(목록) 관리

**목표**: 사이드바에서 리스트를 관리하고 리마인더를 리스트별로 분류

### 작업 목록

**3-1. ReminderList Entity 추가**
```
ReminderList
├── id        Long (PK)
├── name      String (not null)
├── color     String (default "#007AFF")
├── icon      String (nullable)
├── createdAt LocalDateTime
└── updatedAt LocalDateTime
```

**3-2. Reminder Entity에 list FK 추가**
- `@ManyToOne ReminderList list` (nullable)

**3-3. List API 추가** (`/api/lists`)
| Method | Path | 동작 |
|--------|------|------|
| GET | `/api/lists` | 전체 조회 (리마인더 카운트 포함) |
| POST | `/api/lists` | 생성 |
| PATCH | `/api/lists/{id}` | 이름/색상 수정 |
| DELETE | `/api/lists/{id}` | 삭제 (소속 리마인더 미분류로) |

**3-4. Reminders API에 `?listId=` 파라미터 추가**

**3-5. 사이드바 UI**
- 스마트 리스트 4개 (2×2 그리드 카드, Apple 스타일)
- "나의 목록" 섹션 — 색상 원 아이콘 + 이름 + 카운트
- 목록 선택 시 메인 영역 전환
- "+ 목록 추가" 인라인 입력

**3-6. 리마인더 생성 시 현재 리스트 자동 연결**

### 완료 기준
- 리스트 생성/삭제 동작
- 리스트별 리마인더 필터링
- 사이드바 선택으로 뷰 전환

---

## Phase 4: 스마트 뷰 + 검색

**목표**: 오늘/예정/전체/완료됨 스마트 뷰와 검색 기능

### 작업 목록

**4-1. Reminder Entity에 마감일 필드 추가**
- `dueDate LocalDate`, `dueTime LocalTime`

**4-2. 스마트 뷰 API** (`?view=today|scheduled|all|completed`)
- `today`: `dueDate = 오늘` OR (`dueDate IS NULL` AND `DATE(createdAt) = 오늘`)
- `scheduled`: `dueDate > 오늘` AND `isDone = false`
- `all`: `isDone = false` 전체
- `completed`: `isDone = true` AND `completedAt >= 30일 전`

**4-3. 예정 뷰 — 날짜별 섹션 그룹** (프론트엔드 클라이언트 그룹핑)

**4-4. 검색** (`?q=검색어`)
- `title LIKE %q%` OR `note LIKE %q%`
- 사이드바 상단 검색바 UI
- 실시간 디바운스 (300ms)

**4-5. 스마트 카드 카운트** (각 뷰별 실시간 카운트 표시)

### 완료 기준
- 4개 스마트 뷰 정상 동작
- 검색어 입력 시 실시간 필터링

---

## Phase 5: 리마인더 상세 편집

**목표**: 상세 패널에서 모든 필드 편집 가능

### 작업 목록

**5-1. Reminder Entity에 우선순위 추가**
- `priority Enum(NONE, LOW, MEDIUM, HIGH)`

**5-2. 상세 패널 컴포넌트** (우측 슬라이드-인, 270px)
- 제목 편집
- 메모 편집 (멀티라인)
- 마감일 날짜 피커 + 시간 입력
- 우선순위 세그먼트 컨트롤 (없음/낮음/중간/높음)
- 목록 이동 (드롭다운)

**5-3. 우선순위 표시**
- 리마인더 행 우측에 `!` / `!!` / `!!!` (색상별)

**5-4. 마감일 표시**
- 행 우측 날짜 텍스트 (오늘이면 파랑, 지났으면 빨강)

**5-5. 리마인더 삭제** (상세 패널 하단 버튼, 컨텍스트 메뉴)

### 완료 기준
- 상세 패널에서 모든 필드 수정 후 즉시 반영
- 우선순위 색상 표시 동작

---

## Phase 6: 서브태스크 + UX 완성도

**목표**: 서브태스크 지원 및 Apple Reminder와 유사한 UX 완성

### 작업 목록

**6-1. Reminder 자기참조 (서브태스크)**
- `parent @ManyToOne Reminder` (nullable)
- 서브태스크 API: `GET /api/reminders/{id}/subtasks`, `POST` 시 `parentId` 전달

**6-2. 서브태스크 UI**
- 리마인더 행 좌측 펼침 화살표
- 36px 들여쓰기로 서브태스크 표시

**6-3. displayOrder 정렬**
- Reminder에 `displayOrder Int` 추가
- 드래그 앤 드롭 순서 변경 (dnd-kit)

**6-4. 컨텍스트 메뉴** (우클릭)
- 미리 알림(날짜 빠른 설정), 우선순위 변경, 삭제, 서브태스크 추가

**6-5. 키보드 단축키**
- `Cmd+N`: 새 리마인더, `Enter`: 저장 후 다음 행 포커스, `Tab`: 서브태스크로 들여쓰기, `Esc`: 취소

**6-6. 애니메이션 다듬기**
- 체크 완료: 150ms 채워짐 → 500ms 페이드아웃
- 뷰 전환: 300ms 슬라이드
- 상세 패널: 250ms 슬라이드-인

**6-7. 다크모드**
- `prefers-color-scheme: dark` 자동 전환
- CSS 변수로 색상 토큰 관리

**6-8. 반응형 모바일**
- 사이드바: 모바일에서 바텀시트 or 햄버거 메뉴
- 상세 패널: 모달 시트로 전환

### 완료 기준
- 서브태스크 생성/완료 동작
- 드래그 앤 드롭 순서 변경
- 다크모드 자동 전환
- 모바일 375px에서 정상 사용 가능

---

## 개발 우선순위 요약

| Phase | 내용 | 산출물 |
|-------|------|--------|
| 1 | 백엔드 기초 CRUD | REST API (리마인더) |
| 2 | 프론트 기초 | 목록 조회 + 완료 체크 UI |
| 3 | 리스트 관리 | 사이드바 + 목록 필터링 |
| 4 | 스마트 뷰 + 검색 | 오늘/예정/전체/완료됨 뷰 |
| 5 | 상세 편집 | 우선순위, 마감일, 상세 패널 |
| 6 | 서브태스크 + UX | 완성도, 다크모드, 반응형 |
