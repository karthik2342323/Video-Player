package com.example.video_player_1.com.sdcode.videoplayer;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.preference.Preference;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatActivity;

public class preference extends AppCompatActivity
{
    public final static String BG_MUSIC="BG_Music";
    public final static String ORIENTATION="ORIENTATION";
    public final static String shuffle="Shuffle";
    public final static String Repeat_Itself="repeat";
    public final static String Repeat_all="Repeat all";
    public final static String TYPE="type";
    public final static String SELECT_VIDEO="select video";
    public final static String GRID="grid";

      Context context;
     static SharedPreferences pref;
    public  preference(Context context)
    {
        pref=PreferenceManager.getDefaultSharedPreferences(context);
        this.context=context;
    }
    public  Boolean getBgMusic()
    {
        // for debugging Its True
        return pref.getBoolean(BG_MUSIC,false);
    }
    public  void setBgMusic()
    {
         SharedPreferences.Editor edit=pref.edit();
         edit.putBoolean(BG_MUSIC,!getBgMusic());
         edit.apply();
    }
    public  int getRequestOrientation()
    {
        // default will be Sensor
        return pref.getInt(ORIENTATION,ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }
    public  void setOrientation(int orientation)
    {
        SharedPreferences.Editor edit=pref.edit();
        edit.putInt(ORIENTATION,orientation);
        edit.apply();
    }
    public String getType() {
        return pref.getString(TYPE, Repeat_all);
    }
    public void  setType(String val)
    {
        SharedPreferences.Editor edit=pref.edit();
        edit.putString(TYPE,val);
        edit.apply();
    }



    public Boolean getGrid()
    {
        return pref.getBoolean(GRID,false);
    }

    public void setGrid(Boolean value)
    {
        SharedPreferences.Editor editor=pref.edit();
        editor.putBoolean(GRID,value);
        editor.apply();
    }



}
