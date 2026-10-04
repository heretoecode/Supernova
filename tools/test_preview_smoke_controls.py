"""Exercise the real embedded smoke driver's TV activation policy without adb."""
import ast
from pathlib import Path
import re
import types
import unittest
import xml.etree.ElementTree as ET


class SmokeActivationTest(unittest.TestCase):
    def run_target(self, focused, activate):
        script = (Path(__file__).resolve().parents[1] / '.github/build/check-preview-startup.sh').read_text()
        embedded = script.split("<<'PYMORE'\n", 1)[1].split('\nPYMORE', 1)[0]
        function = next(node for node in ast.parse(embedded).body if isinstance(node, ast.FunctionDef) and node.name == 'target')
        calls = []
        scope = {'re': re, 'time': types.SimpleNamespace(sleep=lambda _: None), 'adb': lambda *args: calls.append(args)}
        exec(compile(ast.Module(body=[function], type_ignores=[]), '<actual smoke target>', 'exec'), scope)
        root = ET.fromstring('<hierarchy><node text="OpenSubtitles" focused="' + str(focused).lower() + '" bounds="[10,20][110,60]"/></hierarchy>')
        scope['target'](root, 'OpenSubtitles', activate)
        return calls

    def test_already_focused_control_activates_once_without_touch(self):
        self.assertEqual([('shell', 'input', 'keyevent', '23')], self.run_target(True, True))

    def test_unfocused_tv_control_is_focused_then_activated(self):
        self.assertEqual([('shell', 'input', 'tap', '60', '40'), ('shell', 'input', 'keyevent', '23')], self.run_target(False, True))

    def test_focus_only_does_not_send_centre(self):
        self.assertEqual([('shell', 'input', 'tap', '60', '40')], self.run_target(False, False))


if __name__ == '__main__':
    unittest.main()
