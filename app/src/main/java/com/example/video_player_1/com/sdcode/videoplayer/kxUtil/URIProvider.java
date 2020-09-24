package com.example.video_player_1.com.sdcode.videoplayer.kxUtil;

import android.content.Context;
import android.net.Uri;

import androidx.core.content.FileProvider;

import java.io.File;

public class URIProvider extends FileProvider
{
    public static Uri getUri(File f, Context context)
    {
       // return getUriForFile(context,"com.example.video_player_1.com.sdcode.videoplayer.provider",f);
        return getUriForFile(context,"com.example.video_player_1.provider",f);
    }
}
