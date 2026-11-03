#!/usr/bin/env python3
"""Prove that Java changes only tidy imports and layout (and, if allowed, add comments).

Allowed, and nothing else:
  * layout: whitespace and line breaks (as tools/check_format_only.py);
  * writing a class by its simple name instead of its full name, where the simple name means exactly that
    class in that file: through an existing or newly added single-type import, or because the class is in
    the same package (`com.archos.x.Foo` -> `Foo`, `android.view.View` -> `View` + `import android.view.View;`);
  * removing an import whose name is used nowhere in the file (code or comments);
  * with --allow-new-comments: adding comments; existing comments must stay unchanged.

How it is proved, per file (base -> head):
  1. Each version is put in canonical form: every bare type name that the version's own imports or package
     resolve is written out in full. The two canonical forms must be identical (import lines aside; new
     comment tokens may be skipped with --allow-new-comments). So a bare name in the new file must resolve
     to the same class the old file named — a different class, shadowing or a dropped import shows up as a
     difference.
  2. Added imports must be ordinary single-type imports (no `static`, no `*`) and must not clash with another
     import of the same simple name. Removed imports must not be wildcards and must not be mentioned in
     comments (Javadoc links).

Names that neither version resolves (java.lang, wildcard imports, nested or inherited types) are left as
they are on both sides and must therefore be written identically. The full build and test suite run
afterwards cover the one thing a token check cannot see: a member type inherited from a superclass.

Usage:
  python3 tools/check_imports_only.py --base claude/supernova-github-access-yfhe89
  python3 tools/check_imports_only.py --base <ref> --files <path> [--allow-new-comments]
Exit code 0 = every file only tidied imports/layout (and comments if allowed); 1 = otherwise.
"""
import argparse
import re
import subprocess
import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parent))
from check_format_only import tokens, git, show  # noqa: E402

# Non-Java paths the overnight job may change.
ALLOWED_OTHER = ('docs/codex/reports/', 'docs/Future Development/')
DECLARATION = re.compile(r'\b(?:class|interface|enum|record)\s+([A-Z]\w*)')


def split_imports(toks):
    """Returns (package, imports, body). Imports are (is_static, dotted name); body excludes package/imports."""
    package, imports, body, i = '', [], [], 0
    while i < len(toks):
        if toks[i] in (('word', 'import'), ('word', 'package')) and not (i and toks[i - 1] == ('op', '.')):
            keyword = toks[i][1]
            j, parts, static = i + 1, [], False
            if keyword == 'import' and j < len(toks) and toks[j] == ('word', 'static'):
                static, j = True, j + 1
            while j < len(toks) and toks[j] != ('op', ';'):
                if toks[j][0] != 'comment':
                    parts.append(toks[j][1])
                j += 1
            if keyword == 'package':
                package = ''.join(parts)
            else:
                imports.append((static, ''.join(parts)))
            i = j + 1
            continue
        body.append(toks[i])
        i += 1
    return package, imports, body


def resolver(package, imports, body, path, package_types):
    """Simple name -> full name, for names this file resolves without doubt."""
    names = {}
    own = Path(path).stem
    declared = {m.group(1) for m in DECLARATION.finditer(' '.join(v for k, v in body if k != 'comment'))}
    for simple in package_types:
        names[simple] = package + '.' + simple if package else simple
    for static, name in imports:
        if not static and not name.endswith('*'):
            names[name.rsplit('.', 1)[-1]] = name
    for simple in declared - {own}:
        names.pop(simple, None)  # a nested type in this file shadows the import/package class
    return names


def canonical(body, names):
    out = []
    for n, tok in enumerate(body):
        if tok[0] == 'word' and tok[1] in names and not (n and body[n - 1] == ('op', '.')):
            for k, part in enumerate(names[tok[1]].split('.')):
                if k:
                    out.append(('op', '.'))
                out.append(('word', part))
        else:
            out.append(tok)
    return out


