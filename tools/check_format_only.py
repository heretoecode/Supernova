#!/usr/bin/env python3
"""Prove that Java changes are formatting-only.

For every .java file changed between BASE and HEAD (or the files given), the sequence of Java
tokens must be identical before and after — whitespace and line breaks may change, nothing else.
Comments must also survive with the same text (whitespace inside them is normalised). Any other
difference fails, with the first differing token shown.

Usage:
  python3 tools/check_format_only.py --base origin/claude/supernova-github-access-yfhe89
  python3 tools/check_format_only.py --base <ref> --files src/main/java/.../PreviewSearch.java
Optional:  --allow-unused-imports   also permit removing whole `import ...;` statements
Exit code 0 = every file is formatting-only; 1 = at least one file changed meaning.
"""
import argparse
import re
import subprocess
import sys

TOKEN = re.compile(r'''
    (?P<text>"""[\s\S]*?""")                       # text block
  | (?P<string>"(?:\\.|[^"\\\n])*")                # string literal
  | (?P<char>'(?:\\.|[^'\\\n])+')                  # char literal
  | (?P<block>/\*[\s\S]*?\*/)                      # block / javadoc comment
  | (?P<line>//[^\n]*)                             # line comment
  | (?P<ws>\s+)
  | (?P<num>0[xX][0-9a-fA-F_]+[lL]?|\d[\d_]*(?:\.[\d_]*)?(?:[eE][+-]?\d+)?[fFdDlL]?|\.\d[\d_]*(?:[eE][+-]?\d+)?[fFdD]?)
  | (?P<word>[A-Za-z_$][\w$]*)
  | (?P<op>>>>=|<<=|>>=|>>>|\.\.\.|->|::|\+\+|--|&&|\|\||[=!<>+\-*/%&|^]=|<<|[{}()\[\];,.@=<>!~?:+\-*/&|^%])
''', re.VERBOSE)


def tokens(source):
    """Code tokens and normalised comments, in order. Raises ValueError on untokenisable input."""
    out, pos = [], 0
    while pos < len(source):
        m = TOKEN.match(source, pos)
        if not m:
            raise ValueError(f'cannot tokenise at offset {pos}: {source[pos:pos + 40]!r}')
        kind = m.lastgroup
        value = m.group(kind)
        if kind == 'ws':
            pass
        elif kind == 'line':
            out.append(('comment', ' '.join(value.split())))
        elif kind == 'block':
            # Re-laying out a /** ... */ comment may add or drop the leading `*` on each line.
            opener = '/**' if value.startswith('/**') and value != '/**/' else '/*'
            inner = value[len(opener):-2]
            lines = [re.sub(r'^\s*\*(?!/)', '', line) for line in inner.split('\n')]
            out.append(('comment', opener + ' ' + ' '.join(' '.join(lines).split()) + ' */'))
        else:
            out.append((kind, value))
        pos = m.end()
    return out


def without_imports(toks):
    """Drops `import ... ;` statements (only used with --allow-unused-imports)."""
    out, i = [], 0
    while i < len(toks):
        if toks[i] == ('word', 'import'):
            while i < len(toks) and toks[i] != ('op', ';'):
                i += 1
            i += 1
            continue
        out.append(toks[i])
        i += 1
    return out


def git(*args):
    return subprocess.run(['git', *args], check=True, capture_output=True, text=True).stdout


def show(ref, path):
    try:
        return git('show', f'{ref}:{path}')
    except subprocess.CalledProcessError:
        return None


def compare(before, after, allow_imports=False):
    """Returns None when equivalent, else a description of the first difference."""
    a, b = tokens(before), tokens(after)
    if allow_imports:
        a, b = without_imports(a), without_imports(b)
        # Removing an import is only allowed if no import was added.
        added = set(_imports(after)) - set(_imports(before))
        if added:
            return f'imports added: {sorted(added)}'
    for n, (x, y) in enumerate(zip(a, b)):
        if x != y:
            return f'token {n}: {x[1][:60]!r} became {y[1][:60]!r}'
    if len(a) != len(b):
        return f'token count {len(a)} became {len(b)}'
    return None


def _imports(source):
    return re.findall(r'^\s*import\s+([\w.*\s]+);', source, re.MULTILINE)


def main():
    p = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    p.add_argument('--base', required=True, help='git ref the change started from')
    p.add_argument('--head', default='HEAD')
    p.add_argument('--files', nargs='*', help='limit to these paths (default: every changed .java file)')
    p.add_argument('--allow-unused-imports', action='store_true')
    args = p.parse_args()
    paths = args.files or [line for line in git('diff', '--name-only', args.base, args.head).splitlines() if line.endswith('.java')]
    # The only non-Java change allowed is the overnight report itself.
    other = [line for line in git('diff', '--name-only', args.base, args.head).splitlines()
             if not line.endswith('.java') and not line.startswith('docs/codex/reports/')]
    failures = 0
    for path in paths:
        before, after = show(args.base, path), show(args.head, path)
        if before is None or after is None:
            print(f'FAIL {path}: file added or deleted')
            failures += 1
            continue
        try:
            problem = compare(before, after, args.allow_unused_imports)
        except ValueError as error:
            problem = str(error)
        if problem:
            print(f'FAIL {path}: {problem}')
            failures += 1
        else:
            print(f'ok   {path}  (longest line {max(map(len, before.splitlines()), default=0)} -> {max(map(len, after.splitlines()), default=0)})')
    if not args.files and other:
        print('FAIL non-Java files changed: ' + ', '.join(other))
        failures += 1
    print(f'{len(paths) - min(failures, len(paths))} of {len(paths)} files formatting-only')
    return 1 if failures else 0


if __name__ == '__main__':
    sys.exit(main())
