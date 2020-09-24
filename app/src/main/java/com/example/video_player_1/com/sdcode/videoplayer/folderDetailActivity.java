package com.example.video_player_1.com.sdcode.videoplayer;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.video_player_1.R;
import com.example.video_player_1.com.sdcode.videoplayer.Fragment.videoFragment;
import com.example.video_player_1.com.sdcode.videoplayer.Videos.videoItem;
import com.example.video_player_1.com.sdcode.videoplayer.Adaptors.videoAdaptor;
import com.example.video_player_1.com.sdcode.videoplayer.kxUtil.kxUtil;

import java.util.ArrayList;

public class folderDetailActivity extends baseAcitivity
{


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_folder_detail);

        // toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);

        // set the toolbar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // set Title
        setTitle(globalVar.foldername);

        folders=new videoFragment();

        // for lollipop
        if(Build.VERSION.SDK_INT<=Build.VERSION_CODES.LOLLIPOP_MR1)
        {
            loadEverything();
        }
        else
        {
            // do Later
        }

    }
    public void loadEverything()
    {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,folders).commit();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        menu.clear();


        // Grid or List
        if(!globalVar.multiselected)
        {
            // For Grid
            if((new preference(this)).getGrid())
            {
                getMenuInflater().inflate(R.menu.first,menu);
            }
            // For List
            else
            {
                getMenuInflater().inflate(R.menu.first1,menu);
            }
        }
        // For Multiselect Videos
        else
        {
            getMenuInflater().inflate(R.menu.menu_multiselected_option,menu);
        }


        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Grid or List
        if(!globalVar.multiselected)
        {
            // For Grid
            if((new preference(this)).getGrid())
            {
                getMenuInflater().inflate(R.menu.first,menu);
            }
            // For List
            else
            {
                getMenuInflater().inflate(R.menu.first1,menu);
            }
        }
        // For Multiselect Videos
        else
        {
            getMenuInflater().inflate(R.menu.menu_multiselected_option,menu);
        }


        //getMenuInflater().inflate(R.menu.first1,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        Handler handler=new Handler();

        int id=item.getItemId();
        if(id==R.id.menu_sort_by_az)
        {
            ((videoFragment) folders).sortAZ(true);
        }
        else if(id==R.id.menu_sort_by_za)
        {
            ((videoFragment) folders).sortZA();
        }
        else if(id==R.id.menu_sort_by_size)
        {
            ((videoFragment) folders).sortSize();
        }
        else if(id==R.id.action_view_grid)
        {
            new preference(this).setGrid(true);
            folders=new videoFragment();
            handler.postDelayed(() -> getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,folders).commit(),500);
        }
        else if(id==R.id.action_view_list)
        {
            new preference(this).setGrid(false);
            folders=new videoFragment();
            handler.postDelayed(() -> getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,folders).commit(),500);
        }
        else if(id==R.id.action_play)
        {
            // Reset Flag
            globalVar.multiselected=false;
            // Reset Count
            videoAdaptor.i=0;


            ArrayList<videoItem> first=new ArrayList<>();

            // Remove Selected for OnResume() for Relaunching on Back Press
            for(videoItem videoItem:globalVar.videoTrack)
            {
                if(videoItem.selected)
                {
                    first.add(videoItem);
                    videoItem.selected=false;
                }
            }





            globalVar.videoTrack.clear();
            globalVar.videoTrack.addAll(first);

            globalVar.updatedTrack.clear();
            globalVar.updatedTrack.addAll(first);


            // If Same Video Play then start from beginning
            globalVar.currentPath="";
            globalVar.videoservice.playVideo(globalVar.videoTrack.get(0),false);
            final Intent intent=new Intent(this,videoPlayerMain.class);


            startActivity(intent);


            /*

            // And We Have OnResume

            folders = new videoFragment();
            loadEverything();

             */



        }

        else if(id==R.id.action_share)
        {
            // do work
            ArrayList<videoItem> first=new ArrayList<>();
            for(videoItem v:globalVar.videoTrack)
            {
                if(v.selected)
                {
                    first.add(v);
                }
            }

            // Atleast 1 video
            if(first.size()>=1)
            {
                kxUtil.shareMultiVideo(this,first);
            }
        }

        else if(id==R.id.action_go_to_playing)
        {
            if(globalVar.play_signal && !globalVar.playAsPopup)
            {
                Intent intent=new Intent(this,videoPlayerMain.class);
                startActivity(intent);
            }
            else if(globalVar.play_signal && globalVar.playAsPopup)
            {
                Message_1("  Close the Popup and the click the play button to see Full Screen");
            }
            else
            {
                Message_1(" U Haven't Played Video Yet");
            }
        }

        return super.onOptionsItemSelected(item);
    }

    // This One is for -> this arrow on ToolBar
    @Override
    public boolean onSupportNavigateUp() {

        if(globalVar.multiselected)
        {
            // Reset Flag
            globalVar.multiselected=false;
            // Reset Count
            videoAdaptor.i=0;

            if(globalVar.videoTrack!=null && globalVar.videoTrack.size()!=0)
            {
                for(videoItem videoItem:globalVar.videoTrack)
                {
                    if(videoItem.selected)
                    {
                        videoItem.selected=false;
                    }
                }
            }
            globalVar.multiselected=false;


            folders=new videoFragment();
            loadEverything();
            return false;
        }

        finish();
        return true;
    }

    @Override
    public void onBackPressed() {

        // for debugging
        /*
        if(globalVar.backPressAllow)
        {
            globalVar.backPressAllow=false;
            super.onBackPressed();
        }
         */
        // for debugging

        if(globalVar.multiselected)
        {
            // Reset Flag
            globalVar.multiselected=false;
            // Reset Count
            videoAdaptor.i=0;

            if(globalVar.videoTrack!=null && globalVar.videoTrack.size()!=0)
            {
                for(videoItem videoItem:globalVar.videoTrack)
                {
                    if(videoItem.selected)
                    {
                        videoItem.selected=false;
                    }
                }
            }
            globalVar.multiselected=false;


            folders=new videoFragment();
            loadEverything();
            return;
        }

        super.onBackPressed();
    }




    @Override
    protected void onResume() {

        super.onResume();

        // for debugging
        Message_1(" On Resume");
        // for debugging

        folders = new videoFragment();
        loadEverything();

    }

    @Override
    protected void onPause() {
        //Message_1("onPause");
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    // for debugging

    private void Message_1(String m)
    {
        int duration= Toast.LENGTH_SHORT;
        Toast t=Toast.makeText(getApplicationContext(),m,duration);
        t.show();
    }

    // for debugging


}
