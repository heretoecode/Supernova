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

package com.archos.mediacenter.video.leanback.search;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/** The exported search provider treats missing, empty or null search terms as "no results" instead of crashing. */
public class VideoSearchProviderInputTest {
    @Test
    public void missingOrNullTermsAreNotSearched() {
        assertFalse(VideoSearchProvider.hasSearchText(null));
        assertFalse(VideoSearchProvider.hasSearchText(new String[0]));
        assertFalse(VideoSearchProvider.hasSearchText(new String[] { null }));
        assertFalse(VideoSearchProvider.hasSearchText(new String[] { "" }));
    }

    @Test
    public void realTermsAreSearched() {
        assertTrue(VideoSearchProvider.hasSearchText(new String[] { "bass" }));
    }
}
