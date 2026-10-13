# 03 — Current layout geometry and reconstruction

S for constants/formulas; I for resolved coordinates/text-fit calculations; B for existing renders. **No proposed connected frame is drawn as existing behavior.** See [P52](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L52)–89/120–139/176–181/347–377 and TopNavigation108–143, PreviewToolbar, TvLayout, PreviewDialog187, CinematicCard and SharedThreePanel62 in the ledger.

## Coordinate system and formulas

There is no fixed1920×1080 XML layout for lower Details. The renderer uses Android dp, density, scaledDensity/fontScale and measured bounds. PreviewMoviePage.dp is ordinary density multiplication. TopNavigation.dp uses `TvLayout.dimension = baseline × scale × density`, where `scale=clamp(min(screenWidthDp/960,screenHeightDp/540),.85,1.25)`. Lower fonts/spacing are mostly fixed sp/dp.

Physical coordinates below assume **960×540 logical dp at density2, fontScale1, TvLayout.scale1, zero system/cutout inset**. This is a reproducible nominal1920×1080 illustration, not a measurement of the owner's TV. Actual configuration/overscan/OEM fonts are U.

TvLayout.applyInsets applies system bars/cutout padding to TopNavigation. Its scrolling stage is measured/layout at full shell bounds underneath the bar; navigation height is added as top padding to the page. It is not a separate52dp smaller stage. Top bar height can grow for enlarged text: max(52 scaled dp, tab font-height+tab padding+12 scaled dp). Do not subtract navigation twice.

Let W/H be measured page width/height in pixels, T/B page top/bottom padding and d density. `V=H−T−B`. Body side margin `M=max(24d,min(64d,round(.045W)))`. Hero height `G=max(180d,V−32d)`. Compact if V<380d. Hero top padding24d (compact12d); tab row44d; tab top margin14d (compact8d). Card fills the remainder: `G−heroTopPadding−tabHeight−tabMargin`. Lower minimum height `V−48d`; wrap-content can exceed it. Body bottom padding24d. No fixed visible lower-frame bottom border or maximum content height.

## Annotated nominal1920×1080 reconstruction

Baseline: T104px; V976px; M86px (rounded); body width1748px; G912px; card height748px; lower minimum880px. Down/OK of Details calls `scrollY=max(0,G−88−96)=728px`, clamped to Android content bounds. Initial lower teaser64px. Lower information starts at nominal screen y288px after that explicit scroll. Focus-triggered automatic reveal can also affect position; these coordinates assume the source's requested target.

```text
INITIAL PAGE — scrollY0, nominal1920×1080 (not a photograph)
0    ┌──────────────── top navigation / clock ──────────────────────┐
104  │     top card gap48px                                        │
152  │ x86 ┌─ Cinematic Details card, width1748px ────────────────┐    │
     │     │ title/logo, metadata, context                       │    │
     │     │ technical badges if physical file                  │    │
     │     │ synopsis (1/2/4 lines depending on viewport/card)   │    │
     │     │ Play/Resume, provider, More                        │    │
     │     │ flexible space AFTER actions                      │    │
900  │     └───────────────────────────────────────────────────┘    │
928  │       Details | Extras* | More Like This* [44dp tab row]      │
1016 │       Cast Crew [Key Information] [Reception] [Technical]    │
1080 └──────────── viewport clips lower-content teaser ────────────┘
     Whole show: Seasons & Episodes precedes Details in current code.
```

```text
LOWER VIEW — explicit Down/OK scrollY≈728px
0    ┌───────── top navigation still drawn above stage ─────────────┐
     │ compact reused title/logo below nav (no new focus target)   │
200  │ Details [focus underline] | Extras* | More Like This*        │
288  │                                                             │
308  │ Cast            Crew                                        │
320  │ Name · role     Name · job    [Key Info][Reception][Technical]│
     │ … up to8        … up to8     [label val][label val][label val]│
     │                                                             │
     │       lower background blank where short data ends          │
     │                                      scan overlay*          │
1080 └──────────────── viewport bottom ─────────────────────────────┘
x≈86                 409             733       1092      1451  1794
* conditional; panels wrap height independently; no shared lower frame.
```

