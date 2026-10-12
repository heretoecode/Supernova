#!/usr/bin/env python3
"""Collect unchanged native evidence and write provenance/coverage documentation."""
from pathlib import Path
import hashlib,json,shutil,csv
from PIL import Image
ROOT=Path(__file__).resolve().parents[1];SRC=Path('/workspace/supernova-source-0138')
SHA='b3504c2c4b1725f3070416d14b63e53ae1db3d74';CI='074d7c4a90bafdf6ce487ecf0b2f8b4f773c092f'
BASE='https://github.com/heretoecode/Supernova/blob/'
NATIVE=Path('/workspace/scratch/native138/Supernova/Supernova/nova/Video/build/reports/preview-ui')
refs={
'page-1':('PreviewPagesTest','renderActualPagesAndCheckTabFocus','18 indexed Movie fixtures /movie1..18, year2024, each indexed size1000; synthetic posters/background; offline transport','LS-B-138-MOVIES','Movies header + grid','library-statistics'),
'page-2':('PreviewPagesTest','renderActualPagesAndCheckTabFocus','12 Tvshow fixtures, no snapshot.episodes; count12/size0/sourcecounts0 is fixture sparsity, not owner data','LS-B-138-TV','TV header + grid','library-statistics'),
'library-list':('PreviewPagesTest','renderActualPagesAndCheckTabFocus','Same18 movies; List View selected, real columns/toolbar, synthetic posters','LS-B-138-LIST','Movies header + list columns','library-statistics'),
'0135-custom-page-library':('SupernovaWorkspaceUiTest','fiveStepEditorSavesOnePageAndReopensPrefilled','Empty library Snapshot, default saved Library Page; List View focus. Filename0135 is historical test naming, actual artifact is0.138','LS-B-138-CUSTOM-EMPTY','Custom header empty library + toolbar','library-statistics'),
'customise-home':('Preview41RowsTest','customise','Watch Next moved before Continue Watching and Hidden; media membership fixture, Watch Next focused','CH-B-138-EDITOR','Editor reordered/hidden upper viewport','customise-home'),
'home-genres-initial':('PreviewHomeVisualTest','genreHeaderRemainsVisibleAndChoicesPersistWithoutDone','Fixed27-entry PreviewGenres.NAMES, Drama and Science Fiction checked; All Genres focus, callback no-op','CH-B-138-GENRES-TOP','Genre top focus and checks','customise-home'),
'home-genres-footer-focused':('PreviewHomeVisualTest','genreHeaderRemainsVisibleAndChoicesPersistWithoutDone','Same fixed vocabulary, Western focus after fullScroll; no footer/Done','CH-B-138-GENRES-BOTTOM','Genre scrolled bottom focus','customise-home'),
'home-delete-initial':('PreviewHomeVisualTest','deleteConfirmationKeepsBothButtonsInsideWindow','Test literal Delete row? (lowercase r), production Delete Row?; native AlertDialog Cancel focus','CH-B-138-DELETE-CANCEL','Delete confirmation Cancel focus','customise-home'),
'home-delete-focused':('PreviewHomeVisualTest','deleteConfirmationKeepsBothButtonsInsideWindow','Same test title casing, Delete focus pink-red button','CH-B-138-DELETE-DELETE','Delete confirmation Delete focus','customise-home'),
'home-maximum-keyboard-focused':('PreviewHomeMaximumTest','invalidExactValueKeepsSharedKeyboardOpen','Input initiallybad, validation text Enter a whole number (shorter than production); then value37, Done focus, stale error indicator','CH-B-138-MAX-INPUT','Maximum value37 Done focus after invalid attempt','customise-home')}
records=[]
for filename,(owner,method,fixture,id,state,area) in refs.items():
 target=ROOT/'visuals'/area/(id+'.png');shutil.copyfile(NATIVE/(filename+'.png'),target)
 test=SRC/'src/test/java/com/archos/mediacenter/video/leanback'/(owner+'.java');lines=test.read_text().splitlines()
 line=next((i+1 for i,x in enumerate(lines) if method in x),1)
 assert line!=1 or owner=='Preview41RowsTest'
 records.append({'id':id,'area':area,'state':state,'category':'B — Android native/test render','source_branch':'codex/supernova-0.138-qa-2026-10-12','source_commit':CI,'comparison_baseline':SHA,'equivalence':'git diff CI→signed source lists documentation only; src/res/tests identical','render_method':'Existing Robolectric API28 native View.draw→Bitmap PNG, recovered unchanged; not rerun','date':'2026-10-12','ci_run':'https://github.com/heretoecode/Supernova/actions/runs/38207140954','artifact_id':11728856790,'artifact_entry':'Supernova/Supernova/nova/Video/build/reports/preview-ui/'+filename+'.png','archive_sha256':hashlib.sha256(Path('/workspace/scratch/native138.zip').read_bytes()).hexdigest(),'resolution':list(Image.open(target).size),'viewport':'w960dp-h540dp-land-mdpi; cropped dialog or full960×540 View fixture','fixture':fixture,'source_references':[{'owner':owner,'method_or_token':method,'source_url':BASE+CI+'/'+test.relative_to(SRC).as_posix()+'#L'+str(line)}],'known_deviations':['Synthetic controlled data/artwork; not installed full app or Shield','Framework font/raster/animation can differ from device','State-specific fixture differences described above'],'file':target.relative_to(ROOT).as_posix(),'sha256':hashlib.sha256(target.read_bytes()).hexdigest(),'verified':'Archive CRC and unchanged PNG digest; source/test reference inspected; raster reviewed in gallery'})