def comment_words(body):
    words = set()
    for kind, value in body:
        if kind == 'comment':
            words.update(w.strip('{}()[]<>#.,;:*@`"\'') for w in value.replace('#', ' #').split())
    return words


def compare(before, after, path='T.java', package_types=(), allow_new_comments=False):
    """Returns None when the change only tidies imports/layout (and adds comments if allowed), else a problem."""
    old_pkg, old_imports, old_body = split_imports(tokens(before))
    new_pkg, new_imports, new_body = split_imports(tokens(after))
    if old_pkg != new_pkg:
        return f'package changed: {old_pkg} -> {new_pkg}'
    old_set, new_set = set(old_imports), set(new_imports)
    for static, name in sorted(new_set - old_set):
        if static or name.endswith('*'):
            return f'only ordinary single-type imports may be added: {name}'
    seen = {}
    for static, name in sorted(new_set):
        if static or name.endswith('*'):
            continue
        simple = name.rsplit('.', 1)[-1]
        if simple in seen and seen[simple] != name:
            return f'two imports share the name {simple}: {seen[simple]} and {name}'
        seen[simple] = name
    new_comments = comment_words(new_body)
    for static, name in sorted(old_set - new_set):
        if name.endswith('*'):
            return f'wildcard import removed (cannot prove unused): {name}'
        if name.rsplit('.', 1)[-1] in new_comments:
            return f'removed import {name} is still mentioned in a comment'

    a = canonical(old_body, resolver(old_pkg, old_imports, old_body, path, package_types))
    b = canonical(new_body, resolver(new_pkg, new_imports, new_body, path, package_types))
    i = j = 0
    while i < len(a) or j < len(b):
        if i < len(a) and j < len(b) and a[i] == b[j]:
            i, j = i + 1, j + 1
        elif allow_new_comments and j < len(b) and b[j][0] == 'comment':
            j += 1
        else:
            x = a[i][1][:60] if i < len(a) else '<end>'
            y = b[j][1][:60] if j < len(b) else '<end>'
            return f'token {i}: {x!r} became {y!r}'
    return None


def package_types(ref, path, package):
    """Top-level type names in the same package, across every source set at that ref."""
    if not package:
        return set()
    folder = package.replace('.', '/')
    listing = git('ls-tree', '-r', '--name-only', ref, '--', 'src')
    return {Path(p).stem for p in listing.splitlines()
            if p.endswith('.java') and str(Path(p).parent).endswith('/java/' + folder)}


def main():
    p = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    p.add_argument('--base', required=True, help='git ref the change started from')
    p.add_argument('--head', default='HEAD')
    p.add_argument('--files', nargs='*', help='limit to these paths (default: every changed .java file)')
    p.add_argument('--allow-new-comments', action='store_true', help='also permit added comments')
    args = p.parse_args()
    changed = git('diff', '--name-only', args.base, args.head).splitlines()
    paths = args.files or [line for line in changed if line.endswith('.java')]
    other = [line for line in changed if not line.endswith('.java') and not line.startswith(ALLOWED_OTHER)]
    failures = 0
    for path in paths:
        before, after = show(args.base, path), show(args.head, path)
        if before is None or after is None:
            print(f'FAIL {path}: file added or deleted')
            failures += 1
            continue
        try:
            package = split_imports(tokens(before))[0]
            types = package_types(args.base, path, package) & package_types(args.head, path, package)
            problem = compare(before, after, path, types, args.allow_new_comments)
        except ValueError as error:
            problem = str(error)
        if problem:
            print(f'FAIL {path}: {problem}')
            failures += 1
        else:
            print(f'ok   {path}')
    if not args.files and other:
        print('FAIL files outside the allowed areas changed: ' + ', '.join(other))
        failures += 1
    print(f'{len(paths) - min(failures, len(paths))} of {len(paths)} files only tidy imports/layout'
          + (' (comments may be added)' if args.allow_new_comments else ''))
    return 1 if failures else 0


if __name__ == '__main__':
    sys.exit(main())
