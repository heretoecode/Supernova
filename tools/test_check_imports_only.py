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
        self.assertIsNotNone(compare(BASE, BASE.replace('import java.util.List;\n', '')))

    def test_removing_import_used_only_in_javadoc_fails(self):
        source = BASE.replace('    List<String> items;\n', '    java.util.List<String> items;\n')
        self.assertIn('comment', compare(source, source.replace('import java.util.List;\n', '')))

    def test_shadowing_an_existing_bare_name_fails(self):
        before = 'package a;\nclass T { String s; foo.String f; }\n'
        after = 'package a;\nimport foo.String;\nclass T { String s; String f; }\n'
        self.assertIsNotNone(compare(before, after))

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


    def test_folding_through_an_existing_import_passes(self):
        before = 'package a;\nimport java.util.List;\nclass T { List x; java.util.List y; }\n'
        self.assertIsNone(compare(before, before.replace('java.util.List y', 'List y')))

    def test_folding_a_same_package_class_passes(self):
        before = 'package com.app.ui;\nclass T { com.app.ui.Style s = com.app.ui.Style.make(); }\n'
        after = 'package com.app.ui;\nclass T { Style s = Style.make(); }\n'
        self.assertIsNone(compare(before, after, 'src/T.java', {'Style', 'T'}))
        self.assertIsNotNone(compare(before, after, 'src/T.java', {'T'}), 'unknown package class must not fold')

    def test_folding_past_a_nested_type_of_the_same_name_fails(self):
        before = 'package com.app;\nclass T { com.app.Box b; static class Box { } }\n'
        after = 'package com.app;\nclass T { Box b; static class Box { } }\n'
        self.assertIsNotNone(compare(before, after, 'src/T.java', {'Box', 'T'}))

    def test_new_comments_only_when_allowed(self):
        before = 'package a;\nclass T { int x; }\n'
        after = 'package a;\n/** Holds x. */\nclass T { int x; }\n'
        self.assertIsNotNone(compare(before, after))
        self.assertIsNone(compare(before, after, allow_new_comments=True))
        changed = 'package a;\n/** Holds x. */\nclass T { int y; }\n'
        self.assertIsNotNone(compare(before, changed, allow_new_comments=True))

    def test_existing_comment_edit_fails_even_when_new_comments_allowed(self):
        before = 'package a;\n/** Old. */\nclass T { }\n'
        self.assertIsNotNone(compare(before, before.replace('Old.', 'New.'), allow_new_comments=True))


    def test_added_override_only_when_allowed(self):
        before = 'package a;\nclass T implements Runnable { public void run() { } }\n'
        after = 'package a;\nclass T implements Runnable { @Override public void run() { } }\n'
        self.assertIsNotNone(compare(before, after))
        self.assertIsNone(compare(before, after, allow_added_override=True))

    def test_other_annotations_still_fail_with_override_allowed(self):
        before = 'package a;\nclass T { void f() { } }\n'
        after = 'package a;\nclass T { @Deprecated void f() { } }\n'
        self.assertIsNotNone(compare(before, after, allow_added_override=True))

    def test_removing_override_fails(self):
        before = 'package a;\nclass T implements Runnable { @Override public void run() { } }\n'
        self.assertIsNotNone(compare(before, before.replace('@Override ', ''), allow_added_override=True))


    def test_override_added_above_an_existing_annotation_passes(self):
        before = 'package a;\nclass T implements Runnable { @SuppressWarnings("x") public void run() { } }\n'
        after = 'package a;\nclass T implements Runnable { @Override @SuppressWarnings("x") public void run() { } }\n'
        self.assertIsNone(compare(before, after, allow_added_override=True))
        self.assertIsNotNone(compare(before, after.replace('"x"', '"y"'), allow_added_override=True))


if __name__ == '__main__':
    unittest.main()
