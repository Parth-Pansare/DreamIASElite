# Dream IAS Elite – Product & Flow Overview

## Core Concept
- UPSC prep companion with dashboards, tests (MCQ sessions), PYQ repository, and note-taking.
- Built with Jetpack Compose + Material 3; navigation handled via a single `NavHost` in `MainActivity.kt`.
- Auth required; unauthenticated users see the Auth screen until login/register succeeds.

## Navigation & Shell
- Bottom tabs: `Dashboard`, `Tests`, `PYQ`, `Notes` (Current Affairs exists but tab is hidden from the bar in the current list).
- Drawer: Study Planner, Theme/Appearance, Settings, Help/Feedback, About/Privacy, Logout (plus placeholders for Profile and Analytics).
- Top bar title adapts to the active route; menu opens drawer.
- Routes (all URI-decoded):  
  - `dashboard`, `subject_dashboard/{subject}`  
  - `current_affairs`  
  - `tests`, `test_books/{subject}`, `test_units/{subject}/{book}`, `test_subject/{name}`, `test_session/{subject}`, `test_result`  
  - `pyq`, `pyq_paper/{paperId}`  
  - `notes`  
  - `study_planner`, `theme_appearance`, `settings`, `help_feedback`, `about_privacy`

## Feature Breakdown

### Tests (Prelims practice)
- Entry: `TestsScreen` shows subjects as cards plus a highlight banner.
- Subject → book list (`SubjectBooksScreen`) with per-book units.
- Unit or full-length start → `TestSessionScreen`.
- Question pool is subject-specific (history, geography, polity, economy, environment, science, general). A long scenario MCQ is injected first.
- Test session:
  - Timer (10 minutes), bookmark toggle, question counter.
  - Static Previous/Next/Submit buttons anchored at bottom; question area scrolls for long stems/options.
  - Answers persisted per question; bookmarks tracked.
  - Auto-submit on timer expiry.
- Result: `TestResultScreen` shows score, per-question answer/correct/explanation, time taken, and a back-to-tests action.

### PYQ
- Filters: Year-wise vs Subject-wise, with horizontal selectors.
- Paper cards show status/score and open a PDF export built on the fly from in-memory questions.
- Attempt summary mini dashboard and stats tiles.
- Study tip card:
  - Like button pinned top-right.
  - Auto-rotates tips every 20s; manual swipe left/right to change.
  - Card background softly fades through a curated palette; text switches instantly (no word fade).

### Notes
- Lists mock folders and recent notes with search/filter by text/tags.
- Note cards show subject tag, date, preview, tags, and affordances for edit/share/delete (placeholders).
- Import/Share-all surface buttons at bottom.
- Note-taking tip card mirrors PYQ behavior:
  - Like top-right, auto 20s rotation, manual swipe.
  - Background fades through a palette; text swaps instantly.

### Dashboard & Other Screens (high level)
- Dashboard/Subject dashboards, Current Affairs, Study Planner, Theme/Appearance, Settings, Help/Feedback, About/Privacy exist as routes; content follows each screen’s Compose implementation (see `ui/screen/screens/*`).

## UX/Styling Notes
- Material 3 theme with gradient backgrounds on several screens; rounded cards and pill chips.
- Buttons and icons use primary/neutral palette; tip cards use rotating accent colors.
- Layouts are scroll-friendly; long content in tests uses nested scroll with fixed action rows.

## Data & Persistence
- Auth wiring present (`AuthViewModel`, `AuthRepository`, Room `UserDao`); app currently uses in-memory/static data for tests, PYQs, and notes.
- PDF generation for PYQ export writes to cache and opens via `FileProvider`.

## Known Behaviors & Defaults
- Test question pools are capped at 10 per session (first is the big scenario MCQ).
- Tip rotation interval: 20 seconds; manual swipe threshold ~60px horizontal drag.
- Theme toggle managed via `ThemeController`/`LocalThemeController` with Light/Dark support.

## Suggested Next Steps (optional)
- Wire real data for PYQs, notes, and tests; connect Auth to backend.
- Add persistence for bookmarks, selected answers, and note CRUD.
- Expose bottom tab for Current Affairs if desired.
