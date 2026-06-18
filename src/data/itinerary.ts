export type ActivityType = "hike" | "fossil" | "boulder" | "dws" | "water";

export interface RefLink {
  label: string;
  url: string;
}

export interface Activity {
  id: string;
  type: ActivityType;
  title: string;
  time: string;
  duration: string;
  location: string;
  driveFromBase: string;
  summary: string;
  /** climbing grade guidance shown as a mono data row */
  grades?: string;
  /** crag / route names to type into the Rockfax app */
  rockfax?: string[];
  tips: string[];
  /** prominent safety call-out */
  warning?: string;
  coords: { lat: number; lng: number };
  links: RefLink[];
  optional?: boolean;
}

export interface Day {
  key: string;
  label: string;
  date: string;
  headline: string;
  weather: string;
  activities: Activity[];
}

const maps = (lat: number, lng: number) =>
  `https://www.google.com/maps/search/?api=1&query=${lat},${lng}`;

export const BASE = {
  name: "Chilfrome",
  region: "Dorset",
  note: "Friday evening arrival · home Sunday evening",
  coords: { lat: 50.787777, lng: -2.5863649 },
};

export const days: Day[] = [
  {
    key: "sat",
    label: "Saturday",
    date: "Sat 20 Jun",
    headline: "Hillfort & Fossils",
    weather: "Cloudy, dry · high ~21°C — a walking-and-beachcombing day",
    activities: [
      {
        id: "eggardon",
        type: "hike",
        title: "Eggardon Hill",
        time: "9:30 AM",
        duration: "~1.5 hrs",
        location: "Iron Age hillfort, near Powerstock",
        driveFromBase: "~25 min drive",
        summary:
          "A fairly flat circular walk on the plateau of a 2,500-year-old hillfort, with huge views west and out to the Channel. Gentle enough for everyone, including the parents.",
        tips: [
          "Park in the lay-by at the bottom of the hill — there are no toilets or facilities up top.",
          "Don't leave anything valuable visible in the car: a few walkers have reported break-ins at the lay-by.",
          "Expect grazing cattle (and Highland cows) on the ramparts — keep any dogs on leads and give them space.",
          "The ramparts are steeper than the plateau if you want to push the pace a little.",
        ],
        coords: { lat: 50.75008, lng: -2.650572 },
        links: [
          { label: "Open in Maps", url: maps(50.75008, -2.650572) },
          {
            label: "National Trust info",
            url: "https://www.nationaltrust.org.uk/visit/dorset/eggardon-hill",
          },
        ],
      },
      {
        id: "kingcombe",
        type: "hike",
        title: "Kingcombe Meadows (optional)",
        time: "Late morning",
        duration: "30–60 min",
        location: "Toller Porcorum",
        driveFromBase: "~10 min drive",
        summary:
          "Practically on Chilfrome's doorstep. A gentler wildflower-meadow alternative or add-on, with a small cafe for coffee and cake before the drive to the coast.",
        optional: true,
        tips: [
          "Good fallback if anyone wants something flatter than the hillfort.",
          "Cafe and toilets on site — handy before the longer drive to Charmouth.",
        ],
        coords: { lat: 50.7891067, lng: -2.6335853 },
        links: [{ label: "Open in Maps", url: maps(50.7891067, -2.6335853) }],
      },
      {
        id: "charmouth",
        type: "fossil",
        title: "Charmouth — fossil hunting",
        time: "1:00 PM",
        duration: "2–3 hrs",
        location: "Charmouth Heritage Coast Centre",
        driveFromBase: "~40 min drive",
        summary:
          "One of the most reliable fossil beaches in the UK — ammonites, belemnites and ichthyosaur bone wash loose out of the cliffs. Start at the Heritage Coast Centre for the day's hotspot, then search the shingle east toward Black Ven.",
        warning:
          "Hunt the loose shingle on the beach, NOT the cliffs — the cliffs are unstable and rockfalls are common. Check tide times the day before and head down on a low or falling tide so you don't get cut off.",
        tips: [
          "Pop into the Heritage Coast Centre first (free) — wardens tell you where finds have turned up that day and run guided walks.",
          "You can hire a fossil hammer from the beach shop; goggles if you plan to split nodules.",
          "Best finds are spiral ammonites, bullet-shaped belemnites and dark fossilised bone, sitting loose in the shingle.",
          "Use the JustPark app or bring change for the beach car park.",
        ],
        coords: { lat: 50.7334769, lng: -2.9019721 },
        links: [
          { label: "Open in Maps", url: maps(50.7334769, -2.9019721) },
          { label: "Heritage Coast Centre", url: "https://www.charmouth.org/" },
          {
            label: "Lyme Regis / Charmouth tides",
            url: "https://www.dorset-tides.com/lyme-regis-tide-times",
          },
        ],
      },
    ],
  },
  {
    key: "sun",
    label: "Sunday",
    date: "Sun 21 Jun",
    headline: "Portland Boulders, Lulworth Splash",
    weather: "Sunny · high ~28°C — the warmest day, best for getting wet",
    activities: [
      {
        id: "cuttings",
        type: "boulder",
        title: "The Cuttings — bouldering",
        time: "8:00 AM",
        duration: "~3 hrs",
        location: "East coast of Portland",
        driveFromBase: "~35 min drive",
        summary:
          "600+ problems in one sprawling boulderfield, short flat-landing approaches, and a grade spread that keeps the whole group together — easy slabs for the parents to try, plenty of 5s and low 6s for your mates, and the New Cuttings test-pieces for your project.",
        grades:
          "f3 slabs up to f7a boulders",
        rockfax: [
          "Cuttings Boulderfield",
          "New Cuttings",
          "Rain Shadow Wall (sheltered if breezy)",
        ],
        warning:
          "Portland limestone runs polished and a touch sandbagged on the popular lines — they can feel a grade or two stiffer than indoors. Warm up well below your max before committing to the project.",
        tips: [
          "Free parking near Portland Museum; the field is an easy walk from the road.",
          "Mostly flat landings, but a few pads and a spotter make the higher problems comfortable.",
          "Navigation can be fiddly across the boulderfield — use the Rockfax map and tick off one area at a time.",
          "East-facing, so it catches morning sun and gives shelter from the prevailing south-westerly.",
        ],
        coords: { lat: 50.5403949, lng: -2.4248791 },
        links: [
          { label: "Open in Maps", url: maps(50.5403949, -2.4248791) },
          {
            label: "Dorset Bouldering (Rockfax)",
            url: "https://rockfax.com/climbing-guides/books/dorset-bouldering/",
          },
        ],
      },
      {
        id: "stairhole",
        type: "dws",
        title: "Stair Hole, Lulworth — deep water solo",
        time: "12:00 PM",
        duration: "~2 hrs",
        location: "Stair Hole, Lulworth Cove",
        driveFromBase: "~25 min from the Cuttings",
        summary:
          "Lulworth's classic DWS venue, a 5-minute walk from the main cove car park — overhanging walls dropping straight into deep water, with a grade spread from easy traverses to serious test-pieces. Friends can be paddleboarding in the cove right alongside you.",
        optional: true,
        grades:
          "Maypole's 6a traverse is the standard first-timer's line, up to E1/8a test-pieces for the strong — start low and build up",
        rockfax: [
          "Stair Hole (Lulworth)",
          "Maypole",
          "Animal Magnetism",
          "Hornier Than Thou",
          "Laws Traverse",
        ],
        warning:
          "Check tide AND swell before anyone jumps — know what's under the surface and how you'll climb back out. Never solo alone: keep someone watching from the side at all times, and the water is cold enough to shock, so plan to warm up fast afterwards. Keep a low profile while you're there — Stair Hole attracts media attention and tombstoners, and a discreet approach helps keep access goodwill intact.",
        tips: [
          "First time doing this? Maypole (6a, S0 — no swimming seriousness) is the established beginner line, and it finishes back at the beach — no rope or abseil gear needed for it.",
          "Quick check of the BMC RAD app first for the latest Lulworth access notes.",
          "Bring a few pairs of rock shoes and several chalk bags — they get wet.",
          "Sunday's heat makes this the right day for it — Saturday would be a cold drop.",
        ],
        coords: { lat: 50.6181, lng: -2.2525 },
        links: [
          { label: "Open in Maps", url: maps(50.6181, -2.2525) },
          { label: "BMC Regional Access Database", url: "https://services.thebmc.co.uk/modules/rad/view.aspx?id=161" },
          {
            label: "Lulworth tide times",
            url: "https://www.tidetimes.org.uk/lulworth-cove-tide-times",
          },
        ],
      },
      {
        id: "lulworth-sup",
        type: "water",
        title: "Lulworth Cove — paddleboard",
        time: "12:00 PM",
        duration: "1–2 hrs",
        location: "Lulworth Cove",
        driveFromBase: "~25 min from the Cuttings",
        summary:
          "SUP hire by the hour or day from Lulworth Activities or Jurassic Coast Activities, right on the cove. Stair Hole is a 5-minute walk away, so you can watch (or cheer on) the deep water solo while you paddle. The classic SUP route is paddling out from the sheltered cove to Durdle Door's arch and back.",
        optional: true,
        tips: [
          "Suits all abilities — the cove itself is sheltered and calm.",
          "Hire is usually walk-up in summer, no booking needed.",
          "Check conditions before heading round to Durdle Door — it's more exposed than the cove.",
          "Want real surf instead? Kimmeridge Bay is the nearest spot — a reef break that only works with a clean swell, so it's worth checking a surf forecast app on the day rather than planning around it.",
        ],
        coords: { lat: 50.6211, lng: -2.2493 },
        links: [{ label: "Open in Maps", url: maps(50.6211, -2.2493) }],
      },
    ],
  },
];

