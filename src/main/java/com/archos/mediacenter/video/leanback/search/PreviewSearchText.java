package com.archos.mediacenter.video.leanback.search;
import java.text.Normalizer;
import java.util.Locale;
/** Bounded Unicode matching and ranking over actual indexed library fields. */
public final class PreviewSearchText {
 public static String fold(String value){return value==null?"":Normalizer.normalize(value,Normalizer.Form.NFKD).replaceAll("\\p{M}+","").toLowerCase(Locale.ROOT).replace('ł','l').replace('ø','o').replace("ß","ss").replaceAll("[^\\p{L}\\p{N}]+"," ").trim().replaceAll(" +"," ");}
 private static String articles(String text){return text.replaceFirst("^(the|an|a) +","");}
 public static int rank(String query,String... values){String needle=articles(fold(query));if(needle.isEmpty())return 0;int best=0;
  for(String value:values){String candidate=articles(fold(value));
   if(candidate.equals(needle))best=Math.max(best,100);
   else if(candidate.startsWith(needle))best=Math.max(best,90);
   else if(candidate.contains(needle))best=Math.max(best,80);
   else {boolean match=true;for(String token:needle.split(" ")){boolean present=false;for(String word:candidate.split(" "))if(word.startsWith(token)||token.length()>=4&&oneEdit(token,word)){present=true;break;}if(!present){match=false;break;}}if(match)best=Math.max(best,50);}
  }return best;
 }
 public static boolean matches(String query,String... values){return fold(query).isEmpty()||rank(query,values)>0;}
 /** At most one substitution/insertion/deletion; no unbounded matrix allocation. */
 static boolean oneEdit(String a,String b){if(Math.abs(a.length()-b.length())>1)return false;int i=0,j=0,edits=0;while(i<a.length()&&j<b.length()){if(a.charAt(i)==b.charAt(j)){i++;j++;continue;}if(++edits>1)return false;if(a.length()<=b.length())j++;if(a.length()>=b.length())i++;}return edits+(a.length()-i)+(b.length()-j)<=1;}
 private PreviewSearchText(){}
}
