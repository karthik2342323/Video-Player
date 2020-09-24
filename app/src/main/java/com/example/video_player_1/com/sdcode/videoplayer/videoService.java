package com.example.video_player_1.com.sdcode.videoplayer;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.media.AudioManager;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.media.session.MediaSessionCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationCompat.Builder;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.palette.graphics.Palette;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestFutureTarget;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.video_player_1.R;
import com.example.video_player_1.com.sdcode.videoplayer.apiCall;
import com.example.video_player_1.com.sdcode.videoplayer.customizeUI.PlayPauseDrawable;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;

import com.example.video_player_1.com.sdcode.videoplayer.Videos.videoItem;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;

import java.util.Random;


public class videoService extends Service implements AudioManager.OnAudioFocusChangeListener {


    private static int i=1;

    public static String PLAYPAUSE="playPause";
    private static String PREV="pref";
    private static String NEXT="NEXT";
    private static String CLOSE="close";
    private static ImageView close,fullScreen;
    public static String POPUP="popup";
    public static String NOT_POPUP="notpopup";
    public static String CLOSE_NOTIFICATION="close notification";


    public apiCall player;
    public PlayPauseDrawable playPauseDrawable=new PlayPauseDrawable();

    // For Loading Thumbnail
    private Bitmap thumbNail;

    public FrameLayout container;
    private PlayerView playerView;
    private RelativeLayout layout_drag;

    private RelativeLayout top_UI;
    private ImageButton imageButton;
    public WindowManager windowManager;
    private AudioManager audioRequest;

    private WindowManager.LayoutParams params_formal;
    int para;
    private Context context;
    private MediaSessionCompat session;

    Long lastTouchTime;
    int hideTimout=2000;
    static int z=0,y=0;
    int signal=0,signal_1=0,signal_3=0;

    private static final float MEDIA_VOLUME_DUCK = 0.2f;
    private static int prev_vol;


    int arr[]=new int[0];
    int x=hashCode();



