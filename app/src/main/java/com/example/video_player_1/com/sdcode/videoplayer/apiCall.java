package com.example.video_player_1.com.sdcode.videoplayer;

import android.content.Context;

import android.content.Context;
import android.net.Uri;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import com.example.video_player_1.R;


public class apiCall
{
    Context context;

    private  SimpleExoPlayer simpleExoPlayer;
    private  DataSource.Factory mediaDataSourceFactory;
    private DefaultTrackSelector trackSelector;
    apiCall(Context context)
    {
        this.context=context;
        initPlayer();
    }

    // Api Call for Set Video Player
    public void initPlayer()
    {

        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        mediaDataSourceFactory = new DefaultDataSourceFactory(context,
                Util.getUserAgent(context, context.getString(R.string.app_name)));
        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);

        trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        simpleExoPlayer =  ExoPlayerFactory.newSimpleInstance(context, trackSelector);
        simpleExoPlayer.setPlayWhenReady(true);
    }
    // Play video
    public void play(boolean resetPosition, boolean resetState,String path){

        DefaultExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();

//            MediaSource mediaSource = new ExtractorMediaSource(Uri.parse(GlobalVar.getInstance().getPath()),
//                    mediaDataSourceFactory, extractorsFactory, null, null);
        // Now We are Parseling the Path of video Which is playing
        MediaSource mediaSource = new ExtractorMediaSource.Factory(mediaDataSourceFactory).setExtractorsFactory(extractorsFactory)
                .createMediaSource(Uri.parse(path));
        simpleExoPlayer.prepare(mediaSource, resetPosition, resetState);

    }

    public SimpleExoPlayer getSimpleExoPlayer() {
        return simpleExoPlayer;

    }
    public void setVolume(float volume)
    {
        simpleExoPlayer.setVolume(volume);
    }
}
