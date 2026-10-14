package com.archos.mediacenter.video.utils.backup;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.json.JSONException;

/** Device-only manual-choice revisions; never exported as backup settings or match data. */
public final class MediaMatchManualChanges {
    private static final String EPOCH="supernova_match_manual_epoch",PREFIX="supernova_match_manual:";
    public static long epoch(Map<String,?> preferences)throws JSONException{return number(preferences.get(EPOCH));}
    private static long number(Object value)throws JSONException {
        if(value==null)return 0;if(!(value instanceof Integer||value instanceof Long)||((Number)value).longValue()<0)throw new JSONException("Invalid receiving manual matching revision");return ((Number)value).longValue();
    }
    public static boolean after(Map<String,?> preferences,long id,long before)throws JSONException {return number(preferences.get(PREFIX+id))>before;}
    public static void mark(Context context,long id)throws IOException {mark(context,List.of(id));}
    /** Record explicit user intent before native persistence so observers cannot replay an older queued restore. */
    public static void mark(Context context,Collection<Long> ids)throws IOException {
        if(ids==null||ids.isEmpty())return;
        synchronized(PreferenceRestoreTransaction.LOCK){
            SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(context);
            try{long revision=epoch(preferences.getAll());if(revision==Long.MAX_VALUE)throw new IOException("Manual matching revision limit reached");SharedPreferences.Editor editor=preferences.edit().putLong(EPOCH,revision+1);
                for(Long id:ids){if(id==null||id<=0)throw new IOException("Invalid manual match owner");editor.putLong(PREFIX+id,revision+1);}if(!editor.commit())throw new IOException("Manual match protection could not be saved");
            }catch(JSONException invalid){throw new IOException("Manual match protection is unavailable",invalid);}
        }
    }
    private MediaMatchManualChanges(){}
}
