package cn.elfsoft.assist.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class Config {
    public static void setSettings(Context context, String key, String content) {
        SharedPreferences sp = context.getSharedPreferences("settings", 0);
        Editor editor = sp.edit();
        editor.putString(key, content);
        editor.commit();
    }

    public static String getSettings(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences("settings", 0);
        return sp.getString(key, "");
    }
}