historical=[]
old=[('customise-home','1Wo3EKt_15pRtQEoiQVuHYbzrwodomATB'),('home-genres-initial','1lkHPy2PVKVtv-MG7TJsbbRIC5hnJy5Vy'),('home-genres-footer-focused','1wuHcssl8_piT5alhjb30X3pas3eVbTms'),('home-delete-initial','1ZcZOfgHjAgNkKNZYj9ojlPz_z_bvseys'),('home-delete-focused','1gJN-TfqBMUSpOJofMmFZLEz9L9Zmw51y'),('home-maximum-keyboard-focused','15sYReQDj6kdoUmy7cUO37bAeZhla81oL')]
for i,(name,id) in enumerate(old):
 file=Path('/workspace/scratch/native137/Supernova/Supernova/nova/Video/build/reports/preview-ui')/(name+'.png');match=refs[name]
 historical.append({'id':'CH-B-137-HIST-'+str(i+1),'area':'customise-home','state':match[4],'category':'B — historical Android native/test render','source_branch':'codex/supernova-0.137-qa-2026-10-11','source_commit':'68caa2c0ac3c42073b8ed2168995c2316625b29a','audit_commit':'45521c370563c8c176b0c3d9435fbf08fcd115e0','render_method':'Existing unchanged Robolectric API28 native View capture; historical audit and source/test equivalence retained','date':'2026-10-11 (CI); retrieved 2026-10-12','resolution':list(Image.open(file).size),'viewport':'w960dp-h540dp-land-mdpi','fixture':match[2],'source_references':[{'source_url':BASE+'68caa2c0ac3c42073b8ed2168995c2316625b29a/src/test/java/com/archos/mediacenter/video/leanback/'+match[0]+'.java','method_or_token':match[1]}],'ci_run':'https://github.com/heretoecode/Supernova/actions/runs/38109927407','artifact_id':11691549941,'sha256':hashlib.sha256(file.read_bytes()).hexdigest(),'md5':hashlib.md5(file.read_bytes()).hexdigest(),'file_link':'https://drive.google.com/file/d/'+id+'/view','drive_id':id,'known_deviations':match[2]+'; historical0.137 labels/keyboard geometry; not current0.138/Shield','preservation':'Original Drive file referenced, not replaced, renamed or reuploaded'})
(ROOT/'evidence/native-render-manifest.json').write_text(json.dumps(records,indent=2));(ROOT/'evidence/historical-render-manifest.json').write_text(json.dumps(historical,indent=2))
allimages=historical+records+json.loads((ROOT/'visuals/reconstruction-manifest.json').read_text())
(ROOT/'evidence/image-manifest.json').write_text(json.dumps(allimages,indent=2,ensure_ascii=False))
def img(id):
 r=next(r for r in allimages if r['id']==id);return '['+id+']('+r.get('file_link',r.get('file',''))+')'
