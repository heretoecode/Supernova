package com.archos.mediacenter.video.leanback.search;
import java.text.Normalizer;
import java.util.Locale;
/** Unicode comparison over indexed local titles, not an online search or a second index. */
public final class PreviewSearchText {
 public static String fold(String value){return value==null?"":Normalizer.normalize(value,Normalizer.Form.NFKD).replaceAll("\\p{M}+","").toLowerCase(Locale.ROOT).replace('ł','l').replace('ø','o').replace("ß","ss");}
 public static String titleKey(String value){return fold(value).replaceAll("[’'`]", "").replaceAll("[^\\p{L}\\p{N}]+"," ").trim().replaceFirst("^(the|a|an)\\s+", "").replaceAll("\\s+", " ");}
 public static int rank(String query,String value){String needle=titleKey(query),title=titleKey(value);if(fold(query).trim().equals(fold(value).trim()))return 0;if(title.equals(needle)||title.replace(" ","").equals(needle.replace(" ","")))return 1;return title.startsWith(needle)?2:3;}
 public static boolean matches(String query,String... values){String needle=titleKey(query);for(String value:values){String hay=titleKey(value);if(hay.contains(needle)||hay.replace(" ","").contains(needle.replace(" ","")))return true;}return false;}
}
