"""The imports checker accepts only folding fully qualified names and dropping unused imports."""
import unittest
from check_imports_only import compare

BASE = '''package a.b;
import java.util.List;
import java.util.Map;
/** Uses {@link List}. */
class T {
    android.view.View v = new android.view.View(null);
    List<String> items;
    int size() { return android.text.TextUtils.isEmpty("") ? 0 : 1; }
}
'''


class CheckImportsOnlyTest(unittest.TestCase):
    def test_folding_and_unused_removal_pass(self):
        after = '''package a.b;
import android.text.TextUtils;
import android.view.View;
import java.util.List;
/** Uses {@link List}. */
class T {
    View v = new View(null);
    List<String> items;
    int size() { return TextUtils.isEmpty("") ? 0 : 1; }
}
'''
        self.assertIsNone(compare(BASE, after))

    def test_partial_folding_passes(self):
        after = BASE.replace('import java.util.List;', 'import android.view.View;\nimport java.util.List;')
        after = after.replace('android.view.View v = new android.view.View', 'View v = new android.view.View')
        self.assertIsNone(compare(BASE, after))

    def test_layout_changes_pass(self):
        self.assertIsNone(compare(BASE, BASE.replace('    List<String> items;', 'List<String>\n        items;')))

    def test_code_change_fails(self):
        self.assertIn('token', compare(BASE, BASE.replace('? 0 : 1', '? 1 : 0')))

    def test_comment_change_fails(self):
        self.assertIsNotNone(compare(BASE, BASE.replace('Uses {@link List}.', 'Uses a list.')))

    def test_removing_used_import_fails(self):
        self.assertIn('still used', compare(BASE, BASE.replace('import java.util.List;\n', '')))

    def test_removing_import_used_only_in_javadoc_fails(self):
        source = BASE.replace('    List<String> items;\n', '    java.util.List<String> items;\n')
        self.assertIn('still used', compare(source, source.replace('import java.util.List;\n', '')))

    def test_shadowing_an_existing_bare_name_fails(self):
        before = 'package a;\nclass T { String s; foo.String f; }\n'
        after = 'package a;\nimport foo.String;\nclass T { String s; String f; }\n'
        self.assertIn('already used', compare(before, after))

    def test_clash_with_existing_import_fails(self):
        before = 'package a;\nimport com.app.R;\nclass T { int x = R.id.a + android.R.attr.b; }\n'
        after = 'package a;\nimport android.R;\nimport com.app.R;\nclass T { int x = R.id.a + R.attr.b; }\n'
        self.assertIsNotNone(compare(before, after))

    def test_static_and_wildcard_additions_fail(self):
        before = 'package a;\nclass T { int x = java.lang.Math.max(1, 2); }\n'
        self.assertIsNotNone(compare(before, 'package a;\nimport static java.lang.Math.max;\nclass T { int x = max(1, 2); }\n'))
        self.assertIsNotNone(compare(before, 'package a;\nimport java.lang.*;\nclass T { int x = Math.max(1, 2); }\n'))

    def test_wildcard_removal_fails(self):
        before = 'package a;\nimport java.util.*;\nclass T { }\n'
        self.assertIn('wildcard', compare(before, 'package a;\nclass T { }\n'))

    def test_member_named_like_import_is_not_a_bare_use(self):
        before = 'package a;\nimport x.Thing;\nclass T { int n = other.Thing; }\n'
        self.assertIsNone(compare(before, 'package a;\nclass T { int n = other.Thing; }\n'))


if __name__ == '__main__':
    unittest.main()
