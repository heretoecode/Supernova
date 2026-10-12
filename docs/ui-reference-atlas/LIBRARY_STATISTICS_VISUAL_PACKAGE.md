# Library statistics — source specification and detailed references

**Existing interface only.** Movies, TV Shows and the custom Library already share `LibraryPageHeader` and `PreviewLibrarySummary`; no design change or proposed statistics added. Four native0.138 CI images are recovered unchanged and six labelled category C reconstructions illustrate missing partial/spanning/custom-type fixtures. A Shield screenshot has not been supplied.

## Render ownership and exact current text

`LibraryPageHeader.add(target,title,snapshot,television)` calls describe(...,boolean); custom overload calls describe(...,selectedEntries) and icon kind `Library`. In PreviewPages.PageAdapter.onBindViewHolder, non-Home HERO cells host this header; next HEADER cell hosts toolbar/divider/optional list columns. Summary labels in order:

1. `<snapshot.movies.size()> Movies` or `<snapshot.shows.size()> Shows`.
2. `Total Size    <formatted indexed bytes>`; `≥ ` prefix only if known sizes incomplete.
3. `Local Storage    <distinct local count> movies/shows · <bytes>`.
4. `Network / WebDAV    <distinct remote count> movies/shows · <bytes>`.
5. Conditional `Indexed sizes only; some file sizes are unknown`.
6. TV-only conditional `Shows spanning sources appear in both source counts`.

The singular grammar (`1 movies`, `1 Shows`) and `Network / WebDAV` casing are the actual code. They are recorded without correcting UI. Header title Movies/TV Shows/custom name is separate from first count line. No fabricated file sizes or redesigned metrics.

## Storage/count semantics

The summary reads snapshot entries already loaded from ContentResolver/native indexed metadata. Opening a page **does not** enumerate filesystem or contact network sources. Movies uses snapshot.movies; TV bytes use snapshot.episodes while total show count uses snapshot.shows. Only entries whose media is Video contribute source sets/bytes/knownSizes. `Video.getFileUri()` scheme classifies storage, **not getUri() database row**: absent/null, `file` and `content` are local; any other nonnull scheme is remote. Thus the label Network / WebDAV can cover SMB/SFTP/FTP/UPnP/etc.

Distinct movie keys are Entry.key(); TV key `show:<entry.show>`. Same show with episodes on both storage kinds appears once in each source set, so local+network may exceed total unique shows. Bytes sum all qualifying episode/movie entries by source. `known += entry.knownSizes`; partial iff known < files.size(), including skipped non-Video entries. Only Total Size gets ≥; source byte rows can be incomplete without their own ≥. Unknown-size file contributes whatever indexed bytes/model provides (normally0), not a live size probe. Locale-dependent Android Formatter.formatShortFileSize owns B/KB/GB rounding.

Custom summary filters by configured CustomLibraryPage.entries(snapshot), collects selected Movie/Tvshow objects, and adds **all snapshot episodes of each selected show**. Empty/no-shows → movie block (even empty TV-only selection); no-movies → TV block; mixed → movie block + blank line + TV block. Header adds icons by overall line index0..3 only; mixed block2 and notes have no added icons. This surprising source behaviour is preserved in LS-C-CUSTOM-MIXED. Toolbar filters/hide-watched can further restrict visible cards without changing header's full snapshot/custom-config summary.

## Typography, geometry and focus

| Element | Verified source values | Caveat |
|---|---|---|
| Header title | SharedThreePanel.text30sp then Typeface.BOLD; white | Source requests sans-serif-light before bold reset; resolved Android family/weight depends framework |
| Summary rows | 13sp sans-serif-light, NORMAL, #e1e9ef, +3 scaled line spacing, top/bottom2 scaled padding | Empty mixed separator is a TextView, not arbitrary layout gap |
| Icons | PreviewIcon custom Canvas24×24 paths; bounds17×density, compound padding9×density | First kind Movies/TV Shows/Library; Total Size cylinder, Local drive, Network server outlines; exact references in manifest |
| Parent | PreviewPages list padding28 plain dp horizontal; HERO padding0,12dp,0,18dp, min76dp for non-Home, wrap height | Header height naturally grows with notes/mixed block; no fixed invented statistics panel |
| Toolbar | Following HEADER padding13dp top18dp bottom; TvButton13×scale sp/min44 scaled; gaps8 plain dp; divider1dp +12dp top | List adds focusable column headings; custom Edit Library separated by spacer; horizontal overflow scroll |
| Focus | Summary/title nonfocusable; toolbar and columns/card/list focusable | Filters first normal entry; physical traversal/restoration unknown, source contracts in inventory |

Plain dp and TvLayout-scaled dp differ. C fixtures use screen960×540dp, density/scaledDensity1/fontScale1, scale1 and crop to904-wide content; this simplifies both units without claiming real1080p/4K geometry. C omits live backdrop and derives only header/toolbar, B shows actual full fixture context.

## Native and reconstruction state coverage

