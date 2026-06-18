import {
  Mountain,
  Gem,
  Grip,
  Waves,
  Clock,
  Car,
  MapPin,
  AlertTriangle,
  ExternalLink,
  BookOpen,
} from "lucide-react";
import type { Activity, ActivityType } from "@/data/itinerary";
import { Card, CardContent } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { cn } from "@/lib/utils";

const typeMeta: Record<
  ActivityType,
  { label: string; icon: typeof Mountain; color: string }
> = {
  hike: { label: "Hike", icon: Mountain, color: "text-hike" },
  fossil: { label: "Fossils", icon: Gem, color: "text-fossil" },
  boulder: { label: "Bouldering", icon: Grip, color: "text-boulder" },
  dws: { label: "Deep water solo", icon: Waves, color: "text-dws" },
};

const railColor: Record<ActivityType, string> = {
  hike: "bg-hike",
  fossil: "bg-fossil",
  boulder: "bg-boulder",
  dws: "bg-dws",
};

export function ActivityCard({
  activity,
  index,
  last,
}: {
  activity: Activity;
  index: number;
  last: boolean;
}) {
  const meta = typeMeta[activity.type];
  const Icon = meta.icon;

  return (
    <div className="relative flex gap-4 sm:gap-5">
      {/* Strata rail — the day as a stack of rock layers */}
      <div className="relative flex w-9 flex-none flex-col items-center sm:w-12">
        <div
          className={cn(
            "z-10 flex h-9 w-9 flex-none items-center justify-center rounded-full text-primary-foreground shadow-sm sm:h-12 sm:w-12",
            railColor[activity.type]
          )}
          aria-hidden="true"
        >
          <Icon className="h-4 w-4 sm:h-5 sm:w-5" />
        </div>
        {!last && (
          <div
            className="mt-1 w-px flex-1 bg-[repeating-linear-gradient(180deg,hsl(var(--border)),hsl(var(--border))_5px,transparent_5px,transparent_11px)]"
            aria-hidden="true"
          />
        )}
      </div>

      <Card
        className="mb-7 flex-1 animate-layer-in overflow-hidden"
        style={{ animationDelay: `${index * 90}ms` }}
      >
        <CardContent className="p-0">
          {/* stratum edge */}
          <div className={cn("h-1.5 w-full", railColor[activity.type])} />

          <div className="p-5">
            <div className="flex flex-wrap items-center gap-2">
              <Badge variant="outline" className={cn("gap-1", meta.color)}>
                {meta.label}
              </Badge>
              {activity.optional && (
                <Badge variant="secondary">Optional</Badge>
              )}
              <span className="ml-auto font-mono text-xs text-muted-foreground">
                {activity.time}
              </span>
            </div>

            <h3 className="mt-3 text-2xl leading-tight">{activity.title}</h3>

            <div className="mt-3 grid grid-cols-1 gap-1.5 font-mono text-xs text-muted-foreground sm:grid-cols-3">
              <span className="flex items-center gap-1.5">
                <MapPin className="h-3.5 w-3.5" /> {activity.location}
              </span>
              <span className="flex items-center gap-1.5">
                <Car className="h-3.5 w-3.5" /> {activity.driveFromBase}
              </span>
              <span className="flex items-center gap-1.5">
                <Clock className="h-3.5 w-3.5" /> {activity.duration}
              </span>
            </div>

            <p className="mt-4 text-sm leading-relaxed text-foreground/90">
              {activity.summary}
            </p>

            {activity.grades && (
              <div className="mt-4 rounded-md border border-border bg-secondary/60 px-3 py-2">
                <span className="font-mono text-[0.7rem] uppercase tracking-wider text-muted-foreground">
                  Grades
                </span>
                <p className="mt-0.5 font-mono text-sm text-foreground">
                  {activity.grades}
                </p>
              </div>
            )}

            {activity.rockfax && (
              <div className="mt-3">
                <span className="flex items-center gap-1.5 font-mono text-[0.7rem] uppercase tracking-wider text-muted-foreground">
                  <BookOpen className="h-3.5 w-3.5" /> Search in Rockfax
                </span>
                <div className="mt-2 flex flex-wrap gap-1.5">
                  {activity.rockfax.map((r) => (
                    <span
                      key={r}
                      className="rounded border border-border bg-background px-2 py-0.5 font-mono text-xs text-foreground/80"
                    >
                      {r}
                    </span>
                  ))}
                </div>
              </div>
            )}

            {activity.warning && (
              <div className="mt-4 flex gap-2.5 rounded-md border border-destructive/30 bg-destructive/10 p-3">
                <AlertTriangle className="mt-0.5 h-4 w-4 flex-none text-destructive" />
                <p className="text-sm leading-relaxed text-foreground/90">
                  {activity.warning}
                </p>
              </div>
            )}

            <ul className="mt-4 space-y-2">
              {activity.tips.map((tip) => (
                <li key={tip} className="flex gap-2.5 text-sm leading-relaxed">
                  <span
                    className={cn(
                      "mt-2 h-1.5 w-1.5 flex-none rounded-full",
                      railColor[activity.type]
                    )}
                    aria-hidden="true"
                  />
                  <span className="text-foreground/80">{tip}</span>
                </li>
              ))}
            </ul>

            <div className="mt-5 flex flex-wrap gap-2">
              {activity.links.map((link) => (
                <a
                  key={link.url}
                  href={link.url}
                  target="_blank"
                  rel="noreferrer"
                  className="inline-flex items-center gap-1.5 rounded-md border border-border bg-background px-3 py-1.5 text-xs font-medium text-foreground transition-colors hover:border-primary hover:text-primary focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 focus-visible:ring-offset-background"
                >
                  {link.label}
                  <ExternalLink className="h-3 w-3" />
                </a>
              ))}
            </div>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}
