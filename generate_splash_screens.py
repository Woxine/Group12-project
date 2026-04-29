"""
Generate rideNow uni-app/Android/iOS launcher, splash and storyboard assets.

The scooter motif is rendered programmatically (vector-style Pillow drawing) —
no reliance on raster source files that could be overwritten. Android launcher
tiles use solid light cyan-blue; splash / iOS use the brand gradient + the
same glyph.

Run:
    python generate_splash_screens.py
"""

from __future__ import annotations

import os
import shutil
import zipfile
from pathlib import Path

from PIL import Image, ImageDraw, ImageFont


ROOT = Path(__file__).resolve().parent
BRAND_MASTER = ROOT / "templates" / "brand_scooter_master.png"
OUT_DIR = ROOT / "static" / "app-splash"
# Google SplashScreen (Android 12+) center-logo target dirs.
NATIVE_RES_DIR = ROOT / "nativeResources" / "android" / "res"
# iOS asset paths.
IOS_ICON_DIR = ROOT / "static" / "app-icon-ios"
IOS_SPLASH_DIR = ROOT / "static" / "dc_launchscreen"
IOS_BUILD_DIR = ROOT / "build" / "ios_storyboard"
STORYBOARD_TEMPLATE = ROOT / "templates" / "LaunchScreen.storyboard"

# Brand gradient sampled from the existing app icon
TOP_COLOR = (74, 198, 226)     # light cyan-blue
BOTTOM_COLOR = (12, 90, 196)   # deeper blue
# Solid background used for Android launcher icons – matches TOP_COLOR
# so the icon reads as a clean light cyan-blue tile.
LAUNCHER_BG_RGB = (74, 198, 226)

APP_NAME = "rideNow"

# Portrait splash dimensions (width x height) for each Android density.
# Sized to match common device resolutions: HD / FHD / 4K.
SPLASH_SIZES = {
    "xhdpi":   (720,  1280),
    "xxhdpi":  (1080, 1920),
    "xxxhdpi": (2160, 3840),
}

# Google SplashScreen (Android 12+) center-logo. Square design (no
# rounded corners), brand gradient extending to all edges.
SPLASH_ICON_SIZES = {
    "xhdpi":   480,
    "xxhdpi":  720,
    "xxxhdpi": 960,
}

# Google SplashScreen bottom brand icon (200x80 dp).
SPLASH_BRAND_SIZES = {
    "xhdpi":   (400, 160),
    "xxhdpi":  (600, 240),
    "xxxhdpi": (800, 320),
}

# Android launcher icon set (square, no rounded corners — let the
# launcher / system theme decide whether to mask).
ANDROID_APP_ICON_SIZES = {
    "icon-hdpi-72.png":     72,
    "icon-xhdpi-96.png":    96,
    "icon-xxhdpi-144.png":  144,
    "icon-xxxhdpi-192.png": 192,
}
ANDROID_APP_ICON_DIR = ROOT / "static" / "app-icon"

# iOS application icon set (file name -> pixel size).
IOS_ICON_SIZES = {
    "appstore.png":               1024,
    "iphone-app@2x.png":          120,
    "iphone-app@3x.png":          180,
    "iphone-spotlight@2x.png":    80,
    "iphone-spotlight@3x.png":    120,
    "iphone-settings@2x.png":     58,
    "iphone-settings@3x.png":     87,
    "iphone-notification@2x.png": 40,
    "iphone-notification@3x.png": 60,
    "ipad-app.png":               76,
    "ipad-app@2x.png":            152,
    "ipad-proapp@2x.png":         167,
    "ipad-spotlight.png":         40,
    "ipad-spotlight@2x.png":      80,
    "ipad-settings.png":          29,
    "ipad-settings@2x.png":       58,
    "ipad-notification.png":      20,
    "ipad-notification@2x.png":   40,
}

# Storyboard center icon (matches the template's 112.66 pt imageView).
LAUNCH_ICON_SIZES = {
    "dc_launchscreen_icon@2x.png": 225,
    "dc_launchscreen_icon@3x.png": 338,
}

# Storyboard launcher background RGB (converted to XML 0–1 in build step)
_IOS_STORYBOARD_RGB = LAUNCHER_BG_RGB

# ----------------------------------------------------------------------------
# Brand glyph: programmatic kick-scooter silhouette (master → scale)
# ----------------------------------------------------------------------------
CANVAS_MASTER = 1024


