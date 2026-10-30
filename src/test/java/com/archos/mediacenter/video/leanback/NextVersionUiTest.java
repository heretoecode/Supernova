package com.archos.mediacenter.video.leanback;

import android.app.Application;
import android.graphics.*;
import android.view.View;
import android.widget.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.*;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(application=Application.class,sdk=28,qualifiers="w960dp-h540dp-land-mdpi")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
public class NextVersionUiTest {
 @Test public void remoteCaretSharesTheCentredTextLine(){
  var c=RuntimeEnvironment.getApplication();KeyboardInput input=new KeyboardInput(c);input.setTextSize(16);input.setTextColor(Color.WHITE);input.setBackground(null);input.setPadding(8,4,8,4);input.setText("Name");input.setSelection(4);
  input.measure(View.MeasureSpec.makeMeasureSpec(400,View.MeasureSpec.EXACTLY),View.MeasureSpec.makeMeasureSpec(80,View.MeasureSpec.EXACTLY));input.layout(0,0,400,80);
  Bitmap image=Bitmap.createBitmap(400,80,Bitmap.Config.ARGB_8888);input.draw(new Canvas(image));int first=80,last=-1;
  for(int y=0;y<80;y++)for(int x=0;x<400;x++)if(image.getPixel(x,y)==0xffb9dcf4){first=Math.min(first,y);last=Math.max(last,y);}
  assertTrue("Visible remote caret",last>=first);assertEquals("Caret centred with line rather than at top",40,(first+last)/2,3);image.recycle();
 }
 @Test public void detailsScrimLeavesArtworkOutsideTextAreaUntinted(){
  CinematicCard card=new CinematicCard(RuntimeEnvironment.getApplication());card.addTextScrim();View shade=card.findViewWithTag("details.text.scrim");Bitmap picture=Bitmap.createBitmap(960,540,Bitmap.Config.ARGB_8888);picture.eraseColor(0xffff8000);shade.getBackground().setBounds(0,0,960,540);shade.getBackground().draw(new Canvas(picture));
  assertEquals(0xffff8000,picture.getPixel(800,270));assertTrue(Color.red(picture.getPixel(20,270))<255);assertEquals(0,Color.blue(picture.getPixel(20,270)));picture.recycle();
 }
}