| Element | Initial y / lower-view y | Nominal bounds |
|---|---|---|
| Navigation bar |0 /0 |1920×104px baseline |
| heroPage |104 /−624 |1748×912px |
| card |152 /−576 |1748×748px |
| tab strip |928 /200 |1748×88px |
| lower |1016 /288 |1748×at least880px |
| people heading |lower+20 /308 |Wrapper≈299px wide |
| fact panel top |lower+20+12 /320 |Each≈343px wide with all five sections |
| lower minimum bottom |1896 /1168 |Extends below viewport despite short facts |
| body bottom including24dp padding |1944 /1216 |Not a visible fixed frame border |

[Annotated SVG](evidence/current-lower-layout.svg) uses those lower bounds and synthetic placeholders, no movie art/private data. C: source-derived approximation, no redesign or owner approval. Character-grid horizontal distances are illustrative; formulas/table are authoritative for this assumed configuration. Font glyph metrics/rounding/title foreground effects are U.

## Horizontal geometry — why values truncate

With all people wrappers and panels present, `C=W−2M≈1748px`. Information wrappers each have24px right margin (including last): distribute `C−72≈1676px` by weights1:1:3.6. Cast/Crew≈299px each; Details≈1077px. Three inner panels each have16px right margin: `(1077−48)/3≈343px`. Panel padding12px on each side leaves≈319px. Value left margin16px: label≈.44×(319−16)=133px; value≈.56×(319−16)=170px. Approximate origins Cast86, Crew409, Key733, Reception1092, Technical1451. Trailing wrapper/panel margins leave more space on the right.

Logical widths: **~66.7dp label /84.9dp value**, ~150dp people. A13sp value admits roughly12–15 average Latin characters at normal font assumptions (I), fewer at150% scale. Long filename/path has no full-value reader. Cast/Crew heading precedes its content; Details wrapper heading is GONE, but inner content still has6dp top padding. Thus five headings do not share a baseline.

If Cast/Crew hide, re-evaluate weights over visible wrappers. If Reception/Technical hide, re-evaluate inner weights. Key can widen dramatically. Panel titles can wrap: no maxLines/singleLine set. Fact labels/values cannot wrap. Current tabBar has no horizontal-scroll wrapper; long TV labels/large text can overflow despite minimum control metrics. No adaptive stacked fact-panel breakpoint exists.

## Exact style and geometry tokens

