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
        return prepareInternal(context,owner,requested,receivingFile,receivingShowId,receivingPin,resolver,null);
    }
    private static OwnedCanonicalBatch prepareInternal(Context context,MediaMatchInventory.Owner owner,
            PortableMediaMatches.Match requested,Uri receivingFile,long receivingShowId,Long receivingPin,Resolver resolver,Preparation preparation)throws IOException {
        if(owner==null||resolver==null||requested==null||receivingFile==null||"putio".equalsIgnoreCase(receivingFile.getScheme())||!owner.guard.equals(MediaMatchInventory.guard(owner.videoId,receivingFile.toString())))throw new IOException("Canonical restoration requires an owned receiving identification");
        BaseTags tags=verify(requested,resolver.resolve(context,requested,receivingFile));
        try {
            if(tags instanceof MovieTags){
                if(owner.type==BaseTags.MOVIE&&owner.match!=null)return guard(((MovieTags)tags).prepareOwnedRestore(context,owner.videoId,owner.localId,Long.parseLong(owner.match.identity.split(":")[2]),receivingPin),owner);
                java.io.File store=com.archos.mediaprovider.video.VideoOpenHelper.getDatabaseFile(context);
                if(store.exists())try(android.database.sqlite.SQLiteDatabase database=android.database.sqlite.SQLiteDatabase.openDatabase(store.getPath(),null,android.database.sqlite.SQLiteDatabase.OPEN_READONLY);
                        android.database.Cursor retained=database.query("MOVIE",new String[]{"_id","m_online_id","Nova_pinned"},"video_id=?",new String[]{Long.toString(owner.videoId)},null,null,null)){
                    if(retained.moveToFirst()){long id=retained.getLong(0);Long provider=retained.isNull(1)?null:retained.getLong(1),pin=retained.isNull(2)?null:retained.getLong(2);if(retained.moveToNext())throw new IOException("Retained movie ownership is ambiguous");return guard(((MovieTags)tags).prepareOwnedMetadataRestore(context,owner.videoId,owner.type,owner.localId,id,provider,pin),owner);}
                    long reserved=preparation==null?reserve(database,"MOVIE"):preparation.reserveNext(database,"MOVIE");
                    return guard(((MovieTags)tags).prepareNewRestore(context,owner.videoId,owner.type,owner.localId,reserved),owner);
                }
            }
            if(tags instanceof EpisodeTags&&owner.type==BaseTags.TV_SHOW&&owner.match!=null&&owner.match.episodeProvider!=null){
                long receivingShowProvider=Long.parseLong(owner.match.identity.split(":")[2]);EpisodeTags episode=(EpisodeTags)tags;
                if(episode.getShowTags().getOnlineId()==receivingShowProvider)return guard(episode.prepareOwnedRestore(context,owner.videoId,owner.localId,owner.match.episodeProvider,receivingShowId,receivingShowProvider,receivingPin),owner);
                java.io.File store=com.archos.mediaprovider.video.VideoOpenHelper.getDatabaseFile(context);
                if(store.exists())try(android.database.sqlite.SQLiteDatabase database=android.database.sqlite.SQLiteDatabase.openDatabase(store.getPath(),null,android.database.sqlite.SQLiteDatabase.OPEN_READONLY);
                        android.database.Cursor target=database.query("SHOW",new String[]{"_id","Nova_pinned"},"s_online_id=? AND name_show IS NOT NULL AND trim(name_show)<>''",new String[]{Long.toString(episode.getShowTags().getOnlineId())},null,null,null)){
                    if(target.moveToFirst()){long id=target.getLong(0);Long pin=target.isNull(1)?null:target.getLong(1);if(target.moveToNext())throw new IOException("Canonical receiving parent is ambiguous");return guard(episode.prepareOwnedRestoreToExistingParent(context,owner.videoId,owner.localId,owner.match.episodeProvider,receivingShowId,receivingShowProvider,receivingPin,id,pin),owner);}
                }
            }
        }catch(IllegalArgumentException incompatible){throw new IOException("Canonical material is incompatible with receiving ownership",incompatible);}
        throw new IOException("Canonical replacement requires further receiving dependency preparation");
    }
    private static long reserve(android.database.sqlite.SQLiteDatabase database,String table)throws IOException {
        try(android.database.Cursor next=database.rawQuery("SELECT IFNULL(MAX(_id),0)+1 FROM "+table,null)){
            if(!next.moveToFirst()||next.getType(0)!=android.database.Cursor.FIELD_TYPE_INTEGER||next.getLong(0)<=0)throw new IOException("Cannot reserve canonical metadata owner");return next.getLong(0);
        }
    }
    /** One preparation scope reserves distinct native identities across all selected physical targets. */
    public static final class Preparation {
        private final Context context;private final java.util.Map<String,Long> next=new java.util.HashMap<>();
        public Preparation(Context context){this.context=context;}
        private long reserveNext(android.database.sqlite.SQLiteDatabase database,String table)throws IOException {
            long value=next.containsKey(table)?next.get(table):reserve(database,table);if(value<=0||value==Long.MAX_VALUE)throw new IOException("Canonical metadata identity limit reached");next.put(table,value+1);return value;
        }
        public java.util.List<OwnedCanonicalBatch> prepare(MediaMatchInventory.Owner owner,PortableMediaMatches.Match requested,Uri file,Resolver resolver)throws IOException {
            if(owner==null||requested==null||file==null||resolver==null||"putio".equalsIgnoreCase(file.getScheme())||!owner.guard.equals(MediaMatchInventory.guard(owner.videoId,file.toString())))throw new IOException("Receiving canonical target is unavailable");
            ScrapeDetailResult resolved=resolver.resolve(context,requested,file);BaseTags material=verify(requested,resolved);
            java.io.File store=com.archos.mediaprovider.video.VideoOpenHelper.getDatabaseFile(context);if(!store.exists())throw new IOException("Receiving media store is unavailable");
            try(android.database.sqlite.SQLiteDatabase database=android.database.sqlite.SQLiteDatabase.openDatabase(store.getPath(),null,android.database.sqlite.SQLiteDatabase.OPEN_READONLY)){
                if(material instanceof MovieTags){Long pin=null;if(owner.type==BaseTags.MOVIE&&owner.match!=null)try(android.database.Cursor row=database.query("MOVIE",new String[]{"Nova_pinned"},"_id=? AND video_id=?",new String[]{Long.toString(owner.localId),Long.toString(owner.videoId)},null,null,null)){if(!row.moveToFirst())throw new IOException("Receiving movie owner disappeared");pin=row.isNull(0)?null:row.getLong(0);}
                    return java.util.List.of(prepareInternal(context,owner,requested,file,0,pin,(ctx,match,uri)->resolved,this));
                }
                EpisodeTags episode=(EpisodeTags)material;long target;Long targetPin;
                try(android.database.Cursor row=database.query("SHOW",new String[]{"_id","Nova_pinned","name_show"},"s_online_id=?",new String[]{Long.toString(episode.getShowTags().getOnlineId())},null,null,null)){
                    if(!row.moveToFirst()||row.isNull(2)||row.getString(2).trim().isEmpty())throw new IOException("Receiving canonical parent requires preparation");target=row.getLong(0);targetPin=row.isNull(1)?null:row.getLong(1);if(row.moveToNext())throw new IOException("Receiving parent is ambiguous");
                }
                long metadata;Long oldProvider=null,oldShow=null,oldShowProvider=null,oldPin=null;boolean create;
                try(android.database.Cursor row=database.query("EPISODE",new String[]{"_id","e_online_id","show_episode"},"video_id=?",new String[]{Long.toString(owner.videoId)},null,null,null)){
                    create=!row.moveToFirst();if(create)metadata=reserveNext(database,"EPISODE");else{metadata=row.getLong(0);oldProvider=row.isNull(1)?null:row.getLong(1);oldShow=row.getLong(2);if(row.moveToNext())throw new IOException("Receiving episode owner is ambiguous");}
                }
                if(!create)try(android.database.Cursor row=database.query("SHOW",new String[]{"s_online_id","Nova_pinned"},"_id=?",new String[]{oldShow.toString()},null,null,null)){if(!row.moveToFirst())throw new IOException("Receiving episode parent disappeared");oldShowProvider=row.getLong(0);oldPin=row.isNull(1)?null:row.getLong(1);}
                return java.util.List.of(guard(episode.prepareCanonicalRestore(context,owner.videoId,owner.type,owner.localId,metadata,oldProvider,oldShow,oldShowProvider,oldPin,target,targetPin,create),owner));
            }catch(IllegalArgumentException incompatible){throw new IOException("Canonical episode preparation is incompatible",incompatible);}
        }
    }
    public static OwnedCanonicalBatch guard(OwnedCanonicalBatch batch,MediaMatchInventory.Owner owner) {
        android.content.ContentValues state=new android.content.ContentValues();state.put("ArchosMediaScraper_type",owner.type);state.put("ArchosMediaScraper_id",owner.localId);
        if(owner.match!=null){String[] identity=owner.match.identity.split(":");
            if(identity[1].equals("movie"))state.put("m_online_id",Long.parseLong(identity[2]));
            else {state.put("s_online_id",Long.parseLong(identity[2]));state.put("e_season",Integer.parseInt(identity[3]));state.put("e_episode",Integer.parseInt(identity[4]));if(owner.episodeProviderRaw==null)state.putNull("e_online_id");else state.put("e_online_id",owner.episodeProviderRaw);}
        }
        return batch.withReceivingState(state);
    }
    private CanonicalMatchMaterial(){}
}
