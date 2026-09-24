# Supernova Application Analysis & Interactive Mockup Guide

## Overview

Supernova is a custom high-performance Android TV / NVIDIA Shield video player application derived from NOVA Video Player and Archos Video Player Community Edition.

This documentation suite provides a visual breakdown, architectural analysis, and interactive HTML5/CSS3 prototype of the application surfaces as implemented in the codebase.

---

## Architecture & UI Specification Analysis

### 1. Color System & Styling
- **Primary Canvas Background:** `#0F172A` (Deep Slate)
- **Glass Translucency (Frost Panels):** `#1E293B` at 70-85% opacity with subtle borders (`rgba(255, 255, 255, 0.08)`)
- **Focus Accent:** `#03A9F4` (High-contrast Supernova Cyan) with glowing focus ring outlines (`0 0 20px rgba(3, 169, 244, 0.4)`)
- **Typography:** Modern clean sans-serif typography with high contrast for 10ft TV distance readability.

### 2. Core UI Surfaces Replicated
1. **Home / Featured Selection:**
   - Fixed Top Navigation Bar (Home, Movies, TV Shows, Network, Settings, Clock, Search).
   - Hero Featured Banner with backdrop artwork, match score, technical metadata tags (4K HDR, Dolby Atmos), and quick Play/Details actions.
   - Horizontal scrolling media rows for **Continue Watching** (with progress bars) and **Recently Added** titles.
2. **Movies & TV Show Grid:**
   - 6-column portrait poster grid with uncropped artwork and technical quality indicators.
   - Filter bar with Genre selection and Sort order controls (Recently Added, A-Z, Rating, Release Year).
3. **Movies & TV Show List View:**
   - Compact table layout displaying poster thumbnail, title/genre, release year, rating, video quality badge, audio track info, duration, and file size.
   - Column configuration tool support.
4. **Movie & TV Details Screen:**
   - 1080p 16:9 canvas layout featuring uncropped poster on the left and metadata column on the right.
   - Technical badges (4K UHD, Dolby Vision, Dolby Atmos 7.1, HEVC, 23.976 FPS).
   - Primary Play, Resume, Trailer, and Options action buttons.
   - Full synopsis text and circular cast/crew avatar cards.
5. **Network & Storage Sources:**
   - Support for Internal Storage, USB Drives, SMB3 shares, and WebDAV cloud storage.
   - Full library scanning actions and server configuration controls.
6. **Settings Screen:**
   - Left vertical category rail (Video, Audio, Subtitles, UI, Network, About).
   - 3-column settings card grid displaying individual toggle states and detailed descriptions.
7. **Video Player HUD Overlay:**
   - Top translucent status bar with media title, stream technical parameters, and current time.
   - Bottom transport bar with interactive seek bar, elapsed/remaining time, calculated end time, audio track, subtitle selector, and transport controls.

---

## Interactive Mockup & Screenshots

### Screenshots Included
High-resolution 1920×1080 PNG mockups are stored in `docs/mockups/screenshots/`:
- `01_home_featured.png`
- `02_movies_grid.png`
- `03_movies_list.png`
- `04_movie_details.png`
- `05_network_sources.png`
- `06_settings.png`
- `07_player_hud.png`

### Running the Interactive UI Prototype
1. Open `docs/mockups/index.html` in any modern web browser (Google Chrome, Firefox, Safari, Edge).
2. **Navigation Controls:**
   - **Mouse / Touch:** Click on any button, top nav link, poster card, or quick-switcher pill at the bottom right.
   - **TV Remote / Keyboard D-Pad:**
     - Use **Arrow Keys** (Up / Down / Left / Right) to navigate focused items.
     - Press **Enter** to select an item or open media details.
     - Press **Backspace** or **Escape** to go back to the Home screen.
