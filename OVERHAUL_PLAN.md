# DogApp Overhaul Plan

Goal: turn DogApp into a CV-quality portfolio project — Kotlin Multiplatform, Compose
Multiplatform, Navigation 3, Koin — without scope creep.

Guiding principles:

- **The app builds and runs at the end of every phase.** Each phase is a small, reviewable
  set of commits. A clean migration history is itself a CV asset.
- **Migrate in dependency order.** Every library swap in phases 3–4 is a prerequisite for
  KMP (Hilt, Retrofit, Moshi, and OkHttp are JVM/Android-only), so nothing here is wasted
  work even before the KMP restructure lands.
- **No new features until Phase 9**, and even then only one small one. The CV value is in
  architecture, correctness, and polish — not feature count.

---

## Career acceleration timeline

This project is running alongside a ~5-month prep plan targeting Android Engineer roles
at neobanks (Starling-tier). Budget is ~8–12 hrs/week total, of which ~4–6 hrs/week is
DogApp; the rest is fundamentals, systems design, and light DSA (see
`Android_Engineer_Prep_Plan.md`). Each phase below is tagged with a target week range,
an hour estimate, and the parallel study track running alongside it — so this file is
also the week-by-week execution checklist, not just the technical spec.

| Weeks | Phases | Est. hours |
|---|---|---|
| 1–2 | Phase 0 + Phase 1 | 6–8h |
| 3–5 | Phase 2 + Phase 3 | 8–10h |
| 6–7 | Phase 4 | 6–8h |
| 8–12 | Phase 5 + Phase 6 (the big lift) | 16–20h |
| 13–15 | Phase 7 | 5–6h |
| 16–18 | Phase 8 + Phase 9 | 12–14h |
| 19–20 | Take-home rehearsal (see prep plan) — no new DogApp phases, dress rehearsal only |

Milestones worth treating as checkpoints (not just "done when" per phase):

- **End of week 5**: builds clean on Kotlin 2.2 with Koin, no Hilt/kapt — already a
  legitimate "modernised a legacy app" story even if the project stopped here.
- **End of week 12**: KMP restructure + CMP UI done — the headline achievement.
- **End of week 18**: README, CI, tests, Favourites done — portfolio-ready, shareable.

---

## Current state (July 2026)

Single-module Android app, Kotlin 1.9.22 / AGP 8.3.1 / kapt. Compose UI (Material 3) with
Hilt DI, Retrofit + Moshi + OkHttp against TheDogApi, Coil 2 for images,
navigation-compose 2.7.7 with string routes. Two screens work (random dog, breed detail);
Favourites tab and FAB are stubs. A dead Fragment/ViewBinding layer coexists with the
Compose app. Minimal tests (JUnit 5 + one Compose UI test).

### Target stack

| Concern        | Now                        | Target                                          |
| -------------- | -------------------------- | ----------------------------------------------- |
| Language/build | Kotlin 1.9.22, kapt        | Kotlin 2.2.x, KSP-free (no codegen needed)      |
| UI             | Jetpack Compose (Android)  | Compose Multiplatform in `commonMain`           |
| DI             | Hilt                       | Koin 4.x                                        |
| Network        | Retrofit + OkHttp + Moshi  | Ktor Client + kotlinx.serialization             |
| Images         | Coil 2                     | Coil 3 (multiplatform)                          |
| Navigation     | navigation-compose, string routes | Navigation 3 (`NavDisplay` + typed keys) |
| Modules        | `:app`                     | `:composeApp` (commonMain/androidMain/…)        |

> Verify latest stable versions when each phase starts — the versions above are the
> floor, not the pin.

---

## Phase 0 — Security fix (do immediately, before anything else)

> **Target:** Week 1, ~1h. **Parallel track:** kick off the DSA routine (arrays/hashing/
> two-pointer) and a Kotlin coroutines/Flow refresher — see prep plan.

A **live API key** is hardcoded in
`app/src/main/java/com/example/dog/network/ApiKeyInterceptor.kt:11` and is in git history.

