# Feature blueprint (owner, 16 Nov 2026)

The owner had several research assistants compare media players on Windows, Mac, Linux, Android and iOS and merge
the results into one master feature list. **It is a blueprint of possible future improvements, not a to-do list:**
nothing here is approved by being listed. Ideas taken forward go through the usual loop (owner notes, mock-ups,
approval).

Each line pairs the research's "target" with where Supernova stands (16 Nov 2026):
**Built** · **Planned** (in a version) · **Parked** (`docs/REVISIT_LATER.md`) · **New idea** (not yet discussed) ·
**To check** (needs a look at the code). Where the research's "NOVA 6.6.4 baseline" is wrong for Supernova, the
Supernova column says so.

Research baseline: Android TV (NVIDIA Shield), forked from NOVA 6.6.4; local storage, USB, SMB, NFS, WebDAV (Put.io).
Correction: NOVA does not use ExoPlayer; it has its own engine (FFmpeg-based, with MediaCodec hardware decoding).

## Network and storage
| Feature | Research target | Supernova |
| --- | --- | --- |
| SMB v2/v3 | Buffer tuning for high-bitrate 4K REMUX | Built (NOVA; Samba fixes from NOVA 6.6.5+ in 0.157). Buffer tuning: new idea |
| WebDAV | Digest/OAuth sign-in, multi-thread prefetching | Built (Put.io row, pause/resume sync check in 0.157). Prefetching: new idea |
| Put.io | Cloud-to-local playback, poster indexing | Built (Put.io connection, indexed into the library) |
| NFS / SFTP / FTP | High-throughput buffering (100 Mbps+) | To check which NOVA offers; buffering: new idea |
| Android storage permissions | Full Android 11+ compliance | Built (All files access asked once, 0.157) |
| File operations | Copy, move, rename, delete, new folder, D-pad confirmations | Delete built (warning with options); copy: planned (Media Transfers, 0.158); new folder in the folder picker; rename/move: new idea |
| Folder rules | Exclude folders, folder artwork, per-folder playback rules | To check (scan rules exist); the rest: new idea |

## Video engine
| Feature | Research target | Supernova |
| --- | --- | --- |
| Formats | HEVC, AV1, VP9, H.264, ProRes, VC-1 | Mostly built (NOVA); exact list to check |
| HDR | HDR10, HLG, HDR10+, Dolby Vision 5/7/8 | Dolby Vision work started (libdovi patch needs a new engine build); rest to check |
| Refresh-rate matching | 23.976/24/59.94/60 Hz with a delay | Built in NOVA; configurable delay to check |
| Decoder fallback | Hardware first, software on failure | Built (decoder order and software-decoding notice, 0.157) |

## Audio
| Feature | Research target | Supernova |
| --- | --- | --- |
| Passthrough | DD+, TrueHD, Atmos, DTS-HD MA, DTS:X | Built in NOVA; formats to check |
| Downmixing / night mode | Better downmix, dynamic range compression | To check |
| Audio offset | Sync slider in the player | Built (Playback Settings) |

## Subtitles
| Feature | Research target | Supernova |
| --- | --- | --- |
| Formats | PGS, VobSub, WebVTT | Picture subtitles built (NOVA; position fix planned); WebVTT to check |
| Styling | Full ASS rendering, font, shadow, position, background | Style tab built (0.156); full ASS rendering to check |
| Online subtitles | OpenSubtitles.com, automatic language download | Built (Download Subtitles); "subtitles ready at Preparing" in 0.157 |
| Translate other languages | — | Planned (experimental, moved to 0.158) |

## Playback and sync
| Feature | Research target | Supernova |
| --- | --- | --- |
| Trakt | Live scrobbling, resume, ratings, history | Built (NOVA Trakt sync, not in use by the owner); Trakt per user: parked |
| Intro / credits skipping | IntroDB and chapters, skip prompt | Built (IntroDB, Skip button; Off · Show button · Automatic in 0.157) |
| Resume across devices | Sync resume between devices | New idea |

## Library
| Feature | Research target | Supernova |
| --- | --- | --- |
| Metadata sources | TMDB with fallbacks and NFO overrides | TMDB built; NFO to check |
| Artwork | Fanart.tv logos, clear art, banners | Title logos on the hero built; Fanart.tv: new idea (needs a key) |
| Collections / sagas | Saga grouping, release order | New idea (also feeds "suggested tags" in users and tags) |
| Library statistics | — | Designed (Files › Your Library); not built in 0.157 |

## Player controls
| Feature | Research target | Supernova |
| --- | --- | --- |
| Seek steps | 10 s, 30 s, 1 min, 5 min with acceleration | To check |
| Seek thumbnails | Trickplay previews | Built (experimental) |
| Chapters | Tick marks, chapter names, chapter skip | Built (chapter strip and names) |
| Quick panel | Tracks, aspect ratio, night mode | Built (Playback Settings) |
| Remote buttons | Reliable D-pad focus, key remapping | Focus work built; remapping: new idea |

## Settings, backup, diagnostics
| Feature | Research target | Supernova |
| --- | --- | --- |
| Settings export / import | JSON to local or network storage | Built (Backup and Restore) |
| Watch-state backup | Automatic backup of progress and history | Built (part of Backup); automatic schedule to check |
| Diagnostic logs | In-app log of network, decoder and passthrough events | Built (Diagnostics with export) |
| Playback stats overlay | Frame drops, bitrate, HDR, codec on screen | Technical info on Details built; on-screen overlay: new idea |

## Accessibility and languages
| Feature | Research target | Supernova |
| --- | --- | --- |
| Subtitle contrast and size | Background shading, size, colour, outline | Built (Style tab) |
| Audio description | Prefer audio-description tracks automatically | New idea |
| Forced subtitles | Detect and show forced tracks | To check |
| App languages | Full translations | Supernova's own screens are English only; parked until it is shared |

## Online services
| Service | Use | Supernova |
| --- | --- | --- |
| TMDB | Metadata, artwork, streaming availability | Built |
| Trakt | Sync and scrobbling | Built (not in use) |
| OpenSubtitles | Subtitle search | Built |
| IntroDB | Intro times | Built |
| ChaptersDB | Chapters for files without them | New idea |
| Fanart.tv | Logos and art | New idea |

## Design inspiration
| Source | Pattern | Supernova |
| --- | --- | --- |
| Disney+ | Hero with title logo; collection hubs | Logo hero built; hubs: see collections |
| Netflix | Seek previews; quiet "next episode" | Seek previews built; Play next and Still watching built |
| Prime Video X-Ray | Pause screen with cast and music | New idea |
| Apple TV | Glassy layers, minimal player menus | Deliberately not: Supernova moved to flat charcoal (owner, 7 Nov) |
