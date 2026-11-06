package com.archos.mediacenter.video.utils.backup;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import androidx.preference.PreferenceManager;
import com.archos.mediacenter.video.browser.adapters.object.Video;
import com.archos.mediaprovider.video.VideoOpenHelper;
import com.archos.mediaprovider.video.VideoStore;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/** Selected physical file roles. Schema 1 remains readable; schema 2 adds personal files in the same category. */
public final class PortableHiddenItems
{
    public static final String PENDING = "supernova_restore_pending_hidden_v2";

    public static Map<Long, Boolean> read(SQLiteDatabase database) throws IOException
    {
        Map<Long, Boolean> result = new LinkedHashMap<>();
        try (Cursor rows = database.query(VideoOpenHelper.FILES_TABLE_NAME,
            new String[] {"_id", VideoStore.Video.VideoColumns.ARCHOS_HIDDEN_BY_USER},
            "media_type = ?", new String[] {"3"}, null, null, "_id ASC"))
        {
            while (rows.moveToNext())
            {
                long id = rows.getLong(0);
                long value = rows.isNull(1) ? 0 : rows.getLong(1);
                if (rows.getType(0) != Cursor.FIELD_TYPE_INTEGER || id <= 0 ||
                    !rows.isNull(1) && rows.getType(1) != Cursor.FIELD_TYPE_INTEGER ||
                    value != 0 && value != 1 || result.size() >= 100000)
                    throw new IOException("Invalid indexed hidden-item state");
                result.put(id, value == 1);
            }
        }
        catch (RuntimeException invalid)
        {
            throw new IOException("Hidden-item inventory unavailable", invalid);
        }
        return Collections.unmodifiableMap(result);
    }

    private static void validateRows(JSONArray rows) throws JSONException
    {
        if (rows.length() > 100000)
            throw new JSONException("Too many file roles");
        Set<String> seen = new HashSet<>();
        for (int i = 0; i < rows.length(); i++)
        {
            JSONObject row = rows.getJSONObject(i);
            if (row.length() != 1 || !(row.get("physical") instanceof String))
                throw new JSONException("Invalid file-role fields");
            String ref = row.getString("physical");
            PortablePhysicalReferences.validate(ref);
            if (!seen.add(ref))
                throw new JSONException("Duplicate file-role reference");
        }
    }

    public static void validate(JSONObject payload) throws JSONException
    {
        Object schema = payload.get("schema");
        if (!(schema instanceof Integer || schema instanceof Long))
            throw new JSONException("Unsupported hidden-item schema");
        long version = ((Number)schema).longValue();
        if (version != 1 && version != 2 || payload.length() != (version == 1 ? 2 : 3))
            throw new JSONException("Unsupported hidden-item schema");
        validateRows(payload.getJSONArray("rows"));
        if (version == 2)
            validateRows(payload.getJSONArray("personal"));
    }

    public static JSONObject export(Context context, Map<Long, Boolean> indexed, PortablePhysicalReferences refs)
        throws JSONException
    {
        return export(context, indexed, refs, null);
    }

    public static JSONObject export(Context context, Map<Long, Boolean> indexed, PortablePhysicalReferences refs,
                                     Set<Long> personal) throws JSONException
    {
        Map<String, JSONObject> hidden = new LinkedHashMap<>();
        Map<String, JSONObject> roles = new LinkedHashMap<>();
        for (Map.Entry<Long, Boolean> entry : indexed.entrySet())
        {
            if (entry.getKey() == null || entry.getKey() <= 0 || entry.getValue() == null)
                throw new JSONException("Invalid indexed hidden-item state");
            if (entry.getValue())
                add(hidden, refs.reference(entry.getKey()));
        }
        if (personal != null)
            for (long id : personal)
                add(roles, refs.reference(id));
        String pending = PreferenceManager.getDefaultSharedPreferences(context).getString(PENDING, null);
        if (pending != null)
        {
            JSONObject saved = new JSONObject(pending);
            validate(saved);
            for (String kind : saved.has("personal") ? new String[] {"rows", "personal"} : new String[] {"rows"})
            {
                JSONArray rows = saved.getJSONArray(kind);
                Map<String, JSONObject> target = kind.equals("rows") ? hidden : roles;
                for (int i = 0; i < rows.length(); i++)
                    add(target, rows.getJSONObject(i).getString("physical"));
            }
        }
        JSONObject result = new JSONObject().put("schema", personal == null && roles.isEmpty() ? 1 : 2)
            .put("rows", new JSONArray(hidden.values()));
        if (result.getInt("schema") == 2)
            result.put("personal", new JSONArray(roles.values()));
        validate(result);
        return result;
    }

    private static void add(Map<String, JSONObject> rows, String ref) throws JSONException
    {
        rows.put(ref, new JSONObject().put("physical", ref));
    }

    public static final class Prepared
    {
        public final Map<Long, Boolean> hidden;
        public final Map<Long, Boolean> personal;
        public final String pending;
        public final int unresolved;

        private Prepared(Map<Long, Boolean> hidden, Map<Long, Boolean> personal, JSONArray unknown,
                         JSONArray unknownPersonal, boolean modern) throws JSONException
        {
            this.hidden = Collections.unmodifiableMap(hidden);
            this.personal = Collections.unmodifiableMap(personal);
            JSONObject state = new JSONObject().put("schema", modern ? 2 : 1).put("rows", unknown);
            if (modern)
                state.put("personal", unknownPersonal);
            pending = state.toString();
            unresolved = unknown.length() + unknownPersonal.length();
        }
    }

    public static Prepared prepare(JSONObject payload, Map<Long, Boolean> receiving, PortablePhysicalReferences refs,
                                    boolean matchingValidated) throws JSONException
    {
        validate(payload);
        boolean modern = payload.getInt("schema") == 2;
        Map<Long, Boolean> hidden = new LinkedHashMap<>();
        Map<Long, Boolean> personal = new LinkedHashMap<>();
        for (Map.Entry<Long, Boolean> entry : receiving.entrySet())
        {
            if (entry.getKey() == null || entry.getKey() <= 0 || entry.getValue() == null)
                throw new JSONException("Invalid receiving hidden-item state");
            hidden.put(entry.getKey(), false);
            if (modern)
                personal.put(entry.getKey(), false);
        }
        JSONArray unknown = new JSONArray();
        JSONArray unknownPersonal = new JSONArray();
        for (String kind : modern ? new String[] {"rows", "personal"} : new String[] {"rows"})
        {
            JSONArray rows = payload.getJSONArray(kind);
            boolean role = kind.equals("personal");
            for (int i = 0; i < rows.length(); i++)
            {
                JSONObject row = rows.getJSONObject(i);
                Video target = refs.resolve(row.getString("physical"));
                if (!role && !matchingValidated || target == null || !receiving.containsKey(target.getId()))
                {
                    (role ? unknownPersonal : unknown).put(new JSONObject(row.toString()));
                    continue;
                }
                (role ? personal : hidden).put(target.getId(), true);
            }
        }
        return new Prepared(hidden, personal, unknown, unknownPersonal, modern);
    }

    private PortableHiddenItems()
    {
    }
}