coverage=[
('CH-001','Main editor upper/built-in, Shown/Hidden/reorder','CH-B-138-EDITOR; CH-C-EDITOR-TOP','B+C','Custom focused actions/newly hidden custom state illustrated selectively; huge rows/text/physical restore unknown'),
('CH-001','Custom row / all five controls / active movement','CH-C-EDITOR-CUSTOM-ROW; CH-C-EDITOR-MOVEMENT','C','No native custom-row/movement render in retrieved artifacts'),
('CH-002','Rename prefilled / create blank / empty-name error','CH-C-RENAME; CH-C-CREATE; CH-C-CREATE-ERROR','C','Native setError bubble omitted; key/caps alternate states not all captured'),
('CH-003','Seven root options and individual focus','CH-C-RULE-FOCUS-0; CH-C-RULE-FOCUS-1; CH-C-RULE-FOCUS-2; CH-C-RULE-FOCUS-3; CH-C-RULE-FOCUS-4; CH-C-RULE-FOCUS-5; CH-C-RULE-FOCUS-6; CH-C-RULE-CHANGED','C','Both toggles/type protection/freeze behavior source verified; Cartesian combinations not exhaustively rendered'),
('CH-004','Genre choices/checks/top/bottom/empty cache','CH-B-138-GENRES-TOP; CH-B-138-GENRES-BOTTOM; CH-C-GENRES-EMPTY; CH-C-GENRES-FIXTURE','B+C','Native fixture fixed vocabulary/no-op callback, actual user/cache genre list unknown'),
('CH-005','Sort Date Added / Title / Year selection','CH-C-SORT-0; CH-C-SORT-1; CH-C-SORT-2','C','No native sort child render'),
('CH-006','All9 max options/default/preset/custom nonpreset','CH-C-MAX-PRESETS; CH-C-MAX-SELECT; CH-C-MAX-CUSTOM','C','Every option present; not every intermediate numeric preset separately focused'),
('CH-006','Numeric input37/overflow error/Done focus','CH-B-138-MAX-INPUT; CH-C-MAX-INPUT; CH-C-MAX-ERROR','B+C','Native test short error differs; production validation text source-derived; blank/max/zero boundaries source-only'),
('CH-007','Cancel/Delete focus and production title casing','CH-B-138-DELETE-CANCEL; CH-B-138-DELETE-DELETE; CH-C-DELETE-CANCEL; CH-C-DELETE-DELETE','B+C','B test lower-case row; C production title, native framework alert geometry/icon omitted'),
('CH-008','Membership plus/check/custom/hidden/Create','CH-C-MEMBERSHIP','C','Native membership absent; creation return-to-new-row source-only'),
('HOME-006','Move/Hide and Move row to allpositions','CH-C-MOVE-HIDE; CH-C-MOVE-TO','C','Many row positions/long-label scroll source-only, no physical navigation execution')]
with (ROOT/'evidence/customise-coverage.tsv').open('w') as f:
 w=csv.writer(f,delimiter='\t');w.writerow(['surface_id','states','images','evidence_kind','remaining_gap']);w.writerows(coverage)
