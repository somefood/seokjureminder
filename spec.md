# PRD: SeokjuReminder - Apple Reminder 웹 버전

## 1. 개요

Apple Reminder 앱의 핵심 기능을 웹 브라우저에서 사용할 수 있도록 구현한다.
데스크탑/모바일 웹에서 리마인더를 생성, 관리, 완료 처리할 수 있는 풀스택 웹 애플리케이션이다.

---

## 2. 기술 스택

| 영역 | 기술 |
|------|------|
| Backend | Spring Boot 4.0.3, Java 25 |
| ORM / DB | Spring Data JPA, H2 (인메모리, 개발용) |
| Frontend | Next.js 15 (App Router), TypeScript |
| 스타일 | Tailwind CSS v4 |
| 상태 관리 | TanStack Query (서버 상태), Zustand (UI 상태) |
| API 통신 | REST (JSON) |
| 빌드 | Gradle (backend), npm (frontend) |

---

## 3. 핵심 기능 범위 (MVP)

### 3.1 리스트(그룹) 관리
- 리스트 생성 / 이름 변경 / 삭제
- 리스트별 색상 아이콘 지정
- 기본 제공 스마트 리스트: "오늘", "예정", "전체", "완료됨"

### 3.2 리마인더(할 일) 관리
- 리마인더 생성 / 제목 편집 / 삭제
- 완료 체크 (완료 ↔ 미완료 토글)
- 메모(노트) 추가
- 마감일 + 시간 설정
- 우선순위 설정 (없음 / 낮음 / 중간 / 높음)
- 특정 리스트에 속하거나 미분류 상태 허용
- 하위 리마인더(서브태스크) 1단계 지원

### 3.3 스마트 뷰
- **오늘**: 오늘 마감 또는 기한 없는 오늘 생성 항목
- **예정**: 내일 이후 마감일이 있는 미완료 항목 (날짜별 그룹)
- **전체**: 모든 미완료 항목
- **완료됨**: 완료 처리된 항목 (최근 30일)

### 3.4 검색
- 제목 / 메모 전문 검색 (실시간 필터링)

---

## 4. 데이터 모델

### ReminderList (리스트)
```
id          Long        PK, auto-increment
name        String      리스트 이름 (필수)
color       String      hex 색상코드 (기본 #007AFF)
icon        String      이모지 또는 SF Symbol 이름
createdAt   LocalDateTime
updatedAt   LocalDateTime
```

### Reminder (리마인더)
```
id          Long        PK, auto-increment
title       String      제목 (필수)
note        String      메모 (nullable)
isDone      Boolean     완료 여부 (기본 false)
dueDate     LocalDate   마감일 (nullable)
dueTime     LocalTime   마감 시간 (nullable)
priority    Enum        NONE / LOW / MEDIUM / HIGH
list        FK          ReminderList (nullable - 미분류 허용)
parent      FK          Reminder (자기참조, nullable - 서브태스크)
displayOrder Int        리스트 내 순서
createdAt   LocalDateTime
updatedAt   LocalDateTime
```

---

## 5. API 설계

### Lists
| Method | Path | 설명 |
|--------|------|------|
| GET | /api/lists | 전체 리스트 조회 |
| POST | /api/lists | 리스트 생성 |
| PATCH | /api/lists/{id} | 리스트 수정 (이름/색상/아이콘) |
| DELETE | /api/lists/{id} | 리스트 삭제 (소속 리마인더 미분류로 이동) |

### Reminders
| Method | Path | 설명 |
|--------|------|------|
| GET | /api/reminders | 전체 조회 (쿼리파라미터: listId, view, q) |
| POST | /api/reminders | 리마인더 생성 |
| PATCH | /api/reminders/{id} | 리마인더 수정 |
| PATCH | /api/reminders/{id}/done | 완료 토글 |
| DELETE | /api/reminders/{id} | 삭제 |
| GET | /api/reminders/{id}/subtasks | 서브태스크 조회 |

