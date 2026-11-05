package com.archos.mediacenter.video.leanback;

import android.app.Dialog;
import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.LruCache;
import android.view.*;
import android.widget.*;
import java.util.*;

/**
 * Language icon used everywhere a language is shown (Settings pickers, audio/subtitle tracks,
 * subtitle downloads, Details).
 *
 * Flag policy (owner decision, 2 November 2026 — supersedes the earlier
 * "flag only for an explicit region" rule, which left real-world tracks such as {@code eng}
 * showing a globe):
 * <ol>
 *   <li>An explicit, valid region always wins: {@code en-US} → US, {@code pt-BR} → BR.</li>
 *   <li>Otherwise the language's default flag from {@link #DEFAULT_COUNTRY}:
 *       {@code en}/{@code eng} → GB, {@code fre}/{@code fra}/{@code fr} → FR, and so on.</li>
 *   <li>Languages with no single obvious country (Arabic, Swahili, Catalan, Welsh, Esperanto,
 *       Cantonese, unknown/undetermined…) keep the neutral globe.</li>
 * </ol>
 */
public final class PreviewLanguageIcon extends Drawable {
    private static final Set<String> COUNTRIES = new HashSet<>(Arrays.asList(Locale.getISOCountries()));
    private static final Set<String> LANGUAGES = new HashSet<>();
    /** Three-letter ISO 639-2 codes (terminology and bibliographic) → two-letter ISO 639-1. */
    private static final Map<String, String> ISO3_TO_ISO1 = new HashMap<>();
    static {
        for (String language : Locale.getISOLanguages()) {
            LANGUAGES.add(language);
            try {
                String iso3 = new Locale(language).getISO3Language();
                LANGUAGES.add(iso3);
                ISO3_TO_ISO1.put(iso3, language);
            } catch (MissingResourceException ignored) {
            }
        }
        // Bibliographic ISO 639-2/B codes, common in MKV/MP4 track metadata.
        String[][] bibliographic = {{"fre", "fr"}, {"ger", "de"}, {"chi", "zh"}, {"cze", "cs"}, {"dut", "nl"},
                                    {"per", "fa"}, {"gre", "el"}, {"slo", "sk"}, {"alb", "sq"}, {"arm", "hy"},
                                    {"geo", "ka"}, {"mac", "mk"}, {"may", "ms"}, {"bur", "my"}, {"rum", "ro"},
                                    {"ice", "is"}, {"baq", "eu"}, {"wel", "cy"}, {"tib", "bo"}, {"fil", "tl"}};
        for (String[] pair : bibliographic) {
            ISO3_TO_ISO1.put(pair[0], pair[1]);
            LANGUAGES.add(pair[0]);
        }
        // Android/Java swap legacy and modern codes for Hebrew, Indonesian and Yiddish; accept both.
        LANGUAGES.addAll(Arrays.asList("in", "id", "iw", "he", "ji", "yi"));
    }
    /**
     * Default flag key for a language with no explicit region. Keys are ISO countries ("GB"),
     * subdivisions ("GB-WLS", "ES-CT"), the Arab League ("ARAB") or Supernova-drawn language
     * flags ("L-EO"). Owner 2 Nov 2026: a unique flag wherever possible; the globe remains only
     * for languages without a fitting flag (e.g. Asturian, Aragonese, Navajo, Abkhazian, Syriac,
     * Interlingua, Toki Pona, und).
     */
    static final Map<String, String> DEFAULT_COUNTRY;
    static {
        Map<String, String> map = new HashMap<>();
        String table = "en=GB fr=FR de=DE es=ES it=IT pt=PT nl=NL sv=SE da=DK no=NO nb=NO nn=NO fi=FI is=IS "
                       + "pl=PL cs=CZ sk=SK hu=HU ro=RO bg=BG el=GR ru=RU uk=UA be=BY sr=RS hr=HR sl=SI bs=BA "
                       + "mk=MK sq=AL et=EE lv=LV lt=LT tr=TR he=IL iw=IL fa=IR hi=IN bn=BD ur=PK th=TH vi=VN "
                       + "id=ID in=ID ms=MY tl=PH ja=JP ko=KR zh=CN ka=GE hy=AM az=AZ kk=KZ uz=UZ mn=MN km=KH "
                       + "lo=LA my=MM ne=NP si=LK ga=IE mt=MT lb=LU af=ZA zu=ZA xh=ZA am=ET so=SO yo=NG ig=NG "
                       + "ha=NG mi=NZ ta=IN te=IN mr=IN kn=IN ml=IN gu=IN pa=IN or=IN as=IN sd=PK ps=AF tk=TM "
                       + "tg=TJ ky=KG fo=FO kl=GL "
                       + "cy=GB-WLS gd=GB-SCT ca=ES-CT gl=ES-GA eu=ES-PV ar=ARAB sw=KE "
                       + "eo=L-EO se=L-SE tt=L-TT ku=L-KU kmr=L-KU ckb=L-KU br=L-BR oc=L-OC "
                       + "kaa=UZ sat=IN yue=HK";
        for (String entry : table.split(" ")) {
            String[] kv = entry.split("=");
            map.put(kv[0], kv[1]);
        }
        DEFAULT_COUNTRY = Collections.unmodifiableMap(map);
    }
    /** English language names ("English", "French") for track metadata that carries a name instead of a code. */
    private static final Map<String, String> NAME_TO_LANGUAGE = new HashMap<>();
    static {
        for (String language : Locale.getISOLanguages()) {
            String name = new Locale(language).getDisplayLanguage(Locale.ENGLISH).toLowerCase(Locale.ROOT);
            if (!name.isEmpty() && !name.equals(language))
                NAME_TO_LANGUAGE.putIfAbsent(name, language);
        }
    }
    /** OpenSubtitles-only language codes that are not ISO 639 codes. */
    private static final Map<String, String> PROVIDER_COUNTRY;
    static {
        Map<String, String> map = new HashMap<>();
        map.put("me", "ME");    // Montenegrin
        map.put("pm", "MZ");    // Portuguese (Mozambique)
        map.put("pr", "AF");    // Dari
        map.put("ma", "IN");    // Manipuri
        map.put("sp", "ES");    // Spanish (EU)
        map.put("ea", "MX");    // Spanish (LA) — owner: Mexico flag
        map.put("ze", "CN");    // Chinese bilingual
        map.put("ex", "L-EXT"); // Extremaduran
        map.put("zh-ca", "HK"); // OpenSubtitles Cantonese (not Canada)
        PROVIDER_COUNTRY = Collections.unmodifiableMap(map);
    }
    private static final LruCache<String, Bitmap> FLAGS = new LruCache<>(32);
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
    private final PreviewIcon neutral = new PreviewIcon("language");
    private final Bitmap flag;

