// Copyright 2026 Courville Software
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.archos.mediacenter.video.player;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.os.SystemClock;

import com.archos.mediacenter.utils.introdb.IntroSegments;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

/** A pending "Skip intro" prompt stops working as soon as skipping is switched off, not five seconds later. */
@RunWith(RobolectricTestRunner.class)
public class SkipPromptMasterSwitchTest {
    @Test
    public void promptIsDeadOnceSkippingIsSwitchedOff() {
        PlayerService.SegmentPrompt prompt = new PlayerService.SegmentPrompt(IntroSegments.Type.INTRO, 90000L);
        long now = SystemClock.elapsedRealtime();
        assertTrue(PlayerService.promptLive(prompt, true, now));
        assertFalse(PlayerService.promptLive(prompt, false, now));
    }

    @Test
    public void expiredOrMissingPromptsAreDead() {
        PlayerService.SegmentPrompt prompt = new PlayerService.SegmentPrompt(IntroSegments.Type.INTRO, 90000L);
        assertFalse(PlayerService.promptLive(prompt, true, prompt.expires));
        assertFalse(PlayerService.promptLive(null, true, 0));
    }
}
