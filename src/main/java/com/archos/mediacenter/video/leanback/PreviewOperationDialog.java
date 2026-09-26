package com.archos.mediacenter.video.leanback;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.*;
import android.widget.*;

/** Shared indeterminate operation surface. Dismiss is not cancellation; Back and Cancel are. */
public final class PreviewOperationDialog {
    public static Dialog show(Context context, String title, String message, Runnable cancelled) {
        return build(context,title,message,true,cancelled);
    }
    public static Dialog notice(Context context, String title, String message, Runnable closed) {
        return build(context,title,message,false,closed);
    }
    private static Dialog build(Context context,String title,String message,boolean running,Runnable action) {
        Dialog dialog=PreviewDialog.create(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LinearLayout panel=new LinearLayout(context);panel.setOrientation(LinearLayout.VERTICAL);
        int pad=PreviewDialog.dp(context,20);panel.setPadding(pad,pad,pad,pad);
        panel.setBackground(PreviewDialog.menuSurface(context));
        TextView heading=text(context,title,18);panel.addView(heading);
        TextView detail=text(context,message,15);detail.setPadding(0,pad,0,pad);panel.addView(detail);
        if(running){ProgressBar progress=new ProgressBar(context);progress.setIndeterminate(true);progress.setFocusable(false);
        LinearLayout.LayoutParams spinner=new LinearLayout.LayoutParams(PreviewDialog.dp(context,32),PreviewDialog.dp(context,32));
        spinner.gravity=Gravity.CENTER_HORIZONTAL;spinner.bottomMargin=pad;panel.addView(progress,spinner);}
        TextView cancel=text(context,running?"Cancel":"Close",15);cancel.setGravity(Gravity.CENTER);cancel.setFocusable(true);cancel.setFocusableInTouchMode(true);
        cancel.setBackground(PreviewDialog.focus(context));cancel.setTag(running?"semantic:operation:cancel":"semantic:operation:close");
        cancel.setOnClickListener(view->{if(running)dialog.cancel();else dialog.dismiss();});
        panel.addView(cancel,new LinearLayout.LayoutParams(-1,PreviewDialog.dp(context,40)));
        dialog.setContentView(panel);dialog.setCanceledOnTouchOutside(false);
        if(running)dialog.setOnCancelListener(ignored->action.run());else dialog.setOnDismissListener(ignored->action.run());dialog.show();
        Window window=dialog.getWindow();window.setBackgroundDrawableResource(android.R.color.transparent);window.setDimAmount(.35f);
        window.setLayout(Math.min(PreviewDialog.dp(context,420),context.getResources().getDisplayMetrics().widthPixels-PreviewDialog.dp(context,48)),WindowManager.LayoutParams.WRAP_CONTENT);
        cancel.requestFocus();return dialog;
    }
    private static TextView text(Context context,String value,int size){TextView view=new TextView(context);view.setText(value);view.setTextColor(Color.WHITE);view.setTextSize(size);return view;}
    private PreviewOperationDialog(){}
}