### Smart View 쿼리 파라미터
- `?view=today` : 오늘 뷰
- `?view=scheduled` : 예정 뷰
- `?view=all` : 전체 뷰
- `?view=completed` : 완료됨 뷰
- `?q=검색어` : 검색

---

## 6. UI 레이아웃

```
+--------------------------+-----------------------------------+
|  [사이드바]               |  [메인 콘텐츠]                     |
|                          |                                   |
|  스마트 리스트             |  리스트 제목                        |
|  - 오늘       (N)        |  + 새로운 리마인더 추가              |
|  - 예정       (N)        |                                   |
|  - 전체       (N)        |  [ ] 리마인더 제목        마감일     |
|  - 완료됨                |  [ ] 리마인더 제목        !우선순위  |
|                          |      [ ] 서브태스크                 |
|  나의 리스트              |                                   |
|  - 리스트1    (N)        |                                   |
|  - 리스트2    (N)        |                                   |
|  + 목록 추가              |                                   |
+--------------------------+-----------------------------------+
```

### 인터랙션 패턴
- 리마인더 클릭 시 우측에 상세 패널(detail panel) 슬라이드
- 리스트 항목 인라인 편집 (더블클릭 or 엔터)
- 드래그 앤 드롭으로 순서 변경 (MVP 이후)
- 키보드 단축키: `N` 새 리마인더, `Enter` 저장, `Esc` 취소

---

## 7. 비기능 요구사항

| 항목 | 목표 |
|------|------|
| API 응답 속도 | 95th percentile < 200ms |
| 최초 페이지 로드 | LCP < 2.5s |
| 반응형 | 모바일(375px) ~ 데스크탑(1440px) 지원 |
| 접근성 | WCAG 2.1 AA 수준 (키보드 탐색, ARIA) |
| CORS | 개발: localhost:3000 허용, 운영: 동일 도메인 |

---

## 8. 구현 순서 (페이즈)

### Phase 1 - Backend 기반 (우선)
1. `spring-boot-starter-web` 의존성 추가
2. Entity: `ReminderList`, `Reminder` 정의
3. Repository, Service, Controller 레이어 구현
4. H2 콘솔 + 초기 데이터 시드
5. CORS 설정
6. API 통합 테스트

### Phase 2 - Frontend 기반
1. Next.js 15 프로젝트 생성 (`/frontend` 디렉토리)
2. 레이아웃 구성 (사이드바 + 메인)
3. 스마트 리스트 뷰 구현
4. 리마인더 CRUD UI
5. 리스트 CRUD UI
6. 검색 기능

### Phase 3 - 완성도
1. 서브태스크 UI
2. 상세 패널 (마감일/우선순위/메모 편집)
3. 애니메이션 및 전환 효과
4. 반응형 모바일 레이아웃

---

## 9. 미포함 항목 (Out of Scope - MVP)

- 사용자 인증 / 멀티유저
- 알림 / 푸시 노티피케이션
- 반복 리마인더 (매일/매주 등)
- 위치 기반 리마인더
- iCloud 동기화
- 드래그 앤 드롭 정렬
- 다크모드 (Phase 3 이후 고려)

---

## 10. 디렉토리 구조 (예정)

```
seokjureminder/
├── src/                          # Spring Boot backend
│   └── main/java/seokju/ai/seokjureminder/
│       ├── domain/               # Entity
│       ├── repository/           # JPA Repository
│       ├── service/              # 비즈니스 로직
│       ├── controller/           # REST Controller
│       └── dto/                  # Request/Response DTO
├── frontend/                     # Next.js app
│   ├── app/                      # App Router
│   ├── components/               # UI 컴포넌트
│   ├── hooks/                    # Custom hooks (TanStack Query)
│   ├── stores/                   # Zustand store
│   └── lib/                      # API client, utils
└── prd.md
```