    // Case when Its playing and Plug is unplugged while playing Video
    // so If Its playing then pause it .
    private BroadcastReceiver broadcastReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(AudioManager.ACTION_AUDIO_BECOMING_NOISY))
            {
                // If Its Playing Then Pause
                if(player.getSimpleExoPlayer().getPlayWhenReady())
                {

                    pause();
                }
            }
        }
    };

    private IntentFilter type=new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);

    @Override
    public void onCreate() {

        context=getApplicationContext();

        session=new MediaSessionCompat(context," Session");


        // Initialize the Player
        initPlayer();

        // Notification on oreo+ DO Later

        audioRequest= (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        /* When we pull headphone on playing video then
           Audio will become noisy  so  to track
         */
        registerReceiver(broadcastReceiver,type);

    }
    private void initPlayer()
    {
        if(player==null)
        {
            player=new apiCall(getApplicationContext());
            player.getSimpleExoPlayer().addListener(new Player.EventListener() {
                @Override
                public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

                }

                @Override
                public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

                }

                @Override
                public void onLoadingChanged(boolean isLoading) {

                }


                /*
                For Eg VIdeo is ended
                 */
                @Override
                public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                    String s=(new preference(getApplicationContext())).getType();

                    // Only One video is there in List so direct play with respective Type
                    if(globalVar.updatedTrack.size()==1 && playbackState==Player.STATE_ENDED)
                    {
                        // Individual Play
                        if(s.equals(preference.Repeat_all) || s.equals(preference.Repeat_Itself))
                        {
                            globalVar.currentPath="";
                            playVideo(globalVar.currentPlayingVideo,globalVar.playAsPopup);
                        }
                        // for shuffle atleast 2 videos are needed
                    }

                    // for repeat Itself We Have to play Same
                    else if(playbackState==Player.STATE_ENDED && s.equals(preference.Repeat_Itself))
                    {
                        globalVar.currentPath="";
                        globalVar.videoservice.playVideo(globalVar.currentPlayingVideo,globalVar.playAsPopup);
                    }
                    else
                    {
                        Type(playbackState);
                    }

                }

                @Override
                public void onRepeatModeChanged(int repeatMode) {

                }

                @Override
                public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

                }

                @Override
                public void onPlayerError(ExoPlaybackException error) {

                }

                @Override
                public void onPositionDiscontinuity(int reason) {

                }

                @Override
                public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

                }

                @Override
                public void onSeekProcessed() {

                }
            });
        }
    }

    // This one is for Playing Next Video from
    public void Type(int playbackState)
    {


        String s=(new preference(getApplicationContext())).getType();
        // default
        if(playbackState==Player.STATE_ENDED) {
            int i = 0, signal = 0;

            if(globalVar.updatedTrack.size()==1)
            {
                return ;
            }
            else if (s.equals(preference.Repeat_all) || s.equals(preference.Repeat_Itself)) {
                for (videoItem x : globalVar.updatedTrack) {
                    if (x == globalVar.currentPlayingVideo) {
                        if (i == globalVar.updatedTrack.size() - 1) {
                            signal = 0;
                        } else {
                            signal = 1;
                        }
                        break;
                    }
                    i++;
                }
                // if Video Found
                if (signal == 1) {
                   if(i==globalVar.updatedTrack.size()-1)
                   {
                       i=0;
                       globalVar.videoservice.playVideo(globalVar.updatedTrack.get(i),globalVar.playAsPopup);
                   }
                   else
                   {
                       i++;
                       globalVar.videoservice.playVideo(globalVar.updatedTrack.get(i),globalVar.playAsPopup);
                   }
                }
                // If Its not found because of user has removed from list
                else {
                    // start from beginning as per List 1 video will always there
                    i = 0;
                    if(globalVar.updatedTrack.size()==1)
                    {
                        globalVar.currentPath="";
                    }
                    playVideo(globalVar.updatedTrack.get(i), globalVar.playAsPopup);
                }
            }


            /*
            else if(s.equals(preference.Repeat_Itself))
            {
                globalVar.currentPath="";
                playVideo(globalVar.currentPlayingVideo,globalVar.playAsPopup);
            }

             */

            else if(s.equals(preference.shuffle))
            {

                // for debugging

               // Message_1(" See This Shuffle");

                // for debugging

                signal=0;
                i=0;
                globalVar.currentPath="";
                if(arr.length!=globalVar.updatedTrack.size())
                {
                    arr=new int[globalVar.updatedTrack.size()];
                    Random r=new Random();
                    int buffer;
                    int index;
                    // Initialization
                    for(i=0;i<arr.length;i++)
                    {
                        arr[i]=i;
                    }
                    // shuffle
                    for(i=arr.length-1;i>=1;i--)
                    {
                        index=r.nextInt(i);
                        buffer=arr[i];
                        arr[i]=arr[index];
                        arr[index]=buffer;
                    }
                    globalVar.videoservice.playVideo(globalVar.updatedTrack.get(arr[0]),globalVar.playAsPopup);
                    z=0;
                }
                else
                {
                    if(z==globalVar.updatedTrack.size()-1)
                    {
                        z=0;
                    }
                    else
                    {
                        z++;
                    }
                    globalVar.videoservice.playVideo(globalVar.updatedTrack.get(arr[z]),globalVar.playAsPopup);
                }

            }
        }
    }

    // This one is for prev video
    public void Type_1()
    {

        String s=(new preference(getApplicationContext())).getType();

        int i = 0, signal = 0;
        if(globalVar.updatedTrack.size()==1)
        {
            return ;
        }
        else if (s.equals(preference.Repeat_all) || s.equals(preference.Repeat_Itself)) {
            for (videoItem x : globalVar.updatedTrack) {

                if (x == globalVar.currentPlayingVideo) {

                    signal = 1;

                    break;
                }
                i++;
            }
            // if Video Found
            if (signal == 1) {
                if(i==0)
                {
                    i=globalVar.updatedTrack.size()-1;
                    globalVar.videoservice.playVideo(globalVar.updatedTrack.get(i),globalVar.playAsPopup);
                }
                else
                {
                    i--;
                    globalVar.videoservice.playVideo(globalVar.updatedTrack.get(i),globalVar.playAsPopup);
                }
            }
            // If Its not found because of user has removed from list
            else {
                // start from beginning as per List 1 video will always there
                i = 0;
                if(globalVar.updatedTrack.size()==1)
                {
                    globalVar.currentPath="";
                }
                playVideo(globalVar.updatedTrack.get(i), globalVar.playAsPopup);
            }
        }


        /*
        else if(s.equals(preference.Repeat_Itself))
        {
            globalVar.currentPath="";
            playVideo(globalVar.currentPlayingVideo,globalVar.playAsPopup);
        }

         */

        else if(s.equals(preference.shuffle))
        {
            signal=0;
            i=0;
            globalVar.currentPath="";
            if(arr.length!=globalVar.updatedTrack.size())
            {
                arr=new int[globalVar.updatedTrack.size()];
                Random r=new Random();
                int buffer;
                int index;
                // Initialization
                for(i=0;i<arr.length;i++)
                {
                    arr[i]=i;
                }
                // shuffle
                for(i=arr.length-1;i>=1;i--)
                {
                    index=r.nextInt(i);
                    buffer=arr[i];
                    arr[i]=arr[index];
                    arr[index]=buffer;
                }
                globalVar.videoservice.playVideo(globalVar.updatedTrack.get(arr[0]),globalVar.playAsPopup);
                z=0;
            }
            else
            {
                if(z==0)
                {
                    z=globalVar.updatedTrack.size()-1;
                }
                else
                {
                    z--;
                }
                globalVar.videoservice.playVideo(globalVar.updatedTrack.get(arr[z]),globalVar.playAsPopup);
            }

        }
    }



    // Audio Request

    public boolean audioRequest()
    {
       // audioRequest= (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int status=audioRequest.requestAudioFocus(this,AudioManager.STREAM_MUSIC,AudioManager.AUDIOFOCUS_GAIN);
        return status==AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
    }


    // Api Call
    public void playVideo(videoItem v,boolean playAsPopup)
    {

        globalVar.play_signal=true;


        // for debugging

        lastTouchTime=System.currentTimeMillis();

        // for debugging

        // set the current state
        globalVar.playAsPopup=playAsPopup;

        // get Audio Request before calling api to play
        if(!audioRequest())
        {
            return;
        }


        // if User are not playing same video which we are playing
        if(!v.getPath().equals(globalVar.currentPath))
        {




            // set the path
            globalVar.currentPath=v.getPath();

            // set the video
            globalVar.currentPlayingVideo=v;
            // Play Video
            player.play(true, false, v.getPath());
            // This one depends for traversing from pop to main Screen
            player.getSimpleExoPlayer().setPlayWhenReady(true);


            // for debugging

            videoPlayerMain.init=1;

            // for debugging


            // set accordingly
            if(player.getSimpleExoPlayer().getPlayWhenReady())
            {
                playPauseDrawable.transformToPause(true);
            }
            else
            {
                playPauseDrawable.transformToPlay(true);
            }


            // Notification Launch will do Later
            //  for Version_code <= Lolipop
            if(Build.VERSION.SDK_INT<=Build.VERSION_CODES.LOLLIPOP_MR1)
            {

                if(new preference(context).getBgMusic()) {
                    Notification(v);
                }

            }
            else
            {
                // do later
            }

            if(playAsPopup)
            {

                // Need to add after Lolipop +
                addPopupView();
            }


        }

    }


    public void Notification(videoItem v)
    {



        // Loading ThumbNail on Notification via Glide Module
        Glide.with(this).asBitmap().load(v.getPath()).listener(new RequestListener<Bitmap>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {


                // Do Noting

                return false;
            }

            @Override
            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {


                // fetch the Thumbnail
                thumbNail=resource;

                // Default BG Play is true For that we need to set preference

                //x=hashCode();
                startForeground(x,notification_1(globalVar.currentPlayingVideo));

                return false;
            }
        }).submit();





    }

    public Notification notification_1(videoItem v)
    {


        if(thumbNail==null)
        {
            Message_1(" Thumbnail Null");
            return null;
        }


        globalVar.notificationFlag=true;

        boolean isPlaying=player.getSimpleExoPlayer().getPlayWhenReady();
        int state=isPlaying ? R.drawable.ic_pause_white : R.drawable.ic_play_white;
        // If Notification gets clicked then show home activity default
        Intent intent=new Intent(this,videoPlayerMain.class);
        // But Its AN Pending Intend
        PendingIntent pin=PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        // use  whole dir This One androidx.core.app.NotificationCompat.Builder other wise It will Throw Glide Exception

        androidx.core.app.NotificationCompat.Builder builder=new androidx.core.app.NotificationCompat.Builder(this," Random");
        // This One is Random Means No Usage Needed for showing Notification Activity
        builder.setSmallIcon(R.drawable.exo_icon_play);
        // ThumbNail
        builder.setLargeIcon(thumbNail);
        // title
        builder.setContentTitle(v.getName());
        // set content means path
        builder.setContentText(v.getPath());

        if(!globalVar.playAsPopup) {
            builder.setContentIntent(pin);
        }

        builder.setWhen(System.currentTimeMillis());

        /*
         actions buttons are remaining and Creation is Remaining
         */


        builder.addAction(R.drawable.ic_skip_previous_white,"",onActionReroute(PREV));
        builder.addAction(state,"",onActionReroute(PLAYPAUSE));
        builder.addAction(R.drawable.ic_skip_next_white,"",onActionReroute(NEXT));

        // LAST one is remaining i.e close
        builder.addAction(R.drawable.ic_btn_delete,"",onActionReroute(CLOSE_NOTIFICATION));

        builder.setShowWhen(false);

        builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        if(thumbNail!=null)
        {
            builder.setColor(Palette.from(thumbNail).generate().getVibrantColor(Color.parseColor("#403f4d")));
        }

        androidx.media.app.NotificationCompat.MediaStyle style=new androidx.media.app.NotificationCompat.MediaStyle()
                .setMediaSession(session.getSessionToken())
                .setShowActionsInCompactView(1,2,3,0);
        builder.setStyle(style);

        return builder.build();




        // for temperory
        //return new Notification();
        // for temperory

    }



    public void addPopupView()
    {
        // If Popup is already there Then Not to Reallocate Again
        if(container!=null)
        {
            return;
        }

        // Create Frame Layout
        container=new FrameLayout(this)
        {
            @Override
            public boolean onInterceptTouchEvent(MotionEvent ev) {
                return super.onInterceptTouchEvent(ev);
            }
        };

        // Inflator
        LayoutInflater inflater= (LayoutInflater) getApplication().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // Integration 1 with Frames or Root
        View view=inflater.inflate(R.layout.popup_video,container);

        // get Player
        playerView=view.findViewById(R.id.player_view);
        playerView.setPlayer(player.getSimpleExoPlayer());

        // for debugging
          // set screen size
           playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
        // for debugging

        // Drag Layout to drag Frames
        layout_drag=view.findViewById(R.id.layout_all_control_container);

        // Controller on Top
        top_UI=view.findViewById(R.id.layout_control_top);

        // buttons working

             // playpause
        imageButton=view.findViewById(R.id.btnPlayPause);
        // Attach with Animaton
        imageButton.setImageDrawable(playPauseDrawable);
        // On Initial Loading
        if(player.getSimpleExoPlayer().getPlayWhenReady())
        {
            playPauseDrawable.transformToPause(false);
        }
        else
        {
            playPauseDrawable.transformToPlay(false);
        }
        imageButton.setOnClickListener(v1 -> {
            handleAction(PLAYPAUSE);

        });
        close=view.findViewById(R.id.btnClosePopUp);
        close.setOnClickListener(v -> {
            globalVar.playAsPopup=false;
            pause();

            // remove the view
            windowManager.removeView(container);
            // make container null
            container=null;

            // This one is for if Same video is clicked then play Remove the comparison
            // i.e make It empty path

            globalVar.currentPath="";

        });
        fullScreen=view.findViewById(R.id.btnFullScreenMode);
        fullScreen.setOnClickListener(v -> {

            // pause if BG Music is not enable
            if(!new preference(context).getBgMusic())
            {
                pause();
            }

            // remove the view
            windowManager.removeView(container);
            // make container null
            container=null;

            // start Main Player
            globalVar.playAsPopup=false;

            // for debugging

            globalVar.videoservice.Notification(globalVar.currentPlayingVideo);

            // for debugging

            Intent intent=new Intent(this,videoPlayerMain.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        });


        windowManager= (WindowManager) getSystemService(Context.WINDOW_SERVICE);

        // set Para
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {
            para=WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }
        else
        {
            para=WindowManager.LayoutParams.TYPE_PHONE;
        }



        // set popup size and default axis i.e (0,0) Top Left
        setPopupSize();

        // Integrate 2 with Windows
        windowManager.addView(container,params_formal);

        // set Visible
        container.setVisibility(View.VISIBLE);

        // For Dragging View
        setDragListner();

        // Runner is for hide UI after specific Time Exceeds

        Runnable r=new Runnable() {

            @Override
            public void run() {

                if(System.currentTimeMillis()-lastTouchTime>=hideTimout)
                {
                   hideContainer();
                }

                if(player.getSimpleExoPlayer().getPlayWhenReady())
                {
                    playPauseDrawable.transformToPause(false);
                }
                else
                {
                    playPauseDrawable.transformToPlay(false);
                }
                layout_drag.postDelayed(this,1);


                notificationCall();

            }
        };

        layout_drag.postDelayed(r,1);

        /*
         This is for let say traversing from primary player from
         popup so for that time It will be pause() After reaching
         Over Here Considering That Everything Will Loaded
         At End of Line So Play Video
         */
        play();



    }

    private void showContainer()
    {
        imageButton.setVisibility(View.VISIBLE);
        top_UI.setVisibility(View.VISIBLE);
    }
    private void hideContainer()
    {
        imageButton.setVisibility(View.GONE);
        top_UI.setVisibility(View.GONE);
    }

    private void setDragListner()
    {


        layout_drag.setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            // scaled para
            private float initialTouchX;
            private float initialTouchY;


            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        // Initial Para
                        initialX = params_formal.x;
                        initialY = params_formal.y;
                        // Fetch The Current Scaled Para
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        // show Container
                        showContainer();
                        // for container hide
                        lastTouchTime = System.currentTimeMillis();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        params_formal.x = initialX
                                + (int) (event.getRawX() - initialTouchX);
                        params_formal.y = initialY
                                + (int) (event.getRawY() - initialTouchY);
                        // Update the Axis
                        windowManager.updateViewLayout(container,params_formal);
                        break;
                }

                return true;
            }
        });

    }

    // with initialization of Axis i.e Default Positon (x,y) -> (0,0) Top Left
    public void setPopupSize()
    {
        /*
         Portrait  -> Width
         LandScape -> Height
         */
        int baseSize= Resources.getSystem().getDisplayMetrics().widthPixels;
        if(getApplicationContext().getResources().getConfiguration().orientation== Configuration.ORIENTATION_LANDSCAPE)
        {
            baseSize= Resources.getSystem().getDisplayMetrics().heightPixels;
        }
        params_formal=new WindowManager.LayoutParams( (int) (baseSize / 1.3+60),
                (int)(baseSize / (1.3*1.5)),para,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        // On Initialization of (x,y) Means start from top left coordinate (0,0)
        params_formal.x=0;
        params_formal.y=0;
    }


    public void handleAction(String action)
    {
        if(action.equals(PLAYPAUSE))
        {
            // Take The Reverse
               // If Its Playing Then Pause It
            if(player.getSimpleExoPlayer().getPlayWhenReady())
            {
              pause();
            }
               // If Its Pause Then Play
            else
            {
              play();
            }

            // for debugging

            notificationCall();

            // for debugging
        }
        else if(action.equals(PREV))
        {
            Type_1();
        }
        else if(action.equals(NEXT))
        {
            // 4 is an player Ended
            Type(4);
        }

        // If Notification  was Canceled means no BG Music
        else if(action.equals(CLOSE_NOTIFICATION))
        {
            new preference(context).setBgMusic();
            globalVar.notificationFlag=false;
            stopForeground(true);
        }


    }

    public void play()
    {
        // for debugging

        if(!audioRequest())
        {
            return ;
        }

        // for debugging

        player.getSimpleExoPlayer().setPlayWhenReady(true);
        playPauseDrawable.transformToPause(false);
    }
    public void pause()
    {
        player.getSimpleExoPlayer().setPlayWhenReady(false);
        playPauseDrawable.transformToPlay(false);
    }




    //Audio Resolver for any Interrupt

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange)
        {

            // Rerouting
            case AudioManager.AUDIOFOCUS_GAIN:
                // for case 2
                if(signal==1)
                {
                    play();
                }
                // for case 1
                else if(signal_1==1)
                {
                    setStreamVolume(prev_vol);
                }

                //reset The Signal
                signal=0;
                signal_1=0;
             break;


            // case 1 : Notification sound popup so lower the volume during popup time after that
            // set to Volume It Has been before



            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                signal_1=1;

                // Take previous volume after Notification Set Volume
                prev_vol=getStreamVolume();

                player.setVolume(MEDIA_VOLUME_DUCK);
                break;


            // case 2 : Alarm Interrupt (Reroutine)
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                // Pause
                if(player.getSimpleExoPlayer().getPlayWhenReady())
                {
                    player.getSimpleExoPlayer().setPlayWhenReady(false);
                    signal=1;
                }
                break;

            // case 3 : On PLaying Any Other  Video or Audio while THis video is playing
            case AudioManager.AUDIOFOCUS_LOSS:
                // for debugging

                Message_1(" Working");

                // for debugging
                audioRequest.abandonAudioFocus(this);
                // If Its playing Then Pause It
                if(player.getSimpleExoPlayer().getPlayWhenReady())
                {
                    player.getSimpleExoPlayer().setPlayWhenReady(false);
                }
                break;


        }
    }


    // for debugging

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

    }
    public int getStreamVolume()
    {
        AudioManager audioManager= (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        return audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    // for debugging


    // After Calling This Thing Execute :  Tag <B>
    private PendingIntent onActionReroute(String action)
    {
        // Refrence to Itself
        final ComponentName name=new ComponentName(this,videoService.class);
        Intent intent=new Intent(action);
        intent.setComponent(name);
        return PendingIntent.getService(context,0,intent,0);


    }
    // Tag <B>
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        if(intent.getAction()!=null) {

            handleAction(intent.getAction());
        }
        //return super.onStartCommand(intent, flags, startId);
        return START_NOT_STICKY;
    }


    public void notificationCall()
    {
        if(new preference(context).getBgMusic())
        {
            // We need to relaunch When state gets changed i.e play to pause or pause to play
            if(globalVar.playPause!=player.getSimpleExoPlayer().getPlayWhenReady()) {
                // For thumbNail Loading
                globalVar.playPause=!globalVar.playPause;
                Notification(globalVar.currentPlayingVideo);
                //startForeground(x, notification_1(globalVar.currentPlayingVideo));
            }
            // let say state is same and preference gets true so It wont launch untill state gets changed

            // If It Wont launched Then launched
            else if(!globalVar.notificationFlag)
            {
                // set the flag
                globalVar.notificationFlag=true;

                // Do Work

                // For thumbNail Loading
                Notification(globalVar.currentPlayingVideo);
               // startForeground(x, notification_1(globalVar.currentPlayingVideo));
            }
        }
        else
        {
            if(globalVar.notificationFlag)
            {
                // reset Flag
                globalVar.notificationFlag=false;
                // do work
                stopForeground(true);
            }
        }

    }

    public void notificationCancel()
    {
        // reset Flag
        globalVar.notificationFlag=false;
        // do work
        stopForeground(true);
    }
    // Connection Part Binder
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new kk();
    }

    public class kk extends Binder
    {
        public videoService read()
        {
            return videoService.this;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        unregisterReceiver(broadcastReceiver);
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
}