export interface Essential {
  title: string;
  body: string;
  links?: RefLink[];
}

export const essentials: Essential[] = [
  {
    title: "Tides — check the day before",
    body: "Everything coastal hangs on the tide. For Charmouth you want a low or falling tide so you can search safely and not get cut off near the cliffs. For Stair Hole, the tide and swell decide whether the deep water solo is on at all and how deep the landing is.",
    links: [
      { label: "Lyme Regis / Charmouth tides", url: "https://www.dorset-tides.com/lyme-regis-tide-times" },
      { label: "Portland tides", url: "https://www.tidetimes.org.uk/portland-tide-times" },
      { label: "Lulworth Cove tides", url: "https://www.tidetimes.org.uk/lulworth-cove-tide-times" },
    ],
  },
  {
    title: "Weather window",
    body: "Friday arrival is dry. Saturday is cooler and cloudy — ideal for the hillfort walk and beachcombing. Sunday is the warm, sunny one (~28°C), which is why the deep water solo sits on Sunday afternoon rather than Saturday.",
  },
  {
    title: "Climbing access at Stair Hole",
    body: "Stair Hole sits on Open Access land (CRoW), so climbing and DWS there is legally permitted. It does periodically draw media attention and tombstoning by non-climbers though, so keep a low profile while you're there — it helps keep access goodwill intact. Check the BMC's live access database before you head over for any current notices.",
    links: [
      { label: "BMC Regional Access Database (RAD)", url: "https://services.thebmc.co.uk/modules/rad/view.aspx?id=161" },
    ],
  },
  {
    title: "Deep water solo safety",
    body: "DWS has no rope, so the basics matter: never solo alone, always have a spotter watching from the side, know your exit before you start, and check the water depth and what's submerged. Cold-water shock is real even in summer — get warm and dry quickly after a fall.",
  },
];

export interface PackGroup {
  group: string;
  items: string[];
}

export const packing: PackGroup[] = [
  {
    group: "Climbing",
    items: [
      "Rock shoes (a couple of pairs — DWS pairs get soaked)",
      "Several chalk bags",
      "Bouldering pads + brushes",
      "Short rope & a little gear for setting up an abseil / exit line",
      "Rockfax app downloaded offline (Dorset Bouldering)",
      "Quick-dry shorts, towel and warm layers for after the swim",
    ],
  },
  {
    group: "Fossils & walking",
    items: [
      "Fossil hammer & safety goggles (or hire at Charmouth)",
      "Small bag/box for finds",
      "Walking shoes with grip for shingle and the hillfort",
      "Waterproof just in case + sun cream (Sunday is hot)",
      "Refillable water bottles and snacks",
    ],
  },
  {
    group: "Logistics",
    items: [
      "Cash / JustPark app for beach and crag car parks",
      "Tide times screenshotted for Sat & Sun",
      "Phone power bank (offline maps drain it)",
      "First aid kit & any group meds",
    ],
  },
];