| Element | Current specification |
|---|---|
| Body/scroll |Constructor42dp sides → measured4.5% clamped24–64dp; bottom24dp; fillViewport true, nonfocusable, smoothScrollingEnabled false, clipToPadding/children false. Vertical scrollbar not explicitly disabled. |
| Section wrapper |Vertical, top10dp/bottom8dp; visible people heading16sp #9ED4F7 light sans-serif, no font padding; inner content top6dp. Details/Episodes/Extras/Related wrapper headings GONE. |
| Panel |Equal weights, wrap height, rightMargin8dp, padding6dp; PreviewDialog.surface(false): #EF0B1B29 (~94% opacity), radius6dp, stroke1dp #50426A80 (~31% opacity). No explicit elevation. Colors not themed/accent-resolved here. |
| Panel heading |15sp white sans-serif-light, includeFontPadding false, wrap allowed, no line cap/ellipsis. |
| Fact |Horizontal CENTER_VERTICAL, wrap height, no row margin/padding; label12sp #B9C7D2/value13sp white, light sans-serif/no font padding, ratio.44/.56, value left gap8dp. SingleLine END on both. |
| Person |Padding4dp top/bottom, one14sp SharedThreePanel TextView, sans-serif-light white, default font padding retained, lineSpacing3dp; role.86× font/#A4B6C7; singleLine END. No portrait/border/zoom. |
| Tabs |TvButton13sp×scale; white/light sans-serif; min44 scaled dp but actual layout44 ordinary dp; padding14dp/9dp; natural width plus16dp right gap; singleLine END. Compound icons removed, background null. |
| Underline |PreviewToolbar y=height−density;1dp thick; focused white; selected fallback #88FFFFFF when focus leaves tabs. No attached tabLine or shared frame. |
| Card |CinematicCard #07131F radius12dp/clipToOutline true, nonfocusable; centerCrop artwork; LEFT_RIGHT scrim #E6071522/#B3071522/#00071522; child-action focus adds PreviewFocusGlow radius12, clears on focus departure. |
| Card copy/title |Padding24dp sides,18/16dp top/bottom (compact8/8); title width49% content; height min(scaled96dp,max40dp,24% cardHeight); light sans-serif vertical-center, max2/compact1, END; auto-size18–34sp×scale or compact18–24. |
| Meta/context |Meta18sp×scale max2/compact1, vertical padding3dp. Context13sp #E1E9EF max1 END; episode/season only. Final SharedMetadata renderer replaces intermediate human metadata. |
| Synopsis |14sp, lineSpacing2dp; width min440dp,displayWidth−84dp; wrap-height after measure; max4 if card≥330dp,2 below,compact1; topMargin8/compact4dp; END. No full lower copy. |
| Card actions |After synopsis; row padding12/10dp or compact3/3; More leftMargin18dp; flexible spacer AFTER actions, not bottom anchor. |
| Backdrop |TopNavigation PreviewBackdrop bitmap resize1600×900 centerInside, landscape check; drawing up to420 ordinary dp high, navy base/horizontal/vertical gradients;180ms fade; no-data PreviewAccent utility fallback. |
| Card artwork |Separate ImageView resize1280×720 ordinary dp centerCrop/noFade; setTags picks default backdrop local file if exists, otherwise URL, otherwise first backdrop; [P354](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L354). Separate shell backdrop callbacks from native fragment. |
| Scrolled navigation |PreviewNavigationShade samples stage/backdrop, draws at116 scaled dp extent; opacity220ms. Future20% extent reduction not present. |
| Compact title |[P98](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java#L98)–113 reuses same title drawing; progress clamp((scroll−title.getBottom)/40dp,0,1); fit220×40dp, position bodyMargin/scroll+topPadding+4dp; clip content by48dp×progress. No new title focus or duplicate request. |
| Scan overlay |Shell margin20 scaled dp horizontal,16 bottom/12 top; own padding10dp horizontal/5dp vertical, #D9102638 radius10dp/stroke1physical px #554B738F, text12sp, spinner20dp. No reserved content space. |

## Comparison with approved future geometry

Current side margin is4.5% with clamps; current24dp bottom is scroll content padding. Future baseline is **4% usable width left/right and4% usable height bottom**, stable shared frame across tabs, top below Details card, continuous raised active cap and no separate underline. At raw1920×1080 usable viewport:76.8px side/43.2px bottom before safe-area/rounding. Existing86px side calculation must not be relabeled4%.

## Single-screen preservation pressures

The current lower view scrolls most of the card away. Its blank lower space is **not all available** if the future card remains fixed above the passive panel. Current initial card geometry leaves only32dp teaser; keeping that geometry while adding full synopsis/eight people/all fields cannot fit that region.

Rich movie Key Information can contain14 rows; Reception2; physical technical13; Cast/Crew8 each. Current14sp people with illustrative17dp line-height plus8dp padding need~200dp plus headings;13 technical rows at~16dp need~208dp plus padding/heading. At illustrative18sp future text (unapproved), ~22dp line-height needs286dp for13 technical rows and~240dp for8 padded people. A1000-character synopsis at~900dp width may take6–8 lines (~130–175dp) with ordinary18sp Latin glyph assumptions (I). Fonts/locales change this materially.

Arbitrary synopsis length, audio-track set, storage-folder set and full paths cannot fit a finite viewport at a positive minimum readable font size. No honest universal “full metadata/full values always fit” guarantee exists without bounding inputs/content policy. Report05 asks for owner decisions; it chooses no hiding, unreadable fonts, focus, scroll or truncation policy.

## Existing native renders (B)

[Original render index/provenance](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/docs/0.138/ui/README.md): five-sections.png (remote fixture uses Streaming Availability),150% variant, local-tracks-150-text.png (Movie track fixture), small-viewport.png (640×360 logical). Inspected here without regeneration; hashes checked against committed receipt. Existing tests click Details to scroll before asserting lower sections fit. Panel bounds fit is **not full-text readability** and not physical Shield acceptance.

## Title-logo provenance detail

OfficialTitleArtwork resolves Movie vs parent-TV TMDB ID on a worker; memory alias and decoded caches8MiB each, disk official-title-artwork keyed kind/ID/language,64 PNG/32MiB trim. Requests image metadata with current language,en,null; logo scores language30/en20/no-language10 plus up to9 vote points, accepts validated PNG path, image bound4096², source w500. A24hour negative lookup timestamp suppresses repeated missing-logo fetch. Foreground preserves logo aspect within title bounds; text stays as fallback. No guarantee every title has a provider logo or that owner Chief of War art proves Home parity. Ledger E65 preserves full helper; no owner title assets fetched here.
