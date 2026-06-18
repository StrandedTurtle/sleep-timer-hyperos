import { CloudSun } from "lucide-react";
import { days } from "@/data/itinerary";
import { Hero } from "@/components/Hero";
import { Essentials } from "@/components/Essentials";
import { ActivityCard } from "@/components/ActivityCard";
import {
  Tabs,
  TabsContent,
  TabsList,
  TabsTrigger,
} from "@/components/ui/tabs";

export default function App() {
  return (
    <div className="min-h-dvh">
      <Hero />

      <main>
        <section className="container py-12 sm:py-16">
          <Tabs defaultValue={days[0].key} className="w-full">
            <div className="sticky top-0 z-20 -mx-5 mb-2 bg-background/85 px-5 py-3 backdrop-blur supports-[backdrop-filter]:bg-background/70">
              <TabsList className="w-full sm:w-auto">
                {days.map((day) => (
                  <TabsTrigger
                    key={day.key}
                    value={day.key}
                    className="flex-1 sm:flex-none"
                  >
                    <span className="font-display text-base">{day.label}</span>
                  </TabsTrigger>
                ))}
              </TabsList>
            </div>

            {days.map((day) => (
              <TabsContent key={day.key} value={day.key}>
                <div className="mb-8">
                  <p className="font-mono text-xs uppercase tracking-[0.2em] text-muted-foreground">
                    {day.date}
                  </p>
                  <h2 className="mt-1 text-3xl tracking-tight sm:text-4xl">
                    {day.headline}
                  </h2>
                  <p className="mt-3 flex items-start gap-2 text-sm text-foreground/75">
                    <CloudSun className="mt-0.5 h-4 w-4 flex-none text-accent" />
                    {day.weather}
                  </p>
                </div>

                <div>
                  {day.activities.map((activity, i) => (
                    <ActivityCard
                      key={activity.id}
                      activity={activity}
                      index={i}
                      last={i === day.activities.length - 1}
                    />
                  ))}
                </div>
              </TabsContent>
            ))}
          </Tabs>
        </section>

        <div className="border-y border-border bg-card/40">
          <Essentials />
        </div>
      </main>

      <footer className="container py-10">
        <p className="max-w-2xl text-xs leading-relaxed text-muted-foreground">
          Plan put together for a long weekend on the Dorset coast. Tides,
          weather, and climbing access change constantly — always reconfirm tide
          times, swell, and bird-nesting restrictions on the day before relying
          on the coastal plans. Climb and fossil-hunt within your limits and
          stay clear of unstable cliffs.
        </p>
        <p className="mt-4 font-mono text-[0.7rem] uppercase tracking-[0.2em] text-primary/70">
          Chilfrome · Jurassic Coast · {new Date().getFullYear()}
        </p>
      </footer>
    </div>
  );
}
