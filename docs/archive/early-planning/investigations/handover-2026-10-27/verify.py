"""Offline handover evidence checks. Run from repository root; never builds an APK."""
import collections, hashlib, json, re, subprocess
from pathlib import Path
import xml.etree.ElementTree as ET
ROOT=Path(__file__).resolve().parents[3]
OUT=Path(__file__).resolve().parent
A='{http://schemas.android.com/apk/res/android}'
rows=json.loads((ROOT/'docs/audits/settings-2026-10-26/inventory.json').read_text())['rows']
mapping={line.split('|')[1].strip():[c.strip() for c in line.split('|')[1:-1]] for line in (ROOT/'docs/design/SETTINGS_AUDIT_MAPPING.md').read_text().splitlines() if re.match(r'\| S\d{3} ',line)}
assert len(rows)==len(mapping)==165
assert len({r['key'] for r in rows})==165
assert set(mapping)=={r['id'] for r in rows}
strings={e.attrib['name']:e.text or '' for p in (ROOT/'res/values').glob('*.xml') for e in ET.parse(p).getroot() if e.tag in ('string','bool')}
strings.update({e.attrib['name']:e.text or '' for e in ET.parse(OUT/'evidence/medialib-nfo_import_export.xml').getroot()})
def resolve(s):return strings.get(s[8:],s) if s and s.startswith('@string/') else strings.get(s[6:],s) if s and s.startswith('@bool/') else s
java={str(p.relative_to(ROOT)):p.read_text() for p in (ROOT/'src/main/java').rglob('*.java')}
records=[]
for r in rows:
    m=mapping[r['id']];assert m[5].strip('`')==r['key'],r['id']
    path,line=r['source'].rsplit(':',1);text=(ROOT/path).read_text();assert 0<int(line)<=len(text.splitlines())
    evidence={'declaration':r['source'],'reference_line_exists':True}
    if path.endswith('.xml'):
        nodes=[e for e in ET.fromstring(text).iter() if resolve(e.get(A+'key'))==r['key']]
        assert len(nodes)==1,(r['id'],len(nodes))
        node=nodes[0];evidence.update(xml_type=node.tag,xml_default=node.get(A+'defaultValue'),dependency=node.get(A+'dependency'),entries=node.get(A+'entries'),entry_values=node.get(A+'entryValues'))
        # Runtime fallbacks can override missing/default XML; preserve both sources.
        if node.get(A+'defaultValue') is not None:assert resolve(node.get(A+'defaultValue'))==r['default'],(r['id'],r['default'])
    evidence['consumer_reference_count']=len(r['consumers'])
    for p,n in r['consumers']: assert (ROOT/p).is_file() and 0<n<=len((ROOT/p).read_text().splitlines()),(r['id'],p,n)
    # Literal matches are trace leads, not execution or dependency proof.
    evidence['literal_key_files']=[p for p,t in java.items() if r['key'] in t]
    if r['id']>='S140':
        category=r['key'].removeprefix('supernova_backup_include_').upper()
        enum=java['src/main/java/com/archos/mediacenter/video/utils/backup/BackupCategory.java']
        match=re.search(r'\b'+category+r'\([^\n]*?,\d+,(true|false),',enum)
        assert match and match.group(1)==r['default'],r['id']
        evidence['constructed_key']='BackupCategory.preference(): prefix + enum.name().toLowerCase(Locale.ROOT)'
    disposition='Retain existing capability/account/advanced gate; reuse owner handler'
    if m[2]=='REVIEW / NON-EXPOSED':disposition='Do not render; retain persisted state and existing consumers'
    if r['id'] in ('S089','S101','S126','S070'):disposition='No Settings duplicate; retain Network & Files ownership/backend'
    if r['id'] in ('S052','S125'):disposition='One positive visible proxy S125; inverse persisted S052; repair listener F02'
    if r['id'] in ('S106','S107'):disposition='Backup inclusion compatibility model only; mirror category flags, no duplicate rows'
    if r['id']>='S140':disposition='Nested Export Backup category; restore selections temporary until confirmation'
    if r['id']=='S091':disposition='Legacy sponsor state retained; no new sponsor control; About uses Foundation workspace'
    if r['id'] in ('S001','S092','S093','S094','S095'):disposition='Route disclosure/attribution into existing Foundation About workspace, no duplicate legacy surface'
    records.append({**r,'destination':m[2],'subsection':m[3],'disposition':disposition,'verification':evidence,'runtime_test':'NOT RUN'})
assets={}
for p in ['assets/branding/Supernova_Space_Black_Blend_4K_UHD.png','res/drawable-nodpi/foundation_space_black.png','assets/branding/Supernova_Double_Ring_Final_Visual_Reference.png','res/drawable-nodpi/foundation_banner.png','res/drawable-nodpi/foundation_splash_image.png']:
    assets[p]=hashlib.sha256((ROOT/p).read_bytes()).hexdigest()
exports=json.loads((ROOT/'docs/foundation/BRANDING_EXPORTS.json').read_text())['files']
for p,digest in assets.items():assert exports[p]['sha256']==digest,p
for path in ['src','res','assets']:
    assert not subprocess.run(['git','diff','--quiet','b347707d','HEAD','--',path],cwd=ROOT).returncode
report={'baseline':'b347707defdbf2ab3ae83d920ba9fa7af3936220','definitions':165,'destinations':dict(collections.Counter(r['destination'] for r in records)),'checks':'mapping keys, XML declarations/defaults, consumer reference bounds, 26 backup enum defaults/constructed keys, asset digests, app-tree identity','limits':'No runtime/physical acceptance. General audit dependency strings are not a per-device capability result. Runtime option lists remain dynamically sourced.','assets':assets,'rows':records}
(OUT/'SETTINGS_VERIFICATION.json').write_text(json.dumps(report,indent=2)+'\n')
md=['# Settings row disposition and QA ledger','','Generated by `python docs/investigations/handover-2026-10-27/verify.py`. Keys/defaults/options/consumer leads are in [JSON](SETTINGS_VERIFICATION.json). All runtime results remain NOT RUN. See [specification](SETTINGS_TECHNICAL_SPEC.md) for interaction families and gates.','','| ID | Destination / subsection | Disposition | Required acceptance |','|---|---|---|---|']
for r in records:
    test='Absent from new UI; saved value unchanged' if 'Do not render' in r['disposition'] or 'No Settings duplicate' in r['disposition'] else 'Exercise accepted/cancelled change or action; reopen/restart; gate/failure/focus/Back'
    md.append(f"| {r['id']} | {r['destination']} / {r['subsection']} | {r['disposition']} | {test}; NOT RUN |")
(OUT/'SETTINGS_DISPOSITION.md').write_text('\n'.join(md)+'\n')
print('PASS: 165 mapping rows; XML defaults; consumer references; 26 backup defaults; 5 branding hashes; unchanged app tree. Runtime NOT RUN.')
