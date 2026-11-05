"""Smoke alarm for crammed Java (owner rule, 5 Nov 2026): one statement per line.

A line with three or more statements (three semicolons outside a `for (...)` header, comments and string-only
continuation lines) counts as crammed. Files listed in docs/codex/REFORMAT_QUEUE.txt are allowed their recorded
number of crammed lines while they wait to be reformatted, but may not get worse; every other file must have none.
"""
import pathlib
import re
import unittest

ROOT = pathlib.Path(__file__).resolve().parent.parent
QUEUE = ROOT / "docs" / "codex" / "REFORMAT_QUEUE.txt"
SOURCES = [ROOT / "src" / "main" / "java", ROOT / "src" / "test" / "java"]


def crammed_lines(text):
    count = 0
    for line in text.split("\n"):
        stripped = line.strip()
        if not stripped or stripped.startswith(("//", "*", "/*", "+ \"", "\"")):
            continue
        if stripped.startswith("for") or stripped.startswith("} for"):
            continue
        if stripped.count(";") >= 3:
            count += 1
    return count


def queue_allowances():
    allowed = {}
    if not QUEUE.exists():
        return allowed
    for line in QUEUE.read_text(encoding="utf-8").splitlines():
        match = re.match(r"\s*(\d+) crammed lines .* · (\S+\.java)\s*$", line)
        if match:
            allowed[match.group(2)] = int(match.group(1))
    return allowed


class CodeLayoutTest(unittest.TestCase):
    def test_counts_three_statements_on_one_line(self):
        self.assertEqual(1, crammed_lines("a();b();c();\n"))
        self.assertEqual(0, crammed_lines("for (int i = 0; i < n; i++) {\n"))
        self.assertEqual(0, crammed_lines("a();\nb();\nc();\n"))
        self.assertEqual(0, crammed_lines("// a(); b(); c();\n"))

    def test_no_new_crammed_code(self):
        allowed = queue_allowances()
        problems = []
        for base in SOURCES:
            for path in sorted(base.rglob("*.java")):
                relative = path.relative_to(ROOT).as_posix()
                found = crammed_lines(path.read_text(encoding="utf-8", errors="replace"))
                limit = allowed.get(relative, 0)
                if found > limit:
                    problems.append(f"{relative}: {found} crammed lines (allowed {limit})")
        self.assertEqual([], problems, "Write one statement per line (see docs/codex/BRIEF_0156_CODE.md):\n" +
                         "\n".join(problems))


if __name__ == "__main__":
    unittest.main()
