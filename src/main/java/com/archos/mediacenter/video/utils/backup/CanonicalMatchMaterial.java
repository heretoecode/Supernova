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
        if(owner==null||resolver==null||requested==null||receivingFile==null||"putio".equalsIgnoreCase(receivingFile.getScheme())||!owner.guard.equals(MediaMatchInventory.guard(owner.videoId,receivingFile.toString())))throw new IOException("Canonical restoration requires an owned receiving identification");
        BaseTags tags=verify(requested,resolver.resolve(context,requested,receivingFile));
        try {
            if(tags instanceof MovieTags){
                if(owner.type==BaseTags.MOVIE&&owner.match!=null)return ((MovieTags)tags).prepareOwnedRestore(context,owner.videoId,owner.localId,Long.parseLong(owner.match.identity.split(":")[2]),receivingPin);
                java.io.File store=com.archos.mediaprovider.video.VideoOpenHelper.getDatabaseFile(context);
                if(store.exists())try(android.database.sqlite.SQLiteDatabase database=android.database.sqlite.SQLiteDatabase.openDatabase(store.getPath(),null,android.database.sqlite.SQLiteDatabase.OPEN_READONLY);
                        android.database.Cursor retained=database.query("MOVIE",new String[]{"_id","m_online_id","Nova_pinned"},"video_id=?",new String[]{Long.toString(owner.videoId)},null,null,null)){
                    if(retained.moveToFirst()){long id=retained.getLong(0);Long provider=retained.isNull(1)?null:retained.getLong(1),pin=retained.isNull(2)?null:retained.getLong(2);if(retained.moveToNext())throw new IOException("Retained movie ownership is ambiguous");return ((MovieTags)tags).prepareOwnedMetadataRestore(context,owner.videoId,owner.type,owner.localId,id,provider,pin);}
                }
            }
            if(tags instanceof EpisodeTags&&owner.type==BaseTags.TV_SHOW&&owner.match!=null&&owner.match.episodeProvider!=null){
                long receivingShowProvider=Long.parseLong(owner.match.identity.split(":")[2]);EpisodeTags episode=(EpisodeTags)tags;
                if(episode.getShowTags().getOnlineId()==receivingShowProvider)return episode.prepareOwnedRestore(context,owner.videoId,owner.localId,owner.match.episodeProvider,receivingShowId,receivingShowProvider,receivingPin);
                java.io.File store=com.archos.mediaprovider.video.VideoOpenHelper.getDatabaseFile(context);
                if(store.exists())try(android.database.sqlite.SQLiteDatabase database=android.database.sqlite.SQLiteDatabase.openDatabase(store.getPath(),null,android.database.sqlite.SQLiteDatabase.OPEN_READONLY);
                        android.database.Cursor target=database.query("SHOW",new String[]{"_id","Nova_pinned"},"s_online_id=? AND name_show IS NOT NULL AND trim(name_show)<>''",new String[]{Long.toString(episode.getShowTags().getOnlineId())},null,null,null)){
                    if(target.moveToFirst()){long id=target.getLong(0);Long pin=target.isNull(1)?null:target.getLong(1);if(target.moveToNext())throw new IOException("Canonical receiving parent is ambiguous");return episode.prepareOwnedRestoreToExistingParent(context,owner.videoId,owner.localId,owner.match.episodeProvider,receivingShowId,receivingShowProvider,receivingPin,id,pin);}
                }
            }
        }catch(IllegalArgumentException incompatible){throw new IOException("Canonical material is incompatible with receiving ownership",incompatible);}
        throw new IOException("Canonical replacement requires further receiving dependency preparation");
    }
    private CanonicalMatchMaterial(){}
}
