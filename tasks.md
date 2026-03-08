# Tasks

> `plan.md` 기반 구현 체크리스트

---

## Phase 1 — 백엔드 기초 CRUD

### 환경 설정
- [x] `build.gradle.kts`에 `spring-boot-starter-web` 추가
- [x] `build.gradle.kts`에 `spring-boot-starter-validation` 추가
- [x] `application.properties` H2 인메모리 DB 설정
- [x] `application.properties` JPA DDL auto / show-sql 설정
- [x] `application.properties` H2 콘솔 활성화 (`/h2-console`)
- [x] CORS 설정 (`localhost:3000` 허용) — `WebMvcConfigurer`

### Reminder Entity
- [x] `domain/Reminder.java` Entity 생성
  - [x] `id` (PK, auto-increment)
  - [x] `title` (not null)
  - [x] `note` (nullable)
  - [x] `isDone` (default false)
  - [x] `createdAt`, `updatedAt` (`@PrePersist`, `@PreUpdate`)

### Repository / Service / Controller
- [x] `repository/ReminderRepository.java` (JpaRepository)
- [x] `dto/ReminderRequest.java` (생성/수정 요청 DTO)
- [x] `dto/ReminderResponse.java` (응답 DTO)
- [x] `service/ports/in/ReminderService.java` 인터페이스 + `service/DefaultReminderService.java` 구현체
  - [x] `findAll()`
  - [x] `findById()`
  - [x] `create()`
  - [x] `update()`
  - [x] `delete()`
  - [x] `toggleDone()`
- [x] `controller/ReminderController.java`
  - [x] `GET /api/reminders`
  - [x] `GET /api/reminders/{id}`
  - [x] `POST /api/reminders`
  - [x] `PATCH /api/reminders/{id}`
  - [x] `PATCH /api/reminders/{id}/done`
  - [x] `DELETE /api/reminders/{id}`
- [x] `controller/GlobalExceptionHandler.java` (404, 400 처리)
- [x] `openapi.yml` OpenAPI 3.1 스펙 작성

### 시드 데이터 & 검증
- [x] `resources/data.sql` 초기 샘플 데이터 작성
- [x] 서버 기동 확인 (`./gradlew bootRun`)
- [x] H2 콘솔에서 테이블/데이터 확인
- [x] curl로 전체 엔드포인트 동작 확인

---

## Phase 2 — 프론트엔드 기초

### 프로젝트 셋업
- [x] `frontend/` 디렉토리에 Next.js 15 프로젝트 생성
- [x] Tailwind CSS v4 설정 확인
- [x] `@tanstack/react-query` 설치
- [x] `zustand` 설치
- [x] `app/providers.tsx` — `QueryClientProvider` 설정
- [x] `app/layout.tsx` — Provider 래핑 및 기본 레이아웃

### API 클라이언트
- [x] `lib/api.ts` — 타입 안전 fetch 래퍼
- [x] `lib/types.ts` — `Reminder`, `ReminderList` 공통 타입 정의

### 레이아웃
- [x] 사이드바 + 메인 2단 레이아웃 (사이드바 260px 고정)
- [x] Apple 스타일 색상 CSS 변수 정의 (`--color-blue`, `--bg-main` 등)
- [x] 기본 폰트: `-apple-system, BlinkMacSystemFont`

### 리마인더 목록 UI
- [x] `hooks/useReminders.ts` — TanStack Query (`GET /api/reminders`)
- [x] `components/reminder/ReminderList.tsx` — 목록 렌더링
- [x] `components/reminder/ReminderItem.tsx` — 개별 행
  - [x] 원형 체크박스 (Apple 스타일, `#007AFF` 테두리)
  - [x] 완료 시 취소선 텍스트
  - [x] 완료 시 페이드아웃 애니메이션
- [x] `components/reminder/AddReminder.tsx` — 인라인 추가
  - [x] "+ 새로운 리마인더" 클릭 시 입력 필드 노출
  - [x] `Enter` 저장 / `Esc` 취소

### 동작 확인
- [x] `npm run build` 빌드 성공 (TypeScript 오류 없음)
- [x] 체크박스 클릭으로 완료 토글 (구현)
- [x] 새 리마인더 인라인 추가 동작 (구현)

---

## Phase 3 — 리스트(목록) 관리

### 백엔드
- [ ] `domain/ReminderList.java` Entity 생성
  - [ ] `id`, `name`, `color` (default `#007AFF`), `icon`
  - [ ] `createdAt`, `updatedAt`
- [ ] `domain/Reminder.java`에 `@ManyToOne ReminderList list` 추가 (nullable)
- [ ] `repository/ReminderListRepository.java`
- [ ] `dto/ReminderListRequest.java`, `ReminderListResponse.java` (카운트 포함)
- [ ] `service/ReminderListService.java`
- [ ] `controller/ReminderListController.java`
  - [ ] `GET /api/lists` (리마인더 카운트 포함)
  - [ ] `POST /api/lists`
  - [ ] `PATCH /api/lists/{id}`
  - [ ] `DELETE /api/lists/{id}` (소속 리마인더 미분류 처리)
- [ ] `GET /api/reminders?listId={id}` 파라미터 지원

### 프론트엔드
- [ ] `hooks/useLists.ts` — TanStack Query (`GET /api/lists`)
- [ ] `components/sidebar/Sidebar.tsx`
- [ ] `components/sidebar/SmartListGrid.tsx` — 2×2 카드 그리드
  - [ ] 카드 스타일: 흰 배경, 14px 둥근 모서리, 드롭섀도우
  - [ ] 카드별 색상 아이콘 원 + 큰 볼드 카운트
