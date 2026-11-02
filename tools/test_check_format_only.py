"""The overnight formatting checker accepts layout-only changes and rejects anything else."""
import importlib.util
import unittest
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
spec = importlib.util.spec_from_file_location('fmt', ROOT / 'tools/check_format_only.py')
fmt = importlib.util.module_from_spec(spec)
spec.loader.exec_module(fmt)

DENSE = 'package a;import java.util.*;/** Doc  comment */final class A{int x=1;String s="a;b // not a comment";char c=\';\';void f(){if(x>0){x+=2;}else{x--;}}// trailing\n}'
SPREAD = '''package a;

import java.util.*;

/**
 * Doc comment
 */
final class A {
    int x = 1;
    String s = "a;b // not a comment";
    char c = ';';

    void f() {
        if (x > 0) {
            x += 2;
        } else {
            x--;
        }
    } // trailing
}
'''


class FormatOnlyTest(unittest.TestCase):
    def test_layout_change_is_accepted(self):
        self.assertIsNone(fmt.compare(DENSE, SPREAD))

    def test_changed_literal_is_rejected(self):
        self.assertIsNotNone(fmt.compare(DENSE, SPREAD.replace('x += 2', 'x += 3')))

    def test_changed_string_content_is_rejected(self):
        self.assertIsNotNone(fmt.compare(DENSE, SPREAD.replace('a;b', 'a; b')))

    def test_reordered_or_removed_code_is_rejected(self):
        self.assertIsNotNone(fmt.compare(DENSE, SPREAD.replace('x--;', '')))

    def test_deleted_comment_is_rejected(self):
        self.assertIsNotNone(fmt.compare(DENSE, SPREAD.replace('// trailing', '')))

    def test_import_removal_only_with_permission(self):
        trimmed = SPREAD.replace('import java.util.*;\n', '')
        self.assertIsNotNone(fmt.compare(DENSE, trimmed))
        self.assertIsNone(fmt.compare(DENSE, trimmed, allow_imports=True))
        self.assertIsNotNone(fmt.compare(DENSE, SPREAD.replace('import java.util.*;', 'import java.util.*;\nimport java.io.*;'), allow_imports=True))

    def test_every_real_source_file_tokenises(self):
        for path in (ROOT / 'src').rglob('*.java'):
            with self.subTest(path=path.name):
                fmt.tokens(path.read_text(errors='ignore'))


if __name__ == '__main__':
    unittest.main()
