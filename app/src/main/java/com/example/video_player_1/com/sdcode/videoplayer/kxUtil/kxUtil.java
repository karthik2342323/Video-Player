package com.example.video_player_1.com.sdcode.videoplayer.kxUtil;

// Add function On Requirement

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.video_player_1.R;
import com.example.video_player_1.com.sdcode.videoplayer.Videos.videoItem;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class kxUtil
{
    Context context;

    private final kxUtil instance=new kxUtil();
    public kxUtil getInstance()
    {
        return instance;
    }

    public static String Duration(Long sec)
    {
        String buffer,duration="";
        Long hour,min;
        // get hour
        hour=sec/3600;
        sec%=3600;

        min=sec/60;
        sec%=60;

        // For hour
        if(hour!=0)
        {
            buffer=""+hour;
            // hour
            if(buffer.length()==2)
            {
                duration+=buffer+":";
            }
            // Rare case
            else
            {
                duration+="0"+buffer+":";
            }

        }
        buffer=""+min;
        // for min
        if(buffer.length()==2)
        {
            duration+=buffer+":";
        }
        else
        {
            duration+="0"+buffer+":";
        }
        buffer=""+sec;
        // for sec
        if(buffer.length()==2)
        {
            duration+=buffer;
        }
        else
        {
            duration+="0"+buffer;
        }

        return duration;

    }

    public static String size(Long size)
    {
        String ans="";
        float kb,mb,gb,tb;
        DecimalFormat df=new DecimalFormat("0.00");

        kb=1024.0f;
        mb=kb*kb;
        gb=kb*kb*kb;
        tb=kb*kb*kb*kb;

        if(size<mb) // Represent in KB
        {
        ans=""+(df.format(size/kb))+"KB";
        }
        else if(size<gb) // Represent in MB
        {
            ans=""+(df.format(size/mb))+"MB";
        }

        else if (size<tb) // Represent in GB
        {
            ans=""+(df.format(size/gb))+"GB";
        }


        // TB That Much Bigger File Never exist
        return ans;
    }

    public static Intent shareSingleVideo(videoItem v, Context context)
    {

        Intent intent=new Intent();
        File f=new File(v.getPath());
        if(f.exists())
        {
            String extension=v.extension();
            // let see if Its mp4 or not
            if(extension.equals("MP4") || extension.equals("Mp4") || extension.equals("mp4"))
            {
                intent.setAction(Intent.ACTION_SEND)
                        .putExtra(Intent.EXTRA_STREAM,URIProvider.getUri(f,context))
                        .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        .setType("video/mp4");
            }
            // Same but File Type will be not detected
            else
            {
                intent.setAction(Intent.ACTION_SEND)
                        .putExtra(Intent.EXTRA_STREAM,URIProvider.getUri(f,context))
                        .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        .setType("video/*");
            }
        }
        return intent;
    }

    public static void shareMultiVideo(Context context, ArrayList<videoItem> first)
    {
        ArrayList<Uri> uri=new ArrayList<>();
        for(videoItem v:first)
        {
            File f=new File(v.getPath());

            if(f.exists())
            {
                Uri u=URIProvider.getUri(f,context);

                // for debugging
                Log.d(" uri  "," "+u.toString());
                // for debugging

                uri.add(u);
            }

        }

        // Atleast 1 video
        if(uri.size()>=1)
        {
            Intent intent=new Intent(Intent.ACTION_SEND_MULTIPLE);
            // Discription About Content
            intent.putExtra(Intent.EXTRA_SUBJECT," Share Multiple Video _1");
            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM,uri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setType("video/*");
            // Share Content Name on top CHooser
            context.startActivity(Intent.createChooser(intent," Share Multiple Video"));


            Log.d("URI Sended : ","done"+uri.size());

            // for  debugging

        }



    }

    // for debugging

    private void Message_1(String m)
    {
        int duration= Toast.LENGTH_SHORT;
        Toast t=Toast.makeText(context,m,duration);
        t.show();
    }

    // for debugging


}
