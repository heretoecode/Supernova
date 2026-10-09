#!/usr/bin/env python3
"""Export reviewed runtime legal evidence from resolved archives, without network I/O.

Inputs are a Gradle foundationDependencyInventory and a separately reviewed legal
source directory. Output never includes local cache paths or signing material.
Native component provenance is handled separately, not inferred from Java POMs.
"""
import argparse
from collections import defaultdict
import hashlib
import json
from pathlib import Path
import re
import xml.etree.ElementTree as ET
import zipfile

NS = {'m': 'http://maven.apache.org/POM/4.0.0'}
ROOT = Path(__file__).resolve().parents[1]

def digest(data):
    return hashlib.sha256(data).hexdigest()

def export(inventory, legal):
    legal = Path(legal)
    source_urls = json.loads((legal / 'sources.json').read_text())
    target = ROOT / 'assets/foundation/legal'
    target.mkdir(parents=True, exist_ok=True)
    groups = defaultdict(list)
    sbom = []
    provenance = {}
    def read(name):
        data = (legal / name).read_bytes()
        provenance[name] = {'sha256': digest(data), 'source': source_urls.get(name, 'Reviewed local source')}
        return data.decode('utf-8')
    for artifact in json.loads(Path(inventory).read_text()):
        coordinate = artifact['coordinate']
        group, module, version = coordinate.split(':')
        archive = Path(artifact['file'])
        poms = sorted(archive.parent.parent.glob('*/*.pom'))
        if len(poms) != 1:
            raise ValueError('Expected exact resolved POM for ' + coordinate)
        pom = ET.parse(poms[0]).getroot()
        labels = [x.findtext('m:name', namespaces=NS) for x in pom.findall('m:licenses/m:license', NS)]
        url = pom.findtext('m:url', namespaces=NS)
        texts = []
        with zipfile.ZipFile(archive) as zipped:
            for name in sorted(zipped.namelist()):
                leaf = name.rsplit('/', 1)[-1].lower()
                if not name.endswith(('/', '.class')) and re.match(r'^(license|licence|notice|copying|sentry_third_party_notices)([._-]|$)', leaf):
                    texts.append(('Archive ' + name, zipped.read(name).decode('utf-8', errors='strict')))
        licence = 'Apache-2.0'
        family, destination = module, url
        supplement = []
        if group.startswith('androidx.'):
            family, destination = 'AndroidX', 'https://developer.android.com/jetpack/androidx'
        elif group.startswith(('org.jetbrains', 'org.jetbrains.kotlinx')):
            family, destination = 'Kotlin & JetBrains libraries', 'https://kotlinlang.org/'
        elif group.startswith('com.squareup.okhttp3'):
            family, destination = 'OkHttp', 'https://square.github.io/okhttp/'
        elif group == 'com.squareup.retrofit2':
            family, destination = 'Retrofit', 'https://github.com/square/retrofit'
        elif group == 'org.bouncycastle':
            family, destination, licence = 'Bouncy Castle', 'https://www.bouncycastle.org/', 'MIT'
        elif group.startswith('org.eclipse.jetty'):
            family, destination, licence = 'Jetty', 'https://jetty.org/', 'Apache-2.0 OR EPL-1.0'
            supplement = ['Apache-2.0.txt', 'EPL-1.0.txt', 'JettyNotice.txt']
        elif group == 'org.jupnp':
            family, destination, licence = 'JUPnP', 'https://github.com/jupnp/jupnp', 'CDDL-1.0'
            supplement = ['CDDL-1.0.txt']
        elif group == 'io.sentry':
            family, destination, licence = 'Sentry', 'https://github.com/getsentry/sentry-java', 'MIT'
            supplement = ['SentryNative.txt' if module == 'sentry-native-ndk' else 'Sentry.txt']
        elif module == 'epitaph':
            licence, supplement = 'MIT', ['Epitaph.txt']
        elif module == 'mbassador':
            licence, supplement = 'MIT', ['MBassador.txt']
        elif module == 'jcifs-ng':
            licence, supplement = 'LGPL-2.1-or-later', ['JCIFS.txt']
            destination = 'https://github.com/nova-video-player/jcifs-ng'
        elif module == 'jsch-mwiede':
            licence = 'BSD-3-Clause AND ISC'
            destination = 'https://github.com/nova-video-player/jsch-mwiede'
        elif module == 'javax.servlet-api':
            licence = 'CDDL-1.1 OR GPL-2.0-only WITH Classpath-exception-2.0'
            supplement = ['CDDL-1.1.txt', 'GPL-2.0-only.txt', 'Classpath-exception-2.0.txt']
        elif module == 'threetenbp':
            licence, supplement = 'BSD-3-Clause', ['ThreeTenBP.txt']
        elif module == 'slf4j-api':
            licence, destination = 'MIT', 'https://www.slf4j.org/'
        elif module == 'oro':
            licence, destination = 'Apache-1.1', 'https://attic.apache.org/projects/jakarta-oro.html'
        elif module == 'desugar_jdk_libs':
            family, licence = 'OpenJDK desugared libraries', 'GPL-2.0-only WITH Classpath-exception-2.0'
            supplement = ['DesugarLICENSE.txt', 'DesugarAdditional.txt', 'DesugarAssembly.txt']
        elif module == 'desugar_jdk_libs_configuration':
            family, licence = 'OpenJDK desugaring configuration', 'Apache-2.0'
        if licence == 'Apache-2.0':
            if not labels and not texts and module not in ('core', 'listenablefuture'):
                raise ValueError('No archive/POM licence evidence for ' + coordinate)
            supplement = ['Apache-2.0.txt'] + supplement
        if not destination or not destination.startswith('https://') or any(x in destination for x in ('?', '#', '${')):
            official = {
                'gson': 'https://github.com/google/gson',
                'error_prone_annotations': 'https://github.com/google/error-prone',
                'listenablefuture': 'https://github.com/google/guava',
                'core': 'https://github.com/zxing/zxing',
                'httpcore5': 'https://hc.apache.org/',
                'org.apache.oltu.oauth2.client': 'https://attic.apache.org/projects/oltu.html',
                'simple-xml': 'https://github.com/ngallagher/simplexml',
                'jspecify': 'https://jspecify.dev/',
            }
            destination = official.get(module, destination.replace('http://', 'https://') if destination else None)
        if not destination:
            raise ValueError('No official destination for ' + coordinate)
        original = '\n\n'.join(label + '\n' + text for label, text in texts)
        complete = '\n\n'.join([original] + [read(x) for x in supplement]).strip()
        if len(complete) < 500:
            raise ValueError('Missing full legal text for ' + coordinate)
        record = {'coordinate': coordinate, 'filename': archive.name, 'sha256': digest(archive.read_bytes()),
                  'configuration': artifact.get('configuration', 'noamazonReleaseRuntimeClasspath'),
                  'pom_sha256': digest(poms[0].read_bytes()), 'declared_licences': labels,
                  'licence': licence, 'official_url': destination,
                  'archive_notices': [x[0].removeprefix('Archive ') for x in texts], 'legal_family': family}
        parent = pom.find('m:parent', NS)
        if parent is not None:
            record['pom_parent'] = ':'.join(parent.findtext('m:' + k, namespaces=NS) for k in ('groupId','artifactId','version'))
        sbom.append(record)
        groups[family].append((record, complete))
    catalogue = []
    for name, members in sorted(groups.items()):
        filename = re.sub(r'[^a-z0-9]+', '-', name.lower()).strip('-') + '.txt'
        sections = []
        legal_seen = set()
        for record, text in members:
            sections.append(record['coordinate'] + '\nArchive SHA-256: ' + record['sha256'] + '\nLicence: ' + record['licence'])
            if text not in legal_seen:
                sections.append(text)
                legal_seen.add(text)
        (target / filename).write_text('\n\n'.join(sections) + '\n')
        catalogue.append({'name':name, 'version': ', '.join(sorted({x[0]['coordinate'].split(':')[2] for x in members})),
                          'role':'Runtime software' if not name.startswith('OpenJDK') else 'Compatibility runtime',
                          'licence':' / '.join(sorted({x[0]['licence'] for x in members})),
                          'url':members[0][0]['official_url'], 'text_asset':'foundation/legal/' + filename})
    native_catalogue = ROOT / 'docs/foundation/NATIVE_LICENCE_ENTRIES.json'
    if native_catalogue.exists():
        catalogue.extend(json.loads(native_catalogue.read_text()))
    (ROOT / 'assets/foundation/licences.json').write_text(json.dumps({'schema':1, 'components':catalogue},indent=2)+'\n')
    (ROOT / 'docs/foundation/RUNTIME_SBOM.json').write_text(json.dumps({'schema':1, 'scope':'Resolved external release runtime and core-library desugaring artifacts; conservative inclusion before R8 reachability inspection. Test/build plugins excluded.', 'artifacts':sbom, 'legal_sources':provenance},indent=2)+'\n')
    print(f'Exported {len(sbom)} resolved artifacts in {len(catalogue)} offline legal entries')

if __name__ == '__main__':
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--inventory', required=True)
    parser.add_argument('--legal-sources', required=True)
    args = parser.parse_args()
    export(args.inventory, args.legal_sources)
