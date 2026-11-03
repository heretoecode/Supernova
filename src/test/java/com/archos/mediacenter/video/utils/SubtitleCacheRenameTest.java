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

package com.archos.mediacenter.video.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.net.Uri;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.io.File;

/** Renaming a subtitle also renames its cached copy (the cache File is built from the folder, not a URI string). */
@RunWith(RobolectricTestRunner.class)
public class SubtitleCacheRenameTest {
    @Rule public TemporaryFolder temp = new TemporaryFolder();

    @Test
    public void cacheFileIsTheRealFileInsideTheCacheFolder() throws Exception {
        File subs = temp.newFolder("subs");
        File cached = new File(subs, "Film.srt");
        assertTrue(cached.createNewFile());
        File found = SubtitlesWizardCommon.cacheFile(subs, Uri.parse("smb://nas/Films/Film.srt"));
        assertEquals(cached, found);
        assertTrue(found.exists());
    }

    @Test
    public void renamedCacheFileKeepsTheCacheFolder() throws Exception {
        File subs = temp.newFolder("subs");
        File target = SubtitlesWizardCommon.cacheFile(subs, Uri.parse("file:///storage/x/Film.en.srt"));
        assertEquals(subs, target.getParentFile());
        assertEquals("Film.en.srt", target.getName());
    }
}