    /** Contextless callers retain the neutral safe fallback. App consumers use bundled resources. */
    public PreviewLanguageIcon(String code) {
        this(null, code);
    }

    public PreviewLanguageIcon(Context context, String code) {
        String country = country(providerCode(code));
        Bitmap bitmap = null;
        if (context != null && !country.isEmpty()) {
            synchronized (FLAGS) {
                bitmap = FLAGS.get(country);
                if (bitmap == null) {
                    int id = context.getResources().getIdentifier(resourceName(country), "drawable",
                                                                  context.getPackageName());
                    if (id != 0) {
                        bitmap = BitmapFactory.decodeResource(context.getResources(), id);
                        if (bitmap != null)
                            FLAGS.put(country, bitmap);
                    }
                }
            }
        }
        flag = bitmap;
    }

    /** Flag key for {@code code} (see {@link #DEFAULT_COUNTRY}), or "" for the neutral globe. */
    static String country(String code) {
        if (code == null)
            return "";
        code = code.trim().toLowerCase(Locale.ROOT).replace('_', '-');
        if (code.isEmpty())
            return "";
        if (PROVIDER_COUNTRY.containsKey(code))
            return PROVIDER_COUNTRY.get(code);
        if (!code.matches("[a-z]{2,3}(?:-[a-z0-9]{2,8})*")) {
            // Metadata that carries a language name rather than a code, e.g. "English" or "English (UK)".
            String name = code.replaceAll("\\s*\\(.*\\)$", "").trim();
            String language = NAME_TO_LANGUAGE.get(name);
            return language == null ? "" : country(language);
        }
        Locale locale = Locale.forLanguageTag(code);
        String language = locale.getLanguage();
        if (!wellFormedLanguage(language))
            return ""; // und, mul, zxx, private use (qaa-qtz), commentary…
        String iso1 = language.length() == 3 ? ISO3_TO_ISO1.getOrDefault(language, language) : language;
        String fallback = DEFAULT_COUNTRY.getOrDefault(iso1, "");
        // 1. An explicit, valid region wins — but keep a subdivision flag within its own country (cy-GB → Wales).
        String region = locale.getCountry().toUpperCase(Locale.ROOT);
        if (COUNTRIES.contains(region))
            return fallback.startsWith(region + "-") ? fallback : region;
        // Traditional Chinese script without a region is shown as Taiwan.
        if ("Hant".equals(locale.getScript()) && "zh".equals(iso1))
            return "TW";
        // 2. The language's default flag; 3. otherwise the neutral globe.
        return fallback;
    }