- [X] Rotate the key on thedogapi.com (rotating beats rewriting git history — the old key
      simply stops working, so history can stay).
- [X] Load the new key from `local.properties` → `BuildConfig.DOG_API_KEY` (later:
      BuildKonfig in Phase 5). Confirm `local.properties` is gitignored.
- [ ] Add a `local.properties.example` documenting the required entry.

**Done when:** `git grep live_` finds nothing and the app still fetches dogs.

## Phase 1 — Bug fixes and dead-code purge (Android-only, no new libs)

> **Target:** Weeks 1–2, ~5–7h. **Parallel track:** continue DSA reps; start reading
> "Now in Android" source for reference architecture patterns.

Shrink the migration surface first; every file deleted here is a file that never needs
porting.

Bugs:

- [X] Route mismatch: `AppDestination` uses `"Home"`/`"Favourites"` but `NavGraph`
      registers `"home"`/`"favourites"` — bottom-bar `selected` never matches and
      navigating to those tabs throws `IllegalArgumentException`. Single source of truth
      for routes (becomes trivial with Nav3 typed keys later, but fix now).
- [X] `HomeRepository.getRandomDogImage()`: remove the unbounded `do-while` retry
      (can loop forever against the API) and the `breeds!!` NPE. Cap retries (e.g. 3) or
      rely on `has_breeds=true` on the search endpoint instead.
- [X] `HomeRepository.getBreedDetails()`: `body.first().breeds?.first()!!` crashes on an
      empty list. Use the proper `GET /breeds/{breed_id}` endpoint instead of deriving
      breed data from an image search.
- [X] `BreedDetailScreen`: `life_span!!` / `temperament!!` crash on breeds missing those
      fields — render fallbacks.
- [X] `DogViewModel`/`BreedDetailViewModel`: error branches construct an
      `ApiResult.Error(...)` and discard it. Replace with a real UI state (see below).

Dead code / cruft:

- [X] Delete the Fragment layer: `HomeFragment` (creates a second, orphaned
      NavController), `FragmentViewBindingDelegate`, `res/layout/`, `res/navigation/`,
      `viewBinding = true`.
- [X] Delete unused fields in `DogViewModel` (`_breedDetails`, `_breedImages`).
- [X] Remove unused dependencies: cardview, recyclerview, swiperefreshlayout, preference,
      navigation-fragment/ui-ktx, constraintlayout (view), lifecycle-extensions
      (deprecated), converter-scalars, okhttp-tls, appcompat (after the next item).
- [X] `MainActivity`: `AppCompatActivity` → `ComponentActivity`.
- [X] Remove `Log.d` calls from composables and repository.
- [X] Fix `SharedPetViewModel` file location (`util/` folder, `ui` package) and its
      fragile `LocalContext as ComponentActivity` scoping — hoist it once in
      `MainActivity` and pass it (or its state) down.

Structure & consistency:

- [X] Introduce a proper `UiState` per screen (`Loading / Success / Error`) so the error
      dog image and stubbed error paths become real rendered states.
- [X] Deduplicate `DogImageScreen` — the landscape and portrait branches are ~70 lines of
      copy-paste. Extract the shared card/buttons composable; switch on orientation only
      for the layout container.
- [X] Rename package `com.example.dog` → a real namespace (e.g. `io.github.<you>.dogapp`).
      `com.example` reads as template code to reviewers. Cheapest to do before KMP.
- [X] Map snake_case JSON (`life_span`, `breed_group`, `bred_for`) to camelCase Kotlin
      via annotations, and split API DTOs from a small domain model (`Breed`, `DogImage`)
      — this boundary pays off directly in Phase 5.

**Done when:** all tabs tappable without crashes, `./gradlew lint test` clean, no
Fragment/ViewBinding code remains.

## Phase 2 — Toolchain modernisation

> **Target:** Weeks 3–4, ~3–4h. **Parallel track:** Compose internals — recomposition
> scoping, `remember`/`derivedStateOf`, side-effect APIs. DSA: sliding window basics.

