#!/usr/bin/env python3
"""Offline contract, provenance, command and tampered-package tests (no NDK/CI)."""
import copy
import importlib.util
import io
import json
import tarfile
import tempfile
import unittest
from pathlib import Path
from unittest.mock import patch


def module(name):
    spec = importlib.util.spec_from_file_location(name, Path(__file__).with_name(name + ".py"))
    result = importlib.util.module_from_spec(spec)
    spec.loader.exec_module(result)
    return result


native = module("native-supernova156")
builder = module("build-supernova156-native-stack")
packager = module("package-supernova156-native")


class NativeStackTests(unittest.TestCase):
    def setUp(self):
        self.temporary = tempfile.TemporaryDirectory()
        self.addCleanup(self.temporary.cleanup)
        self.work = Path(self.temporary.name)
        self.lock = native.load_lock()

    def payload(self):
        payload = self.work / "payload"
        for module_name, name in (("MediaLib", "libavos.so"), ("MediaLib", "libavosjni.so"),
                                  ("MediaLib", "libnvpnativehelper.so"), ("FileCoreLibrary", "libfilecore.so")):
            path = payload / "libraries" / module_name / "libs" / native.ABI / name
            path.parent.mkdir(parents=True, exist_ok=True)
            path.write_bytes(b"synthetic ELF fixture, never executed: " + name.encode())
        receipt = {"abi": native.ABI, "source_commit": "a" * 40,
                   "native_lock_sha256": native.sha(native.BUILD / "native-supernova156.lock.json"),
                   "avos_commit": self.lock["sources"]["avos"]["commit"],
                   "avos_tree": self.lock["expected_patched_avos_tree"], "patches": self.lock["patches"],
                   "recipe_hashes": self.lock["recipe_hashes"], "libraries": native.inventory(payload / "libraries")}
        (payload / "native-stack.json").write_text(json.dumps(receipt))
        return payload, receipt

    def archive(self, payload):
        archive = self.work / "fixture.tar.gz"
        with tarfile.open(archive, "w:gz") as packed:
            for path in payload.rglob("*"):
                if path.is_file():
                    packed.add(path, arcname=str(path.relative_to(payload)))
        return archive

    def test_reviewed_lock_and_pending_outputs(self):
        self.assertEqual("pending-first-build", self.lock["artifact"]["archive_sha256"])
        self.assertEqual(5, len(self.lock["patches"]))
        self.assertEqual(["arm64-v8a"], self.lock["delivery_abis"])

    def test_correct_package_stages_both_modules_and_receipt(self):
        payload, receipt = self.payload()
        archive = self.archive(payload)
        destination = self.work / "app"
        evidence = self.work / "evidence/packaging.json"
        packager.install(archive, native.sha(archive), receipt["source_commit"], destination, evidence)
        self.assertEqual(receipt, json.loads(evidence.read_text())["engine"])
        self.assertEqual(native.inventory(payload / "libraries"), native.inventory(destination))

    def test_wrong_archive_sha_refuses_install(self):
        payload, receipt = self.payload()
        with self.assertRaisesRegex(ValueError, "reviewed SHA"):
            packager.install(self.archive(payload), "0" * 64, receipt["source_commit"],
                             self.work / "app", self.work / "receipt")
        self.assertFalse((self.work / "app").exists())

    def test_wrong_provenance_or_patch_order_refused(self):
        payload, receipt = self.payload()
        for field, value in (("source_commit", "b" * 40), ("avos_tree", "bad"), ("native_lock_sha256", "bad"),
                             ("patches", list(reversed(receipt["patches"]))), ("abi", "x86")):
            bad = copy.deepcopy(receipt)
            bad[field] = value
            with self.subTest(field=field), self.assertRaises(ValueError):
                native.verify_payload(payload, bad, self.lock, receipt["source_commit"])

    def test_tampered_or_extra_payload_refused(self):
        payload, receipt = self.payload()
        path = payload / "libraries/MediaLib/libs/arm64-v8a/libavos.so"
        path.write_bytes(b"tampered")
        with self.assertRaisesRegex(ValueError, "inventory"):
            native.verify_payload(payload, receipt, self.lock, receipt["source_commit"])

    def test_stale_destination_refused_before_any_copy(self):
        payload, receipt = self.payload()
        dest = self.work / "app/FileCoreLibrary/libs/arm64-v8a"
        dest.mkdir(parents=True)
        (dest / "obsolete.so").write_bytes(b"stale")
        with self.assertRaisesRegex(ValueError, "Empty destination"):
            packager.install(self.archive(payload), native.sha(self.work / "fixture.tar.gz"),
                             receipt["source_commit"], self.work / "app", self.work / "receipt")
        self.assertFalse((self.work / "app/MediaLib").exists())

    def test_escaping_path_link_special_and_duplicates_refused(self):
        for kind in ("escape", "link", "special", "duplicate"):
            archive = self.work / (kind + ".tar.gz")
            with tarfile.open(archive, "w:gz") as packed:
                info = tarfile.TarInfo("../escape" if kind == "escape" else "file")
                if kind == "link":
                    info.type, info.linkname = tarfile.SYMTYPE, "../../escape"
                elif kind == "special":
                    info.type = tarfile.FIFOTYPE
                packed.addfile(info, io.BytesIO())
                if kind == "duplicate":
                    packed.addfile(info, io.BytesIO())
            with self.subTest(kind=kind), self.assertRaises(ValueError):
                native.unpack(archive, self.work / "out")

    def test_ndk_commands_only_build_owned_wrappers_for_arm64(self):
        ndk = self.work / "ndk"
        clang = ndk / "toolchains/llvm/prebuilt/linux-x86_64/bin/clang"
        clang.parent.mkdir(parents=True)
        clang.symlink_to("clang-real")
        (ndk / "source.properties").write_text("Pkg.BaseRevision = 30.0.14904198")
        with patch.object(builder.native, "run") as run:
            builder.build(self.work / "root", ndk, 4)
        self.assertEqual(3, run.call_count)
        self.assertEqual(["libnativehelper", "FileCoreLibrary", "avos"],
                         [call.kwargs["cwd"].name for call in run.call_args_list])
        for call in run.call_args_list:
            self.assertIn("NDK_APP_ABI=arm64-v8a", call.args[0])
            self.assertIn("APP_PLATFORM=android-23", call.args[0])

    def test_wrong_ndk_refused_without_build(self):
        (self.work / "source.properties").write_text("Pkg.BaseRevision = wrong")
        with patch.object(builder.native, "run") as run, self.assertRaises(ValueError):
            builder.build(self.work / "root", self.work, 4)
        run.assert_not_called()

    def test_workflow_manual_only(self):
        workflow = (native.VIDEO / ".github/workflows/supernova-0156-native-stack.yml").read_text()
        trigger = workflow.split("on:\n", 1)[1].split("permissions:", 1)[0]
        self.assertIn("workflow_dispatch:", trigger)
        self.assertNotIn("push:", trigger)
        self.assertNotIn("pull_request:", trigger)
        self.assertIn("supernova-0156-native-arm64-v8a", workflow)


if __name__ == "__main__":
    unittest.main(verbosity=2)