body='''# Customise Home — detailed current reference package

**Delivered source-derived package, with explicit native/device gaps.** The original [technical audit](https://drive.google.com/file/d/1eR29w23z29BcFFoUCh5ttur_LGMX4Q4A/view) and six PNGs are preserved unchanged in [Customise Home Menu Redesign](https://drive.google.com/drive/folders/1AJJ4_S5FS0u87Hk8mh6SathtWiGHMLKN). Their SHA-256/MD5/Drive IDs remain in [historical manifest](evidence/historical-render-manifest.json). This extension is additive, not a rewrite of that audit.

Current facts use signed 0.138 b3504c2c4b1725f3070416d14b63e53ae1db3d74. New reused native images actually come from CI074d7c4a90bafdf6ce487ecf0b2f8b4f773c092f, unsigned run38207140954/artifact11728856790. Git comparison proves app/resources/tests identical to signed source; do not relabel the CI source itself. No new Android test/Shield capture or APK build occurred.

**Evidence classes:** A physical Shield (none supplied); B native Android/test (6 historical +6 newly recovered current); C code-derived approximation (29 current illustrations); D concept artwork (none used). C images carry a visible provenance banner, controlled fixture name, source references, viewport and limitations. They are source-driven HTML/SVG transcriptions, not Android screenshots. Real Canvas icons are selectively transcribed; typography/font wrapping/native alert/input errors remain approximations. Native B images are unmodified bytes.

## Reviewed controls and state coverage

[Full Home/Customise inventory](areas/01-home-navigation.md) covers exact defaults, persistence, callbacks, menu order and focus. [Coverage matrix TSV](evidence/customise-coverage.tsv) is searchable and includes missing-native reasons.

| Surface/state | References | Remaining fidelity gap |
|---|---|---|
'''
for surface,state,ids,kind,gap in coverage:body+='| '+surface+' — '+state+' | '+', '.join(img(x) for x in ids.split('; '))+' | '+gap+' |\n'
body+='''
## Important provenance distinctions

0.138 system Trakt row labels now use `·`, not the old audit's `—`; constructor resets built-in names. Shared keyboard action rows now32 baseline height/3 gap/Space weight3 rather than historical38/5/fixed action widths. Historical images remain evidence of their historical source. Main editor still capped350; two C crops show upper/bottom portions, including custom actions, without inventing an expanded all-row page.

The native delete tests use `Delete row?`, while production source says `Delete Row?`. Native maximum input tests use validator `Enter a whole number`, while production error is `Enter a whole number from 0 to 2147483647. Zero means No Limit.` These are fixture differences, not new production bugs. Native genre vocabulary is the fixed27 test/icon names and callback no-op, not owner library genres or proof of save callbacks.

C editor fixture adds one real-model-compatible `custom:fixture` named `Atlas Fixture`; Watch Next Hidden; movement prefix comes from editor source. Root fixture preserves all seven exact source labels. Checked state is independent of focus. Membership fixture selects Watch Next and a hidden custom row; creation is last. Reconstruction data is deliberately synthetic and recorded in the image manifest.

## Missing captures, justified scope and next request

Fresh native captures of custom editor/movement/root/rename/create/sort/max presets/membership/rail menus are absent from both retrieved CI image sets. This environment has Java runtime/Chromium but no javac, adb, Android emulator/SDK or prepared sibling-module/native build checkout. Existing native renders were recovered preferentially; deterministic source-derived C images fill the structural references without altering tests/source/build files. No build or CI dispatch was attempted.

Still missing: genuine native production callbacks for these child dialogs, every long-name/large-font/row-count boundary, every focus-return after rebuild/delete, all caps/clear/cancel/numeric boundary/error bubbles, actual user row/genre list, physical Shield overscan/render/focus. Their source facts are documented; unavailable rendering/behaviour is not certified. Use [on-demand protocol](ON_DEMAND_REFERENCES.md) with CH IDs and a controlled snapshot/preferences fixture to request native upgrades later. The atlas is not declared fully accepted.
'''
(ROOT/'CUSTOMISE_HOME_VISUAL_PACKAGE.md').write_text(body)
stats='''# Library statistics — source specification and detailed references

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
'''
for id in ['LS-B-138-MOVIES','LS-B-138-TV','LS-B-138-LIST','LS-B-138-CUSTOM-EMPTY','LS-C-MOVIES-PARTIAL','LS-C-TV-SPANNING','LS-C-CUSTOM-MIXED','LS-C-CUSTOM-MOVIES','LS-C-CUSTOM-TV','LS-C-CUSTOM-EMPTY']:
 r=next(r for r in allimages if r['id']==id);stats+='| '+r['state']+' | '+img(id)+' | '+(r['fixture'] if isinstance(r['fixture'],str) else 'Controlled fixtures: exact input rows in fixtures JSON; en-US size strings supplied, Android formatting not executed')+' |\n'
