package com.example.video_player_1.com.sdcode.videoplayer;

import android.app.Activity;
import android.content.Context;

import java.util.ArrayList;

import com.example.video_player_1.com.sdcode.videoplayer.Videos.videoItem;
import com.example.video_player_1.com.sdcode.videoplayer.Folders.folderItem;

public class globalVar
{
    public static Activity context;

    // This One is use for searching As An All VIdeos List
    public static ArrayList<videoItem> allVideos=new ArrayList<>();

    // Inside folder List of Videos for playing track
    public static ArrayList<videoItem>  videoTrack=new ArrayList<>();

    // This one is for Updated List of video removal
    public static ArrayList<videoItem> updatedTrack=new ArrayList<>();

    // Video is playing or set video for play
    public static videoItem currentVideo;

    // This one is for comparison of videos
   // public static String prevPath="";
    public static String currentPath="";

    // On Launching folder videos
    public static ArrayList<videoItem> launchVideo=new ArrayList<>();

    // folder name
    public static String foldername;

    public  static videoItem currentPlayingVideo;

    // set say Initially Its True for Debugging
    public static boolean playAsPopup=false;



    // Relaunch The Notification When state change play/pause
    public static boolean playPause=true;

    public static videoService videoservice;

    // At Beginning There is no Notification
    public static Boolean notificationFlag=false;


    public static ArrayList<folderItem> folders=new ArrayList<>();

    // for path differ

    public static int random=0;

    public static boolean playFromIntent=false;
    public static videoItem intentVideo;

    public static boolean multiselected=false;

    public static int i=0;

    public static Boolean play_signal=false;


    
}