- [ ] `components/sidebar/MyLists.tsx` — 사용자 목록 섹션
  - [ ] 색상 원 아이콘 + 이름 + 카운트 + chevron
  - [ ] 선택 시 파랑 하이라이트 행
- [ ] `components/sidebar/AddListInput.tsx` — 인라인 목록 추가
- [ ] `stores/uiStore.ts` — 선택된 뷰/목록 상태 관리 (Zustand)
- [ ] 사이드바 목록 선택 시 메인 영역 리마인더 필터링
- [ ] 리마인더 생성 시 현재 선택된 목록 자동 연결

---

## Phase 4 — 스마트 뷰 + 검색

### 백엔드
- [ ] `domain/Reminder.java`에 `dueDate (LocalDate)`, `dueTime (LocalTime)` 추가
- [ ] `GET /api/reminders?view=today` 구현
- [ ] `GET /api/reminders?view=scheduled` 구현 (날짜 오름차순 정렬)
- [ ] `GET /api/reminders?view=all` 구현
- [ ] `GET /api/reminders?view=completed` 구현 (최근 30일)
- [ ] `GET /api/reminders?q={검색어}` — title/note LIKE 검색
- [ ] `completedAt (LocalDateTime)` 필드 추가 (완료 시각 기록)

### 프론트엔드
- [ ] 스마트 뷰별 API 쿼리 (`today`, `scheduled`, `all`, `completed`)
- [ ] 예정 뷰: 클라이언트에서 날짜별 섹션 그룹핑 (오늘/내일/이번 주 등)
- [ ] `components/sidebar/SearchBar.tsx` — 상단 검색바
  - [ ] 둥근 `#E5E5EA` 배경, 돋보기 아이콘
  - [ ] 300ms 디바운스 입력
- [ ] 검색 결과 목록 표시
- [ ] 스마트 카드 카운트 실시간 반영 (각 뷰별 카운트 API)
- [ ] 섹션 헤더 컴포넌트 (오전/오후, 날짜 그룹)

---

## Phase 5 — 상세 편집 패널

### 백엔드
- [ ] `domain/Reminder.java`에 `priority Enum(NONE, LOW, MEDIUM, HIGH)` 추가
- [ ] `PATCH /api/reminders/{id}` — priority, dueDate, dueTime, note 수정 지원

### 프론트엔드
- [ ] `components/reminder/DetailPanel.tsx` — 우측 슬라이드-인 패널 (270px)
  - [ ] 제목 인라인 편집
  - [ ] 메모 멀티라인 텍스트에어리어
  - [ ] 마감일 날짜 피커
  - [ ] 마감 시간 입력
  - [ ] 우선순위 세그먼트 컨트롤 (없음/낮음/중간/높음)
  - [ ] 목록 이동 드롭다운
  - [ ] 삭제 버튼 (하단, 빨강)
- [ ] 상세 패널 250ms 슬라이드-인 애니메이션
- [ ] 리마인더 행 우측 우선순위 배지 (`!` / `!!` / `!!!`, 색상별)
- [ ] 리마인더 행 우측 마감일 텍스트
  - [ ] 오늘 마감: 파랑
  - [ ] 기한 초과: 빨강
  - [ ] 일반: 회색
- [ ] `hooks/useReminderMutation.ts` — update, delete, toggleDone mutation

---

## Phase 6 — 서브태스크 + UX 완성도

### 백엔드
- [ ] `domain/Reminder.java`에 `@ManyToOne Reminder parent` 추가 (자기참조)
- [ ] `domain/Reminder.java`에 `displayOrder (Integer)` 추가
- [ ] `GET /api/reminders/{id}/subtasks`
- [ ] `POST /api/reminders` — `parentId` 파라미터 지원
- [ ] `PATCH /api/reminders/{id}/order` — 순서 변경

### 프론트엔드 — 서브태스크
- [ ] 리마인더 행 좌측 펼침/접힘 화살표
- [ ] 서브태스크 36px 들여쓰기 렌더링
- [ ] `Tab` 키로 서브태스크 들여쓰기 (현재 행 → 서브태스크)

### 프론트엔드 — 드래그 앤 드롭
- [ ] `dnd-kit` 패키지 설치
- [ ] 리마인더 행 드래그 핸들
- [ ] 드롭 후 `displayOrder` 업데이트 API 호출

### 프론트엔드 — 컨텍스트 메뉴
- [ ] 리마인더 행 우클릭 컨텍스트 메뉴
  - [ ] 우선순위 변경
  - [ ] 마감일 빠른 설정 (오늘/내일/다음 주)
  - [ ] 서브태스크 추가
  - [ ] 삭제

### 프론트엔드 — 키보드 단축키
- [ ] `Cmd+N` — 새 리마인더 추가
- [ ] `Enter` (편집 중) — 저장 후 다음 행 포커스
- [ ] `Esc` — 편집 취소 / 패널 닫기
- [ ] `Tab` — 서브태스크 들여쓰기

### 프론트엔드 — 애니메이션
- [ ] 체크 완료: 체크박스 채워짐 150ms → 행 페이드아웃 500ms
- [ ] 뷰 전환: 300ms ease-in-out 슬라이드
- [ ] 스마트 카드 카운트 변경 시 scale 바운스

### 프론트엔드 — 다크모드
- [ ] CSS 변수로 색상 토큰 이원화 (light / dark)
- [ ] `prefers-color-scheme: dark` 미디어 쿼리 적용
- [ ] 다크 배경: `#1C1C1E` (main), `rgba(44,44,46,0.72)` (sidebar)

### 프론트엔드 — 반응형
- [ ] 모바일(375px) 사이드바 → 바텀시트 또는 햄버거 메뉴
- [ ] 모바일 상세 패널 → 모달 시트 전환
- [ ] 태블릿(768px) 중간 레이아웃 처리
