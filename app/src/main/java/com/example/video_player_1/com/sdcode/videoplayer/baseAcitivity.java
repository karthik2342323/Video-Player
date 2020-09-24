package com.example.video_player_1.com.sdcode.videoplayer;

/*
 Every Activity extends This Activity for Theme change
 Implementation
 */

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.video_player_1.com.sdcode.videoplayer.Fragment.baseFragment;
import com.example.video_player_1.com.sdcode.videoplayer.videoService.kk;


public class baseAcitivity extends AppCompatActivity
{
     public  baseFragment folders;

     private static Intent intent;

     protected ServiceConnection serviceConnection=new ServiceConnection() {
         @Override
         public void onServiceConnected(ComponentName name, IBinder service) {

             videoService.kk first=(kk) service;
             if(globalVar.videoservice==null)
             {
                 globalVar.videoservice=first.read();
             }

             if(globalVar.playFromIntent)
             {
                 // set flag
                 globalVar.playFromIntent=false;

                 // do work
                 globalVar.currentPath="";
                 globalVar.videoservice.playVideo(globalVar.intentVideo,globalVar.playAsPopup);
                 if(!globalVar.playAsPopup)
                 {
                     Intent intent=new Intent(getApplicationContext(),videoPlayerMain.class);
                     startActivity(intent);
                 }

                 finish();

             }
         }

         @Override
         public void onServiceDisconnected(ComponentName name) {

             // Do Nothing
         }
     };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initService();

    }
    public void initService()
    {
        intent=new Intent(this,videoService.class);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {
            startForegroundService(intent);
        }
        else
        {
            startService(intent);
        }

        // On Bind Service
        bindService(intent,serviceConnection, Context.BIND_AUTO_CREATE);


    }

}
