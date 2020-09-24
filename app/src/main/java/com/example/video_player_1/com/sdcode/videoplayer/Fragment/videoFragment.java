package com.example.video_player_1.com.sdcode.videoplayer.Fragment;


import android.content.res.Configuration;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.video_player_1.R;
import com.example.video_player_1.com.sdcode.videoplayer.customizeUI.WrapContentGridLayoutManager;
import com.example.video_player_1.com.sdcode.videoplayer.customizeUI.WrapContentLinearLayoutManager;
import com.example.video_player_1.com.sdcode.videoplayer.Videos.videoItem;
import com.example.video_player_1.com.sdcode.videoplayer.globalVar;
import com.example.video_player_1.com.sdcode.videoplayer.Adaptors.videoAdaptor;
import com.example.video_player_1.com.sdcode.videoplayer.kxUtil.kxUtil;
import com.example.video_player_1.com.sdcode.videoplayer.preference;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class videoFragment extends baseFragment
{
    ArrayList<videoItem> videos= globalVar.launchVideo;
    videoAdaptor adaptors;

    RecyclerView recyclerView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adaptors=new videoAdaptor(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v=inflater.inflate(R.layout.fragment_video_list,null);

        // get Recycle View
        recyclerView=v.findViewById(R.id.recyclerView);

        LayoutLaunch(getResources().getConfiguration().orientation);

        // set Layout in Vertical
        //recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));

        // set the adaptor
        recyclerView.setAdapter(adaptors);

        // Launch the Adaptor
        loadEverything();

        return  v;

        //return super.onCreateView(inflater, container, savedInstanceState);
    }

    public void loadEverything()
    {
        adaptors.update(videos);
    }

    public void sortAZ(boolean execute)
    {
        ArrayList<videoItem> first=new ArrayList<>();
        first.addAll(globalVar.launchVideo);

        ArrayList<videoItem> sorted=new ArrayList<>();

        int i,j;

        // get Char
        for(i=65;i<=90;i++)
        {
            j=i+32;
            for(videoItem x:first)
            {
                char a=x.getName().charAt(0);
                if(a==i || a==j)
                {
                    sorted.add(x);
                }

            }
        }

        // get Non Character

        for(videoItem x:first)
        {
            char a=x.getName().charAt(0);
            if(   !( (a>=65 && a<=90) ||(a>=97 && a<=122) )   )
            {
                sorted.add(x);
            }
        }

        // update

            globalVar.launchVideo.clear();
            globalVar.launchVideo.addAll(sorted);

            if(execute) {
                adaptors.update(sorted);
            }
    }

    public void sortZA()
    {
        sortAZ(false);
        Collections.reverse(globalVar.launchVideo);
        adaptors.update(globalVar.launchVideo);
    }

    public void sortSize()
    {
        ArrayList<videoItem> first=new ArrayList<>();

        first.addAll(globalVar.launchVideo);

        int i,j;

        for(i=0;i<first.size();i++)
        {
            for(j=i+1;j<first.size();j++)
            {
                if(first.get(i).getFile_size_float()<first.get(j).getFile_size_float())
                {
                    Collections.swap(first,i,j);
                }
            }
        }

        // Update

        globalVar.launchVideo=first;

        adaptors.update(first);

    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {

        LayoutLaunch(newConfig.orientation);


        super.onConfigurationChanged(newConfig);

    }

    public  void LayoutLaunch(int orientation)
    {

        if(orientation== Configuration.ORIENTATION_PORTRAIT)
        {
            if(new preference(getActivity()).getGrid())
            {
                recyclerView.setLayoutManager(new WrapContentGridLayoutManager(getActivity(),2));
            }
            else
            {
                recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
            }
        }

        else if(orientation== Configuration.ORIENTATION_LANDSCAPE)
        {
            if(new preference(getActivity()).getGrid())
            {
                recyclerView.setLayoutManager(new WrapContentGridLayoutManager(getActivity(),3));
            }
            else
            {
                recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
            }
        }
    }


}