def _render_scooter_silhouette(draw: ImageDraw.ImageDraw, side: float) -> None:
    """
    Side-view kick scooter drawn in white (+ lime accent). Coordinates assume
    a square canvas of size ``side``.
    """
    ox = oy = side * 0.062
    iw = ih = side - 2 * ox
    fw = iw
    fh = ih
    stem_w = max(3.0, side / 90.0)

    def wheel(cx: float, by: float, r: float) -> None:
        draw.ellipse([cx - r, by - 2 * r, cx + r, by], fill=(255, 255, 255, 255))

    by = oy + fh * 0.84
    r = max(side * 0.092, 6.5)
    rear_cx = ox + fw * 0.22
    front_cx = ox + fw * 0.78

    wheel(rear_cx, by, r)
    wheel(front_cx, by, r * 0.96)

    dh = max(4.0, side / 105.0)
    deck_left = rear_cx - r * 0.95
    deck_right = front_cx + r * 0.72
    deck_top = by - r * 1.92
    stem_base_x = ox + fw * 0.58

    draw.polygon(
        [
            (deck_left, deck_top),
            (stem_base_x, deck_top + dh),
            (deck_right, deck_top),
            (deck_right, deck_top + dh * 5.2),
            (deck_left, deck_top + dh * 5.8),
        ],
        fill=(255, 255, 255, 255),
    )

    stem_bot_x = stem_base_x + r * 0.18
    stem_bot_y = deck_top + dh * 5.8
    stem_top_x = stem_bot_x + r * 0.45
    stem_top_y = oy + fh * 0.18

    draw.polygon(
        [
            (stem_bot_x - stem_w, stem_bot_y),
            (stem_bot_x + stem_w, stem_bot_y),
            (stem_top_x + stem_w, stem_top_y + r * 0.08),
            (stem_top_x - stem_w, stem_top_y + r * 0.08),
        ],
        fill=(255, 255, 255, 255),
    )

    hb_y = oy + fh * 0.16
    hb_len = fw * 0.22
    hb_x = stem_top_x + r * 0.45

    radius = stem_w * 3.5
    draw.rounded_rectangle(
        [hb_x - hb_len, hb_y - stem_w * 0.95, hb_x + hb_len, hb_y + stem_w * 1.05],
        radius=min(radius, hb_len * 0.4),
        fill=(255, 255, 255, 255),
    )

    gx = ox + fw * 0.38
    gy = deck_top + dh * 3.25
    gr = side * 0.028

    draw.ellipse(
        [gx - gr * 1.85, gy - gr * 2.05, gx + gr * 1.85, gy + gr * 2.05],
        fill=(60, 220, 130, 255),
    )