stats+='''
Native Movies shows18 Movies/18 kB all-local because each test Movie indexes1000 bytes. Native TV shows12 Shows/0 B/source counts0 because its test snapshot contains show representatives but no episodes; this is sparse synthetic fixture provenance, not a user's lost files. Native custom empty is valid default saved page with no snapshot data, despite historical0135 filename. Test methods/line links/digests/run/artifact are in [image manifest](evidence/image-manifest.json).

[Controlled fixture inputs](evidence/statistics-fixtures.json) define movie sources/bytes/knownSizes, episodes/show IDs and custom selections for C images. Decimal size text is a documented en-US approximation; exact Formatter output under Android/locale must be recaptured for pixel fidelity. Numeric counts/sums/overlap were checked with a source-transcribed pure calculation, not a new app test. Fixtures are not real library data.

## Remaining verification and request

No native populated custom-movie/custom-TV/mixed/partial/spanning source image is present in retrieved CI. The six C states supply detailed source geometry/semantics with visible approximation banners. Native unknown-size and all real Shield data/typography/output/resolution, locale formatting, extreme text and header scroll/key restoration remain unverified. Request LS IDs with synthetic snapshot, URI schemes and knownSizes using [on-demand protocol](ON_DEMAND_REFERENCES.md). Do not ask the owner to upload their private media DB merely to fill fixtures.

Source: [LibraryPageHeader](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/LibraryPageHeader.java#L9), [PreviewLibrarySummary](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewLibrarySummary.java#L12), [PreviewPages bind](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewPages.java#L549), [loader](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewLibraryLoader.java), [Canvas icons](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewIcon.java#L12), [shared text](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/SharedThreePanel.java#L107).
'''
(ROOT/'LIBRARY_STATISTICS_VISUAL_PACKAGE.md').write_text(stats)
fixtures={
'LS-C-MOVIES-PARTIAL':{'movies':[{'id':'m1','scheme':'file','bytes':1000000000,'knownSizes':1},{'id':'m2','scheme':'content','bytes':0,'knownSizes':0},{'id':'m3','scheme':'webdavs','bytes':2000000000,'knownSizes':1}],'shows':[]},
'LS-C-TV-SPANNING':{'movies':[],'shows':['s1','s2'],'episodes':[{'show':'s1','scheme':'file','bytes':1000000000,'knownSizes':1},{'show':'s1','scheme':'smbj','bytes':1000000000,'knownSizes':1},{'show':'s2','scheme':'webdavs','bytes':1000000000,'knownSizes':1},{'show':'s2','scheme':'webdavs','bytes':0,'knownSizes':0}]},
'LS-C-CUSTOM-MIXED':{'movies':[{'id':'m1','scheme':'file','bytes':500000000,'knownSizes':1},{'id':'m2','scheme':'webdavs','bytes':500000000,'knownSizes':1}],'shows':['s1'],'episodes':[{'show':'s1','scheme':'file','bytes':1000000000,'knownSizes':1},{'show':'s1','scheme':'smbj','bytes':1000000000,'knownSizes':1}],'selection':['m1','m2','s1']},
'LS-C-CUSTOM-EMPTY':{'movies':[],'shows':[],'episodes':[],'selection':[]}}
fixtures['LS-C-CUSTOM-MOVIES']={'movies':fixtures['LS-C-CUSTOM-MIXED']['movies'],'shows':[],'episodes':[],'selection':['m1','m2']}
fixtures['LS-C-CUSTOM-TV']={'movies':[],'shows':['s1'],'episodes':fixtures['LS-C-CUSTOM-MIXED']['episodes'],'selection':['s1']}
checks={}
for id,fixture in fixtures.items():
 result={}
 for kind,items in [('movies',fixture['movies']),('shows',fixture.get('episodes',[]))]:
  local={};network={}
  for entry in items:
   key=entry.get('show',entry.get('id'));target=local if entry['scheme'] in ['file','content',None] else network;target.setdefault(key,0);target[key]+=entry['bytes']
  result[kind]={'total_count':len(fixture[kind]),'total_bytes':sum(x['bytes'] for x in items),'local_count':len(local),'local_bytes':sum(local.values()),'network_count':len(network),'network_bytes':sum(network.values()),'partial':sum(x['knownSizes'] for x in items)<len(items),'spanning':bool(set(local)&set(network))}
 checks[id]=result
(ROOT/'evidence/statistics-fixtures.json').write_text(json.dumps({'method':'Pure Python source-transcribed counts/sums check; not Android execution','data_provenance':'Synthetic documentation fixtures, decimal bytes, en-US displayed Formatter text approximated','fixtures':fixtures,'computed':checks},indent=2))
print('Packaged',len(records),'unchanged native PNGs,',len(historical),'historical references and',len(allimages),'total image records')
