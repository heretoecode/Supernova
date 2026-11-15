#!/usr/bin/env python3
"""Sizes the new mark's renders (export-mark.js) into the app's icon, banner and splash resources."""
from pathlib import Path

from PIL import Image

ROOT = Path(__file__).resolve().parents[2]
OUT = Path(__file__).resolve().parent / "out"
NODPI = ROOT / "res/drawable-nodpi"
CHARCOAL = (0x16, 0x18, 0x1C)


def fit(name, size):
    return Image.open(OUT / name).convert("RGBA").resize(size, Image.Resampling.LANCZOS)


def main():
    icon = fit("icon.png", (512, 512))
    icon.convert("RGB").save(NODPI / "foundation_icon.png", optimize=True)
    for density, pixels in [("mdpi", 48), ("hdpi", 72), ("xhdpi", 96), ("xxhdpi", 144), ("xxxhdpi", 192)]:
        small = fit("icon.png", (pixels, pixels)).convert("RGB")
        small.save(ROOT / ("res/mipmap-" + density) / "foundation_launcher.png", optimize=True)
    Image.new("RGB", (512, 512), CHARCOAL).save(NODPI / "foundation_icon_background.png", optimize=True)
    fit("adaptive-foreground.png", (1024, 1024)).save(NODPI / "foundation_adaptive_foreground.png", optimize=True)
    fit("banner.png", (320, 180)).convert("RGB").save(NODPI / "foundation_banner.png", optimize=True)
    fit("splash.png", (1920, 1080)).convert("RGB").save(NODPI / "foundation_splash_image.png", optimize=True)


if __name__ == "__main__":
    main()
