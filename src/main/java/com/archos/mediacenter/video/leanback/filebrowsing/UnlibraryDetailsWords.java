package com.archos.mediacenter.video.leanback.filebrowsing;

/** Plain words for the "not in your library" Details line. */
final class UnlibraryDetailsWords
{
    private UnlibraryDetailsWords()
    {
    }

    /** "2024", "Season 2 · Episode 5", "2019 · Season 1 · Episode 1", or "Unknown" when nothing is known. */
    static String detail(String year, int season, int episode)
    {
        StringBuilder out = new StringBuilder();
        if (year != null && !year.isEmpty())
            out.append(year);
        if (season >= 0) {
            if (out.length() > 0)
                out.append(" · ");
            out.append("Season ").append(season);
        }
        if (episode >= 0) {
            if (out.length() > 0)
                out.append(" · ");
            out.append("Episode ").append(episode);
        }
        return out.length() == 0 ? "Unknown" : out.toString();
    }
}
