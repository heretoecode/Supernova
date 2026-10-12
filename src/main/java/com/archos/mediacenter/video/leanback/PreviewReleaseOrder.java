package com.archos.mediacenter.video.leanback;

import com.archos.mediacenter.video.leanback.PreviewLibraryLoader.Entry;
import java.time.*;
import java.util.Comparator;

/** Provider dates are Date.getTime(), including negative pre-1970 values. Unknowns sort last. */
public final class PreviewReleaseOrder {
    public static LocalDate date(Entry entry) {
        String raw=entry.releaseDate==null?"":entry.releaseDate.trim();
        try {
            if(raw.matches("[0-9]{4}-[0-9]{2}-[0-9]{2}.*"))return LocalDate.parse(raw.substring(0,10));
            if(raw.matches("[0-9]{4}")){int year=Integer.parseInt(raw);if(year>=1800&&year<=9999)return LocalDate.of(year,1,1);}
            if(raw.matches("-?[0-9]+")){long millis=Long.parseLong(raw);if(millis!=0){LocalDate day=Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate();if(day.getYear()>=1800&&day.getYear()<=9999)return day;}}
        }catch(DateTimeException|NumberFormatException invalid){/* Fall back to the title's indexed year. */}
        return entry.year()>0?LocalDate.of(entry.year(),1,1):null;
    }
    public static Comparator<Entry> comparator(boolean ascending) {
        Comparator<LocalDate> dates=ascending?Comparator.naturalOrder():Comparator.reverseOrder();
        return Comparator.comparing(PreviewReleaseOrder::date,Comparator.nullsLast(dates))
            .thenComparing(PreviewPages::displayName,String.CASE_INSENSITIVE_ORDER).thenComparing(Entry::key);
    }
    private PreviewReleaseOrder(){}
}