def render_scooter_rgba(side_px: int) -> Image.Image:
    """Kick scooter only — transparent RGBA tile, centred in a snug square."""

    hi = CANVAS_MASTER
    big = Image.new("RGBA", (hi, hi), (0, 0, 0, 0))
    _render_scooter_silhouette(ImageDraw.Draw(big), float(hi))
    bbox = big.getbbox()
    if bbox:
        big = big.crop(bbox)
        w_, h_ = big.size
        pad = max(w_, h_) + int(hi * 0.035)
        centred = Image.new("RGBA", (pad, pad), (0, 0, 0, 0))
        centred.paste(big, ((pad - w_) // 2, (pad - h_) // 2), big)
        big = centred
    out = big.resize((side_px, side_px), Image.LANCZOS)
    return out


def render_android_launcher_rgb(size_px: int) -> Image.Image:
    """Launcher icon: flat light cyan-blue + complete scooter centred."""
    inner_px = max(48, int(round(size_px * 0.74)))
    scooter = render_scooter_rgba(inner_px)
    bg = Image.new("RGB", (size_px, size_px), LAUNCHER_BG_RGB)
    sx = (size_px - scooter.width) // 2
    sy = (size_px - scooter.height) // 2
    bg.paste(scooter, (sx, sy), scooter)
    return bg


def ensure_brand_master_written() -> None:
    BRAND_MASTER.parent.mkdir(parents=True, exist_ok=True)
    img = render_scooter_rgba(512)
    img.save(BRAND_MASTER, "PNG", optimize=True)


def make_gradient(width: int, height: int) -> Image.Image:
    """Vertical linear gradient between TOP_COLOR and BOTTOM_COLOR."""
    img = Image.new("RGB", (width, height), TOP_COLOR)
    draw = ImageDraw.Draw(img)
    for y in range(height):
        t = y / max(height - 1, 1)
        r = int(TOP_COLOR[0] + (BOTTOM_COLOR[0] - TOP_COLOR[0]) * t)
        g = int(TOP_COLOR[1] + (BOTTOM_COLOR[1] - TOP_COLOR[1]) * t)
        b = int(TOP_COLOR[2] + (BOTTOM_COLOR[2] - TOP_COLOR[2]) * t)
        draw.line([(0, y), (width, y)], fill=(r, g, b))
    return img


def make_brand_icon(width: int, height: int) -> Image.Image:
    """Bottom-of-splash brand image: white "rideNow" wordmark on
    transparent background, fitted into a 200x80 dp rectangle.
    """
    img = Image.new("RGBA", (width, height), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)

    # Pick the largest font that fits within ~85% width and 60% height.
    target_w = int(width * 0.85)
    target_h = int(height * 0.6)
    font_size = target_h
    font = load_font(font_size)
    while font_size > 8:
        try:
            bbox = draw.textbbox((0, 0), APP_NAME, font=font)
            text_w = bbox[2] - bbox[0]
            text_h = bbox[3] - bbox[1]
        except AttributeError:
            text_w, text_h = draw.textsize(APP_NAME, font=font)
        if text_w <= target_w and text_h <= target_h:
            break
        font_size -= 2
        font = load_font(font_size)

    tx = (width - text_w) // 2 - bbox[0]
    ty = (height - text_h) // 2 - bbox[1]
    # Subtle shadow for readability over varying backgrounds.
    draw.text((tx + max(2, width // 200), ty + max(2, width // 200)),
              APP_NAME, font=font, fill=(0, 0, 0, 90))
    draw.text((tx, ty), APP_NAME, font=font, fill=(255, 255, 255, 255))
    return img


def load_font(size: int) -> ImageFont.FreeTypeFont | ImageFont.ImageFont:
    """Try a few common Windows fonts, fall back to PIL default."""
    candidates = [
        r"C:\Windows\Fonts\segoeuib.ttf",   # Segoe UI Bold
        r"C:\Windows\Fonts\seguisb.ttf",    # Segoe UI Semibold
        r"C:\Windows\Fonts\segoeui.ttf",    # Segoe UI
        r"C:\Windows\Fonts\arialbd.ttf",    # Arial Bold
        r"C:\Windows\Fonts\arial.ttf",
    ]
    for path in candidates:
        if os.path.exists(path):
            try:
                return ImageFont.truetype(path, size)
            except Exception:
                pass
    return ImageFont.load_default()


def compose_splash(width: int, height: int, scooter_src: Image.Image) -> Image.Image:
    bg = make_gradient(width, height)

    target_w = int(width * 0.36)
    ratio = target_w / scooter_src.width
    target_h = max(1, int(round(scooter_src.height * ratio)))
    scaled = scooter_src.resize((target_w, target_h), Image.LANCZOS)

    # Place slightly above the vertical center for a balanced look
    cx = (width - target_w) // 2
    cy = int(height * 0.42) - target_h // 2
    bg.paste(scaled, (cx, cy), scaled)

    # Draw the app name centered below the glyph
    draw = ImageDraw.Draw(bg)
    font_size = max(int(width * 0.075), 18)
    font = load_font(font_size)
    try:
        bbox = draw.textbbox((0, 0), APP_NAME, font=font)
        text_w = bbox[2] - bbox[0]
        text_h = bbox[3] - bbox[1]
    except AttributeError:
        text_w, text_h = draw.textsize(APP_NAME, font=font)
    tx = (width - text_w) // 2
    ty = cy + target_h + int(height * 0.03)
    # subtle shadow for legibility
    draw.text((tx + 2, ty + 2), APP_NAME, font=font, fill=(0, 0, 0, 80))
    draw.text((tx, ty), APP_NAME, font=font, fill=(255, 255, 255))

    return bg


def make_ios_icon(size: int, scooter_src: Image.Image) -> Image.Image:
    """iOS tile: brand gradient square + centred scooter silhouette (RGB)."""

    bg = make_gradient(size, size)
    tw = max(10, int(size * 0.76))
    ratio = tw / scooter_src.width
    th = max(1, int(round(scooter_src.height * ratio)))
    scaled = scooter_src.resize((tw, th), Image.LANCZOS)
    ox = (size - scaled.width) // 2
    oy = (size - scaled.height) // 2
    bg.paste(scaled, (ox, oy), scaled)
    return bg.convert("RGB")


def make_launch_icon(size: int, scooter_src: Image.Image) -> Image.Image:
    """Storyboard foreground: scooter on transparency only."""

    canvas = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    tw = max(10, int(size * 0.92))
    ratio = tw / scooter_src.width
    th = max(1, int(round(scooter_src.height * ratio)))
    scaled = scooter_src.resize((tw, th), Image.LANCZOS)
    ox = (size - scaled.width) // 2
    oy = (size - scaled.height) // 2
    canvas.paste(scaled, (ox, oy), scaled)
    return canvas


def build_ios_storyboard_zip(scooter_src: Image.Image) -> Path:
    """Customize LaunchScreen.storyboard for the rideNow brand and
    package it (along with the @2x/@3x icons) into a zip suitable for
    HBuilderX iOS splash configuration.
    """
    if not STORYBOARD_TEMPLATE.exists():
        raise SystemExit(f"storyboard template not found: {STORYBOARD_TEMPLATE}")

    if IOS_BUILD_DIR.exists():
        shutil.rmtree(IOS_BUILD_DIR)
    IOS_BUILD_DIR.mkdir(parents=True, exist_ok=True)

    for name, size in LAUNCH_ICON_SIZES.items():
        img = make_launch_icon(size, scooter_src)
        img.save(IOS_BUILD_DIR / name, "PNG", optimize=True)

    xml = STORYBOARD_TEMPLATE.read_text(encoding="utf-8")
    r, g, b = (c / 255.0 for c in _IOS_STORYBOARD_RGB)
    xml = xml.replace(
        '<color key="backgroundColor" systemColor="systemBackgroundColor"/>',
        f'<color key="backgroundColor" red="{r:.6f}" green="{g:.6f}" '
        f'blue="{b:.6f}" alpha="1" colorSpace="calibratedRGB"/>',
    )
    xml = xml.replace('text="hello uniapp"', f'text="{APP_NAME}"')
    xml = xml.replace(
        '<color key="textColor" white="0.66666666666666663" alpha="1" '
        'colorSpace="custom" customColorSpace="genericGamma22GrayColorSpace"/>',
        '<color key="textColor" red="1" green="1" blue="1" alpha="1" '
        'colorSpace="calibratedRGB"/>',
    )
    (IOS_BUILD_DIR / "LaunchScreen.storyboard").write_text(xml, encoding="utf-8")

    IOS_SPLASH_DIR.mkdir(parents=True, exist_ok=True)
    zip_path = IOS_SPLASH_DIR / "CustomStoryboard.zip"
    if zip_path.exists():
        zip_path.unlink()
    with zipfile.ZipFile(zip_path, "w", zipfile.ZIP_DEFLATED) as zf:
        for f in sorted(IOS_BUILD_DIR.iterdir()):
            if f.is_file() and not f.name.startswith("."):
                zf.write(f, arcname=f.name)
    return zip_path


def main() -> None:
    OUT_DIR.mkdir(parents=True, exist_ok=True)

    ensure_brand_master_written()
    scooter_master = render_scooter_rgba(CANVAS_MASTER)

    for density, (w, h) in SPLASH_SIZES.items():
        img = compose_splash(w, h, scooter_master)
        out_path = OUT_DIR / f"splash-{density}.png"
        img.save(out_path, "PNG", optimize=True)
        print(f"wrote {out_path}  ({w}x{h})")

    for density, size in SPLASH_ICON_SIZES.items():
        target_dir = NATIVE_RES_DIR / f"drawable-{density}"
        target_dir.mkdir(parents=True, exist_ok=True)
        img = make_ios_icon(size, scooter_master)
        out_path = target_dir / "uniappx_splashscreen_icon.png"
        img.save(out_path, "PNG", optimize=True)
        print(f"wrote {out_path}  ({size}x{size})")

    for density, (bw, bh) in SPLASH_BRAND_SIZES.items():
        target_dir = NATIVE_RES_DIR / f"drawable-{density}"
        target_dir.mkdir(parents=True, exist_ok=True)
        img = make_brand_icon(bw, bh)
        out_path = target_dir / "uniappx_splashscreen_brand.png"
        img.save(out_path, "PNG", optimize=True)
        print(f"wrote {out_path}  ({bw}x{bh})")

    ANDROID_APP_ICON_DIR.mkdir(parents=True, exist_ok=True)
    for name, size in ANDROID_APP_ICON_SIZES.items():
        img = render_android_launcher_rgb(size)
        out_path = ANDROID_APP_ICON_DIR / name
        img.save(out_path, "PNG", optimize=True)
        print(f"wrote {out_path}  ({size}x{size})")

    IOS_ICON_DIR.mkdir(parents=True, exist_ok=True)
    for name, size in IOS_ICON_SIZES.items():
        img = make_ios_icon(size, scooter_master)
        out_path = IOS_ICON_DIR / name
        img.save(out_path, "PNG", optimize=True)
        print(f"wrote {out_path}  ({size}x{size})")

    zip_path = build_ios_storyboard_zip(scooter_master)
    print(f"wrote {zip_path}")


if __name__ == "__main__":
    main()