- [X] Kotlin 1.9.22 → 2.3.21 with the `org.jetbrains.kotlin.plugin.compose` compiler
      plugin (drops `composeOptions`/`kotlinCompilerExtensionVersion`).
- [X] AGP → current stable; remove `buildToolsVersion = "32.1.0-rc1"` (an RC older than
      compileSdk — just delete the line and let AGP pick).
- [X] compileSdk/targetSdk → 36 (or current Play requirement).
- [X] JVM target 11 → 17.
- [X] Update Compose BOM, activity-compose, lifecycle, coroutines.
- [X] Coil 2 → Coil 3 (`coil3.*` — multiplatform-ready, needed in Phase 5 anyway).
- [X] Verify the release build still minifies correctly after upgrades.

**Done when:** app builds and runs on Kotlin 2.2.x with the new Compose compiler.

## Phase 3 — DI: Hilt → Koin

> **Target:** Weeks 4–5, ~4–5h. **Parallel track:** architecture reading (MVVM/MVI,
> UDF, offline-first shape). This phase is a direct interview talking point — "DI
> framework only at the edge, ViewModels testable via plain constructor injection" —
> so write that sentence down as you do it.

Do this while still Android-only — it's a mechanical swap and removes kapt entirely
(build-speed win, and kapt/Hilt cannot follow us to KMP).

- [X] Add `koin-android`, `koin-androidx-compose` (Koin 4.x).
- [X] `NetworkModule` → a Koin `module { }`; `@HiltViewModel` → `viewModelOf(...)`
      definitions; `hiltViewModel()` → `koinViewModel()` at call sites.
- [X] `SharedPetViewModel` → activity-scoped via Koin instead of the manual
      `ComponentActivity` cast.
