// Copyright 2026. Licensed under the Apache License, Version 2.0.
package com.archos.mediacenter.video.streaming;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import androidx.preference.*;
import com.archos.mediacenter.video.R;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.*;
import java.util.concurrent.Future;

/** Native NOVA preference rows: no replacement settings screen. */
public final class StreamingPreferences {
    private final PreferenceFragmentCompat fragment;
    private final PreferenceCategory category;
    private final ListPreference country;
    private final MultiSelectListPreference providers;
    private final ListPreference preferred;
    private final Preference status;
    private List<StreamingRepository.Provider> catalogue = Collections.emptyList();
    private Future<?> task;
    private int generation;
    private final Handler main = new Handler(Looper.getMainLooper());

    public StreamingPreferences(PreferenceFragmentCompat fragment) {
        this.fragment = fragment;
        Context context = fragment.requireContext();
        category = fragment.findPreference("streaming_category");
        SwitchPreferenceCompat enabled = new SwitchPreferenceCompat(context);
        enabled.setKey(StreamingRepository.ENABLED);
        enabled.setTitle(R.string.streaming_enabled_title);
        enabled.setSummary(R.string.streaming_enabled_summary);
        enabled.setDefaultValue(true);
        add(enabled);
        country = new ListPreference(context);
        country.setKey(StreamingRepository.COUNTRY);
        country.setTitle(R.string.streaming_country);
        country.setDefaultValue("IE");
        country.setSummaryProvider(ListPreference.SimpleSummaryProvider.getInstance());
        List<String> codes = new ArrayList<>(Arrays.asList(Locale.getISOCountries()));
        codes.sort(Comparator.comparing(code -> new Locale("", code).getDisplayCountry()));
        CharSequence[] names = new CharSequence[codes.size()];
        for (int i = 0; i < names.length; i++) names[i] = new Locale("", codes.get(i)).getDisplayCountry();
        country.setEntries(names); country.setEntryValues(codes.toArray(new CharSequence[0]));
        add(country);
        providers = new MultiSelectListPreference(context);
        providers.setTitle(R.string.streaming_providers);
        providers.setDialogTitle(R.string.streaming_providers);
        providers.setEntries(new CharSequence[0]); providers.setEntryValues(new CharSequence[0]);
        add(providers);
        preferred = new ListPreference(context);
        preferred.setTitle(R.string.streaming_preferred);
        preferred.setSummaryProvider(ListPreference.SimpleSummaryProvider.getInstance());
        add(preferred);
        status = new Preference(context);
        status.setTitle(R.string.streaming_refresh);
        status.setOnPreferenceClickListener(p -> { load(); return true; });
        add(status);
        Preference attribution = new Preference(context);
        attribution.setTitle("JustWatch");
        attribution.setSummary(R.string.streaming_attribution);
        attribution.setOnPreferenceClickListener(p -> {
            StreamingActions.openWeb(fragment.requireActivity(), "https://www.justwatch.com/" + StreamingRepository.country(context).toLowerCase(Locale.ROOT));
            return true;
        });
        add(attribution);
        country.setOnPreferenceChangeListener((p, value) -> {
            StreamingRepository.prefs(context).edit().putString(StreamingRepository.COUNTRY, value.toString()).apply();
            load(); return true;
        });
        providers.setOnPreferenceChangeListener((p, value) -> {
            @SuppressWarnings("unchecked") Set<String> ids = new HashSet<>((Set<String>) value);
            StreamingRepository.prefs(context).edit().putStringSet(StreamingRepository.PROVIDERS + StreamingRepository.country(context), ids).apply();
            updatePreferred(ids); updateSummary(ids); return true;
        });
        load();
    }
    private void add(Preference preference) {
        preference.setIconSpaceReserved(false);
        category.addPreference(preference);
    }
    private void load() {
        final int request = ++generation;
        if (task != null) task.cancel(true);
        final Context context = fragment.requireContext().getApplicationContext();
        final String region = StreamingRepository.country(context);
        providers.setKey(StreamingRepository.PROVIDERS + region);
        preferred.setKey(StreamingRepository.PREFERRED + region);
        catalogue = cachedCatalogue(context, region);
        populate(context);
        status.setSummary(R.string.streaming_loading);
        task = StreamingRepository.IO.submit(() -> {
            try {
                List<StreamingRepository.Provider> loaded = StreamingRepository.providers(context, region);
                JSONArray json = new JSONArray();
                for (StreamingRepository.Provider p : loaded) json.put(new JSONObject().put("id", p.id).put("name", p.name));
                StreamingRepository.prefs(context).edit().putString("streaming_catalogue_" + region, json.toString()).apply();
                main.post(() -> {
                    if (request != generation || !fragment.isAdded()) return;
                    catalogue = loaded; populate(context);
                    status.setSummary(loaded.isEmpty() ? R.string.streaming_no_country_providers : R.string.streaming_ready);
                });
            } catch (Exception e) {
                main.post(() -> {
                    if (request == generation && fragment.isAdded()) status.setSummary(R.string.streaming_settings_error);
                });
            }
        });
    }
    private static List<StreamingRepository.Provider> cachedCatalogue(Context c, String region) {
        List<StreamingRepository.Provider> result = new ArrayList<>();
        try {
            JSONArray data = new JSONArray(StreamingRepository.prefs(c).getString("streaming_catalogue_" + region, "[]"));
            for (int i = 0; i < data.length(); i++) {
                JSONObject p = data.getJSONObject(i);
                result.add(new StreamingRepository.Provider(p.getInt("id"), p.getString("name")));
            }
        } catch (Exception ignored) { }
        return result;
    }
    private void populate(Context context) {
        CharSequence[] names = new CharSequence[catalogue.size()], ids = new CharSequence[catalogue.size()];
        for (int i = 0; i < catalogue.size(); i++) {
            names[i] = catalogue.get(i).name; ids[i] = Integer.toString(catalogue.get(i).id);
        }
        providers.setEntries(names); providers.setEntryValues(ids);
        providers.setEnabled(!catalogue.isEmpty());
        Set<String> chosen = StreamingRepository.selected(context);
        providers.setValues(chosen);
        updatePreferred(chosen); updateSummary(chosen);
    }
    private void updateSummary(Set<String> chosen) {
        List<String> names = new ArrayList<>();
        for (StreamingRepository.Provider p : catalogue) if (chosen.contains(Integer.toString(p.id))) names.add(p.name);
        providers.setSummary(names.isEmpty() ? fragment.getString(R.string.streaming_select_providers) : android.text.TextUtils.join(", ", names));
    }
    private void updatePreferred(Set<String> chosen) {
        List<CharSequence> names = new ArrayList<>(), ids = new ArrayList<>();
        names.add(fragment.getString(R.string.streaming_automatic)); ids.add("");
        for (StreamingRepository.Provider p : catalogue) if (chosen.contains(Integer.toString(p.id))) {
            names.add(p.name); ids.add(Integer.toString(p.id));
        }
        preferred.setEntries(names.toArray(new CharSequence[0])); preferred.setEntryValues(ids.toArray(new CharSequence[0]));
        String current = StreamingRepository.preferred(fragment.requireContext());
        preferred.setValue(chosen.contains(current) ? current : "");
        preferred.setEnabled(!chosen.isEmpty());
    }
    public void close() {
        generation++;
        if (task != null) task.cancel(true);
        main.removeCallbacksAndMessages(null);
    }
}
