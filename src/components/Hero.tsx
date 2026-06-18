import { MapPin, CalendarDays } from "lucide-react";
import { BASE } from "@/data/itinerary";

export function Hero() {
  return (
    <header className="relative overflow-hidden border-b border-border">
      {/* layered strata backdrop */}
      <div
        className="pointer-events-none absolute inset-0 opacity-[0.12]"
        aria-hidden="true"
        style={{
          backgroundImage:
            "repeating-linear-gradient(175deg, hsl(var(--stone)) 0px, hsl(var(--stone)) 2px, transparent 2px, transparent 22px), repeating-linear-gradient(175deg, transparent 0px, transparent 40px, hsl(var(--fossil)) 40px, hsl(var(--fossil)) 41px)",
        }}
      />
      <div className="container relative py-14 sm:py-20">
        <p className="flex items-center gap-2 font-mono text-xs uppercase tracking-[0.2em] text-primary">
          <span className="inline-block h-2 w-2 rounded-full bg-accent" />
          Jurassic Coast field plan
        </p>
        <h1 className="mt-4 max-w-3xl text-5xl leading-[0.95] tracking-tight sm:text-7xl">
          A weekend of rock,
          <br />
          <span className="text-primary">190 million years</span> in the making
        </h1>
        <p className="mt-5 max-w-xl text-base leading-relaxed text-foreground/80 sm:text-lg">
          Two days out of Chilfrome — an Iron Age hillfort, fossil hunting on the
          Jurassic Coast, then a full day of bouldering and a deep water solo on
          Portland.
        </p>
        <div className="mt-7 flex flex-wrap gap-x-5 gap-y-2 font-mono text-sm text-muted-foreground">
          <span className="flex items-center gap-2">
            <MapPin className="h-4 w-4 text-primary" />
            Based at {BASE.name}, {BASE.region}
          </span>
          <span className="flex items-center gap-2">
            <CalendarDays className="h-4 w-4 text-primary" />
            {BASE.note}
          </span>
        </div>
      </div>
    </header>
  );
}
