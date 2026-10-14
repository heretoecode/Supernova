package com.archos.mediacenter.video.utils.backup;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import com.archos.mediascraper.*;
import com.archos.mediascraper.xml.*;
import java.io.IOException;

/** Verified provider material for guarded owned replacement. Not a registered restore codec. */
public final class CanonicalMatchMaterial {
    public interface Resolver {
        ScrapeDetailResult resolve(Context context, PortableMediaMatches.Match requested, Uri receivingFile) throws IOException;
    }
    /** Uses the established scraper, including its artwork/cache preparation; does not save native tags. */
    public static final Resolver PROVIDER = (context, requested, file) -> {
        if (file == null || "putio".equalsIgnoreCase(file.getScheme())) throw new IOException("Receiving file is unavailable for canonical preparation");
        String[] identity=requested.identity.split(":");boolean movie=identity[1].equals("movie");
        SearchResult result=new SearchResult(movie?SearchResult.movie:SearchResult.tvshow,requested.identity,Integer.parseInt(identity[2]));
        result.setFile(file);result.setLanguage(Scraper.getLanguage(context));result.setScraper(movie?new MovieScraper3(context):new ShowScraper4(context));
        Bundle options=new Bundle();
        if(!movie){options.putBoolean(Scraper.ITEM_REQUEST_BASIC_VIDEO,true);options.putBoolean(Scraper.ITEM_REQUEST_REFRESH_SHOW_METADATA,true);options.putInt(Scraper.ITEM_REQUEST_SEASON,Integer.parseInt(identity[3]));options.putInt(Scraper.ITEM_REQUEST_EPISODE,Integer.parseInt(identity[4]));}
        try{return BaseScraper2.getDetails(result,options);}catch(RuntimeException failure){throw new IOException("Canonical provider preparation failed",failure);}
    };
    public static BaseTags verify(PortableMediaMatches.Match requested,ScrapeDetailResult result)throws IOException {
        if(requested==null||result==null||!result.isOkay()||result.tag==null)throw new IOException("Canonical metadata is unavailable");
        String[] identity=requested.identity.split(":");boolean movie=identity[1].equals("movie");BaseTags tags=result.tag;
        if(movie){if(!result.isMovie||!(tags instanceof MovieTags)||tags.getOnlineId()!=Long.parseLong(identity[2]))throw new IOException("Canonical movie identity does not match the request");}
        else {
            if(result.isMovie||!(tags instanceof EpisodeTags))throw new IOException("Canonical episode kind does not match the request");
            EpisodeTags episode=(EpisodeTags)tags;
            if(episode.getShowTags()==null||episode.getShowTags().getOnlineId()!=Long.parseLong(identity[2])||episode.getSeason()!=Integer.parseInt(identity[3])||episode.getEpisode()!=Integer.parseInt(identity[4])||episode.getOnlineId()<=0||requested.episodeProvider!=null&&episode.getOnlineId()!=requested.episodeProvider)throw new IOException("Canonical episode identity does not match the request");
        }
        if(tags.getTitle()==null||tags.getTitle().trim().isEmpty())throw new IOException("Canonical title is unavailable");
        return tags;
    }
    public static OwnedCanonicalBatch prepare(Context context,MediaMatchInventory.Owner owner,
            PortableMediaMatches.Match requested,Uri receivingFile,long receivingShowId,Long receivingPin,Resolver resolver)throws IOException {
        if(owner==null||owner.match==null||owner.localId<=0||resolver==null)throw new IOException("Canonical restoration requires an owned receiving identification");
        BaseTags tags=verify(requested,resolver.resolve(context,requested,receivingFile));
        try {
            if(tags instanceof MovieTags&&owner.type==BaseTags.MOVIE)return ((MovieTags)tags).prepareOwnedRestore(context,owner.videoId,owner.localId,Long.parseLong(owner.match.identity.split(":")[2]),receivingPin);
            if(tags instanceof EpisodeTags&&owner.type==BaseTags.TV_SHOW&&owner.match.episodeProvider!=null){long receivingShowProvider=Long.parseLong(owner.match.identity.split(":")[2]);return ((EpisodeTags)tags).prepareOwnedRestore(context,owner.videoId,owner.localId,owner.match.episodeProvider,receivingShowId,receivingShowProvider,receivingPin);}
        }catch(IllegalArgumentException incompatible){throw new IOException("Canonical material is incompatible with receiving ownership",incompatible);}
        throw new IOException("Canonical replacement requires further receiving dependency preparation");
    }
    private CanonicalMatchMaterial(){}
}
