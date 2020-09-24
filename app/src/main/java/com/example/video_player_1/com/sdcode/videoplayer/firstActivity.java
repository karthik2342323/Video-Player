package com.example.video_player_1.com.sdcode.videoplayer;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.video_player_1.R;
import com.example.video_player_1.com.sdcode.videoplayer.Fragment.folderFragment;
import com.example.video_player_1.com.sdcode.videoplayer.Videos.videoItem;
import com.example.video_player_1.com.sdcode.videoplayer.Fragment.folderFragment;
import com.example.video_player_1.com.sdcode.videoplayer.about.about;

import java.util.ArrayList;

public class firstActivity extends baseAcitivity
{
    Context context;

    private   folderFragment folder_1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);



        context=getApplicationContext();


        // Intend Part
        if(Intent.ACTION_VIEW.equals(getIntent().getAction()))
        {

            Uri path=getIntent().getData();

            if(path==null)
            {
                Message_1(" Path is Null");
                finish();
                return;
            }

            // set video
            videoItem v=new videoItem(path.toString());
            globalVar.intentVideo=v;

            // set track
            globalVar.videoTrack=new ArrayList<>();
            globalVar.videoTrack.add(v);


            // Verry First Time videoService is Null So When Its Connected Then Play The Video
            if(globalVar.videoservice==null)
            {
                // set The flag
                globalVar.playFromIntent=true;

                // go to Service Connection Part i.e baseActivity.Java
            }
            // Directly Play If Its Not Null
            else
            {
                globalVar.currentPath="";

                globalVar.videoservice.playVideo(v,globalVar.playAsPopup);
                if(!globalVar.playAsPopup)
                {
                    Intent intent=new Intent(this,videoPlayerMain.class);
                    startActivity(intent);
                }

                finish();
            }

            return;
        }





        setContentView(R.layout.activity_first);



        // ToolBar
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Launch ToolBar
        if (getSupportActionBar()!=null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        folders=new folderFragment();




        // We Are Taking an case of lollipop
        if(Build.VERSION.SDK_INT<=Build.VERSION_CODES.LOLLIPOP_MR1)
        {

            loadActivity();
        }
        else
        {
            // Do Later for Higher version of Android
        }
    }
    private void loadActivity()
    {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,folders).commit();
    }

    /*
     Oe Time Execution
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.secound,menu);
        return true;
        //return super.onCreateOptionsMenu(menu);
    }

    /*
    Multiple Time Relaunch after Creation
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();

        getMenuInflater().inflate(R.menu.secound,menu);

        //return super.onPrepareOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id=item.getItemId();
        if(id==R.id.menu_sort_by_az)
        {
            ((folderFragment) folders).sortAZ(true);
            return true;
        }
        else if(id==R.id.menu_sort_by_za)
        {
            ((folderFragment) folders).sortZA();
            return true;
        }
        else if(id==R.id.menu_sort_by_total_videos)
        {
            ((folderFragment) folders).sortByVideo();
            return true;
        }
        else if(id==R.id.action_setting)
        {
            Intent intent=new Intent(this,settingActivity.class);
            startActivity(intent);
        }
        else if(id==R.id.action_search)
        {
           Intent intent=new Intent(this,searchActivity.class);
           startActivity(intent);
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

        else if(id==R.id.action_about)
        {
            Intent intent=new Intent(this,about.class);
            startActivity(intent);
        }

        return  false;

       // return super.onOptionsItemSelected(item);
    }




    /*
    This One is for Navigation <- This Arrow for going Back
                 */
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
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
