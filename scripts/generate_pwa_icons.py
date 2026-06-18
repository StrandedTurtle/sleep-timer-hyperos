"""Generate PWA icon PNGs without any third-party packages (pure stdlib).

Draws the app's existing teal ammonite/spiral brand mark as an Archimedean
spiral on the limestone background, kept within the maskable-icon "safe
zone" (a circle of radius 40% of the icon size) so the same files work for
both normal and maskable manifest icon purposes.

Run with: python scripts/generate_pwa_icons.py
"""

import math
import os
import struct
import zlib

BACKGROUND = (0xE9, 0xE3, 0xD4)  # app's --background limestone tone
FOREGROUND = (0x2F, 0x6F, 0x6A)  # app's --primary teal (also the theme-color)
SAFE_ZONE_RATIO = 0.40  # maskable icon spec: safe content radius = 40% of icon size

SIZES = [
    (192, "pwa-192x192.png"),
    (512, "pwa-512x512.png"),
    (180, "apple-touch-icon-180x180.png"),
]


def write_png(path, width, height, pixels):
    def chunk(tag, data):
        return (
            struct.pack(">I", len(data))
            + tag
            + data
            + struct.pack(">I", zlib.crc32(tag + data) & 0xFFFFFFFF)
        )

    signature = b"\x89PNG\r\n\x1a\n"
    ihdr = struct.pack(">IIBBBBB", width, height, 8, 2, 0, 0, 0)  # color type 2 = RGB

    raw = bytearray()
    for y in range(height):
        raw.append(0)  # filter type: None
        for x in range(width):
            raw += bytes(pixels[y * width + x])
    idat = zlib.compress(bytes(raw), 9)

    with open(path, "wb") as f:
        f.write(signature)
        f.write(chunk(b"IHDR", ihdr))
        f.write(chunk(b"IDAT", idat))
        f.write(chunk(b"IEND", b""))


def make_icon(size):
    pixels = [BACKGROUND] * (size * size)

    cx = cy = size / 2
    max_r = size * SAFE_ZONE_RATIO
    stroke = max(size * 0.045, 2.0)
    half = stroke / 2

    turns = 2.0
    theta_max = turns * 2 * math.pi
    a = max_r / theta_max

    # Sample finely enough along the spiral's arc length to avoid gaps.
    arc_length = a * theta_max ** 2 / 2
    steps = max(int(arc_length / 0.5), 500)

    for i in range(steps + 1):
        theta = theta_max * i / steps
        r = a * theta
        x = cx + r * math.cos(theta)
        y = cy + r * math.sin(theta)

        x0, x1 = int(x - half), int(x + half) + 1
        y0, y1 = int(y - half), int(y + half) + 1
        for yy in range(max(0, y0), min(size, y1)):
            for xx in range(max(0, x0), min(size, x1)):
                if (xx - x) ** 2 + (yy - y) ** 2 <= half ** 2:
                    pixels[yy * size + xx] = FOREGROUND

    return pixels


def main():
    out_dir = os.path.join(os.path.dirname(os.path.dirname(os.path.abspath(__file__))), "public")
    os.makedirs(out_dir, exist_ok=True)

    for size, name in SIZES:
        pixels = make_icon(size)
        path = os.path.join(out_dir, name)
        write_png(path, size, size, pixels)
        print(f"wrote {path} ({size}x{size})")


if __name__ == "__main__":
    main()
