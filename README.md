# Hush: a sleep timer for the HyperOS Super Island

Hush stops **all audio and video playback** when a timer runs out, and you control it from the
Xiaomi HyperOS **Super Island** (the dynamic island). It's built for HyperOS phones and tablets and
styled to match the frosted-glass look of HyperOS 3/4.

## How it works

1. **Quick tile.** Add the *Sleep timer* tile (open the app and tap **Add** under Setup, or edit your
   Control Center). Tap it to start a **30-minute** timer. Tap it again to cancel. Long-press it to
   open the app.
2. **Super Island.** While the timer runs, the island shows a moon and a live countdown. Expand it to
   get **−5 min**, **+5 min** and **Stop**.
3. **When time's up,** Hush fades the media volume over about 6 seconds, pauses every active media
   session (Spotify, YouTube, Netflix, podcasts, browsers…), then restores your volume.

The app itself has a glass dial with presets (15/30/45/60/90), a setup checklist, and preferences:
the tile's default duration, fade on/off, and island style. On tablets and unfolded screens the
layout switches to two panes.

## Install with Obtainium / ObtainX

Every push to the default branch builds a signed APK and publishes it as a GitHub Release
(`v1.0.<build>`). In Obtainium:

1. **Add app** → `https://github.com/strandedturtle/jurassic-weekend`
2. For a private repo, put your GitHub token in Obtainium's GitHub source settings.
3. Install. Later releases update in place because every build is signed with the same key.

You can also start a release by hand: **Actions → Build & release APK → Run workflow**. This works
on any branch.

## First-run setup (on the phone)

| Item | Why |
| --- | --- |
| Notifications | Required for the island and its buttons |
| Super Island / focus notifications | HyperOS decides which apps can draw in the island. If the status says *Turn on…*, tap **Open** and enable the island / focus notification toggle for Hush. If HyperOS won't allow it, Hush falls back to an Android 16 **Live Update**, which HyperOS 3+ also shows in the island |
| Pause any app | Notification-listener access lets Hush pause *every* media session, not just the one that owns the media keys |
| Autostart | HyperOS can block alarms for apps without Autostart. Turn it on |

If Android blocks the media-control toggle as a *restricted setting*, open
**Settings → Apps → Hush → ⋮ → Allow restricted settings** and try again.

## Signing

`keystore/hush.jks` is committed so CI can sign every build with the same key; that's what lets
Obtainium update in place. To use your own key instead, add these repository secrets:
`HUSH_KEYSTORE_BASE64`, `HUSH_KEYSTORE_PASSWORD`, `HUSH_KEY_ALIAS` and `HUSH_KEY_PASSWORD`.
Changing keys means uninstalling once.

## Project layout

```
app/src/main/java/com/strandedturtle/hush/
  timer/   TimerStore (state), SleepTimer (start/adjust/stop + exact alarm), receivers
  island/  TimerNotification (miui.focus.param Super Island payload + Live Update fallback), FocusSupport
  media/   MediaStopper (fade, pause all sessions, focus, media key), listener service
  tile/    SleepTileService
  ui/      Compose glass UI (Theme, Glass, Dial, HushScreen, Setup)
```

Build locally with `./gradlew assembleRelease` (JDK 17+ and the Android SDK).
