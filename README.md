# Jurassic Weekend — Chilfrome Field Plan

A mobile-friendly itinerary web app for a long weekend on the Dorset coast:
an Iron Age hillfort walk, fossil hunting at Charmouth, and a day of bouldering
and deep water soloing on Portland.

Built with **Vite + React + TypeScript + Tailwind CSS + shadcn/ui**.

---

## Run it locally

You need [Node.js](https://nodejs.org/) 18+ installed.

```bash
npm install
npm run dev
```

Open the URL it prints (usually http://localhost:5173).

To build the production version locally:

```bash
npm run build
npm run preview
```

---

## Put it on GitHub

```bash
git init
git add .
git commit -m "Jurassic weekend itinerary app"
git branch -M main
git remote add origin https://github.com/<your-username>/jurassic-weekend.git
git push -u origin main
```

(Create the empty repo on github.com first, then use its URL above.)

---

## Deploy to Vercel

1. Go to [vercel.com](https://vercel.com) and sign in with GitHub.
2. Click **Add New → Project** and import the `jurassic-weekend` repo.
3. Vercel auto-detects Vite. Leave the defaults:
   - **Framework Preset:** Vite
   - **Build Command:** `npm run build`
   - **Output Directory:** `dist`
4. Click **Deploy**. You'll get a shareable `*.vercel.app` link in about a minute.

Every push to `main` redeploys automatically.

---

## Editing the content

All the itinerary text lives in one file:

```
src/data/itinerary.ts
```

- `days` — the Saturday/Sunday activities (times, tips, grades, Rockfax names, links).
- `essentials` — the "Before you go" notes (tides, weather, access, safety).
- `packing` — the kit list.

Change the text there and the page updates. No other files need touching for
content edits.

### Add more shadcn/ui components

This project is wired for the shadcn CLI (`components.json` is set up):

```bash
npx shadcn@latest add dialog
```

New components land in `src/components/ui`.

---

## Project structure

```
src/
├── App.tsx                 # page composition (hero, day tabs, essentials)
├── main.tsx                # entry point
├── index.css               # Tailwind + the Jurassic field-guide palette
├── data/itinerary.ts       # ← all the content lives here
├── lib/utils.ts            # cn() helper
└── components/
    ├── Hero.tsx
    ├── Essentials.tsx
    ├── ActivityCard.tsx    # the strata-rail timeline card
    └── ui/                 # shadcn components (button, card, badge, tabs, accordion)
```

---

## A note on safety

Tides, swell, weather and climbing access change constantly. Always reconfirm
tide times, sea conditions and bird-nesting restrictions (via the
[BMC RAD](https://www.thebmc.co.uk/bmc-regional-access-database-rad)) on the day
before relying on any of the coastal plans, and stay clear of unstable cliffs.
