import tempfile
import unittest
from pathlib import Path
from audit_preview_identity import inspect


class IdentityAuditTest(unittest.TestCase):
    def audit(self, package="org.courville.nova.markpreview", authority="org.courville.nova.markpreview.media", shared="archos.openmediacenter"):
        with tempfile.TemporaryDirectory() as directory:
            manifest = Path(directory) / "AndroidManifest.xml"
            manifest.write_text(f'''<manifest xmlns:android="http://schemas.android.com/apk/res/android"
                package="{package}" android:sharedUserId="{shared}">
                <application android:label="Supernova" android:banner="@drawable/preview_custom_banner">
                <provider android:authorities="{authority}"/></application></manifest>''')
            before = manifest.read_bytes()
            result = inspect(manifest)
            self.assertEqual(before, manifest.read_bytes())
            return result

    def test_baseline_is_preserved_with_coexistence_warning(self):
        report = self.audit()
        self.assertEqual([], report["failures"])
        self.assertEqual(1, len(report["warnings"]))

    def test_package_change_is_rejected(self):
        self.assertIn("Unexpected application/package identity", self.audit(package="org.courville.nova")["failures"])

    def test_semicolon_provider_collision_is_rejected(self):
        self.assertIn("Inherited provider authority collision", self.audit(authority="safe;com.archos.media.videocommunity")["failures"])

    def test_shared_identity_change_is_rejected(self):
        self.assertIn("Shared-user identity differs from preserved baseline", self.audit(shared="different")["failures"])


if __name__ == "__main__":
    unittest.main()
