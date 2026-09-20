package com.archos.mediacenter.video.leanback.filebrowsing;

import android.view.*;
import android.widget.*;
import androidx.leanback.widget.Presenter;
import com.archos.filecorelibrary.MetaFile2;
import com.archos.mediacenter.video.browser.adapters.object.Video;
import com.archos.mediacenter.video.leanback.*;

/** Compact filename-first rows; no poster-sized legacy list cells. */
final class PreviewFilePresenter extends Presenter {
    @Override public ViewHolder onCreateViewHolder(ViewGroup parent){android.content.Context c=parent.getContext();TextView row=new TextView(c);row.setTextSize(14);row.setTextColor(0xffd8e5ef);row.setSingleLine(true);row.setEllipsize(android.text.TextUtils.TruncateAt.MIDDLE);row.setGravity(Gravity.CENTER_VERTICAL);row.setPadding(PreviewDialog.dp(c,8),0,PreviewDialog.dp(c,8),0);row.setFocusable(true);row.setFocusableInTouchMode(true);row.setForeground(PreviewDialog.focus(c));android.graphics.drawable.LayerDrawable separator=new android.graphics.drawable.LayerDrawable(new android.graphics.drawable.Drawable[]{new android.graphics.drawable.ColorDrawable(0x080c1722),new android.graphics.drawable.ColorDrawable(0x30738696)});separator.setLayerHeight(1,PreviewDialog.dp(c,1));separator.setLayerGravity(1,Gravity.BOTTOM);row.setBackground(separator);row.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,PreviewDialog.dp(c,42)));return new ViewHolder(row);}
    @Override public void onBindViewHolder(ViewHolder holder,Object item){TextView row=(TextView)holder.view;boolean folder=item instanceof MetaFile2&&((MetaFile2)item).isDirectory();row.setText(item instanceof MetaFile2?((MetaFile2)item).getName():((Video)item).getFilenameNonCryptic());PreviewIcon.apply(row,folder?"folder":"video-file",20);row.setContentDescription((folder?"Folder, ":"File, ")+row.getText());}
    @Override public void onUnbindViewHolder(ViewHolder holder){}
}
