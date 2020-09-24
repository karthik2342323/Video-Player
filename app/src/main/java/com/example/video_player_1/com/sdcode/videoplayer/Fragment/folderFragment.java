package com.example.video_player_1.com.sdcode.videoplayer.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.video_player_1.R;
import com.example.video_player_1.com.sdcode.videoplayer.Videos.videoItem;
import com.example.video_player_1.com.sdcode.videoplayer.customizeUI.WrapContentLinearLayoutManager;
import com.example.video_player_1.com.sdcode.videoplayer.Adaptors.folderAdaptor;
import com.example.video_player_1.com.sdcode.videoplayer.Videos.videoLoader;
import com.example.video_player_1.com.sdcode.videoplayer.Videos.videoLoadListner;
import com.example.video_player_1.com.sdcode.videoplayer.Folders.folderLoader;
import com.example.video_player_1.com.sdcode.videoplayer.Folders.folderItem;
import com.example.video_player_1.com.sdcode.videoplayer.globalVar;

import java.util.ArrayList;
import java.util.Collections;

/*
 FrameLayouts and fragment we are using just for Integrate
 Toolbar with Frames As An Transaction Began
 */


public class folderFragment extends baseFragment
{


    RecyclerView recyclerView;


    folderAdaptor folders;

    ArrayList<videoItem> videos_list;
    ArrayList<folderItem> folders_list;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        folders=new folderAdaptor(getActivity());

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View v=inflater.inflate(R.layout.fragment_folder_list,container,false);
        recyclerView=v.findViewById(R.id.recyclerView);

        // Launch UI in Vertical
        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,false));


        recyclerView.setAdapter(folders);
        // Load folders
        loadEveryThing();
        return v;
        //return super.onCreateView(inflater, container, savedInstanceState);
    }

    private void loadEveryThing()
    {
        // permission checker Read,write
        if(Build.VERSION.SDK_INT>Build.VERSION_CODES.LOLLIPOP_MR1)
        {

            Boolean play=true;
            String arr[]={Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE};
            ArrayList<String> first=new ArrayList<>();
            int i=0;

            // check permission
            for(String x:arr)
            {
                if(  !( PackageManager.PERMISSION_GRANTED==getContext().checkSelfPermission(x) )  )
                {
                    first.add(x);
                }
            }

            // if Some Permission is not granted (Request Permission)
            if(first.size()>=1)
            {
                String arr_1[]=new String[first.size()];

                first.toArray(arr_1);
                getActivity().requestPermissions(arr_1,122);
            }

            // Recheck The Permissions
            for(String s:first)
            {
                if( !( PackageManager.PERMISSION_GRANTED==getContext().checkSelfPermission(s)  )  )
                {
                    return ;
                }
            }
        }


        videoLoadListner listner=new videoLoadListner() {
            // On Success
            @Override
            public void onVideoLoaded(ArrayList<videoItem> videoItems)
            {
                // This One is use for searching videos in List of Videos
                globalVar.allVideos=videoItems;

                videos_list=videoItems;

                folderLoader folders_loader=new folderLoader(videoItems);
                folders_list=folders_loader.getFolders();

                globalVar.folders=folders_list;

                // Update The List of videos
                folders.update(folders_list);


            }

            // On Failure
            @Override
            public void onFail(String s) {


                // DO Nothing
            }
        };
        // load videos request
        videoLoader videos=new videoLoader(getActivity(),listner);
    }

    public void sortAZ(Boolean update)
    {
        ArrayList<folderItem> first=globalVar.folders;
        ArrayList<folderItem> sorted=new ArrayList<>();

        int i,j;

        // sort character
        for(i=65;i<=90;i++)
        {
            j=i+32;
            for(folderItem x:first)
            {
                char s=x.getName().charAt(0);
                if(s==i || s==j)
                {
                    sorted.add(x);
                }
            }
        }

        // Non Character  add

        for(folderItem x:first)
        {
            char a=x.getName().charAt(0);
            if( !( (a>=65 && a<=90)  || (a>=97 && a<=122) ) )
            {
                sorted.add(x);
            }
        }

        // update var
        globalVar.folders=sorted;






        if(update)
        {
            folders.update(sorted);
        }


    }

    public void sortZA()
    {
        sortAZ(false);
        Collections.reverse(globalVar.folders);
        folders.update(globalVar.folders);
    }


    // Folder has more video will come first
    public void sortByVideo()
    {
        ArrayList<folderItem> first=new ArrayList<>();
        first.addAll(globalVar.folders);

        int i,j;
        for(i=0; i<first.size(); i++)
        {
            for(j=i+1; j<first.size(); j++)
            {
                if(first.get(i).first.size()<first.get(j).first.size())
                {
                    Collections.swap(first,i,j);
                }
            }
        }

        globalVar.folders=first;
        folders.update(first);
    }




    // for debugging

    private void Message_1(String m)
    {
        Context context=getActivity();
        int duration= Toast.LENGTH_SHORT;
        Toast t=Toast.makeText(context,m,duration);
        t.show();
    }


    // for debugging



}

