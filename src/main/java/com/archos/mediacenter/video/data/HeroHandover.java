package com.archos.mediacenter.video.data;

/** Carry this value with the Details navigation model; full-details enrichment never changes its chosen image. */
public record HeroHandover(String kind, long tmdbId, String artworkIdentity)
{
    public HeroHandover
    {
        if (!("movie".equals(kind) || "tv".equals(kind)) || tmdbId <= 0 || artworkIdentity == null ||
            artworkIdentity.trim().isEmpty())
            throw new IllegalArgumentException("Exact title and displayed artwork required");
    }

    /** Main-thread safe. A different title must not inherit the previous hero's art. */
    public String resolve(String detailsKind, long detailsId, String defaultArtwork)
    {
        return kind.equals(detailsKind) && tmdbId == detailsId ? artworkIdentity : defaultArtwork;
    }
}
