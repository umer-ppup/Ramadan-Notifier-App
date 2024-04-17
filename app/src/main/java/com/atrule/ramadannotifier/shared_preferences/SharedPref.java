package com.atrule.ramadannotifier.shared_preferences;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPref {

    final Context mContext;
    final SharedPreferences sharedPreferences;

    public SharedPref (Context context)
    {
        this.mContext = context;
        sharedPreferences = mContext.getSharedPreferences("AlarmTiming", Context.MODE_PRIVATE);
    }

    // region Sehr Alarm
    public void setSehrAlarmTime(int time)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("sehr_time", time);
        editor.apply();
    }

    public int getSehrAlarmTime()
    {
        return sharedPreferences.getInt("sehr_time", 30);
    }
    // endregion

    // region Iftar Alarm
    public void setIftarAlarmTime(int time)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("iftar_time", time);
        editor.apply();
    }

    public int getIftarAlarmTime()
    {
        return sharedPreferences.getInt("iftar_time", 1);
    }
    // endregion

}