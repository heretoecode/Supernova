package com.archos.mediacenter.video.leanback;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.app.Application;
import android.text.Spanned;
import android.text.style.UnderlineSpan;
import com.archos.mediacenter.video.leanback.search.PreviewSearchTestAccess;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

/** Checks the approved Search wording: details lines, bold typed letters and recent searches. */
@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 28)
public class PreviewSearchLayoutTest
{
    @Test
    public void detailsLineShowsOnlyWhatIsKnown()
    {
        assertEquals("Film · 155 min · 4K HDR · Science Fiction, Adventure",
                     PreviewSearchTestAccess.details("Film", 155, "4K HDR", "Science Fiction, Adventure"));
        assertEquals("Film", PreviewSearchTestAccess.details("Film", 0, "Resolution unavailable", ""));
    }

    @Test
    public void typedLettersAreUnderlinedAtTheStartOfWords()
    {
        Spanned title = (Spanned)PreviewSearchTestAccess.highlighted("Dune: Part Two", "dune");
        UnderlineSpan[] lines = title.getSpans(0, title.length(), UnderlineSpan.class);
        assertEquals(1, lines.length);
        assertEquals(0, title.getSpanStart(lines[0]));
        assertEquals(4, title.getSpanEnd(lines[0]));
        Spanned none = (Spanned)PreviewSearchTestAccess.highlighted("Sandune", "dune");
        assertEquals(0, none.getSpans(0, none.length(), UnderlineSpan.class).length);
    }

    @Test
    public void recentSearchesKeepTheNewestEightWithoutRepeats()
    {
        Application app = RuntimeEnvironment.getApplication();
        for (int i = 0; i < 10; i++)
            PreviewSearchTestAccess.remember(app, "query " + i);
        PreviewSearchTestAccess.remember(app, "QUERY 9");
        List<String> recent = PreviewSearchTestAccess.recent(app);
        assertEquals(8, recent.size());
        assertEquals("QUERY 9", recent.get(0));
        assertTrue(!recent.contains("query 0"));
    }
}
