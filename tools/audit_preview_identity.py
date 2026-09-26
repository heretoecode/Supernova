#!/usr/bin/env python3
"""Read-only merged-manifest audit; never changes an installed/app identity."""
import argparse
import json
from pathlib import Path
import xml.etree.ElementTree as ET

ANDROID = "{http://schemas.android.com/apk/res/android}"
EXPECTED_PACKAGE = "org.courville.nova.markpreview"
LEGACY_AUTHORITIES = {
    "com.archos.media.videocommunity", "com.archos.media.scrapercommunity",
    "browser.SearchProviderVideocommunity",
}


def inspect(path):
    root = ET.parse(path).getroot()
    app = root.find("application")
    if app is None:
        raise ValueError("Merged manifest has no application")
    authorities = sorted({authority for provider in app.findall("provider")
                          for authority in provider.get(ANDROID + "authorities", "").split(";")
                          if authority})
    shared_uid = root.get(ANDROID + "sharedUserId")
    failures = []
    if root.get("package") != EXPECTED_PACKAGE:
        failures.append("Unexpected application/package identity")
    if app.get(ANDROID + "label") != "Supernova":
        failures.append("Unexpected launcher application label")
    if not app.get(ANDROID + "banner"):
        failures.append("Missing Android TV launcher banner")
    if LEGACY_AUTHORITIES.intersection(authorities):
        failures.append("Inherited provider authority collision")
    # This identity is retained for upgrade compatibility, not silently removed.
    if shared_uid != "archos.openmediacenter":
        failures.append("Shared-user identity differs from preserved baseline")
    return {
        "manifest": str(path), "package": root.get("package"),
        "version_name": root.get(ANDROID + "versionName"),
        "version_code": root.get(ANDROID + "versionCode"),
        "label": app.get(ANDROID + "label"), "banner": app.get(ANDROID + "banner"),
        "shared_user_id": shared_uid,
        "shared_user_max_sdk": root.get(ANDROID + "sharedUserMaxSdkVersion"),
        "provider_authorities": authorities,
        "task_affinities": sorted({node.get(ANDROID + "taskAffinity")
                                   for node in root.iter() if node.get(ANDROID + "taskAffinity")}),
        "warnings": ["Inherited shared-user ID may prevent coexistence with a differently signed Nova installation. Physical installer evidence is required; identity is unchanged."] if shared_uid else [],
        "failures": failures,
    }


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("intermediates", type=Path)
    parser.add_argument("--output", required=True, type=Path)
    args = parser.parse_args()
    candidates = sorted(path for path in args.intermediates.rglob("AndroidManifest.xml")
                        if any(part in ("merged_manifest", "merged_manifests") for part in path.parts)
                        and "noamazondebug" in str(path).lower())
    if not candidates:
        raise SystemExit("No merged NoamazonDebug manifest found; cannot verify identity")
    reports = [inspect(path) for path in candidates]
    args.output.parent.mkdir(parents=True, exist_ok=True)
    args.output.write_text(json.dumps(reports, indent=2) + "\n")
    print(json.dumps(reports, indent=2))
    if any(report["failures"] for report in reports):
        raise SystemExit("Preview identity audit failed")


if __name__ == "__main__":
    main()
