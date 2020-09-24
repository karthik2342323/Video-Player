package com.example.video_player_1.com.sdcode.videoplayer;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.location.SettingInjectorService;
import android.media.AudioManager;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.video_player_1.R;
import com.example.video_player_1.com.sdcode.videoplayer.customizeUI.WrapContentLinearLayoutManager;
import com.github.rubensousa.previewseekbar.PreviewView;
import com.github.rubensousa.previewseekbar.exoplayer.PreviewTimeBar;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.ui.TimeBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import net.cachapa.expandablelayout.ExpandableLayout;
import net.steamcrafted.materialiconlib.MaterialDrawableBuilder;
import net.steamcrafted.materialiconlib.MaterialIconView;

import com.example.video_player_1.com.sdcode.videoplayer.Adaptors.trackListAdaptor;
import com.example.video_player_1.com.sdcode.videoplayer.Videos.videoItem;
import com.example.video_player_1.com.sdcode.videoplayer.kxUtil.kxUtil;

import java.io.File;

public class videoPlayerMain extends AppCompatActivity implements PreviewView.OnPreviewChangeListener
{

    preference pref;
    Context context;

    Toolbar toolbar;
    PlayerView playerView;
    MaterialIconView popup,bg_music,rotation,volume_btn,brightness_btn,open_close,close_track,type,pre,post,pre_10,post_10,resize,lock_enable,lock_disable;
    RelativeLayout volume_region,brightness_region,pre_10_layout,post_10_layout,all_control_layout;
    SeekBar volume_seekBar,brightness_seekbar;
    ExpandableLayout expand,trackVideo;
    PlayerControlView Player_integrate_UI;
    PreviewTimeBar onTimeViewVideo_Bar;
    trackListAdaptor trackListAdaptor;
    RecyclerView recyclerView;

    int orientation;
    private  int i=1;
    public static int init=0;
    public static int resize_1=0;

    public static boolean lock_1=false;

    int executer;

    private static Boolean seek=false;

    int signal_12=0;
    Long prev;
    Long time=100000L;


    // Any Random Val
    int volume=-99;

    private static int condition=1;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


      // for debugging

        globalVar.playAsPopup=false;

        if(globalVar.videoservice.container!=null) {
            globalVar.videoservice.windowManager.removeView(globalVar.videoservice.container);
            globalVar.videoservice.container=null;
        }


      // for debugging


        context=getApplicationContext();

        pref=new preference(context);
        // Lauch in Previously set Orientation Only 1 time


         setRequestedOrientation(new preference(context).getRequestOrientation());




       // setRequestedOrientation(new preference(context).getRequestedOrientation());


