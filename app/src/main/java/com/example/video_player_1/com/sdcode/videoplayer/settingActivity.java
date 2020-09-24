package com.example.video_player_1.com.sdcode.videoplayer;

import android.os.Bundle;
import android.view.View;
import android.widget.Switch;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.example.video_player_1.R;



public class settingActivity extends baseAcitivity
{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);

        Toolbar toolbar=findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Setting");
        }

        Switch s=findViewById(R.id.backgroundAudioSwitch);

        s.setChecked(new preference(this).getBgMusic());


        s.setOnClickListener(v -> {
            new preference(this).setBgMusic();
            s.setChecked(new preference(this).getBgMusic());

            // set Flag
            if(new preference(this).getBgMusic())
            {
                globalVar.playPause=true;
            }
            else
            {
                globalVar.playPause=false;
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}

