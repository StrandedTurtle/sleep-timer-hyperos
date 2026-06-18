import { ExternalLink } from "lucide-react";
import { essentials, packing } from "@/data/itinerary";
import {
  Accordion,
  AccordionContent,
  AccordionItem,
  AccordionTrigger,
} from "@/components/ui/accordion";

export function Essentials() {
  return (
    <section className="container py-12 sm:py-16">
      <div className="grid gap-10 lg:grid-cols-2">
        <div>
          <h2 className="text-3xl tracking-tight sm:text-4xl">
            Before you go
          </h2>
          <p className="mt-2 max-w-md text-sm text-muted-foreground">
            The handful of things that decide whether the coastal plans actually
            happen on the day.
          </p>

          <Accordion
            type="single"
            collapsible
            defaultValue="item-0"
            className="mt-6"
          >
            {essentials.map((e, i) => (
              <AccordionItem key={e.title} value={`item-${i}`}>
                <AccordionTrigger>{e.title}</AccordionTrigger>
                <AccordionContent>
                  <p className="leading-relaxed text-foreground/80">{e.body}</p>
                  {e.links && (
                    <div className="mt-3 flex flex-wrap gap-2">
                      {e.links.map((l) => (
                        <a
                          key={l.url}
                          href={l.url}
                          target="_blank"
                          rel="noreferrer"
                          className="inline-flex items-center gap-1.5 rounded-md border border-border bg-background px-3 py-1.5 text-xs font-medium transition-colors hover:border-primary hover:text-primary"
                        >
                          {l.label}
                          <ExternalLink className="h-3 w-3" />
                        </a>
                      ))}
                    </div>
                  )}
                </AccordionContent>
              </AccordionItem>
            ))}
          </Accordion>
        </div>

        <div>
          <h2 className="text-3xl tracking-tight sm:text-4xl">Kit list</h2>
          <p className="mt-2 max-w-md text-sm text-muted-foreground">
            Packed by what you're doing, not where you're going.
          </p>

          <div className="mt-6 space-y-6">
            {packing.map((group) => (
              <div key={group.group}>
                <h3 className="font-mono text-xs uppercase tracking-[0.2em] text-primary">
                  {group.group}
                </h3>
                <ul className="mt-3 space-y-2">
                  {group.items.map((item) => (
                    <li
                      key={item}
                      className="flex gap-2.5 text-sm leading-relaxed"
                    >
                      <span className="mt-2 h-1.5 w-1.5 flex-none rounded-full bg-accent" />
                      <span className="text-foreground/80">{item}</span>
                    </li>
                  ))}
                </ul>
              </div>
            ))}
          </div>
        </div>
      </div>
    </section>
  );
}
