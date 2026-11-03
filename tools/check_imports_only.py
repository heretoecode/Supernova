#!/usr/bin/env python3
"""Prove that Java changes only tidy imports (plus layout).

Allowed, and nothing else:
  * layout: whitespace and line breaks (as tools/check_format_only.py);
  * folding a fully qualified name into an import: `android.view.View v` -> `import android.view.View;` + `View v`;
  * removing an import whose name is used nowhere in the file (code or comments).

How it is proved, per file (base -> head):
  1. Imports are read from both versions. Added imports must be ordinary single-type imports
     (no `static`, no `*`). Removed imports must be unused in the new file.
  2. An added import's simple name must not appear as a bare name anywhere in the old file. Then no
     existing reference can change meaning (no shadowing of java.lang, same-package, nested or
     wildcard-imported types), and every bare use in the new file came from folding.
  3. In the new file every bare use of an added simple name is expanded back to its full name, and the
     result must equal the old file token for token (import statements aside). Comments must be unchanged.

Usage:
  python3 tools/check_imports_only.py --base claude/supernova-github-access-yfhe89
  python3 tools/check_imports_only.py --base <ref> --files src/main/java/.../SupernovaStyle.java
Exit code 0 = every file only tidied imports/layout; 1 = at least one file changed meaning.
"""
import argparse
import subprocess
import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parent))
from check_format_only import tokens, git, show  # noqa: E402

# Non-Java paths the overnight job may change (reports and the Future Development register).
ALLOWED_OTHER = ('docs/codex/reports/', 'docs/Future Development/')


def split_imports(toks):
    """Returns (imports, body): imports as (is_static, dotted name) tuples, body = tokens without them."""
    imports, body, i = [], [], 0
    while i < len(toks):
        if toks[i] == ('word', 'import'):
            j, parts, static = i + 1, [], False
            if j < len(toks) and toks[j] == ('word', 'static'):
                static, j = True, j + 1
            while j < len(toks) and toks[j] != ('op', ';'):
                if toks[j][0] != 'comment':
                    parts.append(toks[j][1])
                j += 1
            imports.append((static, ''.join(parts)))
            i = j + 1
            continue
        body.append(toks[i])
        i += 1
    return imports, body


def bare_names(body):
    """Words not preceded by `.` (a `.` before a word makes it a member or package part, not a bare name)."""
    names = set()
    for n, (kind, value) in enumerate(body):
        if kind == 'word' and not (n and body[n - 1] == ('op', '.')):
            names.add(value)
    return names


def comment_words(body):
    words = set()
    for kind, value in body:
        if kind == 'comment':
            words.update(w.strip('{}()[]<>#.,;:*@`"\'') for w in value.replace('#', ' #').split())
    return words


def expand(body, added):
    """Replaces each bare use of an added simple name with the tokens of its full name."""
    out = []
    for n, tok in enumerate(body):
        if tok[0] == 'word' and tok[1] in added and not (n and body[n - 1] == ('op', '.')):
            parts = added[tok[1]].split('.')
            for k, part in enumerate(parts):
                if k:
                    out.append(('op', '.'))
                out.append(('word', part))
        else:
            out.append(tok)
    return out


def compare(before, after):
    """Returns None when the change only tidies imports/layout, else the first problem found."""
    old_imports, old_body = split_imports(tokens(before))
    new_imports, new_body = split_imports(tokens(after))
    old_set, new_set = set(old_imports), set(new_imports)
    added, removed = new_set - old_set, old_set - new_set

    simple_added = {}
    old_bare = bare_names(old_body)
    for static, name in sorted(added):
        if static or name.endswith('*'):
            return f'only ordinary single-type imports may be added: {name}'
        simple = name.rsplit('.', 1)[-1]
        if simple in simple_added:
            return f'two added imports share the name {simple}'
        if any(not s and n != name and n.rsplit('.', 1)[-1] == simple for s, n in new_set):
            return f'added import {name} clashes with another import of {simple}'
        if simple in old_bare:
            return f'{simple} was already used as a bare name; importing {name} could change its meaning'
        simple_added[simple] = name

    new_bare, new_comments = bare_names(new_body), comment_words(new_body)
    for static, name in sorted(removed):
        if name.endswith('*'):
            return f'wildcard import removed (cannot prove unused): {name}'
        simple = name.rsplit('.', 1)[-1]
        if simple in new_bare or simple in new_comments:
            return f'removed import {name} is still used ({simple})'

    expanded = expand(new_body, simple_added)
    for n, (x, y) in enumerate(zip(old_body, expanded)):
        if x != y:
            return f'token {n}: {x[1][:60]!r} became {y[1][:60]!r}'
    if len(old_body) != len(expanded):
        return f'token count {len(old_body)} became {len(expanded)}'
    return None


def main():
    p = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    p.add_argument('--base', required=True, help='git ref the change started from')
    p.add_argument('--head', default='HEAD')
    p.add_argument('--files', nargs='*', help='limit to these paths (default: every changed .java file)')
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
            problem = compare(before, after)
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
    print(f'{len(paths) - min(failures, len(paths))} of {len(paths)} files only tidy imports/layout')
    return 1 if failures else 0


if __name__ == '__main__':
    sys.exit(main())
