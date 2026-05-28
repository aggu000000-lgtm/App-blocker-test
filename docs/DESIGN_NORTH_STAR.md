# App Blocker — UI North Star

> One-line goal: **A minimalist app where every touch produces a delightful, physically-believable response.**

This document is the design contract. Any PR that touches UI must satisfy these principles, or it's not done.

---

## 1. The vibe in one sentence

**Linear meets Things 3 meets Arc Browser** — quiet by default, alive on interaction.

Not Material-3-default. Not Duolingo-loud. **Minimal surfaces, maximal motion.**

---

## 2. The Five Laws

These are non-negotiable. If a screen doesn't pass all five, it ships incomplete.

### Law 1 — **Nothing is static**
Every interactive element must visibly respond to *being touched*, not just to *being released*.
- Press: subtle scale-down (`0.96`), spring physics, < 80 ms response
- Release: spring-back overshoot (`1.0` with damping ratio ~0.5)
- Long-press: distinct secondary state (color shift + haptic)
- **If you can tap it and nothing visually moves, it's broken.**

### Law 2 — **Gradients are reactive, not decorative**
Gradients respond to state, gesture, or context — they are never wallpaper.
- Idle: flat / very subtle
- On touch: a gradient *blooms* from the touch point (radial, follows finger)
- Active/selected state: gradient is brighter, animates slowly (5–10s loop)
- Background can have a subtle slow-moving mesh gradient that responds to scroll/time

### Law 3 — **Whitespace is the design**
Cards, dividers, and borders are a last resort. Hierarchy comes from **type + spacing + color value**, not boxes.
- Default surface: solid neutral, **no card backgrounds** unless a card is its own tappable unit
- No divider lines between list items — use 20–28 dp spacing instead
- Iconography: outline weight, 1.5 px stroke, only where it adds meaning
- One accent color per screen. Never two.

### Law 4 — **Motion has weight**
Animations obey physics, not curves. We use `spring()`, not `tween()`, by default.
- Page transitions: shared-axis horizontal slide + fade, 350 ms with spring
- List items: stagger-in on first appear (40 ms between items)
- Toggle switches: thumb has *momentum*, overshoots, settles
- Removing an item: it physically falls away (slide + fade + scale)

### Law 5 — **Haptics are mandatory**
Every successful interaction has a haptic. Every failed interaction has a *different* haptic.
- Tap: `HapticFeedbackType.LongPress` (light tick)
- Toggle on: medium tick
- Toggle off: light tick
- Destructive confirm: heavy tick
- Error/blocked action: double-tick

---

## 3. Visual System

### Color
- **Background:** near-white in light mode (`#FAFAF7`), near-black in dark (`#0E0E10`) — never pure white/black
- **One accent per screen.** App-wide accent: a single warm gradient → `#FF6B47 → #FF3B5C` (sunset coral). Used sparingly.
- **Semantic colors only:** success, danger, warning. Never decorative.
- Surfaces have **three depths max:** base, raised, floating. Differentiate by tint shift of 2–4% only, never by stroke.

### Typography
- **Display font:** custom (Inter, Manrope, or General Sans). Variable weight.
- **Display sizes are LARGE and TIGHT:** headlines 48–64 sp, line-height 0.95×, letter-spacing -2%.
- **Body is calm:** 16 sp, line-height 1.5×, neutral weight.
- **Numbers are tabular** (versions, counts, durations).
- Use weight variation (300 / 500 / 700) as a design tool, not just size.

### Spacing scale
Stick to this scale, period: `4, 8, 12, 16, 24, 32, 48, 64, 96`. No arbitrary values.

### Corners
- Buttons / pills: fully rounded (height/2)
- Cards / sheets: 20 dp
- Bottom sheets: 28 dp top corners only
- Never 4 dp or 8 dp — looks 2014.

### Iconography
- One icon family throughout (Lucide or Phosphor — NOT Material default)
- 1.5 px stroke, 24 dp default size
- Stroke icons only. No filled icons except for active/selected state.

---

## 4. Interaction Patterns That Must Exist

Every screen ships with these or it's not done:

| Pattern | Where |
|---|---|
| **Touch ripple → gradient bloom** | Every button, every list row |
| **Spring scale on press** | Every tappable surface |
| **Haptic on every confirm** | Toggles, FAB, navigation |
| **Page transition with shared axis** | Tab switches, navigation pushes |
| **Pull-to-refresh with custom animation** | Any list (animated, not stock) |
| **Empty states with personality** | Empty Blocker list, empty search results |
| **Loading skeletons, not spinners** | Any async load |
| **Snackbars that float in with overshoot** | All confirmations |
| **Long-press for secondary actions** | List items (preview, delete) |

---

## 5. Screens Spec (current app)

### Home
- Hero number: time saved today / focus streak, in 96-sp display weight
- One soft mesh-gradient background, slowly animating
- One primary CTA, pill-shaped, accent gradient
- No cards. No subtitles. Quiet.

### Blocker (list)
- No card chrome. Each rule is a row with: name (titleLarge), one-line subtext (bodyMedium), and a switch
- Toggle on: row tints with accent gradient bloom from the switch outward
- Long-press a row: row lifts, scales 1.02, haptic, reveals delete/edit
- Adding a rule: FAB morphs into a full-screen sheet (container transform)
- Empty state: large illustration + one-line copy + single CTA

### Settings
- Grouped, but grouped via spacing — **no card backgrounds**
- Section titles in `labelSmall`, uppercase, tracking +8%, low-emphasis color
- Toggles spring, haptic, tint with accent
- Tap a destructive item → full-screen confirm sheet, not a dialog

---

## 6. The Anti-Patterns (instant PR rejection)

🚫 Material Card with elevation around every group
🚫 Material default Switch (use a custom one with bigger thumb + better spring)
🚫 `tween()` animations without justification
🚫 Two accent colors on one screen
🚫 Dividers between list items
🚫 Filled Material icons
🚫 Snackbars that snap in instantly
🚫 Spinners (use skeletons or progress that *means* something)
🚫 Toasts (use snackbars)
🚫 AlertDialogs (use bottom sheets)
🚫 Tappable element without haptic
🚫 Tappable element without press-state animation

---

## 7. Definition of Done (per PR)

A UI PR is mergeable only if:

- [ ] Every new interactive element has a press animation
- [ ] Every new interactive element triggers a haptic
- [ ] No hardcoded colors — all from `MaterialTheme.colorScheme` or a defined token
- [ ] No hardcoded sizes — all from `MaterialTheme.spacing` or scale-compliant
- [ ] No `tween()` without a comment explaining why spring wouldn't work
- [ ] Screen reviewed in **light + dark** modes
- [ ] Screen reviewed on a small phone (compact width)
- [ ] At least one "delight moment" added or preserved per screen

---

## 8. Reference apps

When in doubt, look at:
- **Arc Browser (mobile)** — for restraint + motion
- **Linear** — for typography + spacing
- **Things 3** — for quiet interactions
- **Raycast** — for keyboard-style snappy responses
- **Apple Weather** — for ambient gradient backgrounds
- **Headspace** — for soft mesh gradients
- **Robinhood (old)** — for hero numbers and minimal chrome

Do *not* reference:
- Material 3 sample apps
- Standard Google apps (too utilitarian)
- Duolingo (too loud for our brand)

---

*Version 1.0 — established as the design contract on 2026-05-28.*
*Any change to this document requires explicit owner approval.*
