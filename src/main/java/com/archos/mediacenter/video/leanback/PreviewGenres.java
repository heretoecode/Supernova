package com.archos.mediacenter.video.leanback;

import android.app.Dialog;
import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import java.util.*;
import java.util.function.Consumer;

/** Genre rules and their semantic icons are shared by filters and dynamic Home rows. */
public final class PreviewGenres {
    public static final String[] NAMES={"Action","Adventure","Animation","Comedy","Crime","Documentary","Drama","Family","Fantasy","History","Horror","Kids","Music","Mystery","News","Reality","Romance","Science Fiction","Soap","Talk","Thriller","TV Movie","War","Western","Action & Adventure","Sci-Fi & Fantasy","War & Politics"};
    public static Set<String> parse(String value){Set<String> values=new LinkedHashSet<>();if(value!=null)for(String s:value.split("[|,;/]"))if(!s.trim().isEmpty())values.add(s.trim());return values;}
    public static boolean matches(String genres,Set<String> selected){if(selected.isEmpty())return true;Set<String> values=parse(genres);for(String genre:selected)for(String value:values)if(value.equalsIgnoreCase(genre))return true;return false;}
    public static boolean known(String label){return Arrays.asList(NAMES).contains(label);}
    public static void choose(Context c,Collection<String> options,Set<String> selected,Consumer<Set<String>> accept){
        List<String> names=new ArrayList<>(new TreeSet<>(options));Set<String> result=new LinkedHashSet<>(selected);List<String> labels=new ArrayList<>(names);labels.add("All genres");labels.add("Done");Set<Integer> checks=new HashSet<>();for(int i=0;i<names.size();i++)if(result.contains(names.get(i)))checks.add(i);
        Dialog[] menu={null};menu[0]=PreviewDialog.choose(c,"Genres · match any selected",labels.toArray(new String[0]),-1,checks,false,n->{if(n==names.size()+1){accept.accept(result);menu[0].dismiss();return;}if(n==names.size())result.clear();else if(!result.add(names.get(n)))result.remove(names.get(n));checks.clear();for(int i=0;i<names.size();i++)if(result.contains(names.get(i)))checks.add(i);PreviewDialog.updateChecks(menu[0],checks);});
    }
    public static final class Icon extends Drawable {
        private final String kind;private final Paint paint=new Paint(3);
        public Icon(String genre){kind=genre.toLowerCase(Locale.ROOT);paint.setStyle(Paint.Style.STROKE);paint.setStrokeWidth(1.5f);paint.setStrokeCap(Paint.Cap.ROUND);paint.setStrokeJoin(Paint.Join.ROUND);paint.setColor(0xffe1e9ef);}
        private void line(Canvas c,float... xy){Path p=new Path();p.moveTo(xy[0],xy[1]);for(int i=2;i<xy.length;i+=2)p.lineTo(xy[i],xy[i+1]);c.drawPath(p,paint);}
        @Override public void draw(Canvas c){c.save();c.translate(getBounds().left,getBounds().top);c.scale(getBounds().width()/24f,getBounds().height()/24f);
            if(kind.equals("action")){line(c,4,21,19,3,21,3,21,6,6,21);line(c,4,13,11,20);}
            else if(kind.equals("action & adventure")){line(c,2,22,10,6,17,22,2,22);line(c,14,16,22,3,22,8,17,18);line(c,13,13,20,18);}
            else if(kind.equals("adventure")){line(c,2,21,10,5,16,21,2,21);line(c,13,15,18,7,23,21,16,21);line(c,10,5,10,1,16,3,10,5);}
            else if(kind.equals("animation")){line(c,5,17,17,3,21,7,9,21,5,21,5,17);line(c,14,6,18,10);}
            else if(kind.equals("comedy")||kind.equals("drama")){line(c,3,4,21,4,20,15,12,22,4,15,3,4);c.drawCircle(8,10,1,paint);c.drawCircle(16,10,1,paint);c.drawArc(7,kind.equals("comedy")?10:15,17,kind.equals("comedy")?18:21,kind.equals("comedy")?0:180,180,false,paint);}
            else if(kind.equals("crime")){line(c,12,2,21,6,19,16,12,22,5,16,3,6,12,2);c.drawCircle(12,11,3,paint);}
            else if(kind.equals("documentary")){c.drawRect(3,8,16,20,paint);line(c,16,11,22,8,22,20,16,17);c.drawCircle(7,5,3,paint);c.drawCircle(14,5,3,paint);}
            else if(kind.equals("kids")){line(c,12,2,20,8,12,17,4,8,12,2,12,17,16,20,12,23);line(c,4,8,20,8);}
            else if(kind.equals("family")){c.drawCircle(7,6,3,paint);c.drawCircle(17,6,3,paint);c.drawCircle(12,14,2,paint);line(c,2,21,3,12,7,10,9,12);line(c,22,21,21,12,17,10,15,12);line(c,8,22,9,18,15,18,16,22);}
            else if(kind.equals("fantasy")){line(c,3,22,16,9);line(c,17,2,18,6,22,7,18,8,17,12,16,8,12,7,16,6,17,2);c.drawPoint(6,4,paint);}
            else if(kind.equals("history")){line(c,5,2,19,2,19,6,8,18,8,22);line(c,5,22,19,22,19,18,8,6,8,2);}
            else if(kind.equals("horror")){c.drawArc(4,2,20,18,180,180,false,paint);line(c,4,10,4,22,8,19,12,22,16,19,20,22,20,10);c.drawCircle(9,10,1,paint);c.drawCircle(15,10,1,paint);}
            else if(kind.equals("music")){line(c,9,18,9,4,20,2,20,16);c.drawOval(3,16,9,21,paint);c.drawOval(14,14,20,19,paint);}
            else if(kind.equals("mystery")){c.drawCircle(10,10,7,paint);line(c,15,15,22,22);line(c,9,7,12,7,12,10,10,12);c.drawPoint(10,14,paint);}
            else if(kind.equals("romance")){Path p=new Path();p.moveTo(12,21);p.cubicTo(-8,8,6,-2,12,7);p.cubicTo(18,-2,32,8,12,21);c.drawPath(p,paint);}
            else if(kind.equals("sci-fi & fantasy")){c.drawCircle(9,15,6,paint);line(c,2,20,16,9);line(c,18,1,19,5,23,6,19,7,18,11,17,7,13,6,17,5,18,1);}
            else if(kind.equals("science fiction")){c.drawCircle(12,12,7,paint);c.save();c.rotate(-30,12,12);c.drawOval(1,9,23,15,paint);c.restore();c.drawPoint(3,3,paint);}
            else if(kind.equals("thriller")){line(c,14,2,5,13,12,13,10,22,20,10,13,10,14,2);}
            else if(kind.equals("war & politics")){line(c,2,8,12,2,22,8,2,8);line(c,3,22,21,22);for(int x=5;x<=19;x+=7)line(c,x,10,x,19);}
            else if(kind.equals("war")){line(c,3,2,21,22);line(c,21,2,3,22);line(c,1,16,8,22);line(c,16,22,23,16);}
            else if(kind.equals("western")){line(c,2,17,6,16,8,5,12,8,16,5,18,16,22,17);c.drawArc(2,12,22,22,0,180,false,paint);}
            else if(kind.equals("talk")){c.drawRoundRect(8,2,16,15,4,4,paint);c.drawArc(5,7,19,19,0,180,false,paint);line(c,12,19,12,22,7,22,17,22);}
            else if(kind.equals("reality")){line(c,2,12,7,6,17,6,22,12,17,18,7,18,2,12);c.drawCircle(12,12,3,paint);}
            else if(kind.equals("soap")){c.drawCircle(8,15,6,paint);c.drawCircle(17,7,4,paint);c.drawCircle(18,19,2,paint);}
            else if(kind.equals("news")){c.drawRect(2,3,22,21,paint);c.drawRect(5,7,11,13,paint);line(c,14,7,19,7);line(c,14,12,19,12);line(c,5,17,19,17);}
            else{c.drawRoundRect(2,5,22,19,2,2,paint);line(c,9,8,16,12,9,16,9,8);}
            c.restore();
        }
        @Override public void setAlpha(int a){paint.setAlpha(a);}@Override public void setColorFilter(ColorFilter f){paint.setColorFilter(f);}@Override public int getOpacity(){return PixelFormat.TRANSLUCENT;}
    }
}
