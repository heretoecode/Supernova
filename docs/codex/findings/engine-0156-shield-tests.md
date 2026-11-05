# Engine release: Shield checks (0156 working label)

For the owner, after Claude builds the candidate with the new native engine and
matching Java drafts. Keep the previous APK and a settings backup available.
Use the same Shield, TV, HDMI input and sound setup for the old/candidate
comparison. Write down the file, step and result; an unavailable file or test
server means **not tested**. Record whether the TV supports the HDR format.
The ASS overflow check needs Claude's matching subtitle-canvas work; native
patches alone cannot establish that result. No device checks have been run here.

1. On the current build, record its version and your Dolby Vision, sound and
   subtitle settings. Play your usual SDR and HDR samples and note how they look
   and where subtitles sit. Install the candidate, keeping those saved settings.
2. Play a normal SDR H.264 file. Pause, resume, seek forward/back and reopen it.
   Brightness, colours, sound sync and the remembered position should match the
   old build.
3. Play an HDR10 HEVC sample. Check the TV's HDR indicator and familiar bright
   and dark scenes. Look for washed-out colours, clipped highlights or a dark
   picture compared with the old build.
4. Play an HDR10+ sample. On a supported TV check the reported format; otherwise
   check its ordinary HDR10 fallback against the baseline. Record which path
   your Shield/TV actually supports.
5. Play an HLG sample. Check the TV's reported format where available, natural
   colours, highlight detail and sound sync.
6. With Dolby Vision on Auto, play a profile 5 sample. On a compatible setup
   expect Dolby Vision and natural colours. Green/purple video or an endless
   black screen is a failure.
7. Play a Dolby Vision profile 7 sample. Record whether this Shield uses Dolby
   Vision or the HDR10 base picture. Check correct colours and a prompt start;
   record the actual result rather than assuming full profile 7 support.
8. Play a Dolby Vision profile 8 sample with a compatible HDR10 base picture.
   Check Auto, then Off. Auto should use the supported path; Off should show the
   correctly coloured HDR10 base picture. Restore Auto afterward.
9. Play a Dolby Vision profile 9 AVC sample. Try Auto and Off. If the display
   cannot use this Dolby Vision profile, its SDR base picture should still have
   natural colours, steady motion and correct sound sync.
10. On a known working Dolby Vision file, try the existing Auto, Off and Force
    choices, reopening each time. Record the selected TV format and any error.
    An unsupported Force case must not cause repeated retries or freeze the
    player. Restore the original choice.
11. Play an ordinary AV1 sample and seek in it. Some Shields lack AV1 hardware;
    compare with what the old build could play. A previously working sample
    must still work; an unsupported sample must leave the player responsive.
12. If available, test an AV1 Dolby Vision profile 10 sample too. Record hardware
    limitations and the actual fallback/error. Afterward play the known working
    SDR file to check the failed attempt did not leave playback broken.
13. Use an MPEG-TS recording at least ten minutes long. Check its duration, seek
    to about four minutes, stop and reopen with resume. It should resume within
    a few seconds of that point, with sound in sync and a sensible remaining time.
14. Use a file with more than one video stream, such as a recording containing
    a secondary video or cover stream with different timing. Seek repeatedly and
    reopen. The main picture, duration and resume point must remain sensible;
    watch for sudden jumps, wrong speed or drifting sound.
15. Try a sample that previously started with sound but no picture, or failed
    with its first hardware decoder. Check for a prompt working picture or one
    clear failure, with responsive controls and no endless restart cycle. Record
    the file so Claude can confirm which decoder recovery path it exercised.
16. Use a network file and a test server that can fail one playback request
    while remaining reachable. Seek to a new point, fail that request, then let
    requests succeed. Check that the player reopens once at the latest selected
    point, rather than an older saved bookmark. If you cannot arrange the
    controlled failure, mark this step not tested.
17. Play an SRT-subtitled sample with your existing boxed background settings.
    Compare text size, outline, shadow, opacity and box spacing with step 1.
    Pause and change the existing style controls; the result should update and
    remain readable without moving the video.
18. Use an ASS/SSA sample containing both ordinary dialogue and positioned sign
    text attached to something in the picture. Try Fit width and zoom until the
    picture extends beyond the screen. Dialogue should stay fully on screen;
    positioned signs should stay attached to the picture. Return to the normal
    fit and check that neither size nor position remains wrong.
19. Play a PGS-subtitled sample with the same boxed-background choice saved.
    The bitmap subtitles should retain their original colours, shape and
    video-relative position, including during fit/zoom changes. Watch for
    stretched bitmaps, missing subtitles or an unexpected large rectangle.
20. If you use SBS or top/bottom 3D, pause a subtitled sample and adjust the
    existing subtitle style. Check that both eyes update, spacing matches the
    previous build, and switching back to 2D leaves no stale subtitle frame.
21. Stop playback, turn the TV off/on or reconnect HDMI, then reopen an HDR
    sample followed by SDR. Check the current TV format, natural colours and
    sound; the player should not keep capabilities from an unavailable display.
22. Close and reopen the app. Check that your saved subtitle and Dolby Vision
    settings remain, especially any existing zero-width outline or box style.
    Repeat one boxed SRT and one boxed ASS sample to compare their spacing.
23. Open, stop and replace videos several times, including one broken file and
    one network file. Finish with the working SDR sample. Playback should still
    start normally, release the old picture and respond to Back/Stop. Send Claude
    the candidate version, failed step, file type and observed result.
