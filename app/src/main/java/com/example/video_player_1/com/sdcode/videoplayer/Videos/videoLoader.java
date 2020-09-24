package com.example.video_player_1.com.sdcode.videoplayer.Videos;

import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import com.example.video_player_1.com.sdcode.videoplayer.kxUtil.kxUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.LogRecord;

/*
Without Thread Implementation for Less Memory Allocation
 */

public class videoLoader
{
    public videoLoader(Context context,videoLoadListner videoLoadListner)
    {
        new videoRunner(context,videoLoadListner);
    }
    public class videoRunner
    {
        private Context context;
        private videoLoadListner listner;


        private Handler h=new Handler(Looper.getMainLooper());

        // List of Things to take via request
        private final String arr[]={
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.TITLE,
                MediaStore.Video.Media.DATE_TAKEN,
                MediaStore.Video.Media.RESOLUTION
        };

        videoRunner(Context context,videoLoadListner listner)
        {
            this.context=context;
            this.listner=listner;

            Cursor cursor=context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,arr
                    ,null,null,MediaStore.Video.Media.DEFAULT_SORT_ORDER);

            // Ether Video Fail to Load or No Video Found on Device
            if(cursor==null)
            {
                h.post(()->listner.onFail("Fail to Load or No Video Found "));
                // exit
                return ;
            }

            ArrayList<videoItem> first=new ArrayList<>();


            if(cursor.moveToLast())
            {
                // Actually duration is in Long but we are parceling to kxUtil to get String
                String duration;
                String path,name,resolution,date;
                Long dateAsMiliSec;

                // Video Mining Start
                do
                {
                    // get path
                    path=cursor.getString(cursor.getColumnIndex(arr[0]));

                    // If Path is null
                    if(path==null)
                    {
                        continue;
                    }

                    // get Duration
                    Long sec=cursor.getLong(cursor.getColumnIndex(arr[1]));
                    // convert to String
                    duration=kxUtil.Duration(sec/1000);

                    //get Title
                    name=cursor.getString(cursor.getColumnIndex(arr[2]));

                    // date with alway with Time as we know
                    dateAsMiliSec=cursor.getLong(cursor.getColumnIndex(arr[3]));
                    date=getDate("dd/MM/yyyy hh:mm:ss",dateAsMiliSec);

                    resolution=cursor.getString(cursor.getColumnIndex(arr[4]));

                    // Adding to List

                    // verify whether Its Exist
                    File f=new File(path);
                    if(f.exists())
                    {
                        String parent="Unknown";
                        File p=new File(f.getParent());
                        if(p.exists())
                        {
                            parent=p.getName();
                        }
                        Long size=f.length();

                        String size_orignal=kxUtil.size(size);

                        videoItem v=new videoItem(f.getName(),parent,path,size_orignal,resolution,date,duration,size);
                        first.add(v);

                        //videoItem(String name,String folder,String path,String filesize,String resolution,String date,String duration,Long file_size_float)
                    }
                }
                while(cursor.moveToPrevious());
            }

            cursor.close();

            // send the data via interface
            h.post(()->listner.onVideoLoaded(first));

        }

        public  String getDate(String format,Long date)
        {
            String s= DateFormat.format(format,date).toString();
            return s;
        }


    }
}
