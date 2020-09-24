package com.example.video_player_1.com.sdcode.videoplayer.Videos;

import java.util.ArrayList;

public  interface videoLoadListner
{
    public void onVideoLoaded(ArrayList<videoItem> videoItems);
    public void onFail(String s);
}
