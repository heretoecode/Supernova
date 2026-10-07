"""Bounded emulator check of the real native player/HUD/technical-information route.

Synthetic local clip only. This is not physical Shield or long-duration playback QA.
"""
from pathlib import Path
import re
import os
import tempfile
import zipfile
import subprocess
import time
import xml.etree.ElementTree as ET

OUT = Path('../startup-diagnostics')
PACKAGE = 'org.courville.nova.markpreview'


def adb(*args):
    return subprocess.run(['adb', *args], check=True, stdout=subprocess.PIPE, timeout=45).stdout


def capture(name):
    for attempt in range(3):
        adb('shell', 'rm', '-f', '/sdcard/nova-playback.xml')
        try:
            adb('shell', 'uiautomator', 'dump', '/sdcard/nova-playback.xml')
            data = adb('shell', 'cat', '/sdcard/nova-playback.xml')
            root = ET.fromstring(data)
        except (subprocess.CalledProcessError, ET.ParseError):
            time.sleep(1)
            continue
        (OUT / (name + '.xml')).write_bytes(data)
        (OUT / (name + '.png')).write_bytes(adb('exec-out', 'screencap', '-p'))
        return root
    # API 28 phone AVD can have a focused dialog while active-window lookup is null.
    # Retrieve that same focused app window, retaining all UI assertions below.
    adb('shell', 'CLASSPATH=/data/local/tmp/preview-window-dump.jar', 'app_process',
        '/system/bin', 'supernova.validation.PreviewWindowDump', PACKAGE, '/sdcard/nova-playback.xml')
    data = adb('shell', 'cat', '/sdcard/nova-playback.xml')
    root = ET.fromstring(data)
    (OUT / (name + '.xml')).write_bytes(data)
    (OUT / (name + '.png')).write_bytes(adb('exec-out', 'screencap', '-p'))
    return root


