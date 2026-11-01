package com.archos.mediacenter.video.player;

import android.app.Activity;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Checkable;
import androidx.preference.PreferenceManager;
import com.archos.mediacenter.video.R;
import com.archos.mediacenter.video.player.tvmenu.*;
import java.util.*;

/** Presentation adapter: all track/provider operations remain existing native menu actions. */
final class SubtitlePlaybackBackend implements PlaybackSubtitlePanel.Backend {
    private final Activity owner;private final TVCardView card;private int lastTrack=1;
    SubtitlePlaybackBackend(Activity owner,TVCardView card){this.owner=owner;this.card=card;}
    private SubtitleManager manager(){return owner instanceof PlayerActivity?((PlayerActivity)owner).subtitlePresentationManager():null;}
    private List<TVMenuItem> items(){List<TVMenuItem> out=new ArrayList<>();TVMenu menu=card==null?null:card.previewMenu();if(menu!=null)for(int i=0;i<menu.getChildCount();i++)if(menu.getChildAt(i) instanceof TVMenuItem)out.add((TVMenuItem)menu.getChildAt(i));return out;}
    private List<TVMenuItem> trackItems(){List<TVMenuItem> out=new ArrayList<>();for(TVMenuItem item:items())if(item.findViewById(R.id.info_text) instanceof Checkable)out.add(item);return out;}
    public List<PlaybackSubtitlePanel.Track> tracks(){List<PlaybackSubtitlePanel.Track> out=new ArrayList<>();List<TVMenuItem> items=trackItems();for(int i=0;i<items.size();i++){TVMenuItem item=items.get(i);final int index=i;Object language=item.getTag(R.id.preview_track_language);out.add(new PlaybackSubtitlePanel.Track(androidx.core.text.HtmlCompat.fromHtml(item.getText(),0).toString(),language instanceof String?(String)language:null,item.isChecked(),item.isEnabled(),()->{if(index>0)lastTrack=index;item.previewClick();}));}return out;}
    public int get(String key){SubtitleManager m=manager();if(key.equals("enabled")){List<TVMenuItem> items=trackItems();for(int i=1;i<items.size();i++)if(items.get(i).isChecked())return 1;return 0;}if(m==null)return 0;
        switch(key){case "size":return m.getSize();case "colour":return m.getColor();case "position":return m.getVerticalPosition();case "outline":return m.getOutlineState()?1:0;case "background":return m.getBackgroundState()?1:0;case "opacity":return m.getBackgroundOpacity();case "delay":return ((PlayerActivity)owner).subtitlePresentationDelay();default:throw new IllegalArgumentException("Unknown subtitle control");}}
    public boolean available(){return manager()!=null;}
    public void set(String key,int value){if(key.equals("enabled")){List<TVMenuItem> items=trackItems();if(items.isEmpty())return;if(value==0){for(int i=1;i<items.size();i++)if(items.get(i).isChecked())lastTrack=i;items.get(0).previewClick();}else if(items.size()>1)items.get(Math.max(1,Math.min(lastTrack,items.size()-1))).previewClick();return;}SubtitleManager m=manager();if(m==null)return;
        SharedPreferences.Editor prefs=PreferenceManager.getDefaultSharedPreferences(owner).edit();
        switch(key){case "size":m.setSize(value);prefs.putInt(PlayerActivity.KEY_SUBTITLE_SIZE,m.getSize()).putInt(SubtitleManager.KEY_FONT_SIZE_PT,m.getFontSizePt());break;case "colour":m.setColor(value);prefs.putInt(PlayerActivity.KEY_SUBTITLE_COLOR,value);break;case "position":m.setVerticalPosition(value);prefs.putInt(PlayerActivity.KEY_SUBTITLE_VPOS,value);break;case "outline":m.setOutlineState(value!=0);prefs.putBoolean(PlayerActivity.KEY_SUBTITLE_OUTLINE,m.getOutlineState()).putFloat(SubtitleManager.KEY_OUTLINE_WIDTH,m.getOutlineWidth());break;case "background":m.setBackgroundState(value!=0);prefs.putBoolean(PlayerActivity.KEY_SUBTITLE_BACKGROUND,m.getBackgroundState()).putInt(SubtitleManager.KEY_BG_MODE,m.getBgMode());break;case "opacity":m.setBackgroundOpacity(value);prefs.putInt(PlayerActivity.KEY_SUBTITLE_BG_OPACITY,value);break;case "delay":((PlayerActivity)owner).setSubtitleDelay(value-get("delay"));return;default:throw new IllegalArgumentException("Unknown subtitle control");}prefs.apply();}
    public void download(){for(TVMenuItem item:items())if(owner.getString(R.string.get_subtitles_online).equals(item.getText())){item.previewClick();return;}}
    public Runnable chooseFile(){for(TVMenuItem item:items())if(owner.getString(R.string.get_subtitles_on_drive).equals(item.getText()))return item::previewClick;return null;}
}
