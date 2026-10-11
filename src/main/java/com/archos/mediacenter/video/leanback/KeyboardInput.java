package com.archos.mediacenter.video.leanback;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;
import java.util.ArrayList;
/** Editable/accessibility field without a redundant remote focus stop. */
public final class KeyboardInput extends androidx.appcompat.widget.AppCompatEditText {
 private final Paint caret=new Paint();
 public KeyboardInput(Context c){super(c);setShowSoftInputOnFocus(false);caret.setColor(0xffb9dcf4);}
 @Override public void addFocusables(ArrayList<View> views,int direction,int mode){/* Keyboard keys own D-pad focus; accessibility still exposes an editable field. */}
 @Override protected void onDraw(Canvas canvas){super.onDraw(canvas);if(hasFocus()||getLayout()==null||getSelectionStart()<0)return;int offset=Math.min(length(),getSelectionStart()),line=getLayout().getLineForOffset(offset);float x=getCompoundPaddingLeft()+getLayout().getPrimaryHorizontal(offset)-getScrollX();float top=getExtendedPaddingTop()+getLayout().getLineTop(line)-getScrollY();canvas.drawRect(x,top,x+Math.max(1,getResources().getDisplayMetrics().density),top+getLayout().getLineBottom(line)-getLayout().getLineTop(line),caret);}
}