def activate(node, expected):
    x1, y1, x2, y2 = map(int, re.findall(r'\d+', node.get('bounds')))
    adb('shell', 'input', 'tap', str((x1 + x2) // 2), str((y1 + y2) // 2))
    time.sleep(.3)
    root = capture('playback-activation')
    if not any(n.get('text', '').lower() == expected.lower() for n in root.iter('node')):
        adb('shell', 'input', 'keyevent', '23')
    time.sleep(.5)


# Compile a shell-only capture helper from the SDK already used by this workflow.
# This is test infrastructure, never part of the application APK.
sdk = Path(os.environ['ANDROID_HOME'])
android_jar = sorted((sdk / 'platforms').glob('*/android.jar'))[-1]
d8 = sorted((sdk / 'build-tools').glob('*/d8'))[-1]
with tempfile.TemporaryDirectory() as directory:
    compiled = Path(directory)
    subprocess.run(['javac', '-source', '8', '-target', '8', '-cp', str(android_jar),
        '-d', directory, '.github/build/PreviewWindowDump.java'], check=True)
    subprocess.run([str(d8), '--lib', str(android_jar), '--output', directory,
        str(compiled / 'supernova/validation/PreviewWindowDump.class')], check=True)
    jar = compiled / 'preview-window-dump.jar'
    with zipfile.ZipFile(jar, 'w') as archive:
        archive.write(compiled / 'classes.dex', 'classes.dex')
    adb('push', str(jar), '/data/local/tmp/preview-window-dump.jar')

subprocess.run(['ffmpeg', '-hide_banner', '-loglevel', 'error', '-y', '-f', 'lavfi',
                '-i', 'testsrc2=s=640x360:r=24', '-f', 'lavfi', '-i',
                'sine=frequency=440:sample_rate=48000', '-t', '120', '-c:v',
                'libx264', '-preset', 'ultrafast', '-pix_fmt', 'yuv420p', '-c:a',
                'aac', '-b:a', '64k', '/tmp/supernova-preview-smoke.mp4'], check=True)
adb('push', '/tmp/supernova-preview-smoke.mp4', '/sdcard/Download/supernova-preview-smoke.mp4')
# Startup captures/taps leave this phone AVD in touch mode. Android consumes
# its first directional Down to establish focus before Activity dispatch. Switch
# to remote navigation on the preceding screen, before launching the player.
adb('shell', 'input', 'keyevent', '61')  # Tab changes focus, never toggles a switch.
adb('logcat', '-c')
preferences = adb('shell', 'run-as', PACKAGE, 'cat', 'shared_prefs/' + PACKAGE + '_preferences.xml')
(OUT / 'playback-preferences.xml').write_bytes(preferences)
assert b'name="try_new_ui" value="true"' in preferences, 'Preview must be enabled before playback'
assert b'name="uimode">2<' in preferences, 'Native player must use TV mode in this AVD'
adb('shell', 'am', 'start', '-W', '-n', PACKAGE + '/com.archos.mediacenter.video.player.PlayerActivity',
    '-a', 'android.intent.action.VIEW', '-d', 'file:///sdcard/Download/supernova-preview-smoke.mp4', '-t', 'video/mp4')
try:
    time.sleep(8)
    before = capture('playback-before-reveal')
    if any('Movie Ends' in n.get('text', '') for n in before.iter('node')):
        adb('shell', 'input', 'keyevent', '4')  # Hide an initially visible HUD only.
        before = capture('playback-hidden-precondition')
    assert not any('Movie Ends' in n.get('text', '') for n in before.iter('node')), 'HUD must be hidden before reveal test'
    assert PACKAGE in adb('shell', 'dumpsys', 'activity', 'activities').decode(), 'Player disappeared before reveal'
    # Hidden-HUD Up reveals without opening More or pausing/seeking. The native
    # player owns this state; a phone AVD has no TV-only MediaSession. uiautomator's
    # idle wait exceeds the unchanged HUD timeout, so pause deliberately for capture.
    adb('shell', 'input', 'keyevent', '19')
    time.sleep(.2)
    reveal = adb('logcat', '-d').decode(errors='replace')
    (OUT / 'playback-reveal-native-state.txt').write_text(reveal)
    state = re.search(r'Preview transport event=reveal_complete key=19 playing=true position=(\d+)', reveal)
    assert state, 'HUD reveal gesture did not complete with native playback still running'
    start = re.search(r'Preview transport event=reveal_begin key=19 playing=true position=(\d+)', reveal)
    assert start, 'Reveal did not begin with native playback running'
    assert 0 <= int(state.group(1)) - int(start.group(1)) < 2000, 'Hidden-HUD Up unexpectedly sought'
    (OUT / 'playback-hidden-up-reveal.png').write_bytes(adb('exec-out', 'screencap', '-p'))
    adb('shell', 'input', 'keyevent', '23')
    time.sleep(.2)
    toggled = adb('logcat', '-d').decode(errors='replace')
    (OUT / 'playback-explicit-pause-native-state.txt').write_text(toggled)
    assert re.search(r'Preview transport event=toggle key=-1 playing=false position=', toggled), 'Explicit focused Play/Pause did not pause native playback'
    root = capture('playback-hud-runtime')
    assert any('Movie Ends' in n.get('text', '') for n in root.iter('node')), 'Preview movie end-clock wording missing'
    # Remote focus, not a touch click: touching a non-touch-focusable ImageButton
    # clears keyboard focus and cannot establish the opener this check verifies.
    assert any(n.get('resource-id', '').endswith('/pause') and n.get('focused') == 'true' for n in root.iter('node')), 'HUD did not enter on Play/Pause'
    # Real native frame feedback, not just a moving seek cursor. The testsrc2 clip
    # changes over time; paused screenshots must differ after the selected seek.
    def frame_sample(path):
        return subprocess.run(['ffmpeg','-hide_banner','-loglevel','error','-i',str(path),
            '-vf','crop=iw/3:ih/3:iw/3:ih/4,scale=64:36','-frames:v','1',
            '-f','rawvideo','-pix_fmt','rgb24','-'],check=True,stdout=subprocess.PIPE).stdout
    paused_frame = frame_sample(OUT / 'playback-hud-runtime.png')
    adb('shell','input','keyevent','19')
    adb('shell','input','keyevent','22')
    time.sleep(1)
    preview = capture('playback-seek-frame-preview')
    assert any(n.get('resource-id','').endswith('/seek_progress') and n.get('focused')=='true' for n in preview.iter('node')), 'Seek focus lost'
    assert any(n.get('resource-id','').endswith('/preview_scrub_time') for n in preview.iter('node')), 'Seek timestamp bubble missing'
    preview_frame=frame_sample(OUT / 'playback-seek-frame-preview.png')
    assert sum(abs(a-b) for a,b in zip(paused_frame,preview_frame))/len(paused_frame)>3, 'Native paused picture did not follow seek target'
    seeking=adb('logcat','-d').decode(errors='replace')
    target=re.findall(r'Preview transport event=scrub_preview .*target=(\d+)',seeking)
    completed=re.findall(r'Preview transport event=seek_complete .*position=(\d+)',seeking)
    assert target and completed and abs(int(target[-1])-int(completed[-1]))<2500, 'Native frame seek did not reach target'
    adb('shell','input','keyevent','23')
    time.sleep(1)
    assert 'event=scrub_commit' in adb('logcat','-d').decode(errors='replace'), 'Confirmation did not commit seek'
    adb('shell','input','keyevent','21')
    time.sleep(1)
    adb('shell','input','keyevent','4')
    time.sleep(1)
    cancelled=adb('logcat','-d').decode(errors='replace')
    assert 'event=scrub_cancel' in cancelled, 'Back did not cancel seek deterministically'
    root=capture('playback-seek-cancel-return')
    assert any(n.get('resource-id','').endswith('/pause') and n.get('focused')=='true' for n in root.iter('node')), 'Cancel did not return to Play/Pause'
    adb('shell', 'input', 'keyevent', '22')
    root = capture('playback-more-focused')
    assert any(n.get('resource-id', '').endswith('/preview_more') and n.get('focused') == 'true' for n in root.iter('node')), 'Remote RIGHT did not focus More'
    assert not any(n.get('resource-id', '').endswith('/preview_info') for n in root.iter('node')), 'Info remained in primary HUD'
    adb('shell', 'input', 'keyevent', '37')  # Existing hardware I technical-information shortcut
    root = capture('playback-technical-runtime')
    labels = {n.get('text') for n in root.iter('node')}
    assert {'Video', 'Audio', 'File', 'Source'} <= labels, 'Technical-only information panels missing'
    assert not {'Resume', 'Play from Beginning', 'File and technical details', 'File & Technical Details'} & labels, 'Legacy information actions leaked into technical overlay'
    activities = adb('shell', 'dumpsys', 'activity', 'activities')
    (OUT / 'playback-technical-activities.txt').write_bytes(activities)
    assert b'mResumedActivity' in activities and b'PlayerActivity' in activities
    adb('shell', 'input', 'keyevent', '4')
    root = capture('playback-info-return')
    assert any(n.get('resource-id', '').endswith('/preview_more') and n.get('focused') == 'true' for n in root.iter('node')), 'Back did not restore exact HUD More opener'
    adb('shell', 'pidof', PACKAGE)
finally:
    (OUT / 'playback-final-input.txt').write_bytes(adb('shell', 'dumpsys', 'input'))
    (OUT / 'playback-final-activities.txt').write_bytes(adb('shell', 'dumpsys', 'activity', 'activities'))
    (OUT / 'playback-final-screen.png').write_bytes(adb('exec-out', 'screencap', '-p'))
    logs = adb('logcat', '-d')
    (OUT / 'playback-runtime-logcat.txt').write_bytes(logs)
    assert b'FATAL EXCEPTION' not in logs, 'Playback/Information runtime crash'
    adb('shell', 'am', 'force-stop', PACKAGE)
