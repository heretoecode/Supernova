#!/usr/bin/env bash
set -euo pipefail
apk=$1
phase=$2
package=org.courville.nova.markpreview
activity=com.archos.mediacenter.video.leanback.MainActivityLeanback
mkdir -p ../startup-diagnostics
adb install -r "$apk"
if [[ "$phase" == debug ]]; then
  adb shell run-as "$package" mkdir -p shared_prefs
  printf '%s\n' '<?xml version="1.0" encoding="utf-8"?><map><boolean name="try_new_ui" value="true"/><string name="uimode_leanback">tv</string><string name="uimode">tv</string></map>' | adb shell "run-as $package sh -c 'cat > shared_prefs/${package}_preferences.xml'"
fi
adb shell am force-stop "$package"
adb logcat -c
adb shell am start -W -n "$package/$activity"
sleep 15
adb logcat -d > "../startup-diagnostics/$phase-logcat.txt"
adb shell dumpsys activity activities > "../startup-diagnostics/$phase-activities.txt"
adb exec-out screencap -p > "../startup-diagnostics/$phase.png"
if grep -q 'FATAL EXCEPTION' "../startup-diagnostics/$phase-logcat.txt"; then
  cat "../startup-diagnostics/$phase-logcat.txt"
  exit 1
fi
adb shell pidof "$package" | grep -q '[0-9]'
grep -q "$activity" "../startup-diagnostics/$phase-activities.txt"
