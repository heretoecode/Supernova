package com.archos.mediacenter.video.leanback.details;

import androidx.leanback.widget.Action;
import com.archos.mediacenter.video.leanback.tvshow.TvshowActionAdapter;
import com.archos.mediacenter.video.streaming.StreamingActionPresenter;

/** Native action IDs, rather than translated labels, define the approved More groups. */
public final class PreviewMoreActions {
    public static boolean watched(Action action,boolean television){
        if(action==null)return false;long id=action.getId();
        return television?id==TvshowActionAdapter.ACTION_MARK_SHOW_AS_WATCHED||id==TvshowActionAdapter.ACTION_MARK_SHOW_AS_NOT_WATCHED
                :id==VideoActionAdapter.ACTION_MARK_AS_WATCHED||id==VideoActionAdapter.ACTION_MARK_AS_NOT_WATCHED;
    }
    public static Action current(androidx.leanback.widget.ObjectAdapter adapter,Action previous,boolean television){
        if(adapter==null||previous==null)return null;
        for(int i=0;i<adapter.size();i++){Object item=adapter.get(i);if(item instanceof Action){Action candidate=(Action)item;
            if(group(candidate,television)>=0&&(watched(previous,television)?watched(candidate,television):candidate.getId()==previous.getId()))return candidate;
        }}return null;
    }
    public static int group(Action action,boolean television) {
        if(action==null||action instanceof StreamingActionPresenter.LogoAction)return -1;
        long id=action.getId();
        if(television) {
            if(id==TvshowActionAdapter.ACTION_DELETE)return 3;
            if(id==TvshowActionAdapter.ACTION_MARK_SHOW_AS_WATCHED||id==TvshowActionAdapter.ACTION_MARK_SHOW_AS_NOT_WATCHED
                    ||id==TvshowActionAdapter.ACTION_UNINDEX||id==TvshowActionAdapter.ACTION_CHANGE_INFO)return 1;
            return -1;
        }
        if(id==VideoActionAdapter.ACTION_NEXT_EPISODE)return 0;
        if(id==VideoActionAdapter.ACTION_DELETE||id==VideoActionAdapter.ACTION_CONFIRM_DELETE)return 3;
        if(id==VideoActionAdapter.ACTION_MARK_AS_WATCHED||id==VideoActionAdapter.ACTION_MARK_AS_NOT_WATCHED
                ||id==VideoActionAdapter.ACTION_INDEX||id==VideoActionAdapter.ACTION_UNINDEX
                ||id==VideoActionAdapter.ACTION_SCRAP||id==VideoActionAdapter.ACTION_HIDE||id==VideoActionAdapter.ACTION_UNHIDE)return 1;
        return -1;
    }
    private PreviewMoreActions(){}
}
