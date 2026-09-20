"""Bounded emulator check of the real native player/HUD/technical-information route.

Synthetic local clip only. This is not physical Shield or long-duration playback QA.
"""
from pathlib import Path
import re
import subprocess
import time
import xml.etree.ElementTree as ET

OUT = Path('../startup-diagnostics')
PACKAGE = 'org.courville.nova.markpreview'


def adb(*args):
    return subprocess.run(['adb', *args], check=True, stdout=subprocess.PIPE).stdout


def capture(name):
    for attempt in range(3):
        adb('shell', 'uiautomator', 'dump', '/sdcard/nova-playback.xml')
        data = adb('shell', 'cat', '/sdcard/nova-playback.xml')
        try:
            root = ET.fromstring(data)
        except ET.ParseError:
            time.sleep(1)
            continue
        (OUT / (name + '.xml')).write_bytes(data)
        (OUT / (name + '.png')).write_bytes(adb('exec-out', 'screencap', '-p'))
        return root
    raise AssertionError('Playback accessibility tree unavailable')


def activate(node, expected):
    x1, y1, x2, y2 = map(int, re.findall(r'\d+', node.get('bounds')))
    adb('shell', 'input', 'tap', str((x1 + x2) // 2), str((y1 + y2) // 2))
    time.sleep(.3)
    root = capture('playback-activation')
    if not any(n.get('text', '').lower() == expected.lower() for n in root.iter('node')):
        adb('shell', 'input', 'keyevent', '23')
    time.sleep(.5)


subprocess.run(['ffmpeg', '-hide_banner', '-loglevel', 'error', '-y', '-f', 'lavfi',
                '-i', 'color=c=0x223d55:s=640x360:r=24', '-f', 'lavfi', '-i',
                'sine=frequency=440:sample_rate=48000', '-t', '120', '-c:v',
                'libx264', '-preset', 'ultrafast', '-pix_fmt', 'yuv420p', '-c:a',
                'aac', '-b:a', '64k', '/tmp/supernova-preview-smoke.mp4'], check=True)
adb('push', '/tmp/supernova-preview-smoke.mp4', '/sdcard/Download/supernova-preview-smoke.mp4')
adb('logcat', '-c')
adb('shell', 'am', 'start', '-W', '-n', PACKAGE + '/com.archos.mediacenter.video.player.PlayerActivity',
    '-a', 'android.intent.action.VIEW', '-d', 'file:///sdcard/Download/supernova-preview-smoke.mp4', '-t', 'video/mp4')
try:
    time.sleep(8)
    adb('shell', 'input', 'keyevent', '23')
    root = capture('playback-hud-runtime')
    assert any('Movie Ends' in n.get('text', '') for n in root.iter('node')), 'Preview movie end-clock wording missing'
    info = next(n for n in root.iter('node') if n.get('resource-id', '').endswith('/preview_info'))
    activate(info, 'File and technical details')
    root = capture('playback-information-runtime')
    technical = next(n for n in root.iter('node') if n.get('text', '').lower() == 'file and technical details')
    activate(technical, 'File & Technical Details')
    root = capture('playback-technical-runtime')
    assert any(n.get('text') == 'File & Technical Details' for n in root.iter('node'))
    activities = adb('shell', 'dumpsys', 'activity', 'activities')
    (OUT / 'playback-technical-activities.txt').write_bytes(activities)
    assert b'mResumedActivity' in activities and b'PlayerActivity' in activities
    adb('shell', 'input', 'keyevent', '4')
    adb('shell', 'pidof', PACKAGE)
finally:
    logs = adb('logcat', '-d')
    (OUT / 'playback-runtime-logcat.txt').write_bytes(logs)
    assert b'FATAL EXCEPTION' not in logs, 'Playback/Information runtime crash'
    adb('shell', 'am', 'force-stop', PACKAGE)
