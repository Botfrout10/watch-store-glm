# DESIGN.md — "Light minimal Swiss" design system

Referenced patterns (Mobbin/Dribbble e-commerce watch apps): editorial product presentation,
hairline grids, tabular numerals, restrained accent usage. This app is a watch BOUTIQUE,
not a gadget store — typography and precision carry the identity.

## Brand & palette (light only)

| Token | Value | Usage |
|---|---|---|
| `paper` | #FAFAF8 | app background |
| `card` | #FFFFFF | product cards on paper |
| `ink` | #0F0F0F | primary text, logo, icons |
| `ink60` | #6E6E68 | secondary text (warm gray) |
| `hairline` | #E4E2DC (1dp) | dividers — visible Swiss grid discipline |
| `red` | #E10600 | signal red: CTA, active states, live dial pendulum, progress dots, badge |
| `redSoft` | rgba(225,6,0,0.08) | pressed fills, favorite heart track |
| `steel` | #B8BCBE | metallic accents in imagery gradients |

Rules: red is *rare* (≤2 live accents per screen). No drop shadows except hero imagery
(soft large-radius ambient). Cards are flat white on paper, separated by hairlines.

## Typography

- Display/UI: **Space Grotesk** (400/500/700) — headlines at 56/34/22sp, tight negative tracking (-0.5 to -1.5), sentence case only, generous letterspacing (2-4sp) for EYEBROW caps labels at 11-12sp.
- Numerals/prices/specs: **IBM Plex Mono** (400/600) — everything numeric lines up (prices, reference numbers, delivery dates, countdowns).
- Scale: displayXL 56, display 34, headline 22, title 18, body 15/1.55, caption 12 mono.

## Grid and spacing

- 8dp base grid; screen margins 20dp; card radius 2dp (sharp Swiss edges; avatars/roundels only).
- Editorial masthead pattern on list headers: eyebrow (mono, red dot) → display headline → hairline → content.
- Hairline separators span full bleed; content snaps to the grid.

## Iconography

- Custom 24dp vector set (line style, 2dp stroke, ink; filled variant where stateful):
  home, grid/catalog, search, heart (outline+filled), cart bag, back arrow, chevron r/d,
  plus/minus, close, check, star, share, filter, delivery truck, shield/returns.
- Red used only for active/filled states.

## Screens & motion inventory (pure Compose, no Lottie)

**Onboarding (3 pages, parallax)** — `01 02 03` giant ghost numerals; each page has a live
Canvas-drawn Swiss railway dial (tick second-hand with red pendulum disc, ~1 rad/min sweep,
sub-tick high-rate animation), staggered fade+slide-in copy (60ms steps), spring page indicator
(growing red dot), Skip / Explore Store CTA. Exit: shared-axis to Home.

**Home** — masthead "HOROLOGY Nº 01" w/ red dot; featured hero (parallax on scroll, 1:1 image,
mono price + name), collections rail (press-scale 0.97, border-glow on drag), staggered 75ms
product-grid entrance, bottom nav: sliding red indicator dot, badge pop on cart add (spring overshoot).

**Catalog** — sticky search field (morph underline), filter chips with animated row re-layout
(grid fade-through 150ms), debounced search, ghost skeleton pulse while refining.

**Product detail** — shared-axis slide entry; image gallery w/ swipe dots + crossfade 180ms;
dial spec sheet as expandable accordion (animateContentSize + chevron rotate); mono price;
heart burst (scale 1→1.35→1 + redSoft ring pulse); add-to-cart: button morphs to "added ✓"
then a red dot FLIES to cart badge (parabolic path ~450ms) and badge pops.

**Cart** — swipe-to-dismiss rows (item scale+fade, grid re-layout spring), quantity stepper w/
springy plus/minus, mono total with count-up animation on change.

**Checkout confirm** — red circle draws check (path progress 300ms, spring overshoot), order
number "ticker" reveal, confetti-free (keep Swiss restraint).

**Favorites** — heart pre-filled, unfill animates shrink; list uses same cards.

**Motion principles**: springs (mediumBouncy for motion, fast for state), animateContentSize for
layout changes, no linear animation except dial tick; everything interruptible; 60ms staggering
cascade; reduce-motion respected (win/iOS setting, halved durations).

## Imagery

- Product shots: verified Unsplash/Pexels URLs (HEAD 200 + image/jpeg), Coil w/ crossfade
  and per-product generated gradient placeholder (steel/paper tones) behind.
- Every product card: image on `card` white, name in Grotesk, price in Plex Mono, hairline frame.