| Variant/state | Image | Fixture/data and limitations |
|---|---|---|
| Movies header + grid | [LS-B-138-MOVIES](visuals/library-statistics/LS-B-138-MOVIES.png) · [Drive](https://drive.google.com/file/d/1XBD5DObzUOBJIhfbK35nRdjjt_28iApR/view?usp=drivesdk) | 18 indexed Movie fixtures /movie1..18, year2024, each indexed size1000; synthetic posters/background; offline transport |
| TV header + grid | [LS-B-138-TV](visuals/library-statistics/LS-B-138-TV.png) · [Drive](https://drive.google.com/file/d/1Np0uO-sFHR7d6rcfsUuDAxm3YplAuhEI/view?usp=drivesdk) | 12 Tvshow fixtures, no snapshot.episodes; count12/size0/sourcecounts0 is fixture sparsity, not owner data |
| Movies header + list columns | [LS-B-138-LIST](visuals/library-statistics/LS-B-138-LIST.png) · [Drive](https://drive.google.com/file/d/1L9APU5riBVhp_PUMS5uXJByxI2AqWyKh/view?usp=drivesdk) | Same18 movies; List View selected, real columns/toolbar, synthetic posters |
| Custom header empty library + toolbar | [LS-B-138-CUSTOM-EMPTY](visuals/library-statistics/LS-B-138-CUSTOM-EMPTY.png) · [Drive](https://drive.google.com/file/d/1K1o7zNC8aOIfAA8s6WRGdu71yema763Y/view?usp=drivesdk) | Empty library Snapshot, default saved Library Page; List View focus. Filename0135 is historical test naming, actual artifact is0.138 |
| Movies header and toolbar | [LS-C-MOVIES-PARTIAL](visuals/library-statistics/LS-C-MOVIES-PARTIAL.png) · [Drive](https://drive.google.com/file/d/1g8QfwS7dAZTgqSf-Zi9Al6i0ugQ0scDo/view?usp=drivesdk) | Controlled fixtures: exact input rows in fixtures JSON; en-US size strings supplied, Android formatting not executed |
| TV Shows header and toolbar | [LS-C-TV-SPANNING](visuals/library-statistics/LS-C-TV-SPANNING.png) · [Drive](https://drive.google.com/file/d/1KknOATdJ74jVJEHpQCtDRmd2nVwMkMtP/view?usp=drivesdk) | Controlled fixtures: exact input rows in fixtures JSON; en-US size strings supplied, Android formatting not executed |
| Atlas Library header and toolbar | [LS-C-CUSTOM-MIXED](visuals/library-statistics/LS-C-CUSTOM-MIXED.png) · [Drive](https://drive.google.com/file/d/1S9CCL7OYuSsPgasxWUwgmvJ_cZuH8asx/view?usp=drivesdk) | Controlled fixtures: exact input rows in fixtures JSON; en-US size strings supplied, Android formatting not executed |
| Atlas Movies header and toolbar | [LS-C-CUSTOM-MOVIES](visuals/library-statistics/LS-C-CUSTOM-MOVIES.png) · [Drive](https://drive.google.com/file/d/1AK7M2L2Qjm7PRiT1qiD7KVQvfSfHDqaa/view?usp=drivesdk) | Controlled fixtures: exact input rows in fixtures JSON; en-US size strings supplied, Android formatting not executed |
| Atlas TV header and toolbar | [LS-C-CUSTOM-TV](visuals/library-statistics/LS-C-CUSTOM-TV.png) · [Drive](https://drive.google.com/file/d/1vMbHx-ui-BuXHbXJ4fibR-Grz3ddyzAw/view?usp=drivesdk) | Controlled fixtures: exact input rows in fixtures JSON; en-US size strings supplied, Android formatting not executed |
| Atlas Empty header and toolbar | [LS-C-CUSTOM-EMPTY](visuals/library-statistics/LS-C-CUSTOM-EMPTY.png) · [Drive](https://drive.google.com/file/d/1RJgSGQjUDNxE_j8uVtlFH6mupAKP7jx9/view?usp=drivesdk) | Controlled fixtures: exact input rows in fixtures JSON; en-US size strings supplied, Android formatting not executed |

Native Movies shows18 Movies/18 kB all-local because each test Movie indexes1000 bytes. Native TV shows12 Shows/0 B/source counts0 because its test snapshot contains show representatives but no episodes; this is sparse synthetic fixture provenance, not a user's lost files. Native custom empty is valid default saved page with no snapshot data, despite historical0135 filename. Test methods/line links/digests/run/artifact are in [image manifest](evidence/image-manifest.json).

[Controlled fixture inputs](evidence/statistics-fixtures.json) define movie sources/bytes/knownSizes, episodes/show IDs and custom selections for C images. Decimal size text is a documented en-US approximation; exact Formatter output under Android/locale must be recaptured for pixel fidelity. Numeric counts/sums/overlap were checked with a source-transcribed pure calculation, not a new app test. Fixtures are not real library data.

## Remaining verification and request

No native populated custom-movie/custom-TV/mixed/partial/spanning source image is present in retrieved CI. The six C states supply detailed source geometry/semantics with visible approximation banners. Native unknown-size and all real Shield data/typography/output/resolution, locale formatting, extreme text and header scroll/key restoration remain unverified. Request LS IDs with synthetic snapshot, URI schemes and knownSizes using [on-demand protocol](ON_DEMAND_REFERENCES.md). Do not ask the owner to upload their private media DB merely to fill fixtures.

Source: [LibraryPageHeader](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/LibraryPageHeader.java#L9), [PreviewLibrarySummary](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewLibrarySummary.java#L12), [PreviewPages bind](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewPages.java#L549), [loader](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewLibraryLoader.java), [Canvas icons](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewIcon.java#L12), [shared text](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/SharedThreePanel.java#L107).