- [X] Remove Hilt, kapt, and the Hilt Gradle plugins.
- [X] Port the ViewModel unit tests to plain constructor injection (they shouldn't need
      Koin at all — that's the talking point: DI framework only at the edge).

**Done when:** no `dagger`/`hilt`/`kapt` references anywhere; app runs; tests pass.

## Phase 4 — Networking: Retrofit/OkHttp/Moshi → Ktor + kotlinx.serialization

> **Target:** Weeks 6–7, ~6–8h. **Parallel track:** finish architecture reading from
> Phase 3; start skimming mobile systems-design material ahead of the Phase 5–6 block.

- [X] Add `ktor-client-core`, `ktor-client-okhttp` (Android engine for now),
      `ktor-client-content-negotiation`, `ktor-serialization-kotlinx-json`,
      kotlinx.serialization plugin.
- [X] DTOs: `@JsonClass`/`@Json` → `@Serializable`/`@SerialName`. Delete Moshi codegen.
- [X] `TheDogApi` interface → a small `DogApiClient` class using Ktor (`defaultRequest`
      for base URL + `x-api-key` header replaces the OkHttp interceptor; `Logging`
      plugin replaces HttpLoggingInterceptor).
- [X] Keep `ApiResult` as the repository return type; map Ktor exceptions into it in one
      place.
- [X] Add repository unit tests with Ktor `MockEngine` (replaces MockWebServer — and
      finally gives the repository real coverage).

**Done when:** Retrofit/Moshi/OkHttp direct deps gone; repository tests green against
MockEngine.

## Phase 5 — Restructure to KMP

> **Target:** Weeks 8–10, ~9–11h (biggest single chunk of the project — budget
> accordingly, don't let it bleed past week 10). **Parallel track:** weekly systems-
> design mock (pick one prompt, talk it through 15–20 min, have Claude probe follow-
> ups). DSA: trees/graphs traversal.

The payoff phase. With phases 3–4 done, almost everything below `ui/` is already
multiplatform-legal code; this phase is mostly *moving* files, not rewriting them.

- [X] Restructure to the JetBrains KMP template shape: `:composeApp` with
      `commonMain` / `androidMain` (+ `iosMain` source set, see Phase 8), `iosApp/`
      Xcode wrapper generated by the template.
- [X] Targets: `androidTarget()` + iOS targets declared from the start (they compile on
      Windows via CI later; only *running* them needs a Mac — see Phase 8).
- [X] Move to `commonMain`: DTOs, domain models, `ApiResult`, `DogApiClient`, repository,
      ViewModels (androidx lifecycle ViewModel is multiplatform now —
      `org.jetbrains.androidx.lifecycle:lifecycle-viewmodel-compose`), Koin modules.
- [X] Swap Ktor engine per platform: OkHttp in `androidMain`, Darwin in `iosMain`.
- [X] API key: `BuildConfig` → BuildKonfig (or expect/actual), still fed from
      `local.properties`.
- [X] Keep the Android app compiling from `androidMain` (`MainActivity`, `DogApp`,
      Coil ImageLoader setup) — UI itself moves next phase.

**Done when:** `:composeApp` compiles for android + iosSimulatorArm64
(`compileKotlinIosSimulatorArm64` — no Mac needed to *compile* Kotlin/Native? it does
need a Mac for linking frameworks; on Windows, treat "androidApp runs + commonMain
compiles" as the local bar and let CI cover iOS).

## Phase 6 — UI to Compose Multiplatform

> **Target:** Weeks 11–12, ~7–9h. **Parallel track:** continue weekly systems-design
> mocks; keep the "image loading pipeline" prompt in the rotation since you're
> literally rebuilding one via Coil 3 this phase — narrate it as you go.

- [X] Move theme, screens, and components to `commonMain` under CMP.
- [X] Resources: `R.string`/`R.drawable` + `painterResource(id)` → Compose Multiplatform
      resources (`Res.string.*`, `Res.drawable.*`, `stringResource(Res.string.…)`).
      Move strings.xml content and the paw/bone drawables into `composeResources`.
- [X] Replace Android-only APIs in composables:
      `LocalConfiguration.current.orientation` → window size class
      (`material3-window-size-class` multiplatform) or `BoxWithConstraints`.
- [X] Images: Coil 3 `AsyncImage` from `coil-compose` (multiplatform) with Ktor network
      fetcher; move the ImageLoader config out of `DogApp` into common Koin setup.
- [X] `MainActivity.setContent` just calls a shared `App()` composable.
- [X] Split the module: `:composeApp` becomes a `com.android.kotlin.multiplatform.library`,
      and a new thin `:androidApp` takes `com.android.application` with `MainActivity`, the
      manifest, and `res/`. Then delete `android.newDsl=false` from `gradle.properties` — it
      is a temporary AGP 9 escape hatch that a future AGP will remove.

**Done when:** `androidMain` contains only `MainActivity`, `DogApp`, and platform glue;
every screen renders from `commonMain`; `android.newDsl=false` is gone.

## Phase 7 — Navigation 3

> **Target:** Weeks 13–15, ~5–6h. **Parallel track:** systems-design mocks continue;
> DSA maintenance reps (1–2 problems/week is enough at this point).

- [ ] **Check current CMP support first**: androidx Navigation 3 (`androidx.navigation3`)
      is Compose-first and KMP-friendly by design, but confirm the multiplatform artifact
      / JetBrains support level *now* rather than trusting this document. If common-code
      support isn't stable yet, implement Nav3 in `androidMain` behind a small common
      navigator interface and note the follow-up.
- [ ] Replace string routes with typed, `@Serializable` nav keys:
      `Home`, `Favourites`, `BreedDetail(breedId: String)` — this deletes the
      `AppDestination` route-string mismatch class of bugs permanently.
- [ ] `NavHost` → `NavDisplay` + `NavBackStack`; bottom bar drives the back stack
      directly and derives `selected` from the top key type.
- [ ] `SharedPetViewModel.currentBreedId` likely dies here: `BreedDetail` taking the id
      as a typed key argument removes the need for a shared "current breed" holder.
      The bottom-bar "Details" item can be disabled until a breed has been viewed.
- [ ] ViewModels via `koinViewModel()` scoped to nav entries
      (nav3 lifecycle-viewmodel integration).

**Done when:** no string routes remain; process-death restore works (typed keys are
Serializable); back behaviour correct from the detail screen.

## Phase 8 — Second platform actually running

> **Target:** Weeks 16–17, ~4–6h. **Parallel track:** start tidying GitHub profile and
> pinning the repo; begin sketching CV bullets from completed phases (Phase 5 alone —
> "migrated a single-module Android app to Kotlin Multiplatform" — is a strong, honest
> line once landed).

You're on Windows, so **iOS cannot be built or run locally** — plan around it instead of
letting it block the CV story:

- [ ] Add a **Desktop (JVM)** target — cheap with CMP, runs on Windows, and screenshots
      of the same UI on two platforms is the money shot for the README. (Optional
      stretch: a `wasmJs` target hosted on GitHub Pages so reviewers can try the app in
      a browser without installing anything.)
- [ ] Keep the iOS source set + `iosApp` wrapper compiling via **GitHub Actions on a
      macOS runner** (free tier is enough for a build check). Locally-untested but
      CI-proven iOS is an honest and respectable CV position — say exactly that in the
      README.

**Done when:** desktop app runs the full flow on Windows; CI builds the iOS framework.

## Phase 9 — CV polish (the part reviewers actually see)

> **Target:** Week 18, ~8–10h. **Parallel track:** none new — this week's study time
> folds into finishing this phase. Once done, DogApp is portfolio-ready and weeks
> 19–20 shift to a timed take-home rehearsal instead of further DogApp work.

- [ ] Implement **Favourites** — the one permitted feature, because two stub buttons
      (tab + FAB) look worse than a missing feature. Smallest honest version: FAB
      toggles the current dog into an in-memory + persisted set
      (`androidx.datastore` KMP or `multiplatform-settings` — **not** Room/SQLDelight,
      that's creep), Favourites screen shows a grid of saved images. Delete the
      non-functional `AnimalTabs` (cats were never coming) or repurpose it inside
      Favourites.
- [ ] **README.md**: what it is, screenshots/GIF (Android + Desktop side by side),
      architecture sketch (common/platform split), stack list with one-line "why" each
      (KMP, CMP, Nav3, Koin, Ktor), how to build, where the API key goes, honest
      "known limitations" section.
- [ ] **GitHub Actions CI**: build + unit tests on every push (ubuntu), iOS compile
      check (macos). A green checkmark on the repo front page matters.
- [ ] Tests to a respectable floor: repository (MockEngine), ViewModels
      (coroutines-test + Turbine for flows), one CMP UI test for the happy path.
      Don't chase coverage numbers.
- [ ] Static analysis: ktlint or detekt with default rules, wired into CI.
- [ ] Delete this file, or move it to `docs/` as a migration log — a written migration
      plan you actually followed is itself worth showing.

---

## Explicitly out of scope (scope-creep tripwires)

- No offline caching / database (Room, SQLDelight).
- No cats, no extra API endpoints beyond breeds + images.
- No design-system rebuild — keep the existing Material 3 theme, just make it consistent.
- No paging, no search, no settings screen.
- No wasm target unless Phase 8 is done and you're bored (it's the only optional item).

## Risks / open questions

1. **Navigation 3 multiplatform maturity** — the biggest unknown. Verify before Phase 7;
   the fallback (Nav3 on Android behind a thin common interface) is fine but slightly
   dilutes the "shared UI" story.
2. **No Mac** — iOS is compile-checked in CI only. Desktop target is the mitigation for
   a demonstrable second platform.
3. **TheDogApi free tier** — the retry-loop fix in Phase 1 matters because the current
   code can burn through rate limits; keep an eye on 429s during testing.
4. **Order rigidity** — Phases 3 and 4 can swap; everything else is genuinely ordered
   (KMP restructure before CMP UI before Nav3).

## After Phase 9 (weeks 19–20)

No further DogApp phases. Instead: a timed rehearsal of a Starling-style take-home —
build one small feature end-to-end against a fresh constraint, under a ~1-week
timebox, using the same stack this project now runs on. See
`Android_Engineer_Prep_Plan.md` for the full interview-prep detail.