        /*
         Consisting of seekBar (Volume,brightness) ,Unlock
         , Video Track List
         */
        setContentView(R.layout.activity_play_video);
        // get the toolbar
        toolbar=findViewById(R.id.toolbar);
        // set Toolbar Property
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }


        initVideoPlayer();
        onControlAction();

    }

    private void initVideoPlayer()
    {


        // This one is for Time change on left
        Player_integrate_UI=findViewById(R.id.player_control_view);
        Player_integrate_UI.setPlayer(globalVar.videoservice.player.getSimpleExoPlayer());

        // Hide or Show ToolBar etc Stuff
        Player_integrate_UI.setVisibilityListener(visibility -> {

            if(visibility==PlayerControlView.VISIBLE)
            {
                toolbar.setVisibility(View.VISIBLE);
                // set some flag
                showSystemUI();
            }
            if(visibility==PlayerControlView.GONE)
            {
                toolbar.setVisibility(View.GONE);
                //set some flag
                hideSystemUI();
            }

        });

        // Tap For Hide : <Tap For>
        Player_integrate_UI.setOnClickListener(v -> {
            hideUI();
        });
        all_control_layout=findViewById(R.id.layout_all_control_container);
        // Tap for Show : <Tap For>
        all_control_layout.setOnClickListener(v -> {
            showUI();

        });









        // take player view for displaying the video play
        playerView=findViewById(R.id.player_view);

        // integration Point of merging same player to both That to popup and main view
        playerView.setPlayer(globalVar.videoservice.player.getSimpleExoPlayer());
        // default set
        playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);


    }

    private void onControlAction()
    {
        // playpause
        FloatingActionButton playPause=findViewById(R.id.btnPlayPause);
        // set Animation
        playPause.setImageDrawable(globalVar.videoservice.playPauseDrawable);

        playPause.setOnClickListener(v -> {
            timeReset();
            playPause();
        });

        // popup
        popup=findViewById(R.id.btn_popup);
        popup.setOnClickListener(v -> {

            globalVar.playAsPopup=true;
            //pause();
            globalVar.videoservice.addPopupView();


            // for debugging
            globalVar.videoservice.Notification(globalVar.currentPlayingVideo);
            // for debugging

            finish();

        });

        // bg music
        bg_music=findViewById(R.id.btn_bgAudio);
          // set initial
          bg_music.setColor(new preference(context).getBgMusic() ? Color.GREEN : Color.WHITE);
        bg_music.setOnClickListener(v -> {

            timeReset();
           new  preference(context).setBgMusic();
            bg_music.setColor(new preference(context).getBgMusic() ? Color.GREEN : Color.WHITE);
        });

        // Rotation

        // only one time launch
        /*
        if(i==0)
        {
            setRequestedOrientation(new preference(context).getRequestedOrientation());
            i++;
        }

         */

        rotation=findViewById(R.id.btn_btnRotation);
          // on Initial Launch Icon
          rotateIcon(getRequestedOrientation());
        rotation.setOnClickListener(v -> {
            timeReset();

            changeOrientation();
        });

        // volume

        volume_btn=findViewById(R.id.btnVolumes);
        volume_region=findViewById(R.id.region_volume);
        volume_seekBar=findViewById(R.id.seekBar_volume);

        volume_btn.setOnClickListener(v -> {
            timeReset();
            brightness_region.setVisibility(View.INVISIBLE);
            volume_region.setVisibility(View.VISIBLE);
        });
        // Initializing the bar statistics
        if(getMaxVolume()!=-1)
        {
            // set the max value
            volume_seekBar.setMax(getMaxVolume());
            // set the current progress
            volume_seekBar.setProgress(getStreamVolume());
        }


        volume_seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                timeReset();
                setStreamVolume(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        brightness_btn=findViewById(R.id.btnBrightness);
        brightness_region=findViewById(R.id.region_brightness);
        brightness_seekbar=findViewById(R.id.seekBar_brightness);

        brightness_btn.setOnClickListener(v -> {
            timeReset();
            volume_region.setVisibility(View.INVISIBLE);
            brightness_region.setVisibility(View.VISIBLE);
        });
        // 100 is Max for Brightness
        brightness_seekbar.setMax(100);
        brightness_seekbar.setProgress(getBrightness());

        brightness_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                timeReset();
                setBrightness(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        // Layout for expansion and close
        expand=findViewById(R.id.expandable_layout);
        // This is an Button
        open_close=findViewById(R.id.btn_btnExpandControl);

        open_close.setOnClickListener(v -> {
            timeReset();
            if (i % 2 == 0) {
                expand.expand();
            }
            else
            {
                brightness_region.setVisibility(View.INVISIBLE);
                volume_region.setVisibility(View.INVISIBLE);
                expand.collapse();
            }
            i++;

        });

        onTimeViewVideo_Bar=findViewById(R.id.previewTimebar);

        onTimeViewVideo_Bar.addOnPreviewChangeListener(this);

        onTimeViewVideo_Bar.setDuration(globalVar.videoservice.player.getSimpleExoPlayer().getDuration());

        //Message_1(" On Create");
        onTimeViewVideo_Bar.setPosition(globalVar.videoservice.player.getSimpleExoPlayer().getCurrentPosition());
        onTimeViewVideo_Bar.addListener(new TimeBar.OnScrubListener() {
            @Override
            public void onScrubStart(TimeBar timeBar, long position) {
                timeReset();
                seek=true;
            }

            @Override
            public void onScrubMove(TimeBar timeBar, long position) {
                timeReset();
                // do Nothing
            }

            @Override
            public void onScrubStop(TimeBar timeBar, long position, boolean canceled) {
                timeReset();
                globalVar.videoservice.player.getSimpleExoPlayer().seekTo(position);
                onTimeViewVideo_Bar.setPosition(position);
                seek=false;
            }
        });

        if(getSupportActionBar()!=null)
        {
            getSupportActionBar().setTitle(globalVar.currentPlayingVideo.getName());
        }

        // for showing video list track

        trackVideo=findViewById(R.id.expandable_recyclerView_layout);

        // for debugging

       // Message_1(" Size : "+globalVar.videoTrack.size());

        // for debugging

        // everytime Its get launched Its will be Null Since Its not static And Its not needed
        if(trackListAdaptor==null) {
            trackListAdaptor = new trackListAdaptor(context);
            recyclerView = findViewById(R.id.recyclerView_playlist);
            // This one is neede without Its Wont launched It will show Empty List of Arrays
            recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
            recyclerView.setAdapter(trackListAdaptor);
            trackListAdaptor.update(globalVar.updatedTrack);
        }

        close_track=findViewById(R.id.btn_CloseList);
        // close the layout of track list
        close_track.setOnClickListener(v -> {
            trackVideo.collapse();
        });


        // On Initial Set
        type=findViewById(R.id.btn_repeatMode);

        // On Launching Icon Set
        iconChange();

        type.setOnClickListener(v -> {
            type();
            iconChange();
        });

        // next button
        post=findViewById(R.id.btn_skip_next);

        post.setOnClickListener(v -> {
            timeReset();
            globalVar.videoservice.Type(4);
        });

        // prev button
        pre=findViewById(R.id.btn_skip_pre);
        pre.setOnClickListener(v -> {
            timeReset();
            globalVar.videoservice.Type_1();
        });

        pre_10=findViewById(R.id.btn_skip_pre_10s);
        pre_10.setOnClickListener(v -> {
            timeReset();
            long position=globalVar.videoservice.player.getSimpleExoPlayer().getCurrentPosition();
            if(position-10000<0)
            {
                position=0;
            }
            globalVar.videoservice.player.getSimpleExoPlayer().seekTo(position-10000);

        });

        post_10=findViewById(R.id.btn_skip_next_10s);
        post_10.setOnClickListener(v -> {
            timeReset();
            long position=globalVar.videoservice.player.getSimpleExoPlayer().getCurrentPosition();
            if(position+10000<globalVar.videoservice.player.getSimpleExoPlayer().getDuration())
            {
                globalVar.videoservice.player.getSimpleExoPlayer().seekTo(position+10000);
            }

        });

        pre_10_layout=findViewById(R.id.layout_skip_pre_10s);
        post_10_layout=findViewById(R.id.layout_skip_next_10s);
        //pre_10_layout.setVisibility(View.VISIBLE);
       // post_10_layout.setVisibility(View.VISIBLE);


        // resize
        resize=findViewById(R.id.btnResize);
        resize.setOnClickListener(v -> {
            timeReset();
            // resize range from 0 to 4

            playerView.setResizeMode(resize_1);
            resize_1++;
            if(resize_1>4)
            {
                resize_1=0;
            }


        });

        lock_enable=findViewById(R.id.btnLock);
        lock_disable=findViewById(R.id.btnEnableAllControl);

        lock_enable.setOnClickListener(v -> {
            timeReset();

            hideUI();
            lock_disable.setVisibility(View.VISIBLE);
            lock_1=true;
        });
        lock_disable.setOnClickListener(v -> {
            lock_1=false;
            showUI();
            lock_disable.setVisibility(View.GONE);
        });

        // runner

        Runnable r=new Runnable() {
            @Override
            public void run() {

                Player_integrate_UI.postDelayed(this,10);

                if(init==1 && globalVar.videoservice.player.getSimpleExoPlayer().getPlaybackState()==Player.STATE_READY)
                {
                    //Message_1(" See This "+globalVar.videoservice.player.getSimpleExoPlayer().getCurrentPosition());
                    onTimeViewVideo_Bar.setDuration(globalVar.videoservice.player.getSimpleExoPlayer().getDuration());
                    onTimeViewVideo_Bar.setPosition(globalVar.videoservice.player.getSimpleExoPlayer().getCurrentPosition());
                    init=0;

                    // set Title
                    if(getSupportActionBar()!=null)
                    {
                        getSupportActionBar().setTitle(globalVar.currentPlayingVideo.getName());
                    }
                }
                if(!seek)
                {
                    onTimeViewVideo_Bar.setPosition(globalVar.videoservice.player.getSimpleExoPlayer().getCurrentPosition());
                }
                onTimeViewVideo_Bar.setDuration(globalVar.videoservice.player.getSimpleExoPlayer().getDuration());

                // for update the icon
                animPlay();

                // orientation change
                //layoutChange(getResources().getConfiguration().orientation);

                // volume change

                if(volume!=getStreamVolume()) {
                    setStreamVolume(getStreamVolume());
                }



                volume_seekBar.setProgress(getStreamVolume());

                // Title
                if(getSupportActionBar()!=null)
                {
                    getSupportActionBar().setTitle(globalVar.currentPlayingVideo.getName());
                }

                // for Full Screen



                // for debugging
                if(lock_1) {
                    hideSystemUI();
                    //Message_1(" Working");
                }
                // for debugging

                if(System.currentTimeMillis()-time>3000)
                {
                    hideSystemUI();
                    hideUI();
                }

                // Updating Notification on State  Play/Pause
                globalVar.videoservice.notificationCall();


                // for updating BG Music
                if(bg_music!=null)
                {
                    bg_music.setColor(new preference(context).getBgMusic() ? Color.GREEN : Color.WHITE);
                }

            }


        };
        Player_integrate_UI.postDelayed(r,10);

        // one time execution is needed
        layoutChange(getResources().getConfiguration().orientation);


    }

    private void type()
    {
         /*
             Flow

            Repeat All    -> Repeat Itself
            Repeat Itself -> Shuffle
            shuffle -> Repeat All

             */
        String s=(new preference(context)).getType();
        if(s.equals(preference.Repeat_all))
        {
            (new preference(context)).setType(preference.Repeat_Itself);
        }
        else if(s.equals(preference.Repeat_Itself))
        {
            (new preference(context)).setType(preference.shuffle);
        }
        else if(s.equals(preference.shuffle))
        {
            (new preference(context)).setType(preference.Repeat_all);
        }
        // Change The Icon
        iconChange();
    }

    private void iconChange()
    {
         /*
             Flow

            Repeat All    -> Repeat Itself
            Repeat Itself -> Shuffle
            shuffle -> Repeat All

             */

        String s=(new preference(context)).getType();
        if(s.equals(preference.Repeat_all))
        {
            type.setIcon(MaterialDrawableBuilder.IconValue.REPEAT_ONCE);
        }
        else if(s.equals(preference.Repeat_Itself))
        {
            type.setIcon(MaterialDrawableBuilder.IconValue.SHUFFLE);
        }
        else if(s.equals(preference.shuffle))
        {
            type.setIcon(MaterialDrawableBuilder.IconValue.REPEAT);
        }
    }

    private void setBrightness(int val)
    {

        if(val<5)
        {
            val=5;
        }
        WindowManager.LayoutParams layoutParams=getWindow().getAttributes();
        layoutParams.screenBrightness=(float) val/100;
        getWindow().setAttributes(layoutParams);
    }
    private int getBrightness()
    {
        // as an initial
        int x=-12;
        try
        {
           x=Settings.System.getInt(getContentResolver(),Settings.System.SCREEN_BRIGHTNESS);
        }
        catch(Exception E)
        {
            // DO Nothing
        }
        return x;
    }
    public void setStreamVolume(int volume)
    {
        AudioManager audioManager= (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int oldVolume,newVolume;

        newVolume=volume;
        oldVolume=getStreamVolume();


        if(newVolume>=oldVolume)
        {
            // flag 0 is the creation 1 is the execution
            audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,AudioManager.ADJUST_RAISE,0);
        }
        else
        {
            audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,AudioManager.ADJUST_LOWER,0);
        }


        // set At the End
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,newVolume,0);


        if(this.volume!=volume)
        {
            this.volume=volume;
        }

    }
    public int getStreamVolume()
    {
        AudioManager audioManager= (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        return audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
    }
    public int  getMaxVolume()
    {
        AudioManager audioManager= (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        // random int
       int x=12;
        try {
            x=audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        }
        catch (Exception E)
        {
            // Do Nothing
        }
        return x;

    }

    public void changeOrientation()
    {

        /*
         Here Its flow
         Portrait    -> Landscape
         Landscape   -> Auto Sensor
         Auto Sensor -> Portrait
         */
         orientation=getRequestedOrientation();

         // On first run It may be -1
         if(orientation==ActivityInfo.SCREEN_ORIENTATION_PORTRAIT || orientation==ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT || orientation==-1)
         {
             new preference(context).setOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
             rotateIcon(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
             Message_1(" Working 1 : "+new preference(context).getRequestOrientation());
             setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
         }
         else if(orientation==ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE || orientation==ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE)
         {

             new preference(context).setOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
             rotateIcon(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
             Message_1(" Working 2 : "+new preference(context).getRequestOrientation());
             setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
         }
         else if(orientation==ActivityInfo.SCREEN_ORIENTATION_SENSOR || orientation==ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR)
         {
             new preference(context).setOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
             Message_1(" Working 3 : "+new preference(context).getRequestOrientation());
             rotateIcon(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
             setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
         }


         // set preference




         // update Icon


    }
    public void rotateIcon(int orientation)
    {

        if(orientation==ActivityInfo.SCREEN_ORIENTATION_PORTRAIT || orientation==ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT)
        {
            rotation.setIcon(MaterialDrawableBuilder.IconValue.PHONE_ROTATE_LANDSCAPE);
        }
        else if(orientation==ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE || orientation==ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE)
        {
            rotation.setIcon(MaterialDrawableBuilder.IconValue.SCREEN_ROTATION);
        }
        else if(orientation==ActivityInfo.SCREEN_ORIENTATION_SENSOR || orientation==ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR)
        {
            rotation.setIcon(MaterialDrawableBuilder.IconValue.PHONE_ROTATE_PORTRAIT);
        }
    }


    public void playPause()
    {


        // for debugging
        if(!globalVar.videoservice.audioRequest())
        {
            return ;
        }
        // for debugging

        if(globalVar.videoservice.player.getSimpleExoPlayer().getPlayWhenReady())
        {
            pause();
        }
        else
        {
            play();
        }



    }
    public void play()
    {
        globalVar.videoservice.player.getSimpleExoPlayer().setPlayWhenReady(true);
        globalVar.videoservice.playPauseDrawable.transformToPause(true);
    }
    public void pause()
    {
        globalVar.videoservice.player.getSimpleExoPlayer().setPlayWhenReady(false);
        globalVar.videoservice.playPauseDrawable.transformToPlay(true);
    }
    public void animPlay()
    {


        if(globalVar.videoservice.player.getSimpleExoPlayer().getPlayWhenReady())
        {
            globalVar.videoservice.playPauseDrawable.transformToPause(false);
        }
        else
        {
            globalVar.videoservice.playPauseDrawable.transformToPlay(false);
        }
    }



    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        layoutChange(newConfig.orientation);
    }
    private void layoutChange(int orientation)
    {
        if(orientation==Configuration.ORIENTATION_PORTRAIT)
        {
            pre_10_layout.setVisibility(View.GONE);
            post_10_layout.setVisibility(View.GONE);

            if(toolbar!=null)
            {
                ViewGroup.MarginLayoutParams params= (ViewGroup.MarginLayoutParams) toolbar.getLayoutParams();
                params.setMarginEnd(10*(int) getResources().getDisplayMetrics().density);
            }
            if(open_close!=null)
            {
                ViewGroup.MarginLayoutParams params= (ViewGroup.MarginLayoutParams) open_close.getLayoutParams();
                params.setMarginEnd(10*(int) getResources().getDisplayMetrics().density);
            }
        }
        else if (orientation==Configuration.ORIENTATION_LANDSCAPE)
        {
            pre_10_layout.setVisibility(View.VISIBLE);
            post_10_layout.setVisibility(View.VISIBLE);

            if(toolbar!=null)
            {
                ViewGroup.MarginLayoutParams params= (ViewGroup.MarginLayoutParams) toolbar.getLayoutParams();
                params.setMarginEnd(50*(int) getResources().getDisplayMetrics().density);
            }
            if(open_close!=null)
            {
                ViewGroup.MarginLayoutParams params= (ViewGroup.MarginLayoutParams) open_close.getLayoutParams();
                params.setMarginEnd(50*(int) getResources().getDisplayMetrics().density);
            }
        }
    }

    public void infoDialog(videoItem v)
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        View view=getLayoutInflater().inflate(R.layout.content_video_info,null);
        TextView title,path,format,size,duration,resolution,dateAndTime;
        title=view.findViewById(R.id.txtVideoTitle);
        title.setText(v.getName());
        path=view.findViewById(R.id.txtLocation_value);
        path.setText(v.getPath());
        format=view.findViewById(R.id.txtFormat_value);
        format.setText(v.extension());
        size=view.findViewById(R.id.txtFileSize_value);
        size.setText(v.getFilesize());
        duration=view.findViewById(R.id.txtDuration_value);
        duration.setText(v.getDuration());
        resolution=view.findViewById(R.id.txResolution_value);
        resolution.setText(v.getResolution());
        dateAndTime=view.findViewById(R.id.txtDateAdded_value);
        dateAndTime.setText(v.getDate());

        builder.setView(view);

        builder.create().show();


    }


    // set some Flag
    private void hideSystemUI()
    {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }
    private void showSystemUI()
    {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }
    private void showUI()
    {
        timeReset();

        if(!lock_1) {
            time=System.currentTimeMillis();
            Player_integrate_UI.show();
        }
        // to show Lock
        else
        {
            lock_disable.setVisibility(View.VISIBLE);
        }

    }
    private void hideUI()
    {
        timeReset();

        hideVolumeAndBrightnessRegion();
        Player_integrate_UI.hide();

        // set Lock Invisible
        if(lock_1)
        {
            lock_disable.setVisibility(View.INVISIBLE);
        }
    }

    private void hideVolumeAndBrightnessRegion()
    {
        volume_region.setVisibility(View.GONE);
        brightness_region.setVisibility(View.GONE);
    }

    private void  timeReset()
    {
        time=System.currentTimeMillis();
    }

    @Override
    public boolean onSupportNavigateUp() {
        super.onBackPressed();

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.more_option,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        // three parallel line
        if(id==R.id.action_menu)
        {
            // open/close
            if(trackVideo!=null)
            {
                trackVideo.toggle();
            }
        }
        else if(id==R.id.action_info)
        {
            infoDialog(globalVar.currentPlayingVideo);
        }
        else if(id==R.id.action_share)
        {
            startActivity(Intent.createChooser(kxUtil.shareSingleVideo(globalVar.currentPlayingVideo,context),"Share Video"));
        }


        // default
        return false;
    }

    @Override
    public void onBackPressed() {

        if(lock_1)
        {
            return;
        }

        if(!(new preference(context)).getBgMusic())
        {
            // pause if BG is not allowed
            globalVar.videoservice.player.getSimpleExoPlayer().setPlayWhenReady(false);
        }

        super.onBackPressed();


    }

    @Override
    protected void onPause()
    {

        if(!(new preference(context)).getBgMusic())
        {
            // pause if BG is not allowed
            globalVar.videoservice.player.getSimpleExoPlayer().setPlayWhenReady(false);
        }
        super.onPause();
    }



    // for debugging
    private void Message_1(String m)
    {
        Context context=getApplicationContext();
        int duration= Toast.LENGTH_SHORT;
        Toast t=Toast.makeText(context,m,duration);
        t.show();
    }

    // for debugging

    /*
    // dir detection

    // for debugging

        for(File file : context.getExternalFilesDirs("external"))
          {
            Message_1(" See This Dir : "+file.getPath());
          }

    // for debugging

     */

    @Override
    public void onStartPreview(PreviewView previewView, int progress) {

    }

    @Override
    public void onStopPreview(PreviewView previewView, int progress) {

    }

    @Override
    public void onPreview(PreviewView previewView, int progress, boolean fromUser) {

    }
}
