package helper.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import java.util.Set;

/**
 * Created by Shine on 2016/2/12.
 */
public class SharePrefUtils {
    private static final String name = "config";
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    public SharePrefUtils(Context context) {
        sharedPref = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
    }

    public void putString(String key, @Nullable String value) {
        editor.putString(key, value);
        editor.commit();
    }

    public void putStringSet(String key, @Nullable Set<String> values) {
        editor.putStringSet(key, values);
        editor.commit();
    }

    public void putInt(String key, int value) {
        editor.putInt(key, value);
        editor.commit();
    }

    public void putLong(String key, long value) {
        editor.putLong(key, value);
        editor.commit();
    }

    public void putFloat(String key, float value) {
        editor.putFloat(key, value);
        editor.commit();
    }

    public void putBoolean(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.commit();
    }

    @Nullable
    public String getString(String key, @Nullable String defValue) {
        return sharedPref.getString(key, defValue);
    }

    @Nullable
    public String getString(String key) {
        return getString(key, "");
    }

    @Nullable
    public Set<String> getStringSet(String key, @Nullable Set<String> defValues) {
        return sharedPref.getStringSet(key, defValues);
    }

    public int getInt(String key, int defValue) {
        return sharedPref.getInt(key, defValue);
    }

    public int getInt(String key) {
        return getInt(key, 0);
    }

    public long getLong(String key, long defValue) {
        return sharedPref.getLong(key, defValue);
    }

    public long getLong(String key) {
        return getLong(key, 0);
    }

    public float getFloat(String key, float defValue) {
        return sharedPref.getFloat(key, defValue);
    }

    public float getFloat(String key) {
        return getFloat(key, 0);
    }

    public boolean getBoolean(String key, boolean defValue) {
        return sharedPref.getBoolean(key, defValue);
    }

    public boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    public boolean contains(String key) {
        return sharedPref.contains(key);
    }
}