    /** Any well-formed ISO 639 code (including rarer 639-3 codes such as kmr, sat, kaa), excluding special codes. */
    static boolean wellFormedLanguage(String language) {
        if (language == null || !language.matches("[a-z]{2,3}"))
            return false;
        if (Arrays.asList("und", "mul", "zxx", "mis").contains(language))
            return false;
        if (language.length() == 3 && language.compareTo("qaa") >= 0 && language.compareTo("qtz") <= 0)
            return false;
        return language.length() == 3 || LANGUAGES.contains(language);
    }

    /** Drawable resource name for a flag key: "GB" → region_gb, "GB-WLS" → region_gb_wls, "L-EO" → lang_eo. */
    static String resourceName(String key) {
        String k = key.toLowerCase(Locale.ROOT);
        return k.startsWith("l-") ? "lang_" + k.substring(2) : "region_" + k.replace('-', '_');
    }

    @Override
    public void draw(Canvas canvas) {
        Rect bounds = getBounds();
        if (flag == null) {
            neutral.setBounds(bounds);
            neutral.draw(canvas);
            return;
        }
        canvas.drawBitmap(flag, null, bounds, paint);
    }

    @Override
    public void setAlpha(int alpha) {
        paint.setAlpha(alpha);
        neutral.setAlpha(alpha);
        invalidateSelf();
    }

    @Override
    public void setColorFilter(ColorFilter filter) {
        neutral.setColorFilter(filter);
        invalidateSelf();
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    /** OpenSubtitles uses zh-ca for Cantonese, not a reliable Canadian region. */
    public static String providerCode(String code) {
        return "zh-ca".equalsIgnoreCase(code) ? "yue" : code;
    }

    public static void apply(TextView text, String code) {
        apply(text, code, PreviewDialog.dp(text.getContext(), 20));
    }

    /** Bounds must be final before binding: TextView caches compound drawable dimensions. */
    public static void apply(TextView text, String code, int sizePx) {
        PreviewLanguageIcon icon = new PreviewLanguageIcon(text.getContext(), code);
        int size = Math.max(1, sizePx);
        icon.setBounds(0, 0, size, size);
        Drawable[] existing = text.getCompoundDrawables();
        text.setCompoundDrawables(icon, existing[1], existing[2], existing[3]);
        text.setCompoundDrawablePadding(PreviewDialog.dp(text.getContext(), 8));
    }

    /** Inline icon for legacy/mixed metadata text; keep each track's own reliable locale. */
    public static CharSequence inline(Context context, String code, CharSequence label) {
        PreviewLanguageIcon icon = new PreviewLanguageIcon(context, providerCode(code));
        int size = PreviewDialog.dp(context, 18);
        icon.setBounds(0, 0, size, size);
        SpannableStringBuilder text = new SpannableStringBuilder("\uFFFC ");
        text.setSpan(new ImageSpan(icon, ImageSpan.ALIGN_BASELINE), 0, 1,
                     Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return text.append(label);
    }

    public static void bind(Dialog dialog, int index, String code) {
        if (dialog == null || dialog.getWindow() == null)
            return;
        View label = dialog.getWindow().getDecorView().findViewWithTag("preview-label:" + index);
        if (label == null || !(label.getParent() instanceof ViewGroup))
            return;
        ViewGroup row = (ViewGroup)label.getParent();
        if (row.getChildCount() > 0 && row.getChildAt(0) instanceof ImageView)
            ((ImageView)row.getChildAt(0)).setImageDrawable(new PreviewLanguageIcon(row.getContext(), code));
    }
}
